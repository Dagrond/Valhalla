package dagrond;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import Events.onJoin;
import dagrond.Commands.ValhallaCommand;
import dagrond.Utils.DataManager;


public final class Main extends JavaPlugin {
  
  private DataManager Data;
  
  public void onEnable() {
    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aLadowanie pluginu..."));
    this.Data = new DataManager(this);
    Bukkit.getServer().getPluginManager().registerEvents(new onJoin(this, Data), this);
    getCommand("Valhalla").setExecutor(new ValhallaCommand(this, Data));
    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &aPlugin pomyslnie wlaczony!"));
  }
  
  public void onDisable() {
    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3&l[&6&lValhalla&3&l] &cWylaczanie pluginu..."));
    Data.saveData();
  }
}
