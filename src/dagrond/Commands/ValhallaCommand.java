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
        } else if (args[0].equalsIgnoreCase("forceadd")) {
          if (!(sender instanceof Player)) {
            if (args.length > 1) {
              Player player = Bukkit.getPlayer(args[1]);
              if (player != null) {
                Data.addMember(player);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie dodano gracza &e&l"+args[1]+" &ado Valhalli!"));
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
                Data.removeMember(player);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomyslnie usunieto gracza &e&l"+args[1]+" &az Valhalli!"));
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
        } else if (args[0].equalsIgnoreCase("tak") || args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("yes")) {
        } else if (args[0].equalsIgnoreCase("nie") || args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("no")) {
        } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("pomoc")) {
          if (sender.hasPermission("valhalla.member")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomoc:"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh tak (gracz) - &6glosujesz ZA przyjeciem gracza do Valhalli."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh nie (gracz) - &6glosujesz PRZECIW przyjeciu gracza do Valhalli"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh status (gracz) - &6informacje na temat gracza"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh oczekujacy - &6Lista oczekujacych na zbawienie"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh lista - &6lista wszystkich czlonkow Valhalli"));
            return true;
          } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPomoc:"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh dolacz - &6Wysyla prosbe o dolaczenie do Valhalli"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh status - &6lWyswietla Twoj status"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5- /vh lista - &6lista wszystkich czlonkow Valhalli"));
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
