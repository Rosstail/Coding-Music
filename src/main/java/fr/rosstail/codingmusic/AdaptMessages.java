package fr.rosstail.codingmusic;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AdaptMessages {

    public void message(CommandSender commandSender, String message, String string) {

        if (message != null) {
            if (string != null) {
                message = message.replaceAll("<TITLE>", string);
            }
            message = ChatColor.translateAlternateColorCodes('&', message);

            commandSender.sendMessage(message);
        }
    }
}
