package pavlyi.authtools.connections;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pavlyi.authtools.AuthTools;

public class YAMLConnection {
	private AuthTools instance = AuthTools.getInstance();
	public File f;
	public FileConfiguration c;


	public void createPlayerData(boolean silently) {
		String fileName = "playerData.yml";

		if (instance.getConfigHandler().CONNECTION_YAML_FILENAME != null && instance.getConfigHandler().CONNECTION_YAML_FILENAME.endsWith(".yml"))
			fileName = instance.getConfigHandler().CONNECTION_YAML_FILENAME;

		f = new File(instance.getDataFolder()+"/"+fileName);
		c = new YamlConfiguration();

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException ex) {
				instance.log("&r  &cError: &fWhile creating &cPlayerData &ffile an error ocurred!");
				ex.printStackTrace();

				instance.getPluginManager().disablePlugin(instance);
			}

			if (!silently)
				instance.log("&r  &aSuccess: &cPlayerData &ffile has been created!");
		}

		loadPlayerData(silently);
	}

	public void deletePlayerData(boolean silently) {
		String fileName = "playerData.yml";

		if (instance.getConfigHandler().CONNECTION_YAML_FILENAME != null && instance.getConfigHandler().CONNECTION_YAML_FILENAME.endsWith(".yml"))
			fileName = instance.getConfigHandler().CONNECTION_YAML_FILENAME;

		f = new File(instance.getDataFolder()+"/"+fileName);
		c = new YamlConfiguration();

		if (f.exists()) {
			f.delete();

			if (!silently)
				instance.log("&r  &aSuccess: &cPlayerData &ffile has been deleted!");
		}
	}

	public void savePlayerData() {
		try {
			c.save(f);
		} catch (IOException ex) {
			instance.log("&r  &cError: &fWhile saving &cPlayerData &ffile an error ocurred!");
			ex.printStackTrace();
			instance.getPluginManager().disablePlugin(instance);
		}
	}

	public void loadPlayerData(boolean silently) {
		try {
			getPlayerData().load(f);

			if (!silently)
				instance.log("&r  &aSuccess: &cPlayerData &ffile has been loaded!");
		} catch (IOException | org.bukkit.configuration.InvalidConfigurationException ex) {
			instance.log("&r  &cError: &fWhile loading &cPlayerData &ffile an error ocurred!");
			ex.printStackTrace();
			instance.getPluginManager().disablePlugin(instance);
		}
	}	










	public File getFile() {
		return f;
	}

	public FileConfiguration getPlayerData() {
		return c;
	}

}
