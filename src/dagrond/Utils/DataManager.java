package dagrond.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import dagrond.Main;
import fr.xephi.authme.api.v3.AuthMeApi;

public class DataManager {
  /*                Zapisywanie glosowan w data.yml:
   * votes:
   *  (uuid czlonka valhalli):
   *    (uuid gracza ktory sie stara dostac do valhalli): (dane: tak/nie)
   *    (kolejne uuid): (kolejne dane)
   *  (nastepny czlonek valhalli):
   *    (jakies uuid pretendenta): (tak/nie)
   */
  protected Main plugin;
  protected ConfigAccessor dataAccessor;
  protected HashSet<UUID> onlineMembers = new HashSet<>(); //gracze, ktorzy sa czlonkami valhalli i sa online
  protected HashSet<UUID> members = new HashSet<>(); //gracze, ktorzy sa w Valhalli
  protected HashSet<UUID> waitingPlayers = new HashSet<>(); //gracze, ktorzy zaaplikowali ale nie dostali jeszcze odpowiedniej ilosci glosow
  protected HashSet<UUID> waitingMembers = new HashSet<>(); //gracze, ktorzy juz dostali sie do valhalli ale serwer oczekuje z ich dodaniem do czasu, az beda online
  protected HashSet<UUID> membersToRemove = new HashSet<>(); //gracze, ktorzy zostana usunieci po wejsciu
  
  public DataManager(Main plugin) {
    this.plugin = plugin;
    dataAccessor = new ConfigAccessor(plugin, "data.yml");
    dataAccessor.saveDefaultConfig();
    loadData();
  }
  
  public void addWaitingPlayer(Player player) {
    waitingPlayers.add(player.getUniqueId());
  }
  
  public HashSet<UUID> getAllMembers() {
    return members;
  }
  
  public boolean isMember(Player player) {
    if (members.contains(player.getUniqueId()))
      return true;
    else
      return false;
  }
  
  public void broadcastToMembers (String msg) {
    for(UUID uuid: getOnlineMembers()) {
      Bukkit.getPlayer(uuid).sendMessage(msg);
    }
  }
  
  public boolean isWaiting(Player player) {
    if (waitingMembers.contains(player.getUniqueId()))
      return true;
    else
      return false;
  }
  
  public HashSet<UUID> getBannedMembers() {
	    return membersToRemove;
	  }
  
  public HashSet<UUID> getWaitingPlayers() {
    return waitingPlayers;
  }
  
  public HashSet<UUID> getWaitingMembers() {
    return waitingMembers;
  }
  
  public void setPlayerVote(Player player, UUID candidate, String vote) {
    dataAccessor.getConfig().set("votes."+player.getUniqueId().toString()+"."+candidate.toString(), vote);
  }
  
  public HashMap<UUID, String> getSpecificPlayerVotes (Player player) {
    HashMap<UUID, String> votes = new HashMap<>();
    if (dataAccessor.getConfig().isConfigurationSection("votes."+player.getUniqueId())) {
      for (Object uuid : dataAccessor.getConfig().getConfigurationSection("votes."+player.getUniqueId()).getKeys(false)) {
        votes.put(UUID.fromString(uuid.toString()), dataAccessor.getConfig().getString("votes."+player.getUniqueId()));
      }
    }
    return votes;
  }
  
  public HashMap<UUID, String> getPlayerResults (Player player) {
    HashMap<UUID, String> votes = new HashMap<>();
    for (UUID uuid: members) {
      if (dataAccessor.getConfig().isConfigurationSection("votes."+uuid.toString()+"."+player.getUniqueId().toString())) {
        votes.put(uuid, dataAccessor.getConfig().getString("votes."+uuid.toString()+"."+player.getUniqueId().toString()));
      } else {
        votes.put(uuid, "brak");
      }
    }
    return votes;
  }
  
  public void checkPlayer(Player player) {
    if (player.isOnline()) {
      if (AuthMeApi.getInstance().isAuthenticated(player)) {
        if (members.contains(player.getUniqueId())) {
          if (!onlineMembers.contains(player.getUniqueId()))
            onlineMembers.add(player.getUniqueId());
          if (membersToRemove.contains(player.getUniqueId()))
            removeMember(player);
        }
      }
    }
  }
  
  public void loadData() {
    if (dataAccessor.getConfig().isList("Members")) {
      for (Object uuid : dataAccessor.getConfig().getList("Members")) {
        members.add(UUID.fromString(uuid.toString()));
      }
    }
    if (dataAccessor.getConfig().isList("WaitingMembers")) {
      for (Object uuid : dataAccessor.getConfig().getList("WaitingMembers")) {
        waitingMembers.add(UUID.fromString(uuid.toString()));
      }
    }
    if (dataAccessor.getConfig().isList("MembersToRemove")) {
        for (Object uuid : dataAccessor.getConfig().getList("MembersToRemove")) {
        	membersToRemove.add(UUID.fromString(uuid.toString()));
        }
      }
    if (dataAccessor.getConfig().isList("WaitingPlayers")) {
      for (Object uuid : dataAccessor.getConfig().getList("WaitingPlayers")) {
        waitingPlayers.add(UUID.fromString(uuid.toString()));
      }
    }
  }
  
  public void saveData() {
    dataAccessor.getConfig().set("Members", members);
    dataAccessor.getConfig().set("WaitingMembers", waitingMembers);
    dataAccessor.getConfig().set("WaitingPlayers", waitingPlayers);
    /*for (UUID uuid : waitingPlayers.keySet()) {
      dataAccessor.getConfig().set("WaitingPlayers."+uuid.toString(), waitingPlayers.get(uuid));
    }*/
    dataAccessor.saveConfig();
  }
  
  public HashSet<UUID> getOnlineMembers() {
    onlineMembers = new HashSet<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (members.contains(player.getUniqueId()))
        onlineMembers.add(player.getUniqueId());
    }
    return onlineMembers;
  }
  
  public void addMember(Player player) {
    if (player.isOnline()) {
      player.getEnderChest().clear();
      player.getInventory().clear();
      player.setTotalExperience(0);
      player.setExp(0);
      player.setLevel(0);
      player.getInventory().setChestplate(null);;
      player.getInventory().setBoots(null);
      player.getInventory().setLeggings(null);
      player.getInventory().setHelmet(null);
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aWitaj w Valhalli!"));
      Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGracz &e&l"+player.getDisplayName()+"&a zostal dodany do Valhalli!"));
      Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex user "+player.getName()+" group set rigcz");
      Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn "+player.getName());
      members.add(player.getUniqueId());
      if (waitingPlayers.contains(player.getUniqueId()))
        waitingPlayers.remove(player.getUniqueId());
    } else {
      waitingPlayers.add(player.getUniqueId());
    }
    saveData();
  }
  
  public void removeMember(Player player) {
    if (player.isOnline()) {
      player.getEnderChest().clear();
      player.getInventory().clear();
      player.setTotalExperience(0);
      player.setExp(0);
      player.setLevel(0);
      player.getInventory().setChestplate(null);;
      player.getInventory().setBoots(null);
      player.getInventory().setLeggings(null);
      player.getInventory().setHelmet(null);
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cZostales wyrzucony z Valhalli!"));
      Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cGracz &e&l"+player.getDisplayName()+"&c zostal wyrzucony z Valhalli!"));
      Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex user "+player.getName()+" group set gracz");
      Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn "+player.getName());
      members.remove(player.getUniqueId());
      if (membersToRemove.contains(player.getUniqueId()))
        membersToRemove.remove(player.getUniqueId());
    } else {
      members.remove(player.getUniqueId());
      membersToRemove.add(player.getUniqueId());
      if (dataAccessor.getConfig().isConfigurationSection("votes."+player.getUniqueId())) {
        dataAccessor.getConfig().getConfigurationSection("votes."+player.getUniqueId()).getKeys(true).clear();
      }
    }
    saveData();
  }
}
