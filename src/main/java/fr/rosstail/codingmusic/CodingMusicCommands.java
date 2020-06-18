package fr.rosstail.codingmusic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CodingMusicCommands implements CommandExecutor {
    private final CodingMusic plugin;
    private final GetSet getSet;
    private final TracksEdit tracksEdit;

    /**
     * Constructor
     * @param plugin
     */
    CodingMusicCommands(CodingMusic plugin) {
        this.plugin = plugin;
        this.getSet = new GetSet(plugin);
        this.tracksEdit = new TracksEdit(plugin);
    }

    /**
     * When /music command is launched
     * @param commandSender
     * @param command
     * @param s
     * @param strings
     * @return
     */
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings != null) {
            if(strings.length >= 1) {
                if (strings[0].equalsIgnoreCase("add")) {
                    tracksEdit.addTrack(commandSender, command, s, strings);
                } else if (strings[0].equalsIgnoreCase("delete") || strings[0].equalsIgnoreCase("del")) {
                    tracksEdit.delTrack(commandSender, command, s, strings);
                }
            }
        }
            return true;
    }

}
