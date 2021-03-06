package fr.rosstail.codingmusic;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnect implements Listener {

    private final CodingMusic plugin;
    private final GetSet getSet;

    /**
     * constructor
     * @param codingMusic #plugin
     */
    PlayerConnect (CodingMusic codingMusic) {
        this.plugin = codingMusic;
        this.getSet = new GetSet(plugin);
    }

    /**
     * Actions on player connection into the server
     * @param event
     */
    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getSet.createPlayerData(player);
        getSet.setPlayerOnlineStatus(player);
    }

    /**
     * Actions when player disconnect from the server
     * @param event
     */
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        getSet.setPlayerOfflineStatus(player);
    }
}
