package dagrond;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import dagrond.Commands.ValhallaCommand;
import dagrond.Utils.ConfigAccessor;
import dagrond.Utils.DataManager;


public final class Main extends JavaPlugin {
  
  private DataManager Data;
  
  public void onEnable() {
    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aLadowanie pluginu..."));
    this.Data = new DataManager(this);
    getCommand("Valhalla").setExecutor(new ValhallaCommand(this, Data));
    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPlugin pomyslnie wlaczony!"));
  }
  
  public void onDisable() {
    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cWylaczanie pluginu..."));
    Data.saveData();
  }
}
