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

    @Override
    public void onLoad() {
        if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            new WGPreps().worldGuardHook();
        }
    }

    /**
     * Actions when plugin is enabled
     */
    @Override
    public void onEnable() {

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
        setRegionLinkTable();
    }

    /**
     * Create players table for INGAME players
     */
    public void setPlayersTable() {
        String request = "CREATE TABLE IF NOT EXISTS CODINGMUSIC_Ig_Users (UUID varchar(40) UNIQUE NOT NULL," +
                "NickName varchar(16) UNIQUE NOT NULL," +
                "Location varchar(30) NOT NULL," +
                "Is_Online boolean NOT NULL);";
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
        " PassWord varchar(50) NOT NULL," +
        " profilimg varchar(200) DEFAULT \"/projetweb/img/iconprofile.png\");";

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
     * Create region link table
     */
    public void setRegionLinkTable() {
        String request = "CREATE TABLE IF NOT EXISTS CODINGMUSIC_Region_Link (UUID varchar(40) UNIQUE NOT NULL," +
                " NickName varchar(16) NOT NULL," +
                " Musics varchar(200) NOT NULL);";
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
        configuration.set("wrong-source", "[CodingMusic] &cSource must contain an int !");
        configuration.set("add-music-syntax", "[CodingMusic] /music add [Titre] [Source] (X) (Y) (Z) [Radius] / (minX) (minY) (minZ) (maxX) (maxY) (maxZ)] [Link]");
        configuration.set("add-music-spheric-number-error", "[CodingMusic] SOURCE, X, Y, Z and RADIUS must be INT !");
        configuration.set("source-int-error", "[CodingMusic] SOURCE must be an INT !");
        configuration.set("new-music-spheric-own", "[CodingMusic] Music added in a spheric area around you.");
        configuration.set("new-music-spheric", "[CodingMusic] Music added in a spheric area.");
        configuration.set("new-music-region", "[CodingMusic] Music added into the worldguard region.");
        configuration.set("new-music-area", "[CodingMusic] Music added in the cuboid area.");
        configuration.set("deleted-music", "[CodingMusic] Music <TITLE> has been successfully deleted.");
        configuration.set("delete-music-syntax", "[CodingMusic] /music del/delete [Title1] (Title2) (Title3)...");


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
        configuration.set("wrong-source", "[CodingMusic] &cSource doit contenir un nombre entier !");
        configuration.set("add-music-syntax", "[CodingMusic] /music add [Titre] [Source] (X) (Y) (Z) [Rayon] / (minX) (minY) (minZ) (maxX) (maxY) (maxZ)] [Lien]");
        configuration.set("add-music-spheric-number-error", "[CodingMusic] SOURCE, X, Y, Z et RAYON doivent être un nombre entier !");
        configuration.set("source-int-error", "[CodingMusic] SOURCE doit être un nombre entier !");
        configuration.set("new-music-spheric-own", "[CodingMusic] Musique ajoutée dans une sphère autour de vous.");
        configuration.set("new-music-spheric", "[CodingMusic] Musique ajoutée dans une sphère.");
        configuration.set("new-music-region", "[CodingMusic] Musique ajoutée à la région worldguard.");
        configuration.set("new-music-area", "[CodingMusic] Musique ajoutée au cuboid.");
        configuration.set("deleted-music", "[CodingMusic] Musique <TITLE> supprimée avec succès.");
        configuration.set("delete-music-syntax", "[CodingMusic] /music del/delete [Titre1] (Titre2) (Titre3)...");

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

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
