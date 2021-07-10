package pavlyi.authtools.spigot.storages;

import org.bukkit.Location;
import org.bukkit.World;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.enums.ConnectionType;
import pavlyi.authtools.spigot.enums.VersionType;

import java.util.HashMap;
import java.util.UUID;

public class Variables {
    private static final AuthTools instance = AuthTools.getInstance();

    // Values
    private static final HashMap<UUID, User> users = new HashMap<>();
    private static final VersionType versionType = VersionType.getVersion();
    private static ConnectionType connectionType;

    // Getting values
    public static User getUser(UUID uuid) {
        if (!users.containsKey(uuid))
            users.put(uuid, new User(uuid));

        return users.get(uuid);
    }

    public static ConnectionType getConnectionType() {
        return connectionType;
    }

    // Setting values
    public static void setConnectionType(ConnectionType value) {
        connectionType = value;
    }

    public static Location getSpawn(String spawn) {
        try {
            if (instance.getSpawnHandler().getConfig().get(spawn) != null) {
                World world = instance.getServer().getWorld(instance.getSpawnHandler().getConfig().getString(spawn + ".world"));
                double x = instance.getSpawnHandler().getConfig().getDouble(spawn + ".x");
                double y = instance.getSpawnHandler().getConfig().getDouble(spawn + ".y");
                double z = instance.getSpawnHandler().getConfig().getDouble(spawn + ".z");
                int yaw = instance.getSpawnHandler().getConfig().getInt(spawn + ".yaw");
                int pitch = instance.getSpawnHandler().getConfig().getInt(spawn + ".pitch");

                return new Location(world, x, y, z, yaw, pitch);
            }
        } catch (NullPointerException exception) {
            instance.log("&r  &cError: &fWhile getting &cspawn &fan error ocurred!");
            exception.printStackTrace();
        }

        return null;
    }

    public static VersionType getVersion() {
        return versionType;
    }

    public static void createSpawn(String spawn, Location location) {
        try {
            instance.getSpawnHandler().getConfig().set(spawn + ".world", location.getWorld().getName());
            instance.getSpawnHandler().getConfig().set(spawn + ".x", location.getX());
            instance.getSpawnHandler().getConfig().set(spawn + ".y", location.getY());
            instance.getSpawnHandler().getConfig().set(spawn + ".z", location.getZ());
            instance.getSpawnHandler().getConfig().set(spawn + ".yaw", location.getYaw());
            instance.getSpawnHandler().getConfig().set(spawn + ".pitch", location.getPitch());
            instance.getSpawnHandler().save();
        } catch (Exception exception) {
            instance.log("&r  &cError: &fWhile creating &cspawn &fan error ocurred!");
            exception.printStackTrace();
        }
    }
}
