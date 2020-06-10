package fr.rosstail.codingmusic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CodingMusicCommands implements CommandExecutor {
    private final CodingMusic plugin;
    private final GetSet getSet;

    /**
     * Constructor
     * @param plugin
     */
    CodingMusicCommands(CodingMusic plugin) {
        this.plugin = plugin;
        this.getSet = new GetSet(plugin);
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
        commandSender.sendMessage("tu as envoyé une commande basée sur /music.");
        if (strings != null) {
            if (strings[0].equalsIgnoreCase("add")) {
                commandSender.sendMessage("Ajout de musique");

                if (strings.length == 8){
                    try {
                        Integer.parseInt(strings[2]);
                        Integer.parseInt(strings[3]);
                        Integer.parseInt(strings[4]);
                        Integer.parseInt(strings[5]);
                        Integer.parseInt(strings[6]);
                        strings[3] = strings[3] + " " + strings[4] + " " + strings[5] + " " + strings[6];
                        strings[4] = strings[7];
                        getSet.setTrack(strings);
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage("SOURCE, X, Y, Z et RAYON doivent être un nombre !");
                    }
                } else if (commandSender instanceof Player) {
                    Player player = ((Player) commandSender).getPlayer();
                    if (strings.length == 5) {
                        try {
                            Integer.parseInt(strings[2]);
                            Integer.parseInt(strings[3]);
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage("SOURCE et RAYON doivent être un nombre !");
                            return false;
                        }
                        strings[3] = (int) player.getLocation().getX() + " " + (int) player.getLocation().getY() + " " + (int) player.getLocation().getZ() + " " + strings[3];
                        getSet.setTrack(strings);
                    }
                }
            }
        }
            return true;
    }

}
