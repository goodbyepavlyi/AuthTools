package pavlyi.authtools.spigot.authentication;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.storages.Variables;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class User {
    private final AuthTools instance = AuthTools.getInstance();

    private final Player player;
    private final UUID uuid;
    private final Setup settingUpEmail;
    private Session session;
    private boolean needsToBeAuthenticated;
    private boolean settingUp2FA;
    private Location spawnLocation;
    private PlayerInventory playerInventory;


    public User(UUID uuid) {
        this.player = instance.getServer().getPlayer(uuid);
        this.uuid = this.player.getUniqueId();
        this.settingUpEmail = new Setup();
    }

    // -------------------------------------------------
    // Querying for player in database
    // -------------------------------------------------
    public void create() {
        if (exists())
            return;

        switch (Variables.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().createSection(String.valueOf(uuid));
                instance.getYamlConnection().getConfig().set(uuid + ".ip", null);
                instance.getYamlConnection().getConfig().set(uuid + ".email", null);
                instance.getYamlConnection().getConfig().set(uuid + ".2fa", null);
                instance.getYamlConnection().getConfig().set(uuid + ".2faSecret", null);
                instance.getYamlConnection().getConfig().set(uuid + ".recoveryCode", null);

                instance.getYamlConnection().save();

                break;

            case MYSQL:
                instance.getMySQL().update("INSERT INTO authtools (uuid) VALUES ('" + uuid + "');");

                break;

            case SQLITE:
                instance.getSQLite().update("INSERT INTO authtools (uuid) VALUES ('" + uuid + "');");

                break;

            case MONGODB:
                Document document = new Document("uuid", uuid);

                document.append("email", null);
                document.append("ip", null);
                document.append("2fa", false);
                document.append("2faSecret", null);
                document.append("recoveryCode", null);

                instance.getMongoDB().getCollection().insertOne(document);

                break;
        }

    }

    public boolean exists() {
        switch (Variables.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(String.valueOf(uuid)) != null)
                    return true;

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE c='" + uuid + "'");

                    if (rs.next())
                        return true;

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return true;

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case MONGODB:
                return (instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first()) != null;
        }

        return false;
    }

    // -------------------------------------------------
    // Getting values stored in the database for players
    // -------------------------------------------------
    public String getEmail() {
        switch (Variables.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(uuid + ".email") != null)
                    return instance.getYamlConnection().getConfig().getString(uuid + ".email");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getString("email");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getString("email");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    return found.getString("email");

                break;
        }

        return null;
    }

    // -------------------------------------------------
    // Storing values in the database for players
    // -------------------------------------------------
    public void setEmail(String value) {
        switch (Variables.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(uuid + ".email", value);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!exists()) {
                    instance.getMySQL().update("INSERT INTO authtools (uuid, email) VALUES ('" + uuid + "', '" + value + "');");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET email='" + value + "' WHERE uuid='" + uuid + "';");

                break;

            case SQLITE:
                if (!exists()) {
                    instance.getSQLite().update("INSERT INTO authtools (uuid, email) VALUES ('" + uuid + "', '" + value + "');");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET email='" + value + "' uuid='" + uuid + "';");

                break;

            case MONGODB:
                create();

                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    instance.getMongoDB().getCollection().updateOne(found, new Document("$set", new Document("email", value)));

                break;
        }
    }

    public boolean get2FA() {
        switch (Variables.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(uuid + ".2fa") != null)
                    return instance.getYamlConnection().getConfig().getBoolean(uuid + ".2fa");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getBoolean("2fa");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getBoolean("tfa");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    return found.getBoolean("2fa");

                break;
        }

        return false;
    }

    public void set2FA(boolean value) {
        switch (Variables.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(uuid + ".2fa", value);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!exists()) {
                    instance.getMySQL().update("INSERT INTO authtools (uuid, 2fa) VALUES ('" + uuid + "', " + value + ");");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET 2fa=" + value + " WHERE uuid='" + uuid + "';");

                break;

            case SQLITE:
                if (!exists()) {
                    instance.getSQLite().update("INSERT INTO authtools (uuid, tfa) VALUES ('" + uuid + "', " + ((value) ? 1 : 0) + ");");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET tfa=" + ((value) ? 1 : 0) + " WHERE uuid='" + uuid + "';");

                break;

            case MONGODB:
                create();

                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    instance.getMongoDB().getCollection().updateOne(found, new Document("$set", new Document("2fa", value)));

                break;
        }
    }

    public String get2FAsecret() {
        switch (Variables.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(uuid + ".2faSecret") != null)
                    return instance.getYamlConnection().getConfig().getString(uuid + ".2faSecret");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getString("2faSecret");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getString("tfaSecret");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    return found.getString("2faSecret");
        }

        return null;
    }

    public void set2FAsecret(String value) {
        switch (Variables.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(uuid + ".2faSecret", value);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!exists()) {
                    instance.getMySQL().update("INSERT INTO authtools (uuid, 2faSecret) VALUES ('" + uuid + "', '" + value + "');");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET 2faSecret='" + value + "' WHERE uuid='" + uuid + "';");

                break;

            case SQLITE:
                if (!exists()) {
                    instance.getSQLite().update("INSERT INTO authtools (uuid, tfaSecret) VALUES ('" + uuid + "', '" + value + "');");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET tfaSecret='" + value + "' WHERE uuid='" + uuid + "';");

                break;

            case MONGODB:
                create();

                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    instance.getMongoDB().getCollection().updateOne(found, new Document("$set", new Document("2faSecret", value)));

                break;
        }
    }

    public int getRecoveryCode() {
        switch (Variables.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(uuid + ".recoveryCode") != null)
                    return instance.getYamlConnection().getConfig().getInt(uuid + ".recoveryCode");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getInt("recoveryCode");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getInt("recoveryCode");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    return found.getInteger("recoveryCode");
        }

        return 0;
    }

    public void setRecoveryCode(boolean value) {
        StringBuilder stringBuilder = new StringBuilder();
        int recoveryCode = 0;
        if (!value) {
            for (int i = 1; i <= 8; i++)
                stringBuilder.append(new Random().nextInt(9));

            recoveryCode = Integer.parseInt(stringBuilder.toString());
        }

        switch (Variables.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(uuid + ".recoveryCode", recoveryCode);

                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!exists()) {
                    instance.getMySQL().update("INSERT INTO authtools (uuid, recoveryCode) VALUES ('" + uuid + "''" + recoveryCode + "');");

                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET recoveryCode=" + null + " WHERE uuid='" + uuid + "';");

                break;

            case SQLITE:
                if (!exists()) {
                    instance.getSQLite().update("INSERT INTO authtools (uuid, recoveryCode) VALUES ('" + uuid + "''" + recoveryCode + "');");

                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET recoveryCode=" + null + " WHERE uuid='" + uuid + "';");

                break;

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    instance.getMongoDB().getCollection().updateOne(found, new Document("$set", new Document("recoveryCode", recoveryCode)));

                break;
        }
    }

    public String getIP() {
        switch (Variables.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(uuid + ".ip") != null)
                    return instance.getYamlConnection().getConfig().getString(uuid + ".ip");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getString("ip");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE uuid='" + uuid + "'");

                    if (rs.next())
                        return rs.getString("ip");

                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                break;

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    return found.getString("ip");
        }

        return null;
    }

    public void setIP(InetSocketAddress value) {
        String hostName = value.getHostName();

        switch (Variables.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(uuid + ".ip", hostName);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!exists()) {
                    instance.getMySQL().update("INSERT INTO authtools (uuid, ip) VALUES ('" + uuid + "', '" + hostName + "');");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET ip='" + hostName + "' WHERE uuid='" + uuid + "';");

                break;

            case SQLITE:
                if (!exists()) {
                    instance.getSQLite().update("INSERT INTO authtools (uuid, ip) VALUES ('" + uuid + "', '" + hostName + "');");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET ip='" + hostName + "' WHERE uuid='" + uuid + "';");

                break;

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("uuid", uuid)).first();

                if (found != null)
                    instance.getMongoDB().getCollection().updateOne(found, new Document("$set", new Document("ip", hostName)));

                break;
        }

    }

    public void needsToBeAuthenticated(boolean value) {
        this.needsToBeAuthenticated = value;
    }

    // -------------------------------------------------
    // Storing local values
    // -------------------------------------------------
    public boolean isSettingUp2FA() {
        return this.settingUp2FA;
    }

    public void setSettingUp2FA(boolean value) {
        this.settingUp2FA = value;
    }

    public Setup getSettingUpEmail() {
        return this.settingUpEmail;
    }

    public boolean hasToBeAuthenticated() {
        return needsToBeAuthenticated;
    }

    public Session getSession() {
        return session;
    }

    // -------------------------------------------------
    // Getting local values
    // -------------------------------------------------
    public void setSession(long endsAt) {
        if (endsAt == 0) {
            session = null;
            return;
        }

        session = new Session(player.getAddress(), endsAt);
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location value) {
        this.spawnLocation = value;
    }

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    public void setPlayerInventory(PlayerInventory value) {
        this.playerInventory = value;
    }
}
