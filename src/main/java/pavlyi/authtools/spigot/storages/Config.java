package pavlyi.authtools.spigot.storages;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pavlyi.authtools.spigot.AuthTools;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {
    private final AuthTools instance = AuthTools.getInstance();
    private final ArrayList<String> configContent = new ArrayList<>();
    public String CONNECTION_TYPE;
    public String CONNECTION_YAML_FILENAME;
    public String CONNECTION_MYSQL_HOSTNAME;
    public Integer CONNECTION_MYSQL_PORT;
    public String CONNECTION_MYSQL_DATABASE;
    public String CONNECTION_MYSQL_USERNAME;
    public String CONNECTION_MYSQL_PASSWORD;
    public String CONNECTION_MYSQL_OPTIONS;
    public String CONNECTION_SQLITE_FILENAME;
    public String CONNECTION_MONGODB_CLUSTER;
    public String CONNECTION_MONGODB_DATABASE;
    public String CONNECTION_MONGODB_USERNAME;
    public String CONNECTION_MONGODB_PASSWORD;
    public String HOOK_TYPE;
    public boolean HOOK_BUNGEECORD;
    public boolean HOOK_REGISTER_AFTER_AUTHENTICATION;
    public String QRCODE_NAME;
    public String SETTINGS_SEND_PLAYER_TO;
    public boolean SETTINGS_SESSION_ENABLE;
    public long SETTINGS_SESSION_TIMEOUT;
    public List<String> SETTINGS_RESTRICTIONS_ALLOWED_COMMANDS = new ArrayList<>();
    public boolean SETTINGS_RESTRICTIONS_ALLOW_MOVEMENT;
    public int SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS;
    public int SETTINGS_RESTRICTIONS_TIMEOUT;
    public boolean SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN;
    public boolean SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION;
    public boolean SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE;
    public String SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_SMTP;
    public int SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_PORT;
    public String SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_ADDRESS;
    public String SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_PASSWORD;
    public boolean SETTINGS_TITLE_ANNOUNCEMENT_TITLE_ENABLE;
    public int SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEIN;
    public int SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEOUT;
    public boolean SETTINGS_TITLE_ANNOUNCEMENT_TITLE_USE_IN_REGISTER;
    public boolean SETTINGS_TITLE_ANNOUNCEMENT_TITLE_USE_IN_LOGIN;
    public boolean SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_ENABLE;
    public int SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEIN;
    public int SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEOUT;
    public boolean SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_USE_IN_REGISTER;
    public boolean SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_USE_IN_LOGIN;
    public boolean SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_ENABLE;
    public boolean SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_USE_IN_REGISTER;
    public boolean SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_USE_IN_LOGIN;
    public String SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_TITLE;
    public String SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_SUBTITLE;
    public String SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_ACTIONBAR;
    public String SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_TITLE;
    public String SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_SUBTITLE;
    public String SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_ACTIONBAR;
    private FileConfiguration config;

    public void create() {
        config = new YamlConfiguration();

        if (!getFile().exists()) {
            try {
                InputStream inputStream = instance.getResource("spigot/config.yml");
                OutputStream outStream = new FileOutputStream(getFile());
                byte[] buffer = new byte[inputStream.available()];

                inputStream.read(buffer);
                outStream.write(buffer);
            } catch (IOException exception) {
                instance.log("&r  &cError: &fWhile creating &cconfig &fan error ocurred!");
                exception.printStackTrace();

                instance.getServer().shutdown();
            }
        }

        try {
            getConfig().load(getFile());
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException exception) {
            instance.log("&r  &cError: &fWhile saving &cconfig &fan error ocurred!");
            exception.printStackTrace();

            instance.getServer().shutdown();
        }

        configContent.add("connection.type");
        configContent.add("connection.yaml.fileName");
        configContent.add("connection.mysql.hostname");
        configContent.add("connection.mysql.port");
        configContent.add("connection.mysql.database");
        configContent.add("connection.mysql.username");
        configContent.add("connection.mysql.password");
        configContent.add("connection.mysql.options");
        configContent.add("connection.sqlite.fileName");
        configContent.add("connection.mongodb.cluster");
        configContent.add("connection.mongodb.database");
        configContent.add("connection.mongodb.username");
        configContent.add("connection.mongodb.password");
        configContent.add("hook.type");
        configContent.add("hook.bungeeCord");
        configContent.add("hook.registerAfterAuthentication");
        configContent.add("qrcode.name");
        configContent.add("settings.sendPlayerTo");
        configContent.add("settings.session.enable");
        configContent.add("settings.session.timeout");
        configContent.add("settings.restrictions.allowedCommands");
        configContent.add("settings.restrictions.allowMovement");
        configContent.add("settings.restrictions.allowedMovementRadius");
        configContent.add("settings.restrictions.timeout");
        configContent.add("settings.restrictions.teleportUnAuthedToSpawn");
        configContent.add("settings.restrictions.forceSingleSession");
        configContent.add("settings.restrictions.kickOnWrong2FAcode");
        configContent.add("settings.recovery.email.authentication.smtp");
        configContent.add("settings.recovery.email.authentication.port");
        configContent.add("settings.recovery.email.authentication.address");
        configContent.add("settings.recovery.email.authentication.password");
        configContent.add("settings.announcement.title.enable");
        configContent.add("settings.announcement.title.fadeIn");
        configContent.add("settings.announcement.title.fadeOut");
        configContent.add("settings.announcement.title.useInRegister");
        configContent.add("settings.announcement.title.useInLogin");
        configContent.add("settings.announcement.subtitle.enable");
        configContent.add("settings.announcement.subtitle.fadeIn");
        configContent.add("settings.announcement.subtitle.fadeOut");
        configContent.add("settings.announcement.subtitle.useInRegister");
        configContent.add("settings.announcement.subtitle.useInLogin");
        configContent.add("settings.announcement.actionbar.enable");
        configContent.add("settings.announcement.actionbar.useInRegister");
        configContent.add("settings.announcement.actionbar.useInLogin");
        configContent.add("settings.announcement.register.title");
        configContent.add("settings.announcement.register.subtitle");
        configContent.add("settings.announcement.register.actionbar");
        configContent.add("settings.announcement.login.title");
    }

    public String check() {
        for (String path : configContent) {
            if (getConfig().get(path) == null) {
                return path;
            }
        }

        return null;
    }

    public void migrate() {
        try {
            FileUpdater.update(instance, "spigot/config.yml", getFile(), Collections.singletonList("\""));
        } catch (IOException exception) {
            instance.log("&r  &cError: &fWhile migrating &cconfig &fan error ocurred!");
            exception.printStackTrace();
        }
    }

    public void reload() {
        try {
            getConfig().load(getFile());
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException exception) {
            instance.log("&r  &cError: &fWhile reloading &cconfig &fan error ocurred!");
            exception.printStackTrace();

            instance.getServer().shutdown();
        }
    }

    public void load() {
        try {
            getConfig().load(getFile());
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException exception) {
            instance.log("&r  &cError: &fWhile loading &cconfig &fan error ocurred!");
            exception.printStackTrace();

            instance.getServer().shutdown();
        }

        CONNECTION_TYPE = getConfig().getString("connection.type");

        CONNECTION_YAML_FILENAME = getConfig().getString("connection.yaml.fileName");

        CONNECTION_MYSQL_HOSTNAME = getConfig().getString("connection.mysql.hostname");
        CONNECTION_MYSQL_PORT = getConfig().getInt("connection.mysql.port");
        CONNECTION_MYSQL_DATABASE = getConfig().getString("connection.mysql.database");
        CONNECTION_MYSQL_USERNAME = getConfig().getString("connection.mysql.username");
        CONNECTION_MYSQL_PASSWORD = getConfig().getString("connection.mysql.password");
        CONNECTION_MYSQL_OPTIONS = getConfig().getString("connection.mysql.options");

        CONNECTION_SQLITE_FILENAME = getConfig().getString("connection.sqlite.fileName");

        CONNECTION_MONGODB_CLUSTER = getConfig().getString("connection.mongodb.cluster");
        CONNECTION_MONGODB_DATABASE = getConfig().getString("connection.mongodb.database");
        CONNECTION_MONGODB_USERNAME = getConfig().getString("connection.mongodb.username");
        CONNECTION_MONGODB_PASSWORD = getConfig().getString("connection.mongodb.password");

        HOOK_TYPE = getConfig().getString("hook.type");
        HOOK_BUNGEECORD = getConfig().getBoolean("hook.bungeeCord");
        HOOK_REGISTER_AFTER_AUTHENTICATION = getConfig().getBoolean("hook.registerAfterAuthentication");

        QRCODE_NAME = getConfig().getString("qrcode.name");

        SETTINGS_SEND_PLAYER_TO = getConfig().getString("settings.sendPlayerTo");

        SETTINGS_SESSION_ENABLE = getConfig().getBoolean("settings.session.enable");
        SETTINGS_SESSION_TIMEOUT = getConfig().getLong("settings.session.timeout");

        for (String tempMessage : getConfig().getStringList("settings.restrictions.allowedCommands")) {
            SETTINGS_RESTRICTIONS_ALLOWED_COMMANDS.add(instance.color(tempMessage));
        }

        SETTINGS_RESTRICTIONS_ALLOW_MOVEMENT = getConfig().getBoolean("settings.restrictions.allowMovement");
        SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS = getConfig().getInt("settings.restrictions.allowedMovementRadius");
        SETTINGS_RESTRICTIONS_TIMEOUT = getConfig().getInt("settings.restrictions.timeout");
        SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN = getConfig().getBoolean("settings.restrictions.teleportUnAuthedToSpawn");
        SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION = getConfig().getBoolean("settings.restrictions.forceSingleSession");
        SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE = getConfig().getBoolean("settings.restrictions.kickOnWrong2FAcode");

        SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_SMTP = getConfig().getString("settings.recovery.email.authentication.smtp");
        SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_PORT = getConfig().getInt("settings.recovery.email.authentication.port");
        SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_ADDRESS = getConfig().getString("settings.recovery.email.authentication.address");
        SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_PASSWORD = getConfig().getString("settings.recovery.email.authentication.password");

        SETTINGS_TITLE_ANNOUNCEMENT_TITLE_ENABLE = getConfig().getBoolean("settings.announcement.title.enable");
        SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEIN = getConfig().getInt("settings.announcement.title.fadeIn");
        SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEOUT = getConfig().getInt("settings.announcement.title.fadeOut");
        SETTINGS_TITLE_ANNOUNCEMENT_TITLE_USE_IN_REGISTER = getConfig().getBoolean("settings.announcement.title.useInRegister");
        SETTINGS_TITLE_ANNOUNCEMENT_TITLE_USE_IN_LOGIN = getConfig().getBoolean("settings.announcement.title.useInLogin");

        SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_ENABLE = getConfig().getBoolean("settings.announcement.subtitle.enable");
        SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEIN = getConfig().getInt("settings.announcement.subtitle.fadeIn");
        SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEOUT = getConfig().getInt("settings.announcement.subtitle.fadeOut");
        SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_USE_IN_REGISTER = getConfig().getBoolean("settings.announcement.subtitle.useInRegister");
        SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_USE_IN_LOGIN = getConfig().getBoolean("settings.announcement.subtitle.useInLogin");

        SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_ENABLE = getConfig().getBoolean("settings.announcement.actionbar.enable");
        SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_USE_IN_REGISTER = getConfig().getBoolean("settings.announcement.actionbar.useInRegister");
        SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_USE_IN_LOGIN = getConfig().getBoolean("settings.announcement.actionbar.useInLogin");

        SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_TITLE = instance.color(getConfig().getString("settings.announcement.register.title"));
        SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_SUBTITLE = instance.color(getConfig().getString("settings.announcement.register.subtitle"));
        SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_ACTIONBAR = instance.color(getConfig().getString("settings.announcement.register.actionbar"));

        SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_TITLE = instance.color(getConfig().getString("settings.announcement.login.title"));
        SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_SUBTITLE = instance.color(getConfig().getString("settings.announcement.login.subtitle"));
        SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_ACTIONBAR = instance.color(getConfig().getString("settings.announcement.login.actionbar"));
    }


    public File getFile() {
        return new File(instance.getDataFolder() + "/config.yml");
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
