package dev.appeazethecheese.spartanbridge;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.WebhookUtil;
import me.vagdedes.spartan.api.PlayerViolationEvent;
import me.vagdedes.spartan.system.Enums;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventHandler implements Listener {
    private final String LogChannelID;
    private final String StaffRoleID;

    private final int GreenCode = Color.green.getRGB();
    private final int YellowCode = Color.yellow.getRGB();
    private final int OrangeCode = Color.ORANGE.getRGB();
    private final int RedCode = Color.red.getRGB();

    private final Map<UUID, ArrayList<Enums.HackType>> PingsByPlayer = new HashMap<>();

    public EventHandler(String channelId, String roleId){
        LogChannelID = channelId;
        StaffRoleID = roleId;
    }

    @org.bukkit.event.EventHandler
    public void ViolationEvent(PlayerViolationEvent args){
        if(LogChannelID == null)
            return;
        if(args.isFalsePositive() || args.isCancelled())
            return;

        Player player = args.getPlayer();
        Enums.HackType hack = args.getHackType();
        String message = args.getMessage();
        int violation = args.getViolation();

        TextChannel LogChannel = DiscordUtil.getTextChannelById(LogChannelID);
        if(LogChannel == null)
            return;

        Role role = DiscordUtil.getRole(StaffRoleID);
        boolean pingStaff = role != null && ShouldPingStaff(player.getUniqueId(), hack, violation);
        Location pos = player.getLocation();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(player.getName());
        builder.setThumbnail(GetIconUrl(player));
        builder.setColor(GetColor(violation));
        builder.addField("Hack", hack.toString(), false);
        builder.addField("Ping", String.valueOf(player.getPing()), false);
        builder.addField("Location", "X:" + pos.getX() + " Y:" + pos.getY() + " Z:" + pos.getZ() + "\n" +
                "Pitch:" + pos.getPitch() + " Yaw:" + pos.getYaw(), false);
        builder.addField("Status", GetStatus(violation),false);
        builder.addField("Message", message, false);
        MessageEmbed embed = builder.build();

        WebhookUtil.deliverMessage(LogChannel, player, pingStaff ? "<@&" + role.getId() + ">" : "", embed);
    }

    private String GetIconUrl(Player player){
        UUID id = player.getUniqueId();
        String idStr = id.toString().replace("-", "");
        return "https://crafatar.com/avatars/" + idStr + ".png";
    }

    private int GetColor(int violations){
        if(violations < 4)
            return GreenCode;
        if(violations < 8)
            return YellowCode;
        if(violations < 14)
            return OrangeCode;
        return RedCode;
    }

    private String GetStatus(int violations){
        if(violations < 4)
            return "Negligible";
        if(violations < 8)
            return "Abnormal";
        if(violations < 14)
            return "Potential";
        return "Certain";
    }

    private Boolean ShouldPingStaff(UUID playerId, Enums.HackType type, int violations){
        if(violations < 8)
            return false;

        if(!PingsByPlayer.containsKey(playerId))
            PingsByPlayer.put(playerId, new ArrayList<>());
        if(!PingsByPlayer.get(playerId).contains(type))
        {
            PingsByPlayer.get(playerId).add(type);
            new BukkitRunnable(){
                @Override
                public void run() {
                    PingsByPlayer.get(playerId).remove(type);
                }
            }.runTaskLater(Main.Instance, 1000*60*60); // Runs after an hour
            return true;
        }
        return false;
    }
}
