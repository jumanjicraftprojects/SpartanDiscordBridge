package dev.appeazethecheese.spartanbridge;

import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static JavaPlugin Instance;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        String logChannelId = getConfig().getString("LogChannelID");
        String staffRoleId = getConfig().getString("StaffRoleID");

        boolean willPingStaff = true;
        if(logChannelId == null){
            getLogger().severe("You must specify a channel to log to. Please do so and reload the plugin.");
            return;
        }

        var channel = DiscordUtil.getTextChannelById(logChannelId);
        if(channel == null){
            getLogger().severe("There is no text channel with the provided ID. Please update the ID and reload the plugin.");
            return;
        }

        if(!channel.canTalk()){
            getLogger().severe("I don't have permission to talk in the specified channel. Please fix this and reload the plugin.");
            return;
        }

        if(staffRoleId == null){
            getLogger().warning("The staff role is not set. Staff will not be pinged.");
            willPingStaff = false;
        }
        else{
            var role = DiscordUtil.getRole(staffRoleId);
            if(role == null){
                getLogger().warning("The provided staff role ID does not exist. Staff will not be pinged.");
                willPingStaff = false;
            }
            else if(!role.isMentionable()){
                getLogger().warning("I don't have permission to mention the provided staff role. Staff will not be pinged.");
                willPingStaff = false;
            }
        }

        Instance = this;
        getServer().getPluginManager().registerEvents(new EventHandler(logChannelId, staffRoleId), this);

        DiscordUtil.sendMessage(channel, "Spartan Discord Bridge successfully started. Staff " + (willPingStaff ? "will" : "will not") + " be pinged.");
    }

    @Override
    public void onDisable() {
        String logChannelId = getConfig().getString("LogChannelID");

        if(logChannelId != null){
            var channel = DiscordUtil.getTextChannelById(logChannelId);
            if(channel != null && channel.canTalk()){
                DiscordUtil.sendMessage(channel, "Spartan Discord Bridge disabled.");
            }
        }
    }
}
