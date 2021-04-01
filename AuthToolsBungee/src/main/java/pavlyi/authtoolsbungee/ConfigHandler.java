package pavlyi.authtoolsbungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigHandler {
	private AuthToolsBungee instance = AuthToolsBungee.getInstance();

	private File f;
	private Configuration c;

	public List<String> AUTH_SERVERS;
	public boolean COMMANDS_REQUIRES_AUTH;
	public List<String> WHITELISTED_COMMANDS;
	public boolean SERVER_SWITCH_REQUIRES_AUTH;
	public String SERVER_SWITCH_REQUIRES_AUTH_KICK_MESSAGE;

	public void createConfig() {
		if (!instance.getDataFolder().exists())
			instance.getDataFolder().mkdir();

        f = new File(instance.getDataFolder(), "config.yml");

        if (!f.exists()) {
            try (InputStream in = instance.getResourceAsStream("config.yml")) {
                Files.copy(in, f.toPath());
                instance.log("&r  &aSuccess: &cConfig &fhas been created!");
            } catch (IOException ex) {
            	instance.log("&r  &cError: &fWhile saving &cconfig &fan error ocurred!");
    			ex.printStackTrace();
    			instance.getPluginManager().getPlugin(instance.getDescription().getName()).onDisable();
    		}
        }

        loadConfig();
	}

	public void loadConfig() {
		try {
            c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(instance.getDataFolder(), "config.yml"));
            instance.log("&r  &aSuccess: &cConfig &fhas been loaded!");
        } catch (IOException ex) {
        	instance.log("&r  &cError: &fWhile saving &cconfig &fan error ocurred!");
			ex.printStackTrace();
			instance.getPluginManager().getPlugin(instance.getDescription().getName()).onDisable();
        }

		AUTH_SERVERS = new ArrayList<String>();
		for (String authServers : getConfig().getStringList("authServers")) {
			AUTH_SERVERS.add(authServers);
		}

		COMMANDS_REQUIRES_AUTH = getConfig().getBoolean("commandsRequireAuth");

		WHITELISTED_COMMANDS = new ArrayList<String>();
		for (String authServers : getConfig().getStringList("whitelistedCommands")) {
			WHITELISTED_COMMANDS.add(authServers);
		}

		SERVER_SWITCH_REQUIRES_AUTH = getConfig().getBoolean("serverSwitchRequiresAuth");
		SERVER_SWITCH_REQUIRES_AUTH_KICK_MESSAGE = getConfig().getString("serverSwitchRequiresAuthKickMessage");
	}

	public File getFile() {
        return f;
    }

	public Configuration getConfig() {
        return c;
    }
}
