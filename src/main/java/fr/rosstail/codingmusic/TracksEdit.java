package fr.rosstail.codingmusic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TracksEdit {
    private final CodingMusic plugin;
    private final GetSet getSet;

    TracksEdit(CodingMusic plugin) {
        this.plugin = plugin;
        this.getSet = new GetSet(plugin);
    }

    public void addTrack(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            Integer.parseInt(strings[2]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage("SOURCE doit être un nombre !");
            return;
        }

        if (strings.length == 5) {
            try {
                Integer.parseInt(strings[3]);
                if (commandSender instanceof Player) {
                    Player player = ((Player) commandSender).getPlayer();
                    strings[3] = (int) player.getLocation().getX() + " " + (int) player.getLocation().getY() + " " + (int) player.getLocation().getZ() + " " + strings[3];
                    commandSender.sendMessage("Musique ajoutée dans une sphère autour de vous.");
                }
            } catch (NumberFormatException e) {
                strings[3] = strings[3].toLowerCase();
                commandSender.sendMessage("Musique ajoutée à la région.");
            }
            getSet.setTrack(strings);
        } else if (strings.length == 8 || strings.length == 10) {
            try {
                for (int i = 4; i < strings.length - 1; i++) {
                    Integer.parseInt(strings[i]);
                    strings[3] = strings[3] + " " + strings[i];
                }
                strings[4] = strings[strings.length - 1];
                getSet.setTrack(strings);
                if (strings.length == 8) {
                    commandSender.sendMessage("Musique ajoutée dans une sphère");
                } else {
                    commandSender.sendMessage("Musique ajoutée dans la zone donnée");
                }
            } catch (NumberFormatException e) {
                commandSender.sendMessage("SOURCE, X, Y, Z et RAYON doivent être un nombre !");
            }
        }
    }

    public void delTrack(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length >= 2) {
            for (int i = 1; i <= strings.length - 1; i++) {
                getSet.deleteTrack(strings[i]);
                commandSender.sendMessage("Musique " + strings[i] + " supprimmée avec succès.");
            }
        }
    }
}
