package pavlyi.authtools.spigot.storages;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pavlyi.authtools.spigot.AuthTools;

import java.io.File;
import java.io.IOException;

public class Spawn {
    private static final AuthTools instance = AuthTools.getInstance();
    private static final File file = new File(instance.getDataFolder() + "/spawn.yml");
    private static final FileConfiguration config = new YamlConfiguration();

    public void create() {
        if (!getFile().exists()) {
            try {
                getFile().createNewFile();
            } catch (IOException exception) {
                instance.log("&r  &cError: &fWhile creating &cspawn file &fan error ocurred!");
                exception.printStackTrace();

                instance.getServer().shutdown();
            }
        }

        load();
    }

    public void save() {
        try {
            getConfig().save(getFile());
        } catch (IOException exception) {
            instance.log("&r  &cError: &fWhile saving &cspawn file &fan error ocurred!");
            exception.printStackTrace();
        }
    }

    public void load() {
        try {
            getConfig().load(getFile());
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException exception) {
            instance.log("&r  &cError: &fWhile loading &cspawn file &fan error ocurred!");
            exception.printStackTrace();
        }
    }


    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
