package dagrond.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import dagrond.Main;

public class DataManager {
  /*                Zapisywanie glosowan w data.yml:
   * votes:
   *  (uuid gracza ktory sie stara dostac do valhalli):
   *    (uuid czlonka valhalli): (dane: tak/nie)
   *    (nastepny czlonek valhalli): (kolejne dane)
   *  (nastepne uuid gracza ktory sie stara dostac do valhalli):
   *    (uuid czlonka valhalli): (tak/nie)  
   */
  protected Main plugin;
  protected ConfigAccessor dataAccessor;
  protected HashSet<UUID> onlineMembers = new HashSet<>(); //gracze, ktorzy sa czlonkami valhalli i sa online
  protected HashSet<UUID> members = new HashSet<>(); //gracze, ktorzy sa w Valhalli
  protected HashMap<UUID, String> waitingPlayers = new HashMap<>(); //gracze, ktorzy zaaplikowali ale nie dostali jeszcze odpowiedniej ilosci glosow (UUID gracza: jego opis)
  protected HashSet<UUID> waitingMembers = new HashSet<>(); //gracze, ktorzy juz dostali sie do valhalli ale serwer oczekuje z ich dodaniem do czasu, az beda online
  protected HashSet<UUID> membersToRemove = new HashSet<>(); //gracze, ktorzy zostana usunieci po wejsciu
  
  public DataManager(Main plugin) {
    this.plugin = plugin;
    dataAccessor = new ConfigAccessor(plugin, "data.yml");
    dataAccessor.saveDefaultConfig();
    loadData();
  }
  
  public String checkPlayerVotes(Player player) {
    String votes = "";
    int required = 0;
    int yes = 0;
    int no = 0;
    for (UUID uuid : members) {
      if (System.currentTimeMillis()-Bukkit.getOfflinePlayer(uuid).getLastPlayed() <= 172800000) { //if player played in last 2 days
        if (dataAccessor.getConfig().getString("votes."+player.getUniqueId().toString()+"."+uuid).equals("tak")) {
          yes++;
        } else if (dataAccessor.getConfig().getString("votes."+player.getUniqueId().toString()+"."+uuid).equals("nie")) {
          no++;
        } else {
          no++;
        }
        required++;
      }
    }
    votes = required+", "+yes+", "+no;
    return votes;
  }
  
  public void addWaitingPlayer(Player player, String cause) {
    waitingPlayers.put(player.getUniqueId(), cause);
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
  
  public boolean isWaitingForRemove(Player player) {
    if (membersToRemove.contains(player.getUniqueId()))
      return true;
    else
      return false;
  }
  
  public void removeFromMembersToRemove(Player player) { //WTF is that name
    membersToRemove.remove(player.getUniqueId());
  }
  
  public void removeWaitingMember(Player player) { //WTF is that name too
    waitingMembers.remove(player.getUniqueId());
  }
  
  public boolean getMemberVote (Player voter, OfflinePlayer player) {
    if (dataAccessor.getConfig().isConfigurationSection("votes."+player.getUniqueId().toString()+"."+voter.getUniqueId().toString())) {
      if (dataAccessor.getConfig().getString("votes."+player.getUniqueId().toString()+"."+voter.getUniqueId().toString()).equalsIgnoreCase("tak")) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  public void addMemberVote (Player voter, OfflinePlayer player, String vote) {
    dataAccessor.getConfig().set("votes."+player.getUniqueId().toString()+"."+voter.getUniqueId().toString(), vote);
  }
  
  public void addToRemove(Player player) {
    membersToRemove.add(player.getUniqueId());
  }
  
  public HashSet<UUID> getBannedMembers() {
	    return membersToRemove;
	  }
  
  public HashMap<UUID, String> getWaitingPlayers() {
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
    if (dataAccessor.getConfig().isConfigurationSection("WaitingPlayers")) {
      for (String uuid : dataAccessor.getConfig().getConfigurationSection("WaitingPlayers").getKeys(false)) {
          waitingPlayers.put(UUID.fromString(uuid), dataAccessor.getConfig().getString(uuid));
      }
    }
  }
  
  public void saveData() {
    dataAccessor.getConfig().set("Members", members);
    dataAccessor.getConfig().set("WaitingMembers", waitingMembers);
    dataAccessor.getConfig().set("membersToRemove", membersToRemove);
    for (UUID uuid : waitingPlayers.keySet()) {
      dataAccessor.getConfig().set(uuid.toString(), waitingPlayers.get(uuid));
    }
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
      if (waitingPlayers.containsKey(player.getUniqueId()))
        waitingPlayers.remove(player.getUniqueId());
      if (waitingMembers.contains(player.getUniqueId()))
        waitingMembers.remove(player.getUniqueId());
      if (dataAccessor.getConfig().isConfigurationSection("votes."+player.getUniqueId())) {
        dataAccessor.getConfig().getConfigurationSection("votes."+player.getUniqueId()).getKeys(true).clear();
      }
    } else {
      waitingMembers.add(player.getUniqueId());
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
