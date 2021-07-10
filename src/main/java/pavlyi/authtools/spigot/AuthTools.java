package pavlyi.authtools.spigot;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pavlyi.authtools.spigot.authentication.AuthHandler;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.commands.AuthCommand;
import pavlyi.authtools.spigot.commands.AuthToolsCommand;
import pavlyi.authtools.spigot.commands.RecoverCommand;
import pavlyi.authtools.spigot.commands.TFACommand;
import pavlyi.authtools.spigot.connections.MongoDB;
import pavlyi.authtools.spigot.connections.MySQL;
import pavlyi.authtools.spigot.connections.SQLite;
import pavlyi.authtools.spigot.connections.YAMLConnection;
import pavlyi.authtools.spigot.enums.ConnectionType;
import pavlyi.authtools.spigot.enums.VersionType;
import pavlyi.authtools.spigot.listeners.MainListener;
import pavlyi.authtools.spigot.listeners.SetupListener;
import pavlyi.authtools.spigot.storages.Config;
import pavlyi.authtools.spigot.storages.Messages;
import pavlyi.authtools.spigot.storages.Spawn;
import pavlyi.authtools.spigot.storages.Variables;

import java.io.*;

public class AuthTools extends JavaPlugin {
    private static AuthTools instance;
    private PluginManager pluginManager;

    // Classes
    private Config config;
    private Messages messages;
    private Spawn spawn;
    private MySQL mySQL;
    private SQLite sqlite;
    private MongoDB mongoDB;
    private YAMLConnection yamlConnection;

    private boolean printDisableMessage = false;

    public static void main(String[] args) {
        System.out.println("You need to run this plugin in Spigot or BungeeCord Server!");
    }

    public static AuthTools getInstance() {
        return instance;
    }

    public void onEnable() {
        // Initializing classes
        instance = this;
        pluginManager = getServer().getPluginManager();

        config = new Config();
        messages = new Messages();
        spawn = new Spawn();
        mySQL = new MySQL();
        sqlite = new SQLite();
        mongoDB = new MongoDB();
        yamlConnection = new YAMLConnection();

        // Printing the intro
        log("&f&m-------------------------");
        log("&r  &cAuthTools &fv" + getDescription().getVersion());
        log("&r  &cAuthor: &fPavlyi");
        log("&r  &cWebsite: &fhttps://pavlyi.eu");
        log("");

        // Checking if server version is supported
        if (!isValidVersion()) {
            log("&r  &cError: &fUnsupported version!");
            log("&r         &fThe plugin has been &cdisabled&f, because you are trying to run this plugin on unsupported version!");
            log("&r         &fSupported versions are from &c1.9 &fto &c1.13&f!");
            log("&f&m-------------------------");

            printDisableMessage = true;
            getServer().shutdown();

            return;
        }

        // Creating plugin folders and files
        createPluginFolders();
        getConfigHandler().create();
        getConfigHandler().migrate();
        getConfigHandler().reload();

        String configCheck = getConfigHandler().check();
        if (configCheck != null) {
            log("&r  &cError: &fInvalid config!");
            log("&r         &fThe plugin has been &cdisabled&f, because there is missing content in the config,");
            log("&r         &fmissing line \"&c" + configCheck + "&f\"");
            log("&f&m-------------------------");

            printDisableMessage = true;
            getServer().shutdown();

            return;
        }

        getConfigHandler().load();


        getMessagesHandler().create();
        getMessagesHandler().migrate();
        getMessagesHandler().reload();

        String messagesCheck = getMessagesHandler().check();
        if (messagesCheck != null) {
            log("&r  &cError: &fInvalid config!");
            log("&r         &fThe plugin has been &cdisabled&f, because there is missing content in the messages,");
            log("&r         &fmissing line \"&c" + messagesCheck + "&f\"");
            log("&f&m-------------------------");

            printDisableMessage = true;
            getServer().shutdown();

            return;
        }


        getMessagesHandler().load();
        getSpawnHandler().create();
        createEmailFile();

        Variables.setConnectionType(ConnectionType.valueOf(getConfigHandler().CONNECTION_TYPE));

        Updater updater = new Updater(getDescription().getVersion());
        updater.checkForUpdate();
        switch (updater.getResult()) {
            case UPDATE_FOUND:
                String downloadUrl = updater.getDownloadURL();

                if (downloadUrl == null)
                    downloadUrl = "Link not found";

                log("&r  &cWarning: &fYour plugin is outdated please update the plugin! &c(&f" + downloadUrl + "&c)");

                break;

            case BAD_ID:
                log("&r  &cError: &fAn error occurred while looking for a resource on Spigot!");
                break;

            case NO_UPDATE:
                log("&r  &aInfo: &fYour plugin is up-to-date!");
                break;
        }

        switch (Variables.getConnectionType()) {
            case YAML:
                getYamlConnection().connect(false);
                break;

            case MYSQL:
                getMySQL().connect(false);
                break;

            case SQLITE:
                getSQLite().connect(false);
                break;

            case MONGODB:
                getMongoDB().connect(false);
                break;
        }

        if (getConfigHandler().HOOK_BUNGEECORD)
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getCommand("authtools").setExecutor(new AuthToolsCommand());
        getCommand("authtools").setTabCompleter(new AuthToolsCommand());
        getCommand("2fa").setExecutor(new TFACommand());
        getCommand("recover").setExecutor(new RecoverCommand());
        getCommand("auth").setExecutor(new AuthCommand());

        for (Player player : getServer().getOnlinePlayers()) {
            User user = Variables.getUser(player.getUniqueId());

            user.create();
            user.setIP(player.getAddress());
        }

        try {
            getPluginManager().registerEvents(new MainListener(), this);
        } catch (Exception exception) {
            log("&r  &cError: &fListener &cMainListener &fcouldn't get registered!");
            exception.printStackTrace();
            getServer().shutdown();
        }

        try {
            getPluginManager().registerEvents(new SetupListener(), this);
        } catch (Exception exception) {
            log("&r  &cError: &fListener &cSetupListener &fcouldn't get registered!");
            exception.printStackTrace();
            getServer().shutdown();
        }

        for (Player player : getServer().getOnlinePlayers())
            new AuthHandler(player).requestAuthentication();

        if (getPluginManager().isPluginEnabled(this))
            log("&f&m-------------------------");
    }

    public void onDisable() {
        boolean printDisable = printDisableMessage;

        if (!printDisable) {
            log("&f&m-------------------------");
            log("&r  &cAuthTools &fv" + getDescription().getVersion());
            log("&r  &cAuthor: &fPavlyi");
            log("&r  &cWebsite: &fhttps://pavlyi.eu");
            log("");

            Updater updater = new Updater(getDescription().getVersion());
            updater.checkForUpdate();
            switch (updater.getResult()) {
                case UPDATE_FOUND:
                    String downloadUrl = updater.getDownloadURL();

                    if (downloadUrl == null)
                        downloadUrl = "Link not found";

                    log("&r  &cWarning: &fYour plugin is outdated please update the plugin! &c(&f" + downloadUrl + "&c)");

                    break;

                case BAD_ID:
                    log("&r  &cError: &fAn error occurred while looking for a resource on Spigot!");
                    break;

                case NO_UPDATE:
                    log("&r  &aInfo: &fYour plugin is up-to-date!");
                    break;
            }
        }

        getServer().getScheduler().cancelTasks(instance);


        for (Player player : getServer().getOnlinePlayers()) {
            User user = Variables.getUser(player.getUniqueId());

            if (user.isSettingUp2FA()) {
                user.setSettingUp2FA(false);
                user.set2FA(false);
                user.set2FAsecret(null);
                user.setRecoveryCode(true);

                if (!Variables.getVersion().equals(VersionType.ONE_NINE) && user.getPlayerInventory() != null) {
                    player.getInventory().clear();
                    player.getInventory().setContents(user.getPlayerInventory().getContents());

                    user.setPlayerInventory(null);
                }
            }
        }

        if (Variables.getConnectionType() != null)
            switch (Variables.getConnectionType()) {
                case MYSQL:
                    getMySQL().disconnect(printDisable);
                    break;

                case MONGODB:
                    getMongoDB().disconnect(printDisable);
                    break;

                case SQLITE:
                    getSQLite().disconnect(printDisable);
                    break;

                case YAML:
                    getYamlConnection().disconnect(printDisable);
                    break;
            }

        if (!printDisable) {
            log("&r  &aSuccess: &cPlugin &fhas been disabled!");
            log("&f&m-------------------------");
        }
    }

    public String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void log(String msg) {
        getServer().getConsoleSender().sendMessage(color("&f[&cAuthTools&f] " + msg));
    }

    public void reloadPlugin() {
        // Printing the intro
        log("&f&m-------------------------");
        log("&r  &cAuthTools &fv" + getDescription().getVersion());
        log("&r  &cAuthor: &fPavlyi");
        log("&r  &cWebsite: &fhttps://pavlyi.eu");
        log("");

        createPluginFolders();
        getConfigHandler().create();
        getConfigHandler().migrate();
        getConfigHandler().reload();

        String configCheck = getConfigHandler().check();
        if (configCheck != null) {
            log("&r  &cError: &fInvalid config!");
            log("&r         &fThe plugin has been &cdisabled&f, because there is missing content in the config,");
            log("&r         &fmissing line \"&c" + configCheck + "&f\"");
            log("&f&m-------------------------");

            printDisableMessage = true;
            getServer().shutdown();

            return;
        }

        getConfigHandler().load();

        Variables.setConnectionType(ConnectionType.valueOf(getConfigHandler().CONNECTION_TYPE));

        getMessagesHandler().create();
        getMessagesHandler().migrate();
        getMessagesHandler().reload();

        String messagesCheck = getMessagesHandler().check();
        if (messagesCheck != null) {
            log("&r  &cError: &fInvalid config!");
            log("&r         &fThe plugin has been &cdisabled&f, because there is missing content in the messages,");
            log("&r         &fmissing line \"&c" + messagesCheck + "&f\"");
            log("&f&m-------------------------");

            printDisableMessage = true;
            getServer().shutdown();

            return;
        }


        getMessagesHandler().load();
        getSpawnHandler().create();
        createEmailFile();

        switch (Variables.getConnectionType()) {
            case YAML:
                getYamlConnection().connect(false);
                break;

            case MYSQL:
                getMySQL().connect(false);
                break;

            case SQLITE:
                getSQLite().connect(false);
                break;

            case MONGODB:
                getMongoDB().connect(false);
                break;
        }

        if (getConfigHandler().HOOK_BUNGEECORD)
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        for (Player player : getServer().getOnlinePlayers()) {
            User user = Variables.getUser(player.getUniqueId());

            user.create();
            user.setIP(player.getAddress());
        }

        if (getPluginManager().isPluginEnabled(this))
            log("&f&m-------------------------");
    }

    public boolean isValidVersion() {
        String version = getServer().getClass().getPackage().getName().substring(getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);

        switch (version) {
            case "v1_9_R1":
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1":
            case "v1_13_R1":
                return true;
        }

        return false;
    }

    public void createPluginFolders() {
        new File(instance.getDataFolder() + "/").mkdirs();
        new File(instance.getDataFolder() + "/tempFiles/").mkdirs();
    }

    public void createEmailFile() {
        if (!new File(instance.getDataFolder() + File.separator + "recovery_code_email.html").exists()) {
            try {
                InputStream inputStream = instance.getResource("spigot/recovery_code_email.html");
                OutputStream outStream = new FileOutputStream(instance.getDataFolder() + File.separator + "recovery_code_email.html");
                byte[] buffer = new byte[inputStream.available()];

                inputStream.read(buffer);
                outStream.write(buffer);
            } catch (IOException exception) {
                instance.log("&r  &cError: &fWhile creating &cEmail for Recovery Code &fan error ocurred!");
                exception.printStackTrace();
            }
        }

        if (!new File(instance.getDataFolder() + File.separator + "verification_code_email.html").exists()) {
            try {
                InputStream inputStream = instance.getResource("spigot/verification_code_email.html");
                OutputStream outStream = new FileOutputStream(instance.getDataFolder() + File.separator + "verification_code_email.html");
                byte[] buffer = new byte[inputStream.available()];

                inputStream.read(buffer);
                outStream.write(buffer);
            } catch (IOException exception) {
                instance.log("&r  &cError: &fWhile creating &cEmail for Verification Code &fan error ocurred!");
                exception.printStackTrace();
            }
        }
    }

    public void switchConnection(ConnectionType switchToConnection) {
        if (switchToConnection == null || Variables.getConnectionType() == null)
            return;

        switch (switchToConnection) {
            case YAML:
                switch (Variables.getConnectionType()) {
                    case MYSQL:
                        getMySQL().disconnect(true);
                        getYamlConnection().connect(true);
                        Variables.setConnectionType(ConnectionType.YAML);

                        break;

                    case SQLITE:
                        getSQLite().disconnect(true);
                        getYamlConnection().connect(true);
                        Variables.setConnectionType(ConnectionType.YAML);

                        break;

                    case MONGODB:
                        getMongoDB().disconnect(true);
                        getYamlConnection().connect(true);
                        Variables.setConnectionType(ConnectionType.YAML);

                        break;
                }

                break;

            case MYSQL:
                switch (Variables.getConnectionType()) {
                    case YAML:
                        getMySQL().connect(true);
                        Variables.setConnectionType(ConnectionType.MYSQL);

                        break;

                    case SQLITE:
                        getSQLite().disconnect(true);
                        getMySQL().connect(true);
                        Variables.setConnectionType(ConnectionType.MYSQL);

                        break;

                    case MONGODB:
                        getMongoDB().disconnect(true);
                        getMySQL().connect(true);
                        Variables.setConnectionType(ConnectionType.MYSQL);

                        break;
                }

                break;

            case SQLITE:
                switch (Variables.getConnectionType()) {
                    case YAML:
                        getSQLite().connect(true);
                        Variables.setConnectionType(ConnectionType.SQLITE);

                        break;

                    case MYSQL:
                        getMySQL().disconnect(true);
                        getSQLite().connect(true);
                        Variables.setConnectionType(ConnectionType.SQLITE);

                        break;

                    case MONGODB:
                        getMongoDB().disconnect(true);
                        getSQLite().connect(true);
                        Variables.setConnectionType(ConnectionType.SQLITE);

                        break;
                }

                break;

            case MONGODB:
                switch (Variables.getConnectionType()) {
                    case YAML:
                        getMongoDB().connect(true);
                        Variables.setConnectionType(ConnectionType.MONGODB);

                        break;

                    case MYSQL:
                        getMySQL().disconnect(true);
                        getMongoDB().connect(true);
                        Variables.setConnectionType(ConnectionType.MONGODB);

                        break;

                    case SQLITE:
                        getSQLite().disconnect(true);
                        getMongoDB().connect(true);
                        Variables.setConnectionType(ConnectionType.MONGODB);

                        break;
                }

                break;
        }
    }


    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public Config getConfigHandler() {
        return config;
    }

    public Messages getMessagesHandler() {
        return messages;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public SQLite getSQLite() {
        return sqlite;
    }

    public MongoDB getMongoDB() {
        return mongoDB;
    }

    public YAMLConnection getYamlConnection() {
        return yamlConnection;
    }

    public Spawn getSpawnHandler() {
        return spawn;
    }
}
