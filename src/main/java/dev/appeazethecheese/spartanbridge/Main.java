package dev.appeazethecheese.spartanbridge;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {
    public static JavaPlugin Instance;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        String logChannelId = getConfig().getString("LogChannelID");
        String staffRoleId = getConfig().getString("StaffRoleID");
        if(logChannelId == null){
            getLogger().log(Level.SEVERE, "You must specify a channel to log to. Please do so and reload the plugin.");
            return;
        }
        Instance = this;
        getServer().getPluginManager().registerEvents(new EventHandler(logChannelId, staffRoleId), this);
    }
}
