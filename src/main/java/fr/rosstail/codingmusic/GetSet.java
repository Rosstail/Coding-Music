package fr.rosstail.codingmusic;

import org.bukkit.entity.Player;

import java.sql.*;

public class GetSet {
    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;
    private final CodingMusic plugin;

    /**
     * Constructor
     * @param codingMusic
     */
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

    /**
     * Check if player is already inside CODINGMUSIC_Ig_Users table in databas
     * @param player
     * @return
     */
    public boolean ifPlayerExistsInDTB(Player player) {
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                Statement statement = plugin.connection.createStatement();
                String UUID = String.valueOf(player.getUniqueId());
                ResultSet result = statement.executeQuery("SELECT UUID FROM CODINGMUSIC_Ig_Users WHERE UUID = '" + UUID + "';");
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

    /**
     * Create default values for player in CODINGMUSIC_Ig_Users table in database
     * @param player
     */
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
            createPlayerMusicData(player);
        }
    }

    public void createPlayerMusicData(Player player) {
        String uuid = player.getUniqueId().toString();
        String nickName = player.getName();
        WGPreps wgPreps = new WGPreps();

        String musicList = wgPreps.checkMusicFlagList(player).toString();
        musicList = musicList.replaceAll("\\[", "").replaceAll("]", "");
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                PreparedStatement preparedStatement = plugin.connection.prepareStatement("INSERT INTO CODINGMUSIC_Region_Link (UUID, NickName, Musics)\n" +
                        "VALUES (?, ?, ?);");

                preparedStatement.setString(1, uuid);
                preparedStatement.setString(2, nickName);
                preparedStatement.setString(3, musicList);

                preparedStatement.execute();
                preparedStatement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getplayerLocalRegions(Player player) {
        String[] pLoc = getPlayerLocation(player).split(" ");

        String list = null;
        int pX = Integer.parseInt(pLoc[0]);
        int pY = Integer.parseInt(pLoc[1]);
        int pZ = Integer.parseInt(pLoc[2]);

        String query = "SELECT * FROM CODINGMUSIC_Tracks;";
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                Statement statement = plugin.connection.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    String loc;
                    String[] tempArray;

                    loc = result.getString("Location");
                    tempArray = loc.split(" ");

                    if (tempArray.length == 4) {
                        int locX = Integer.parseInt(tempArray[0]);
                        int locY = Integer.parseInt(tempArray[1]);
                        int locZ = Integer.parseInt(tempArray[2]);
                        int locR = Integer.parseInt(tempArray[3]);
                        if (pX >= locX - locR && pX <= locX + locR) {
                            if (pY >= locY - locR && pY <= locY + locR) {
                                if (pZ >= locZ - locR && pZ <= locZ + locR) {
                                    if (list != null) {
                                        list = list + " " + result.getString("Title");;
                                    } else {
                                        list = result.getString("Title");
                                    }
                                }
                            }
                        }
                    } else if (tempArray.length == 6) {
                        int locXMin = Integer.parseInt(tempArray[0]);
                        int locYMin = Integer.parseInt(tempArray[1]);
                        int locZMin = Integer.parseInt(tempArray[2]);
                        int locXmax = Integer.parseInt(tempArray[3]);
                        int locYMax = Integer.parseInt(tempArray[4]);
                        int locZMax = Integer.parseInt(tempArray[5]);
                        if (pX >= locXMin && pX <= locXmax) {
                            if (pY >= locYMin && pY <= locYMax) {
                                if (pZ >= locZMin && pZ <= locZMax) {
                                    if (list != null) {
                                        list = list + " " + result.getString("Title");;
                                    } else {
                                        list = result.getString("Title");
                                    }
                                }
                            }
                        }
                    }
                }
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void setPlayerMusicList(Player player) {
        String uuid = player.getUniqueId().toString();
        String nickName = player.getName();
        String musicList = getPlayerWGtracks(player);
        musicList = musicList + getplayerLocalRegions(player);
        String query = "UPDATE CODINGMUSIC_Region_Link SET NickName = ?, Musics = ? WHERE UUID = ?;";
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                PreparedStatement preparedStatement = plugin.connection.prepareStatement(query);

                preparedStatement.setString(1, nickName);
                preparedStatement.setString(2, musicList);
                preparedStatement.setString(3, uuid);

                preparedStatement.executeUpdate();
                preparedStatement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerWGtracks(Player player) {
        WGPreps wgPreps = new WGPreps();
        String flag = wgPreps.checkMusicFlagList(player).toString();
        flag = flag.replaceAll("\\[", "").replaceAll("]", "");
        String track = null;

        if (flag.length() > 0) {
            String query = "SELECT * FROM CODINGMUSIC_Tracks WHERE Location LIKE '%" + flag + "%'";
            try {
                if (plugin.connection != null && !plugin.connection.isClosed()) {
                    Statement statement = plugin.connection.createStatement();
                    ResultSet result = statement.executeQuery(query);
                    while (result.next()) {
                        String loc;
                        String[] tempArray;

                        loc = result.getString("Title");
                        tempArray = loc.split(" ");

                        if (tempArray.length == 1) {
                            track = result.getString("Title") + " ";
                        }
                    }
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (track != null) {
                return track;
            }
        }
        return "";
    }

    /**
     * Mark the player as connected in database
     * @param player
     */
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

    /**
     * Set the player offline in the database
     * @param player
     */
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

    /**
     * Get and return the player location in database
     * @param player
     * @return
     */
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
                return location;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update player location into database
     * @param player
     * @param loc
     */
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

    public void setTrack(String[] strings) {
        String query = "INSERT INTO CODINGMUSIC_Tracks (Title, Link, Source, Location)\n" +
                "VALUES (?, ?, ?, ?);";
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                PreparedStatement preparedStatement = plugin.connection.prepareStatement(query);

                preparedStatement.setString(1, strings[1]);
                preparedStatement.setString(2, strings[4]);
                preparedStatement.setInt(3, Integer.parseInt(strings[2]));
                preparedStatement.setString(4, strings[3]);

                preparedStatement.executeUpdate();
                preparedStatement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTrack(String string) {
        String query = "DELETE FROM CODINGMUSIC_Tracks WHERE Title = ?;";
        try {
            if (plugin.connection != null && !plugin.connection.isClosed()) {
                PreparedStatement preparedStatement = plugin.connection.prepareStatement(query);

                preparedStatement.setString(1, string);

                preparedStatement.executeUpdate();
                preparedStatement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
