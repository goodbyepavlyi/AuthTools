package pavlyi.authtools.spigot.connections;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pavlyi.authtools.spigot.AuthTools;

import java.io.File;
import java.io.IOException;

public class YAMLConnection {
    private static File file;
    private static FileConfiguration config;
    private final AuthTools instance = AuthTools.getInstance();

    public boolean connect(boolean silent) {
        String fileName = "playerData.yml";

        if (instance.getConfigHandler().CONNECTION_YAML_FILENAME != null && instance.getConfigHandler().CONNECTION_YAML_FILENAME.endsWith(".yml"))
            fileName = instance.getConfigHandler().CONNECTION_YAML_FILENAME;

        file = new File(instance.getDataFolder() + "/" + fileName);
        config = new YamlConfiguration();

        if (!getFile().exists()) {
            try {
                getFile().createNewFile();

                load();


                if (!silent) {
                    instance.log("&r  &aSuccess: &cYAML &fconnection has been successful!");
                }

                return true;
            } catch (IOException ex) {
                instance.log("&r  &cError: &fWhile connecting to &cYAML &fan error ocurred!");
                ex.printStackTrace();

                return false;
            }
        }

        return load();
    }

    public void disconnect(boolean silent) {
        try {
            getConfig().save(getFile());

            if (!silent)
                instance.log("&r  &aSuccess: &cYAML &fhas been disconnected!");
        } catch (IOException ex) {
            instance.log("&r  &cError: &fWhile disconnecting &cYAML &fan error ocurred!");
            ex.printStackTrace();
        }
    }

    public void delete(boolean silent) {
        if (getFile().exists()) {
            getFile().delete();

            if (!silent)
                instance.log("&r  &aSuccess: &cYAML &fhas been purged!");
        }
    }

    public void save() {
        try {
            getConfig().save(getFile());
        } catch (IOException ex) {
            instance.log("&r  &cError: &fWhile saving &cYAML &fan error ocurred!");
            ex.printStackTrace();

            instance.getPluginManager().disablePlugin(instance);
        }
    }

    private boolean load() {
        try {
            getConfig().load(getFile());
            return true;
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException ex) {
            instance.log("&r  &cError: &fWhile loading &cYAML &fan error ocurred!");
            ex.printStackTrace();

            return false;
        }
    }


    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }

}
