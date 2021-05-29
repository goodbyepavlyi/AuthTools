package pavlyi.authtools.spigot;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pavlyi.authtools.spigot.commands.AuthToolsCommand;
import pavlyi.authtools.spigot.commands.TFACommand;
import pavlyi.authtools.spigot.connections.MongoDB;
import pavlyi.authtools.spigot.connections.MySQL;
import pavlyi.authtools.spigot.connections.SQLite;
import pavlyi.authtools.spigot.connections.YAMLConnection;
import pavlyi.authtools.spigot.enums.ConnectionType;
import pavlyi.authtools.spigot.enums.HookType;
import pavlyi.authtools.spigot.enums.VersionType;
import pavlyi.authtools.spigot.handlers.AuthHandler;
import pavlyi.authtools.spigot.handlers.User;
import pavlyi.authtools.spigot.handlers.VariablesHandler;
import pavlyi.authtools.spigot.listeners.AuthMeListener;
import pavlyi.authtools.spigot.listeners.MainListener;
import pavlyi.authtools.spigot.listeners.SetupListener;
import pavlyi.authtools.spigot.listeners.nLoginListener;
import pavlyi.authtools.spigot.storages.ConfigHandler;
import pavlyi.authtools.spigot.storages.MessagesHandler;
import pavlyi.authtools.spigot.storages.SpawnHandler;

import java.io.File;

public class AuthTools extends JavaPlugin {
    private static AuthTools instance;
    private PluginManager pluginManager;

    // Classes
    private ConfigHandler configHandler;
    private MessagesHandler messagesHandler;
    private MySQL mySQL;
    private SQLite sqlite;
    private MongoDB mongoDB;
    private YAMLConnection yamlConnection;
    private SpawnHandler spawnHandler;

    private boolean printDisableMessage = false;

    public static void main(String[] args) {
        System.out.println("You need to run this plugin in Spigot or BungeeCord Server!");
    }

    public static AuthTools getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        pluginManager = getServer().getPluginManager();

        // Initializing classes
        configHandler = new ConfigHandler();
        messagesHandler = new MessagesHandler();
        mySQL = new MySQL();
        sqlite = new SQLite();
        mongoDB = new MongoDB();
        yamlConnection = new YAMLConnection();
        spawnHandler = new SpawnHandler();

        // Printing the intro
        log("&f&m-------------------------");
        log("&r  &cAuthTools &fv" + getDescription().getVersion());
        log("&r  &cAuthor: &fPavlyi");
        log("&r  &cWebsite: &fhttps://pavlyi.eu");
        log("");

        if (!isValidVersion()) {
            log("&r  &cError: &fUnsupported version!");
            log("&r         &fThe plugin has been &cdisabled&f, because you are trying to run this plugin on unsupported version!");
            log("&r         &fSupported versions are from &c1.9 &fto &c1.13&f!");
            log("&f&m-------------------------");

            printDisableMessage = true;
            getPluginManager().disablePlugin(this);

            return;
        }

        createPluginFolders();
        getConfigHandler().create();

        String configCheck = getConfigHandler().check();
        if (configCheck != null) {
            log("&r  &cError: &fInvalid config!");
            log("&r         &fThe plugin has been &cdisabled&f, because there is missing content in the config,");
            log("&r         &fmissing line \"&c" + configCheck + "&f\"");
            log("&f&m-------------------------");

            printDisableMessage = true;
            getPluginManager().disablePlugin(this);

            return;
        }

        getConfigHandler().load();

        VariablesHandler.setConnectionType(ConnectionType.valueOf(getConfigHandler().CONNECTION_TYPE));
        VariablesHandler.setHookType(HookType.valueOf(getConfigHandler().HOOK_TYPE));

        getMessagesHandler().create();

        String messagesCheck = getMessagesHandler().check();
        if (messagesCheck != null) {
            log("&r  &cError: &fInvalid config!");
            log("&r         &fThe plugin has been &cdisabled&f, because there is missing content in the messages,");
            log("&r         &fmissing line \"&c" + messagesCheck + "&f\"");
            log("&f&m-------------------------");

            printDisableMessage = true;
            getPluginManager().disablePlugin(this);

            return;
        }


        getMessagesHandler().load();
        getSpawnHandler().create();

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

        switch (VariablesHandler.getConnectionType()) {
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
        getCommand("2fa").setTabCompleter(new TFACommand());

        for (Player player : getServer().getOnlinePlayers()) {
            User user = new User(player.getName());

            user.create();
            user.setIP(player.getAddress());
            user.setUUID();
        }

        if (!VariablesHandler.getHookType().equals(HookType.API)) {
            try {
                getPluginManager().registerEvents(new MainListener(), this);
            } catch (Exception ex) {
                log("&r  &cError: &fListener &cMainListener &fcouldn't get registered!");
            }

            try {
                getPluginManager().registerEvents(new SetupListener(), this);
            } catch (Exception ex) {
                log("&r  &cError: &fListener &cSetupListener &fcouldn't get registered!");
            }
        }

        switch (VariablesHandler.getHookType()) {
            case STANDALONE:
                for (Player player : getServer().getOnlinePlayers()) {
                     AuthHandler authHandler = new AuthHandler(player);

                    authHandler.requestAuthentication();
                }

                break;

            case API:
                log("&r  &aInfo: &fPlugin is listening only for actions requested by &cAPI&f!");

                break;

            case AUTHME:
                if (hookPlugin("AuthMe")) {
                    log("&r  &cError: &cAuthMe &fwasn't found!");
                    log("&r         &fPlugin has been &cdisabled&f, because you defined to hook &cAuthMe &fin config,");
                    log("&r         &fbut &cAuthMe &fwasn't found");
                    log("&f&m-------------------------");

                    printDisableMessage = true;

                    getPluginManager().disablePlugin(this);

                    break;
                }

                try {
                    getPluginManager().registerEvents(new AuthMeListener(), this);
                } catch (Exception ex) {
                    log("&r  &cError: &fListener &cAuthMeListener &fcouldn't get registered!");
                }

                break;

            case NLOGIN:
                if (hookPlugin("nLogin")) {
                    log("&r  &cError: &cnLogin &fwasn't found!");
                    log("&r         &fPlugin has been &cdisabled&f, because you defined to hook &cnLogin &fin config,");
                    log("&r         &fbut &cnLogin &fwasn't found");
                    log("&f&m-------------------------");

                    printDisableMessage = true;

                    getPluginManager().disablePlugin(this);

                    break;
                }

                try {
                    getPluginManager().registerEvents(new nLoginListener(), this);
                } catch (Exception ex) {
                    log("&r  &cError: &fListener &cnLoginListener &fcouldn't get registered!");
                }

                break;
        }

        if (getPluginManager().isPluginEnabled(this)) {
            log("&f&m-------------------------");
        }
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
            User user = new User(player.getName());

            if (user.getSettingUp2FA()) {
                user.setSettingUp2FA(false);
                user.set2FA(false);
                user.set2FAsecret(null);
                user.setRecoveryCode(true);

                if (!VariablesHandler.getVersion().equals(VersionType.ONE_NINE))
                    if (VariablesHandler.getPlayerInventories().containsKey(player.getUniqueId())) {
                        player.getInventory().clear();
                        player.getInventory().setContents(VariablesHandler.getPlayerInventories().get(player.getUniqueId()).getContents());

                        VariablesHandler.getPlayerInventories().remove(player.getUniqueId());
                    }
            }
        }

        if (VariablesHandler.getConnectionType() != null)
            switch (VariablesHandler.getConnectionType()) {
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

        if (getConfigHandler().check() != null) {
            log("do checkconfiuration");
            return;
        }

        VariablesHandler.setConnectionType(ConnectionType.valueOf(getConfigHandler().CONNECTION_TYPE));
        VariablesHandler.setHookType(HookType.valueOf(getConfigHandler().HOOK_TYPE));

        switch (VariablesHandler.getConnectionType()) {
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
        getCommand("2fa").setTabCompleter(new TFACommand());

        for (Player player : getServer().getOnlinePlayers()) {
            User user = new User(player.getName());

            user.create();
            user.setIP(player.getAddress());
            user.setUUID();
        }

        HandlerList.unregisterAll(this);

        try {
            getPluginManager().registerEvents(new MainListener(), this);
        } catch (Exception ex) {
            log("&r  &cError: &fListener &cMainListener &fcouldn't get registered!");
        }

        try {
            getPluginManager().registerEvents(new SetupListener(), this);
        } catch (Exception ex) {
            log("&r  &cError: &fListener &cSetupListener &fcouldn't get registered!");
        }

        switch (VariablesHandler.getHookType()) {
            case API:
                log("&r  &aInfo: &fPlugin is listening only for actions requested by &cAPI&f!");

                break;

            case AUTHME:
                if (hookPlugin("AuthMe")) {
                    log("&r  &cError: &cAuthMe &fwasn't found!");
                    log("&r         &fPlugin has been &cdisabled&f, because you defined to hook &cAuthMe &fin config,");
                    log("&r         &fbut &cAuthMe &fwasn't found");
                    log("&f&m-------------------------");

                    printDisableMessage = true;

                    getPluginManager().disablePlugin(this);

                    break;
                }


                try {
                    getPluginManager().registerEvents(new AuthMeListener(), this);
                } catch (Exception ex) {
                    log("&r  &cError: &fListener &cAuthMeListener &fcouldn't get registered!");
                }

                break;

            case NLOGIN:
                if (hookPlugin("nLogin")) {
                    log("&r  &cError: &cnLogin &fwasn't found!");
                    log("&r         &fPlugin has been &cdisabled&f, because you defined to hook &cnLogin &fin config,");
                    log("&r         &fbut &cnLogin &fwasn't found");
                    log("&f&m-------------------------");

                    printDisableMessage = true;

                    getPluginManager().disablePlugin(this);

                    break;
                }

                try {
                    getPluginManager().registerEvents(new nLoginListener(), this);
                } catch (Exception ex) {
                    log("&r  &cError: &fListener &cnLoginListener &fcouldn't get registered!");
                }

                break;
        }

        log("&f&m-------------------------");
    }

    public boolean hookPlugin(String pluginName) {
        return getPluginManager().getPlugin(pluginName) == null;
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

    public void switchConnection(ConnectionType switchToConnection) {
        if (switchToConnection == null)
            return;

        if (VariablesHandler.getConnectionType() == null)
            return;

        switch (switchToConnection) {
            case YAML:
                switch (VariablesHandler.getConnectionType()) {
                    case MYSQL:
                        getMySQL().disconnect(true);
                        getYamlConnection().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.YAML);

                        break;

                    case SQLITE:
                        getSQLite().disconnect(true);
                        getYamlConnection().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.YAML);

                        break;

                    case MONGODB:
                        getMongoDB().disconnect(true);
                        getYamlConnection().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.YAML);

                        break;
                }

                break;

            case MYSQL:
                switch (VariablesHandler.getConnectionType()) {
                    case YAML:
                        getMySQL().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.MYSQL);

                        break;

                    case SQLITE:
                        getSQLite().disconnect(true);
                        getMySQL().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.MYSQL);

                        break;

                    case MONGODB:
                        getMongoDB().disconnect(true);
                        getMySQL().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.MYSQL);

                        break;
                }

                break;

            case SQLITE:
                switch (VariablesHandler.getConnectionType()) {
                    case YAML:
                        getSQLite().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.SQLITE);

                        break;

                    case MYSQL:
                        getMySQL().disconnect(true);
                        getSQLite().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.SQLITE);

                        break;

                    case MONGODB:
                        getMongoDB().disconnect(true);
                        getSQLite().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.SQLITE);

                        break;
                }

                break;

            case MONGODB:
                switch (VariablesHandler.getConnectionType()) {
                    case YAML:
                        getMongoDB().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.MONGODB);

                        break;

                    case MYSQL:
                        getMySQL().disconnect(true);
                        getMongoDB().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.MONGODB);

                        break;

                    case SQLITE:
                        getSQLite().disconnect(true);
                        getMongoDB().connect(true);
                        VariablesHandler.setConnectionType(ConnectionType.MONGODB);

                        break;
                }

                break;
        }
    }


    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public MessagesHandler getMessagesHandler() {
        return messagesHandler;
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

    public SpawnHandler getSpawnHandler() {
        return spawnHandler;
    }
}
