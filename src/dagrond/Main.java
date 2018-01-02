package dagrond;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import dagrond.Commands.ValhallaCommand;
import dagrond.Utils.ConfigAccessor;


public final class Main extends JavaPlugin {
  
  private ConfigAccessor dataAccessor = new ConfigAccessor(this, "data.yml");
  
  public void onEnable() {
    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aLadowanie pluginu..."));
    getCommand("Valhalla").setExecutor(new ValhallaCommand(this, dataAccessor));
    dataAccessor.saveDefaultConfig();
    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPlugin pomyslnie wlaczony!"));
  }
  
  public void onDisable() {
    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cWylaczanie pluginu..."));
    dataAccessor.saveConfig();
  }
}
