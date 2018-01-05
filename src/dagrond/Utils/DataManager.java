package dagrond.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dagrond.Main;

public class DataManager {
  protected Main plugin;
  protected ConfigAccessor dataAccessor;
  protected List<String> onlineMembers = new ArrayList<>();
  protected List<String> members = new ArrayList<>();
  
  public DataManager(Main plugin) {
    this.plugin = plugin;
    dataAccessor = new ConfigAccessor(plugin, "data.yml");
    dataAccessor.saveDefaultConfig();
    loadData();
  }
  
  public List<String> getAllMembers() {
    return members;
  }
  
  @SuppressWarnings("unchecked")
  public void loadData() {
    if (this.dataAccessor.getConfig().isList("valhallamembers"))
      this.members = (List<String>) this.dataAccessor.getConfig().getList("valhallamembers");
  }
  
  public void saveData() {
    this.dataAccessor.getConfig().set("valhallamembers", members);
    dataAccessor.saveConfig();
  }
  
  public List<String> getOnlineMembers() {
    onlineMembers = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (members.contains(player.getUniqueId().toString()))
        onlineMembers.add(player.getUniqueId().toString());
    }
    return onlineMembers;
  }
  
  public void addMember(Player player) {
    
  }
  
  public void removeMember(Player player) {
    
  }
}
