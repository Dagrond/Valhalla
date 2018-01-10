package Events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dagrond.Main;
import dagrond.Utils.DataManager;

public class onJoin implements Listener {
  
  private Main plugin;
  private DataManager Data;
  
  public onJoin(Main plugin, DataManager Data) {
    this.plugin = plugin;
    this.Data = Data;
  }
  
  @EventHandler
  public void onLogin(final PlayerJoinEvent e) {
    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
      @Override
      public void run() {
        Data.checkPlayer(e.getPlayer());
      }
    }, 600);
  }

}
