package dagrond.Commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dagrond.Main;


public class ValhallaCommand implements CommandExecutor {
  Main plugin;
  private Boolean votingInProgress = false;
  private HashMap<Player, Boolean> onlineMembers = new HashMap<>();
  public ValhallaCommand (Main instance) {
    plugin = instance;
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
                for (Player player : Bukkit.getOnlinePlayers()) {
                  if (player.hasPermission("valhalla.member") && !(player.isOp())) 
                    onlineMembers.put(player, false);
                }
                if (onlineMembers.size() >= 5) {
                  Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGlosowanie nad przyjeciem gracza &e&l"+args[1]+"&a rozpoczelo sie!"));
                  Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aAktywni czlonkowie: &e&l"+onlineMembers.size()));
                  Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aLiczba potrzebnych glosow do przyjecia: &e&l"+Math.round(onlineMembers.size()*0.80)));
                  Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGlosowanie potrwa dwie minuty. Powodzenia!"));
                  Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aAby zaglosowac za przyjeciem &e&l"+args[1]+" &awpisz /vh tak"), "valhalla.member");
                  Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aAby zaglosowac przeciwko przyjeciu &e&l"+args[1]+" &awpisz /vh nie"), "valhalla.member");
                  votingInProgress = true;
                  Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                      votingInProgress = false;
                      int votes = 0;
                      for (boolean vote : onlineMembers.values()) {
                        if (vote)
                          votes++;
                      }
                      Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGlosowanie nad przyjeciem gracza &e&l"+args[1]+"&a zakonczylo sie!"));
                      Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aUzyskane glosy: &e&l"+votes+"/"+onlineMembers.size()+" &a(Potrzebne: &e&l"+Math.round(onlineMembers.size()*0.80)+"&a)"));
                      if (votes >= Math.round(onlineMembers.size()*0.80)) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGracz &e&l"+args[1]+"&a dostal sie do Valhalli!"));
                        if (Bukkit.getPlayer(args[1]) != null) {
                          Player newbie = Bukkit.getPlayer(args[1]);
                          newbie.getEnderChest().clear();
                          newbie.getInventory().clear();
                          newbie.setTotalExperience(0);
                          newbie.getInventory().setChestplate(null);;
                          newbie.getInventory().setBoots(null);
                          newbie.getInventory().setLeggings(null);
                          newbie.getInventory().setHelmet(null);
                          Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex user "+args[1]+" group set rigcz");
                          Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn "+args[1]);
                          newbie.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aWitaj w Valhalli!"));
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
              if (Bukkit.getPlayer(args[1]) != null) {
                Player newbie = Bukkit.getPlayer(args[1]);
                newbie.getEnderChest().clear();
                newbie.getInventory().clear();
                newbie.setTotalExperience(0);
                newbie.getInventory().setChestplate(null);;
                newbie.getInventory().setBoots(null);
                newbie.getInventory().setLeggings(null);
                newbie.getInventory().setHelmet(null);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex user "+args[1]+" group set rigcz");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn "+args[1]);
                newbie.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aWitaj w Valhalli!"));
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
              if (Bukkit.getPlayer(args[1]) != null) {
                Player newbie = Bukkit.getPlayer(args[1]);
                newbie.getEnderChest().clear();
                newbie.getInventory().clear();
                newbie.setTotalExperience(0);
                newbie.getInventory().setChestplate(null);;
                newbie.getInventory().setBoots(null);
                newbie.getInventory().setLeggings(null);
                newbie.getInventory().setHelmet(null);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex user "+args[1]+" group set gracz");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn "+args[1]);
                newbie.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cZegnaj!"));
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
        } else if (args[0].equalsIgnoreCase("help")) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomoc:"));
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh dodaj (gracz) - &6Rozpoczyna glosowanie o dodanie gracza do Valhalli"));
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh tak - &6Rozpoczyna glosowanie o dodanie gracza do Valhalli"));
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh nie (gracz) - &6Rozpoczyna glosowanie o dodanie gracza do Valhalli"));
          return true;
        }
      } else {
        sender.sendMessage(ChatColor.RED+"Nie masz odpowiednich uprawnien!");
        return true;
      }
    }
    return true;
  }
}
