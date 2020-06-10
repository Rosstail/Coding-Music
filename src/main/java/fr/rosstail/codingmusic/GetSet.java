package fr.rosstail.codingmusic;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class GetSet {
    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;
    private final CodingMusic plugin;

    GetSet(CodingMusic codingMusic) {
        this.plugin = codingMusic;
        this.host = codingMusic.getConfig().getString("mysql.host");
        this.database = codingMusic.getConfig().getString("mysql.database");
        this.username = codingMusic.getConfig().getString("mysql.username");
        this.password = codingMusic.getConfig().getString("mysql.password");
        this.port = codingMusic.getConfig().getInt("mysql.port");
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean ifPlayerExistsInDTB(Player player) {
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                Statement statement = plugin.connection.createStatement();
                String UUID = String.valueOf(player.getUniqueId());
                ResultSet result = statement.executeQuery("SELECT UUID FROM CODINGMUSIC_Ig_users WHERE UUID = '" + UUID + "';");
                if (result.next()) {
                    return true;
                }
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        String nickName = player.getName();
        String playerLoc = (int) player.getLocation().getX() + " " + (int) player.getLocation().getY() + " " + (int) player.getLocation().getZ();
        if (!ifPlayerExistsInDTB(player)) {
            try {
                if (plugin.connection != null && !plugin.connection.isClosed()) {
                    PreparedStatement preparedStatement = plugin.connection.prepareStatement("INSERT INTO CODINGMUSIC_Ig_Users (UUID, NickName, Location, Is_Online)\n" +
                            "VALUES (?, ?, ?, ?);");

                    preparedStatement.setString(1, uuid);
                    preparedStatement.setString(2, nickName);
                    preparedStatement.setString(3, playerLoc);
                    preparedStatement.setBoolean(4, true);

                    preparedStatement.execute();
                    preparedStatement.close();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayerOnlineStatus(Player player) {
        String uuid = player.getUniqueId().toString();
        Boolean isOnline = player.isOnline();
        String query = "UPDATE CODINGMUSIC_Ig_Users SET Is_Online = ? WHERE UUID = ?;";
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                PreparedStatement preparedStatement = plugin.connection.prepareStatement(query);

                preparedStatement.setBoolean(1, isOnline);
                preparedStatement.setString(2, uuid);

                preparedStatement.executeUpdate();
                preparedStatement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerOfflineStatus(Player player) {
        String uuid = player.getUniqueId().toString();
        String query = "UPDATE CODINGMUSIC_Ig_Users SET Is_Online = ? WHERE UUID = ?;";
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                PreparedStatement preparedStatement = plugin.connection.prepareStatement(query);

                preparedStatement.setBoolean(1, false);
                preparedStatement.setString(2, uuid);

                preparedStatement.executeUpdate();
                preparedStatement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerLocation(Player player) {
        String uuid = player.getUniqueId().toString();
        String query = "SELECT Location FROM CODINGMUSIC_Ig_Users WHERE UUID = '" + uuid + "';";
        String location = null;
        try {
                if (plugin.connection != null && !plugin.connection.isClosed()) {
                    Statement statement = plugin.connection.createStatement();
                    ResultSet result = statement.executeQuery(query);
                    while (result.next()) {
                        location = result.getString("Location");
                    }
                    statement.close();
                    System.out.println(location);
                    return location;
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPlayerLocation(Player player, String loc) {
        String uuid = player.getUniqueId().toString();
        String query = "UPDATE CODINGMUSIC_Ig_Users SET Location = ? WHERE UUID = ?;";
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                PreparedStatement preparedStatement = plugin.connection.prepareStatement(query);

                preparedStatement.setString(1, loc);
                preparedStatement.setString(2, uuid);

                preparedStatement.executeUpdate();
                preparedStatement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
