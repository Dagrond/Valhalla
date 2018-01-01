package dagrond;

import org.bukkit.plugin.java.JavaPlugin;

import dagrond.Commands.ValhallaCommand;


public final class Main extends JavaPlugin {
  
  public void onEnable() {
    getCommand("Valhalla").setExecutor(new ValhallaCommand(this));
  }
  
  public void onDisable() {}
}
