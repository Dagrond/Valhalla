package Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dagrond.Main;
import dagrond.Utils.DataManager;
import fr.xephi.authme.api.v3.AuthMeApi;

public class onJoin implements Listener {
  
  @SuppressWarnings("unused")
  private Main plugin;
  private DataManager Data;
  
  public onJoin(Main plugin, DataManager Data) {
    this.plugin = plugin;
    this.Data = Data;
  }
  
  @EventHandler
  public void onLogin(final PlayerJoinEvent e) {
    Player player = e.getPlayer();
    while(player.isOnline()) {
      if (AuthMeApi.getInstance().isAuthenticated(player)) {
        if (Data.isWaiting(player)) {
          Data.addMember(player);
        } else if (Data.isWaitingForRemove(player)) {
          Data.removeMember(player);
        } else {
         return; 
        }
      }
    }
  }

}
