package pavlyi.authtools.bungee;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigHandler {
	private AuthToolsBungee instance = AuthToolsBungee.getInstance();

	private File file;
	private Configuration config;
	private final ArrayList<String> configContent = new ArrayList<>();

	public List<String> AUTH_SERVERS;
	public boolean COMMANDS_REQUIRES_AUTH;
	public List<String> WHITELISTED_COMMANDS;
	public boolean SERVER_SWITCH_REQUIRES_AUTH;
	public String SERVER_SWITCH_REQUIRES_AUTH_KICK_MESSAGE;

	public void create() {
		if (!instance.getDataFolder().exists())
			instance.getDataFolder().mkdir();

        file = new File(instance.getDataFolder(), "config.yml");

		try {
			if (!getFile().exists()) {
				InputStream inputStream = instance.getResourceAsStream("bungee/config.yml");
				OutputStream outStream = new FileOutputStream(getFile());
				byte[] buffer = new byte[inputStream.available()];

				inputStream.read(buffer);
				outStream.write(buffer);
			}

			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(instance.getDataFolder(), "config.yml"));
		} catch (IOException exception) {
			instance.log("&r  &cError: &fWhile creating &cconfig &fan error ocurred!");
			exception.printStackTrace();

			instance.onDisable();
		}

        configContent.add("authServers");
        configContent.add("commandsRequireAuth");
        configContent.add("whitelistedCommands");
        configContent.add("serverSwitchRequiresAuth");
        configContent.add("serverSwitchRequiresAuthKickMessage");
	}

	public String check() {
		for (String path : configContent) {
			if (getConfig().get(path) == null) {
				return path;
			}
		}

		return null;
	}

	public void load() {
		try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(instance.getDataFolder(), "config.yml"));
            instance.log("&r  &aSuccess: &cConfig &fhas been loaded!");
        } catch (IOException exception) {
        	instance.log("&r  &cError: &fWhile saving &cconfig &fan error ocurred!");
			exception.printStackTrace();
			instance.onDisable();
        }

		AUTH_SERVERS = new ArrayList<>();
		AUTH_SERVERS.addAll(getConfig().getStringList("authServers"));

		COMMANDS_REQUIRES_AUTH = getConfig().getBoolean("commandsRequireAuth");

		WHITELISTED_COMMANDS = new ArrayList<>();
		WHITELISTED_COMMANDS.addAll(getConfig().getStringList("whitelistedCommands"));

		SERVER_SWITCH_REQUIRES_AUTH = getConfig().getBoolean("serverSwitchRequiresAuth");
		SERVER_SWITCH_REQUIRES_AUTH_KICK_MESSAGE = getConfig().getString("serverSwitchRequiresAuthKickMessage");
	}

	public File getFile() {
        return file;
    }

	public Configuration getConfig() {
        return config;
    }
}
