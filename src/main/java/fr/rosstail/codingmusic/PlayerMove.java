package fr.rosstail.codingmusic;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    private final CodingMusic plugin;
    private final long updateDelay;
    private final GetSet getSet;

    /**
     * Constructor
     * @param plugin
     */
    public PlayerMove(CodingMusic plugin) {
        this.plugin = plugin;
        this.updateDelay = this.plugin.getConfig().getLong("mysql.delay-between-request") * 1000;
        this.getSet = new GetSet(plugin);
    }

    long buffer = 0;

    /**
     * Actions when player move/rotate
     * @param event
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        long timestamp = System.currentTimeMillis();

        int posX = (int) player.getLocation().getX();
        int posY = (int) player.getLocation().getY();
        int posZ = (int) player.getLocation().getZ();
        String pLoc = posX + " " + posY + " " + posZ;


        if (timestamp > buffer) {
            buffer = timestamp + updateDelay;
            if (!pLoc.equals(getSet.getPlayerLocation(player))) {
                getSet.setPlayerLocation(player, pLoc);
            }
        }
    }
}
