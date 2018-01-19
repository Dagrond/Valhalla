package dagrond.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dagrond.Main;
import dagrond.Utils.DataManager;


public class ValhallaCommand implements CommandExecutor {
  Main plugin;
  private DataManager Data;
  
  public ValhallaCommand (Main instance, DataManager Data) {
    plugin = instance;
    this.Data = Data;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("Valhalla") || cmd.getName().equalsIgnoreCase("vh")) {
        if (args.length < 1) {
          sender.sendMessage(ChatColor.RED+"Wpisz /vh pomoc aby otrzymac pomoc.");
          return true;
        }
        if (args[0].equalsIgnoreCase("dolacz") || args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("apply")) {
          if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!Data.isMember(player)) {
              if (!player.isOp()) {
                if (!Data.isWaiting(player)) {
                  String cause = "";
                  for (int i = 2; i>=args.length-1; i++) {
                    cause += args[i];
                  }
                  Data.addWaitingPlayer(player, cause);
                  sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie dolaczono do listy oczekujacych graczy!"));
                  sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aAby sprawdzic swoj status wpisz &e/vh status"));
                  Data.broadcastToMembers(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aGracz &e"+player.getDisplayName()+"&a chce dolaczyc do Valhalli!"));
                  Data.broadcastToMembers(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aWpisz &e/vh status "+player.getDisplayName()+"&a aby zobaczyc jego status"));
                  Data.broadcastToMembers(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aWpisz &e/vh tak "+player.getDisplayName()+"&a lub &e/vh nie "+player.getDisplayName()+"&a aby zaglosowac!"));
                  return true;
                } else {
                  sender.sendMessage(ChatColor.RED+"Jestes juz na liscie oczekujacych! Aby sprawdzic swoj status, wpisz /vh status");
                  return true;
                }
              } else {
                sender.sendMessage(ChatColor.RED+"MODE NIE MOZE BYC W VALHALLI REEEEEEEEEEEEEEEEE!");
                return true;
              }
            } else {
              sender.sendMessage(ChatColor.RED+"Jestes juz czlonkiem Valhalli!");
              return true;
            }
          } else {
            sender.sendMessage(ChatColor.RED+"Ta komende moze wywolac tylko gracz!");
            return true;
          }
        } else if (args[0].equalsIgnoreCase("forceadd")) {
          if (sender.isOp()) {
            if (args.length > 1) {
              Player player = Bukkit.getPlayer(args[1]);
              if (!Data.isWaiting(player)) {
                if (!Data.isMember(player)) {
                  if (player != null) {
                    Data.addMember(player);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie usunieto gracza &e&l"+args[1]+" &az Valhalli!"));
                    return true;
                  } else {
                    sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+ChatColor.RED+" nie jest online! Zostanie usuniety z Valhalli automatycznie po wejsciu na serwer.");
                    Data.addToRemove(player);
                    return true;
                  }
                } else {
                  sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+" jest juz czlonkiem Valhalli!");
                  return true;
                }
              } else {
                sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+" oczekuje juz na dodanie! Aby usunac go z listy oczekujacych wpisz /vh pardon (gracz)");
                return true;
              }
            } else {
              sender.sendMessage(ChatColor.RED+"Uzycie: /vh forceadd (gracz)");
              return true;
            }
          } else {
            sender.sendMessage(ChatColor.RED+"Nie posiadasz odpowiednich uprawnien aby uzyc tej komendy");
            return true;
          }
        } else if (args[0].equalsIgnoreCase("forceremove")) {
          if (sender.isOp()) {
            if (args.length > 1) {
              Player player = Bukkit.getPlayer(args[1]);
              if (!Data.isWaitingForRemove(player)) {
                if (Data.isMember(player)) {
                  if (player != null) {
                    Data.removeMember(player);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie usunieto gracza &e&l"+args[1]+" &az Valhalli!"));
                    return true;
                  } else {
                    sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+ChatColor.RED+" nie jest online! Zostanie usuniety z Valhalli automatycznie po wejsciu na serwer.");
                    Data.addToRemove(player);
                    return true;
                  }
                } else {
                  sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+" nie jest czlonkiem Valhalli!");
                  return true;
                }
              } else {
                sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+" oczekuje juz na usuniecie! Aby usunac go z listy oczekujacych wpisz /vh pardon (gracz)");
                return true;
              }
            } else {
              sender.sendMessage(ChatColor.RED+"Uzycie: /vh forceremove (gracz)");
              return true;
            }
          } else {
            sender.sendMessage(ChatColor.RED+"Nie posiadasz odpowiednich uprawnien aby uzyc tej komendy");
            return true;
          }
        } else if (args[0].equalsIgnoreCase("pardon")) {
          if (sender.isOp()) {
            if (args.length>1) {
              Player player = Bukkit.getPlayer(args[1]);
              if (Data.isWaitingForRemove(player)) {
                Data.removeFromMembersToRemove(player);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie usunieto gracza &e&l"+args[1]+" &az listy oczekujacych do usuniecia z Valhalli!"));
                return true;
              } else if (Data.isWaiting(player)) {
                Data.removeWaitingMember(player);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie usunieto gracza &e&l"+args[1]+" &az listy oczekujacych do dodania do Valhalli!"));
                return true;
              }  else {
                sender.sendMessage(ChatColor.RED+"Gracz "+ChatColor.DARK_RED+args[1]+" nie jest na liscie oczekujacych!");
                return true;
              }
            } else {
              sender.sendMessage(ChatColor.RED+"Uzycie: /vh pardon (gracz)");
              return true;
            }
          } else {
            sender.sendMessage(ChatColor.RED+"Nie posiadasz odpowiednich uprawnien aby uzyc tej komendy");
            return true;
          }
        } else if (args[0].equalsIgnoreCase("tak") || args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("yes")) {
          //todo
        } else if (args[0].equalsIgnoreCase("nie") || args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("no")) {
          //todo
        } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("pomoc")) {
          if (sender.hasPermission("valhalla.member")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomoc:"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh tak (gracz) - &6glosujesz ZA przyjeciem gracza do Valhalli."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh nie (gracz) - &6glosujesz PRZECIW przyjeciu gracza do Valhalli"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh status (gracz) - &6informacje na temat gracza"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh oczekujacy - &6Lista oczekujacych na zbawienie"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh lista - &6lista czlonkow Valhalli"));
            return true;
          } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomoc:"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh dolacz - &6Wysyla prosbe o dolaczenie do Valhalli"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh status - &6lWyswietla Twoj status"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh lista - &6lista czlonkow Valhalli"));
            return true;
          }
        } else if (args[0].equalsIgnoreCase("lista") || args[0].equalsIgnoreCase("list")) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aLista czlonkow Valhalli:"));
          String list = "";
          for (UUID uuid : Data.getAllMembers()) {
            list += Bukkit.getOfflinePlayer(uuid).getName()+", ";
          }
          sender.sendMessage(ChatColor.DARK_AQUA+list);
          return true;
        } else {
          sender.sendMessage(ChatColor.RED+"Podany argument nie istnieje! Wpisz /vh pomoc");
          return true;
        }
    }
    return true;
  }
}
