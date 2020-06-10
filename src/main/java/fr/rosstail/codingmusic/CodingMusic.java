package fr.rosstail.codingmusic;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CodingMusic extends JavaPlugin implements Listener {

    public Connection connection;
    public String host, database, username, password;
    public int port;

    /**
     * Actions when plugin is enabled
     */
    @Override
    public void onEnable() {
        System.out.println("PLUGINMUSIC ON");

        this.saveDefaultConfig();
        if (this.getConfig().getBoolean("mysql.active")) {
            prepareConnection();
            GetSet getSet = new GetSet(this);
        }

        createLangFiles();
        Bukkit.getPluginManager().registerEvents(new PlayerConnect(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(this), this);
        this.getCommand("music").setExecutor(new CodingMusicCommands(this));
    }

    /**
     * Prepare init tables for MySQL
     */
    private void prepareConnection() {
        host = this.getConfig().getString("mysql.host");
        database = this.getConfig().getString("mysql.database");
        username = this.getConfig().getString("mysql.username");
        password = this.getConfig().getString("mysql.password");
        port = this.getConfig().getInt("mysql.port");

        try {

            openConnection();
            setTableToDataBase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create database connection if she doesn't exists
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    /**
     * Create tables in database
     */
    public void setTableToDataBase() {
        setPlayersTable();
        setUsersTable();
        setTracksTable();
    }

    /**
     * Create players table for INGAME players
     */
    public void setPlayersTable() {
        String request = "CREATE TABLE IF NOT EXISTS CODINGMUSIC_Ig_Users (UUID varchar(40) UNIQUE NOT NULL," +
                "NickName varchar(16) UNIQUE NOT NULL," +
                "Location varchar(30) NOT NULL," +
                "Is_Online boolean NOT NULL);";

        System.out.println(request);
        try {
            if (connection != null && !connection.isClosed()) {
                Statement statement = connection.createStatement();
                statement.execute(request);
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create users table for website users
     */
    public void setUsersTable() {
        String request = "CREATE TABLE IF NOT EXISTS CODINGMUSIC_Web_Users (ID int PRIMARY KEY AUTO_INCREMENT," +
        " NickName varchar(16) UNIQUE NOT NULL," +
        " Mail_Adress varchar(50) UNIQUE NOT NULL," +
        " PassWord varchar(50) NOT NULL);";

        System.out.println(request);
        try {
            if (connection != null && !connection.isClosed()) {
                Statement statement = connection.createStatement();
                statement.execute(request);
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create tracks table for musics list
     */
    public void setTracksTable() {
        String request = "CREATE TABLE IF NOT EXISTS CODINGMUSIC_Tracks (ID int PRIMARY KEY AUTO_INCREMENT," +
                " Title varchar(30) NOT NULL," +
                " Link varchar(100) NOT NULL," +
                " Source int," +
                " Location varchar(100) NOT NULL);";
        System.out.println(request);
        try {
            if (connection != null && !connection.isClosed()) {
                Statement statement = connection.createStatement();
                statement.execute(request);
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Create the subfolder and files for languages
     */
    public void createLangFiles() {
        File file = new File(this.getDataFolder(), "lang/");
        if (!file.exists()) {
            file.mkdir();
            getServer().getConsoleSender().sendMessage("&9Creating default language files");
            setEnglishLang();
            setFrenchLang();
        }
    }

    /**
     * Create en_EN.yml lang file
     */
    private void setEnglishLang() {
        File file = new File(this.getDataFolder(), "lang/en_EN.yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("by-player-only", "[CodingMusic] This command must be send by a player.");
        configuration.set("permission-denied", "[CodingMusic] &cYou don't have permission !");

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create fr_FR.yml lang file
     */
    private void setFrenchLang() {
        File file = new File(this.getDataFolder(), "lang/fr_FR.yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("by-player-only", "[CodingMusic] Cette commande doit être lancée par un joueur.");
        configuration.set("permission-denied", "[CodingMusic] &cVous n'avez pas la permission !");

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actions when plugin / server shutting down
     */
    @Override
    public void onDisable() {
        System.out.println("PLUGINMUSIC OFF");

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
