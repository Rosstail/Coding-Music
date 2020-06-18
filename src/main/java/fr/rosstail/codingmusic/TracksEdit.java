package fr.rosstail.codingmusic;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class TracksEdit {
    private final CodingMusic plugin;
    private final AdaptMessages adaptMessages;
    private final GetSet getSet;
    private final File langFile;
    private final YamlConfiguration configLang;
    String message;

    TracksEdit(CodingMusic plugin) {
        this.plugin = plugin;
        this.langFile = new File(plugin.getDataFolder(),
                "lang/" + plugin.getConfig().getString("general.lang") + ".yml");
        this.configLang = YamlConfiguration.loadConfiguration(langFile);
        this.getSet = new GetSet(plugin);
        this.adaptMessages = new AdaptMessages();
    }

    public void addTrack(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length >= 2) {
            try {
                Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                message = configLang.getString("source-error-int");
                adaptMessages.message(commandSender, message, null);
                return;
            }

            if (strings.length == 5) {
                try {
                    Integer.parseInt(strings[3]);
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        strings[3] = (int) player.getLocation().getX() + " " + (int) player.getLocation().getY() + " " + (int) player.getLocation().getZ() + " " + strings[3];
                        message = configLang.getString("new-music-spheric-player");
                        adaptMessages.message(commandSender, message, null);
                    }
                } catch (NumberFormatException e) {
                    strings[3] = strings[3].toLowerCase();
                    message = configLang.getString("new-music-region");
                    adaptMessages.message(commandSender, message, null);
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
                        message = configLang.getString("new-music-spheric");
                        adaptMessages.message(commandSender, message, null);
                    } else {
                        message = configLang.getString("new-music-area");
                        adaptMessages.message(commandSender, message, null);
                    }
                    commandSender.sendMessage(message);
                } catch (NumberFormatException e) {
                    message = configLang.getString("add-music-spheric-number-error");
                    adaptMessages.message(commandSender, message, null);
                }
            }
        } else {
            message = configLang.getString("add-music-syntax");
            commandSender.sendMessage(message);
        }
    }

    public void delTrack(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length >= 2) {
            for (int i = 1; i <= strings.length - 1; i++) {
                getSet.deleteTrack(strings[i]);
                message = configLang.getString("deleted-music");
                adaptMessages.message(commandSender, message, strings[i]);
            }
        } else {
            message = configLang.getString("delete-music-syntax");
            adaptMessages.message(commandSender, message, null);
        }
    }
}
