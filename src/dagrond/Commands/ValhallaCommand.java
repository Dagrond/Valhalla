package dagrond.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dagrond.Main;
import dagrond.Utils.ConfigAccessor;


public class ValhallaCommand implements CommandExecutor {
  Main plugin;
  private Boolean votingInProgress = false;
  private HashMap<Player, Boolean> onlineMembers = new HashMap<>();
  private ConfigAccessor dataAccessor;
  private List<String> members = new ArrayList<>();
  
  @SuppressWarnings("unchecked")
  public ValhallaCommand (Main instance, ConfigAccessor dataAccessor) {
    plugin = instance;
    this.dataAccessor = dataAccessor;
    if (this.dataAccessor.getConfig().isList("valhallamembers"))
      this.members = (List<String>) this.dataAccessor.getConfig().getList("valhallamembers");
  }
  
  @SuppressWarnings("deprecation")
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("Valhalla") || cmd.getName().equalsIgnoreCase("vh")) {
      if (sender.hasPermission("valhalla.cmd")) {
        if (args.length < 1) {
          sender.sendMessage(ChatColor.RED+"Wpisz /vh help aby otrzymac pomoc.");
          return true;
        }
        if (args[0].equalsIgnoreCase("dodaj")) {
          if (args.length > 1) {
            if (Bukkit.getPlayer(args[1]) != null) {
              if (!(Bukkit.getPlayer(args[1]).hasPermission("valhalla.bypass"))) {
                if (!votingInProgress) {
                  for (Player player : Bukkit.getOnlinePlayers()) {
                    if (members.contains(player.getUniqueId().toString())) 
                      onlineMembers.put(player, false);
                  }
                  if (onlineMembers.size() >= 5) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGlosowanie nad przyjeciem gracza &e&l"+args[1]+"&a rozpoczelo sie!"));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aAktywni czlonkowie: &e&l"+onlineMembers.size()));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aLiczba potrzebnych glosow do przyjecia: &e&l"+Math.floor(onlineMembers.size()*0.80)));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGlosowanie potrwa dwie minuty. Powodzenia!"));
                    Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aAby zaglosowac za przyjeciem &e&l"+args[1]+" &awpisz /vh tak"), "valhalla.member");
                    Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aAby zaglosowac przeciwko przyjeciu &e&l"+args[1]+" &awpisz /vh nie"), "valhalla.member");
                    votingInProgress = true;
                    Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                      @Override
                      public void run() {
                        votingInProgress = false;
                        onlineMembers = new HashMap<>();
                        int votes = 0;
                        for (boolean vote : onlineMembers.values()) {
                          if (vote)
                            votes++;
                        }
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGlosowanie nad przyjeciem gracza &e&l"+args[1]+"&a zakonczylo sie!"));
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aUzyskane glosy: &e&l"+votes+"/"+onlineMembers.size()+" &a(Potrzebne: &e&l"+Math.floor(onlineMembers.size()*0.80)+"&a)"));
                        if (votes >= Math.round(onlineMembers.size()*0.80)) {
                          Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGracz &e&l"+args[1]+"&a dostal sie do Valhalli!"));
                          Player player = Bukkit.getPlayer(args[1]);
                          if (player != null) {
                            resetPlayer(player, "rigcz");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aWitaj w Valhalli!"));
                            return;
                          } else {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cGracz &4&l"+args[1]+"&c otrzymal wymagana ilosc glosow ale nie jest online!"));
                            return;
                          }
                        } else {
                          Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cGracz &4&l"+args[1]+"&c nie dostal sie do Valhalli!"));
                          return;
                        }
                      }
                    }, 2400);
                    return true;
                  } else {
                    sender.sendMessage(ChatColor.RED+"Aby rozpoczac glosowanie musi byc online conajmniej 5 czlonkow Valhalli!");
                    return true;
                  }
                } else {
                  sender.sendMessage(ChatColor.RED+"Glosowanie jest juz w toku!");
                  return true;
                }
              } else {
                sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+ChatColor.RED+" jest juz zbawiony.");
                return true;
              }
            } else {
              sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+ChatColor.RED+" nie jest online!");
              return true;
            }
          } else {
            sender.sendMessage(ChatColor.RED+"Uzycie: /vh dodaj (gracz)");
            return true;
          }
        } else if (args[0].equalsIgnoreCase("forceadd")) {
          if (!(sender instanceof Player)) {
            if (args.length > 1) {
              Player player = Bukkit.getPlayer(args[1]);
              if (player != null) {
                resetPlayer(player, "rigcz");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aWitaj w Valhalli!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie dodano gracza &e&l"+args[1]+" &ado Valhalli!"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGracz &e&l"+args[1]+"&a zostal dodany do Valhalli!"));
                return true;
              } else {
                sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+ChatColor.RED+" nie jest online!");
                return true;
              }
            } else {
              sender.sendMessage(ChatColor.RED+"Uzycie: /vh forceadd (gracz)");
              return true;
            }
          } else {
            sender.sendMessage(ChatColor.RED+"Ta komende mozna wywolac tylko z konsoli!");
            return true;
          }
        } else if (args[0].equalsIgnoreCase("forceremove")) {
          if (!(sender instanceof Player)) {
            if (args.length > 1) {
              Player player = Bukkit.getPlayer(args[1]);
              if (player != null) {
                resetPlayer(player, "gracz");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cZegnaj!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie usunieto gracza &e&l"+args[1]+" &az Valhalli!"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cGracz &4&l"+args[1]+"&c zostal usuniety z Valhalli!"));
                return true;
              } else {
                sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+ChatColor.RED+" nie jest online!");
                return true;
              }
            } else {
              sender.sendMessage(ChatColor.RED+"Uzycie: /vh forceremove (gracz)");
              return true;
            }
          } else {
            sender.sendMessage(ChatColor.RED+"Ta komende mozna wywolac tylko z konsoli!");
            return true;
          }
        } else if (args[0].equalsIgnoreCase("tak")) {
          if (votingInProgress) {
            onlineMembers.put((Player) sender, true);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie zaglosowales na TAK."));
            return true;
          } else {
            sender.sendMessage(ChatColor.RED+"Aktualnie nie ma zadnego glosowania!");
            return true;
          }
        } else if (args[0].equalsIgnoreCase("nie")) {
          if (votingInProgress) {
            onlineMembers.put((Player) sender, false);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie zaglosowales na NIE."));
            return true;
          } else {
            sender.sendMessage(ChatColor.RED+"Aktualnie nie ma zadnego glosowania!");
            return true;
          }
        } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("pomoc")) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomoc:"));
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh dodaj (gracz) - &6Rozpoczyna glosowanie o dodanie gracza do Valhalli"));
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh tak - &6glosujesz ZA przyjeciem gracza do Valhalli."));
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh nie - &6glosujesz PRZECIW przyjeciu gracza do Valhalli"));
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh lista - &6lista wszystkich czlonkow Valhalli"));
          return true;
        } else if (args[0].equalsIgnoreCase("lista")) {
          sender.sendMessage("Lista: "+members.toString());
          return true;
        } else if (args[0].equalsIgnoreCase("addmember")) {
            if (!(sender instanceof Player)) {
              if (args.length == 2) {
                members.add(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                saveData();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie dodano gracza &e&l"+args[1]+" &ado listy czlonkow Valhalli!"));
                return true;
              } else {
                sender.sendMessage(ChatColor.RED+"Uzycie: /vh addmember (gracz)");
                return false;
              }
            } else {
              sender.sendMessage(ChatColor.RED+"Ta komende mozna wywolac tylko z konsoli!");
              return true;
            }
        } else {
          sender.sendMessage(ChatColor.RED+"Podany argument nie istnieje! Wpisz /vh pomoc");
          return true;
        }
      } else {
      sender.sendMessage(ChatColor.RED+"Nie masz odpowiednich uprawnien!");
      return true;
     }
    }
    return true;
  }
  
  public void resetPlayer(Player player, String ranga) {
    player.getEnderChest().clear();
    player.getInventory().clear();
    player.setTotalExperience(0);
    player.setExp(0);
    player.setLevel(0);
    player.getInventory().setChestplate(null);;
    player.getInventory().setBoots(null);
    player.getInventory().setLeggings(null);
    player.getInventory().setHelmet(null);
    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex user "+player.getDisplayName()+" group set "+ranga);
    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn "+player.getDisplayName());
    if (ranga.equalsIgnoreCase("rigcz"))
      members.add(player.getUniqueId().toString());
    else
      members.remove(player.getUniqueId().toString());
    return;
  }
  
  public void saveData() {
    this.dataAccessor.getConfig().set("valhallamembers", (List<String>)members);
    this.dataAccessor.saveConfig();
  }
}
