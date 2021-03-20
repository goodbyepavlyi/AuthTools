package pavlyi.authtools.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pavlyi.authtools.AuthTools;

public class ConfigHandler {
	private AuthTools instance = AuthTools.getInstance();
	public File f;
	public FileConfiguration c;
	
	
	public String CONNECTION_TYPE;

	public String CONNECTION_YAML_FILENAME;
	
	public String CONNECTION_MYSQL_HOSTNAME;
	public Integer CONNECTION_MYSQL_PORT;
	public String CONNECTION_MYSQL_DATABASE;
	public String CONNECTION_MYSQL_USERNAME;
	public String CONNECTION_MYSQL_PASSWORD;

	public String CONNECTION_SQLITE_FILENAME;

	public boolean SETTINGS_HOOK_INTO_AUTHME;
	public String SETTINGS_SERVER_NAME;

	public List<String> SETTINGS_RESTRICTIONS_ALLOWED_COMMANDS = new ArrayList<String>();
	public boolean SETTINGS_RESTRICTIONS_ALLOW_MOVEMENT;
	public int SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS;
	public int SETTINGS_RESTRICTIONS_TIMEOUT;
	public boolean SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN;
	public boolean SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION;
	public boolean SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE;


	public void createConfig() {
		f = new File(instance.getDataFolder()+"/config.yml");
		c = new YamlConfiguration();

		if (!f.exists()) {
			instance.saveResource("config.yml", false);
			instance.log("&r  &aSuccess: &cConfig &fhas been created!");
		}

		loadConfig();
	}

	public void saveConfig() {
		try {
			c.save(f);
		} catch (IOException ex) {
			instance.log("&r  &cError: &fWhile saving &cconfig &fan error ocurred!");
			ex.printStackTrace();
			instance.getPluginManager().disablePlugin(instance);
		}
	}

	public void loadConfig() {
		try {
			getConfig().load(f);
			instance.log("&r  &aSuccess: &cConfig &fhas been loaded!");
		} catch (IOException | org.bukkit.configuration.InvalidConfigurationException ex) {
			instance.log("&r  &cError: &fWhile loading &cconfig &fan error ocurred!");
			ex.printStackTrace();
			instance.getPluginManager().disablePlugin(instance);
		}
		

		CONNECTION_TYPE = getConfig().getString("connection.type");

		CONNECTION_YAML_FILENAME = getConfig().getString("connection.yaml.fileName");

		CONNECTION_MYSQL_HOSTNAME = getConfig().getString("connection.mysql.hostname");
		CONNECTION_MYSQL_PORT = getConfig().getInt("connection.mysql.port");
		CONNECTION_MYSQL_DATABASE = getConfig().getString("connection.mysql.database");
		CONNECTION_MYSQL_USERNAME = getConfig().getString("connection.mysql.username");
		CONNECTION_MYSQL_PASSWORD = getConfig().getString("connection.mysql.password");

		CONNECTION_SQLITE_FILENAME = getConfig().getString("connection.sqlite.fileName");

		SETTINGS_HOOK_INTO_AUTHME = getConfig().getBoolean("settings.hookIntoAuthMe");
		SETTINGS_SERVER_NAME = getConfig().getString("settings.serverName");

		for (String tempMessage : getConfig().getStringList("settings.restrictions.allowedCommands")) {
			SETTINGS_RESTRICTIONS_ALLOWED_COMMANDS.add(instance.color(tempMessage));
		}

		SETTINGS_RESTRICTIONS_ALLOW_MOVEMENT = getConfig().getBoolean("settings.restrictions.allowMovement");
		SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS = getConfig().getInt("settings.restrictions.allowedMovementRadius");
		SETTINGS_RESTRICTIONS_TIMEOUT = getConfig().getInt("settings.restrictions.timeout");
		SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN = getConfig().getBoolean("settings.restrictions.teleportUnAuthedToSpawn");
		SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION = getConfig().getBoolean("settings.restrictions.forceSingleSession");
		SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE = getConfig().getBoolean("settings.restrictions.kickOnWrong2FAcode");
	}










	public File getFile() {
		return f;
	}

	public FileConfiguration getConfig() {
		return c;
	}
}
