package fr.rosstail.codingmusic;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    private final CodingMusic plugin;
    private final long updateDelay;
    private final GetSet getSet;

    public PlayerMove(CodingMusic plugin) {
        this.plugin = plugin;
        this.updateDelay = this.plugin.getConfig().getLong("mysql.delay-between-request") * 1000;
        this.getSet = new GetSet(plugin);
    }

    long buffer = 0;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        long timestamp = System.currentTimeMillis();

        int posX = (int) player.getLocation().getX();
        int posY = (int) player.getLocation().getY();
        int posZ = (int) player.getLocation().getZ();
        String pLoc = posX + " " + posY + " " + posZ;


        if (timestamp > buffer) {
            if (!pLoc.equals(getSet.getPlayerLocation(player))) {
                player.sendMessage(pLoc);
                getSet.setPlayerLocation(player, pLoc);
                buffer = timestamp + updateDelay;
            } else {
                player.sendMessage("Tu n'as pas changé de bloc");
            }
        } else {
            player.sendMessage("cooldown");
        }

    }
}
