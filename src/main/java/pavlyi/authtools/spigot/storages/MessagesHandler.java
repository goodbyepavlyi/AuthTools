package pavlyi.authtools.spigot.storages;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pavlyi.authtools.spigot.AuthTools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MessagesHandler {
    public String NO_PERMISSIONS;
    public String PLUGIN_RELOADED;
    public String PLAYER_NOT_FOUND;
    public String ONLY_PLAYER_CAN_EXECUTE_COMMAND;
    public List<String> COMMANDS_AUTHTOOLS_HELPUSAGE = new ArrayList<>();
    public List<String> COMMANDS_AUTHTOOLS_ABOUT = new ArrayList<>();
    public List<String> COMMANDS_AUTHTOOLS_INFO = new ArrayList<>();
    public String COMMANDS_AUTHTOOLS_RELOADUSAGE;
    public String COMMANDS_AUTHTOOLS_ABOUTUSAGE;
    public String COMMANDS_AUTHTOOLS_RESETUSAGE;
    public String COMMANDS_AUTHTOOLS_BACKENDUSAGE;
    public String COMMANDS_AUTHTOOLS_INFOUSAGE;
    public String COMMANDS_AUTHTOOLS_IMPORTUSAGE;
    public String COMMANDS_AUTHTOOLS_SETSPAWNUSAGE;
    public String COMMANDS_AUTHTOOLS_SETLOBBYUSAGE;
    public String COMMANDS_AUTHTOOLS_SETSPAWN;
    public String COMMANDS_AUTHTOOLS_SETLOBBY;
    public String COMMANDS_AUTHTOOLS_IMPORT_SUCESSFULLY_IMPORTED;
    public String COMMANDS_AUTHTOOLS_IMPORT_ERROR_WHILE_IMPORTING;
    public String COMMANDS_AUTHTOOLS_IMPORT_INCORRECT_TYPE;
    public String COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_2FA_ENABLED;
    public String COMMANDS_AUTHTOOLS_RESET_DISABLED_2FA;
    public String COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED;
    public String COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION;
    public String COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION;
    public String COMMANDS_AUTHTOOLS_BACKEND_WRONG_CONNECTION_TYPE;
    public List<String> COMMANDS_2FA_SETUP_AUTHAPP = new ArrayList<>();
    public String COMMANDS_2FA_SETUP_ALREADY_REGISTERED;
    public String COMMANDS_2FA_SETUP_ENABLED;
    public String COMMANDS_2FA_SETUP_DISABLED;
    public String COMMANDS_2FA_SETUP_SETUP_CANCELLED;
    public String COMMANDS_2FA_SETUP_INVALID_CODE;
    public String COMMANDS_2FA_SETUP_QRCODE_TITLE;
    public List<String> COMMANDS_2FA_SETUP_QRCODE_LORE = new ArrayList<>();
    public String COMMANDS_2FA_RECOVER_USAGE;
    public String COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE;
    public String COMMANDS_2FA_RECOVER_RECOVERED;
    public String COMMANDS_2FA_RECOVER_2FA_DISABLED;
    public String COMMANDS_2FA_LOGIN_LOGIN_MESSAGE;
    public String COMMANDS_2FA_LOGIN_LOGGED_IN;
    public String COMMANDS_2FA_LOGIN_ALREADY_LOGGED_IN;
    public String COMMANDS_2FA_LOGIN_TIMED_OUT;
    public String COMMANDS_2FA_LOGIN_DENIED_COMMAND;
    public String COMMANDS_2FA_LOGIN_WRONG_CODE_KICK;
    public String COMMANDS_2FA_LOGIN_PLAYER_IS_ONLINE_KICK;
    public double CONFIG_VERSION;
    private final AuthTools instance = AuthTools.getInstance();
    private File file;
    private FileConfiguration config;
    private final ArrayList<String> configContent = new ArrayList<>();

    public void create() {
        file = new File(instance.getDataFolder() + "/messages.yml");
        config = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            try {
                InputStream inputStream = instance.getResource("spigot/messages.yml");
                OutputStream outStream = new FileOutputStream(getFile());
                byte[] buffer = new byte[inputStream.available()];

                inputStream.read(buffer);
                outStream.write(buffer);
            } catch (IOException exception) {
                instance.log("&r  &cError: &fWhile creating &cconfig &fan error ocurred!");
                exception.printStackTrace();

                instance.getPluginManager().disablePlugin(instance);
            }
        }

        try {
            getConfig().load(getFile());
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException exception) {
            instance.log("&r  &cError: &fWhile saving &cmessages &fan error ocurred!");
            exception.printStackTrace();

            instance.getPluginManager().disablePlugin(instance);
        }

        configContent.add("noPermissions");
        configContent.add("pluginReloaded");
        configContent.add("playerNotFound");
        configContent.add("onlyPlayerCanExecuteCommand");
        configContent.add("commands.authtools.helpusage");
        configContent.add("commands.authtools.about");
        configContent.add("commands.authtools.info");
        configContent.add("commands.authtools.reloadusage");
        configContent.add("commands.authtools.aboutusage");
        configContent.add("commands.authtools.resetusage");
        configContent.add("commands.authtools.backendusage");
        configContent.add("commands.authtools.infousage");
        configContent.add("commands.authtools.importusage");
        configContent.add("commands.authtools.setspawnusage");
        configContent.add("commands.authtools.setlobbyusage");
        configContent.add("commands.authtools.setspawn");
        configContent.add("commands.authtools.setlobby");
        configContent.add("commands.authtools.import.sucessfullyImported");
        configContent.add("commands.authtools.import.errorWhileImporting");
        configContent.add("commands.authtools.import.incorrectType");
        configContent.add("commands.authtools.reset.playerDoesntHave2FAenabled");
        configContent.add("commands.authtools.reset.disabled2FA");
        configContent.add("commands.authtools.backend.alreadyConnected");
        configContent.add("commands.authtools.backend.switchedToConnection");
        configContent.add("commands.authtools.backend.couldntSwitchConnection");
        configContent.add("commands.authtools.backend.wrongConnectionType");
        configContent.add("commands.2fa.setup.authApp");
        configContent.add("commands.2fa.setup.alreadyRegistered");
        configContent.add("commands.2fa.setup.enabled");
        configContent.add("commands.2fa.setup.disabled");
        configContent.add("commands.2fa.setup.setupCancelled");
        configContent.add("commands.2fa.setup.invalidCode");
        configContent.add("commands.2fa.setup.qrcode.title");
        configContent.add("commands.2fa.setup.qrcode.lore");
        configContent.add("commands.2fa.recover.usage");
        configContent.add("commands.2fa.recover.invalidRecoveryCode");
        configContent.add("commands.2fa.recover.recovered");
        configContent.add("commands.2fa.recover.2faDisabled");
        configContent.add("commands.2fa.login.loginMessage");
        configContent.add("commands.2fa.login.loggedIn");
        configContent.add("commands.2fa.login.alreadyLoggedIn");
        configContent.add("commands.2fa.login.timedOut");
        configContent.add("commands.2fa.login.deniedCommand");
        configContent.add("commands.2fa.login.wrongCodeKick");
        configContent.add("commands.2fa.login.playerIsOnlineKick");
        configContent.add("config-version");
    }

    public String check() {
        for (String path : configContent) {
            if (getConfig().get(path) == null) {
                return path;
            }
        }

        return null;
    }

    public void save() {
        try {
            getConfig().save(getFile());
        } catch (IOException exception) {
            instance.log("&r  &cError: &fWhile saving &cmessages &fan error ocurred!");
            exception.printStackTrace();

            instance.getPluginManager().disablePlugin(instance);
        }
    }

    public void load() {
        try {
            getConfig().load(getFile());
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException exception) {
            instance.log("&r  &cError: &fWhile saving &cmessages &fan error ocurred!");
            exception.printStackTrace();

            instance.getPluginManager().disablePlugin(instance);
        }

        NO_PERMISSIONS = instance.color(getConfig().getString("noPermissions"));
        PLUGIN_RELOADED = instance.color(getConfig().getString("pluginReloaded"));
        PLAYER_NOT_FOUND = instance.color(getConfig().getString("playerNotFound"));
        ONLY_PLAYER_CAN_EXECUTE_COMMAND = instance.color(getConfig().getString("onlyPlayerCanExecuteCommand"));

        for (String tempMessage : getConfig().getStringList("commands.authtools.helpusage")) {
            COMMANDS_AUTHTOOLS_HELPUSAGE.add(instance.color(tempMessage));
        }

        for (String tempMessage : getConfig().getStringList("commands.authtools.about")) {
            COMMANDS_AUTHTOOLS_ABOUT.add(instance.color(tempMessage));
        }

        for (String tempMessage : getConfig().getStringList("commands.authtools.info")) {
            COMMANDS_AUTHTOOLS_INFO.add(instance.color(tempMessage));
        }

        COMMANDS_AUTHTOOLS_RELOADUSAGE = instance.color(getConfig().getString("commands.authtools.reloadusage"));
        COMMANDS_AUTHTOOLS_ABOUTUSAGE = instance.color(getConfig().getString("commands.authtools.aboutusage"));
        COMMANDS_AUTHTOOLS_RESETUSAGE = instance.color(getConfig().getString("commands.authtools.resetusage"));
        COMMANDS_AUTHTOOLS_BACKENDUSAGE = instance.color(getConfig().getString("commands.authtools.backendusage"));
        COMMANDS_AUTHTOOLS_INFOUSAGE = instance.color(getConfig().getString("commands.authtools.infousage"));
        COMMANDS_AUTHTOOLS_IMPORTUSAGE = instance.color(getConfig().getString("commands.authtools.importusage"));
        COMMANDS_AUTHTOOLS_SETSPAWNUSAGE = instance.color(getConfig().getString("commands.authtools.setspawnusage"));
        COMMANDS_AUTHTOOLS_SETLOBBYUSAGE = instance.color(getConfig().getString("commands.authtools.setlobbyusage"));

        COMMANDS_AUTHTOOLS_SETSPAWN = instance.color(getConfig().getString("commands.authtools.setspawn"));
        COMMANDS_AUTHTOOLS_SETLOBBY = instance.color(getConfig().getString("commands.authtools.setlobby"));

        COMMANDS_AUTHTOOLS_IMPORT_SUCESSFULLY_IMPORTED = instance.color(getConfig().getString("commands.authtools.import.sucessfullyImported"));
        COMMANDS_AUTHTOOLS_IMPORT_ERROR_WHILE_IMPORTING = instance.color(getConfig().getString("commands.authtools.import.errorWhileImporting"));
        COMMANDS_AUTHTOOLS_IMPORT_INCORRECT_TYPE = instance.color(getConfig().getString("commands.authtools.import.incorrectType"));

        COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_2FA_ENABLED = instance.color(getConfig().getString("commands.authtools.reset.playerDoesntHave2FAenabled"));
        COMMANDS_AUTHTOOLS_RESET_DISABLED_2FA = instance.color(getConfig().getString("commands.authtools.reset.disabled2FA"));

        COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED = instance.color(getConfig().getString("commands.authtools.backend.alreadyConnected"));
        COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION = instance.color(getConfig().getString("commands.authtools.backend.switchedToConnection"));
        COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION = instance.color(getConfig().getString("commands.authtools.backend.couldntSwitchConnection"));
        COMMANDS_AUTHTOOLS_BACKEND_WRONG_CONNECTION_TYPE = instance.color(getConfig().getString("commands.authtools.backend.wrongConnectionType"));

        for (String tempMessage : getConfig().getStringList("commands.2fa.setup.authApp")) {
            COMMANDS_2FA_SETUP_AUTHAPP.add(instance.color(tempMessage));
        }

        COMMANDS_2FA_SETUP_ALREADY_REGISTERED = instance.color(getConfig().getString("commands.2fa.setup.alreadyRegistered"));
        COMMANDS_2FA_SETUP_ENABLED = instance.color(getConfig().getString("commands.2fa.setup.enabled"));
        COMMANDS_2FA_SETUP_DISABLED = instance.color(getConfig().getString("commands.2fa.setup.disabled"));
        COMMANDS_2FA_SETUP_SETUP_CANCELLED = instance.color(getConfig().getString("commands.2fa.setup.setupCancelled"));
        COMMANDS_2FA_SETUP_INVALID_CODE = instance.color(getConfig().getString("commands.2fa.setup.invalidCode"));
        COMMANDS_2FA_SETUP_QRCODE_TITLE = instance.color(getConfig().getString("commands.2fa.setup.qrcode.title"));

        for (String tempMessage : getConfig().getStringList("commands.2fa.setup.qrcode.lore")) {
            COMMANDS_2FA_SETUP_QRCODE_LORE.add(instance.color(tempMessage));
        }

        COMMANDS_2FA_RECOVER_USAGE = instance.color(getConfig().getString("commands.2fa.recover.usage"));
        COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE = instance.color(getConfig().getString("commands.2fa.recover.invalidRecoveryCode"));
        COMMANDS_2FA_RECOVER_RECOVERED = instance.color(getConfig().getString("commands.2fa.recover.recovered"));
        COMMANDS_2FA_RECOVER_2FA_DISABLED = instance.color(getConfig().getString("commands.2fa.recover.2faDisabled"));

        COMMANDS_2FA_LOGIN_LOGIN_MESSAGE = instance.color(getConfig().getString("commands.2fa.login.loginMessage"));
        COMMANDS_2FA_LOGIN_LOGGED_IN = instance.color(getConfig().getString("commands.2fa.login.loggedIn"));
        COMMANDS_2FA_LOGIN_ALREADY_LOGGED_IN = instance.color(getConfig().getString("commands.2fa.login.alreadyLoggedIn"));
        COMMANDS_2FA_LOGIN_TIMED_OUT = instance.color(getConfig().getString("commands.2fa.login.timedOut"));
        COMMANDS_2FA_LOGIN_DENIED_COMMAND = instance.color(getConfig().getString("commands.2fa.login.deniedCommand"));
        COMMANDS_2FA_LOGIN_WRONG_CODE_KICK = instance.color(getConfig().getString("commands.2fa.login.wrongCodeKick"));
        COMMANDS_2FA_LOGIN_PLAYER_IS_ONLINE_KICK = instance.color(getConfig().getString("commands.2fa.login.playerIsOnlineKick"));

        CONFIG_VERSION = getConfig().getDouble("config-version");
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}

