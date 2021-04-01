package pavlyi.authtools.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pavlyi.authtools.AuthTools;

public class SpawnHandler {
	private AuthTools instance = AuthTools.getInstance();
	public File f;
	public FileConfiguration c;


	public void createSpawnFile() {
		f = new File(instance.getDataFolder()+"/spawn.yml");
		c = new YamlConfiguration();

		if (!f.exists()) {
			try {
				f.createNewFile();
				instance.log("&r  &aSuccess: &cSpawn file &fhas been created!");
			} catch (IOException ex) {
				instance.log("&r  &cError: &fWhile saving &cspawn file &fan error ocurred!");
				ex.printStackTrace();
				instance.getPluginManager().disablePlugin(instance);
			}
		}

		loadSpawnFile();
	}

	public void saveSpawnFile() {
		try {
			c.save(f);
		} catch (IOException ex) {
			instance.log("&r  &cError: &fWhile saving &cspawn file &fan error ocurred!");
			ex.printStackTrace();
			instance.getPluginManager().disablePlugin(instance);
		}
	}

	public void loadSpawnFile() {
		try {
			getConfig().load(f);
			instance.log("&r  &aSuccess: &cSpawn file &fhas been loaded!");
		} catch (IOException | org.bukkit.configuration.InvalidConfigurationException ex) {
			instance.log("&r  &cError: &fWhile loading &cspawn file &fan error ocurred!");
			ex.printStackTrace();
			instance.getPluginManager().disablePlugin(instance);
		}
	}

	
	public void createSpawn(String spawn, Location location) {
		try {
			getConfig().set("spawns."+spawn+".world", location.getWorld().getName());
			getConfig().set("spawns."+spawn+".x", location.getX());
			getConfig().set("spawns."+spawn+".y", (location.getY()+1));
			getConfig().set("spawns."+spawn+".z", location.getZ());
			getConfig().set("spawns."+spawn+".yaw", location.getYaw());
			getConfig().set("spawns."+spawn+".pitch", location.getPitch());
			saveSpawnFile();	
		} catch (Exception ex) {
			instance.log("&r  &cError: &fWhile creating &cspawn &fan error ocurred!");
			ex.printStackTrace();
		}
	}

	public Location getSpawn(String spawn) {
		try {
			if (getConfig().get("spawns."+spawn) != null)
				return new Location(instance.getServer().getWorld(c.getString("spawns."+spawn+".world")), c.getInt("spawns."+spawn+".x"), c.getInt("spawns."+spawn+".y"), c.getInt("spawns."+spawn+".z"), c.getInt("spawns."+spawn+".yaw"), c.getInt("spawns."+spawn+".pitch"));
		} catch (NullPointerException ex) {
			instance.log("&r  &cError: &fWhile getting &cspawn &fan error ocurred!");
			ex.printStackTrace();
		}

		return null;
	}







	public File getFile() {
		return f;
	}

	public FileConfiguration getConfig() {
		return c;
	}
}
