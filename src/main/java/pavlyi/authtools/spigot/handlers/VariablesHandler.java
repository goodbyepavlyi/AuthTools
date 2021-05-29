package pavlyi.authtools.spigot.handlers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.PlayerInventory;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.enums.ConnectionType;
import pavlyi.authtools.spigot.enums.HookType;
import pavlyi.authtools.spigot.enums.VersionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class VariablesHandler {
    private static final AuthTools instance = AuthTools.getInstance();

    // Values
    private static final ArrayList<String> authLocked = new ArrayList<>();

    private static final HashMap<UUID, Location> playerSpawnLocations = new HashMap<>();
    private static final HashMap<UUID, PlayerInventory> playerInventories = new HashMap<>();

    private static ConnectionType connectionType;
    private static HookType hookType;
    private static final VersionType versionType = VersionType.getVersion();

    // Getting values
    public static ArrayList<String> getAuthLocked() {
        return authLocked;
    }

    public static HashMap<UUID, Location> getPlayerSpawnLocations() {
        return playerSpawnLocations;
    }

    public static HashMap<UUID, PlayerInventory> getPlayerInventories() {
        return playerInventories;
    }

    public static ConnectionType getConnectionType() {
        return connectionType;
    }

    public static void setConnectionType(ConnectionType value) {
        connectionType = value;
    }

    // Setting values
    public static void createSpawn(String spawn, Location location) {
        try {
            instance.getSpawnHandler().getConfig().set(spawn + ".world", location.getWorld().getName());
            instance.getSpawnHandler().getConfig().set(spawn + ".x", location.getX());
            instance.getSpawnHandler().getConfig().set(spawn + ".y", location.getY());
            instance.getSpawnHandler().getConfig().set(spawn + ".z", location.getZ());
            instance.getSpawnHandler().getConfig().set(spawn + ".yaw", location.getYaw());
            instance.getSpawnHandler().getConfig().set(spawn + ".pitch", location.getPitch());
            instance.getSpawnHandler().save();
        } catch (Exception ex) {
            instance.log("&r  &cError: &fWhile creating &cspawn &fan error ocurred!");
            ex.printStackTrace();
        }
    }

    public static HookType getHookType() {
        return hookType;
    }

    public static void setHookType(HookType value) {
        hookType = value;
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
        } catch (NullPointerException ex) {
            instance.log("&r  &cError: &fWhile getting &cspawn &fan error ocurred!");
            ex.printStackTrace();
        }

        return null;
    }

    public static VersionType getVersion() {
        return versionType;
    }
}
