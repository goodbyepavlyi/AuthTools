package pavlyi.authtools.spigot.handlers;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.entity.Player;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.enums.ConnectionType;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class User {
    private final AuthTools instance = AuthTools.getInstance();

    private final Player player;
    private final String name;

    public User(String player) {
        this.player = instance.getServer().getPlayer(player);
        this.name = player;
    }

    // Creating player in databases
    public void create() {
        if (VariablesHandler.getConnectionType().equals(ConnectionType.MONGODB)) {
            if (!isInDatabase()) {
                Document document = new Document("name", name);

                document.append("uuid", null);
                document.append("email", null);
                document.append("ip", null);
                document.append("2fa", false);
                document.append("2faSecret", null);
                document.append("2faRecoveryCode", null);
                document.append("2faSettingUp", false);

                instance.getMongoDB().getCollection().insertOne(document);
            }
        }
    }

    // Checking if the player is in the database
    public boolean isInDatabase() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(name) != null)
                    return true;

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='" + getName() + "'");

                    if (rs.next())
                        return true;

                    rs.close();
                    return false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='" + getName() + "'");

                    if (rs.next())
                        return true;

                    rs.close();
                    return false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                return found != null;
        }

        return false;
    }

    // Settings values in the database for player
    public void setUUID() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(name + ".uuid", player.getUniqueId().toString());
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!isInDatabase()) {
                    instance.getMySQL().update("INSERT INTO authtools (name, uuid) VALUES ('" + name + "', '" + player.getUniqueId().toString() + "');");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET uuid='" + player.getUniqueId().toString() + "' WHERE name='" + name + "';");

                break;

            case SQLITE:
                if (!isInDatabase()) {
                    instance.getSQLite().update("INSERT INTO authtools (name, uuid) VALUES ('" + name + "', '" + player.getUniqueId().toString() + "');");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET uuid='" + player.getUniqueId().toString() + "' WHERE name='" + name + "';");

                break;

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    Bson updatedValue = new Document("uuid", player.getUniqueId().toString());
                    Bson updateOperation = new Document("$set", updatedValue);

                    instance.getMongoDB().getCollection().updateOne(found, updateOperation);
                }

                break;
        }
    }

    // Getting values stored in the database for player
    public String getEmail() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(name + ".email") != null)
                    return instance.getYamlConnection().getConfig().getString(name + ".email");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("email");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("email");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    return found.getString("email");
                }

                return null;
        }

        return null;
    }

    public void setEmail(String email) {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(name + ".email", email);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!isInDatabase()) {
                    instance.getMySQL().update("INSERT INTO authtools (name, email) VALUES ('" + name + "', '" + email + "');");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET email='" + email + "' WHERE name='" + name + "';");

                break;

            case SQLITE:
                if (!isInDatabase()) {
                    instance.getSQLite().update("INSERT INTO authtools (name, email) VALUES ('" + name + "', '" + email + "');");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET email='" + email + "' WHERE name='" + name + "';");

                break;

            case MONGODB:
                create();

                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    Bson updatedValue = new Document("email", email);
                    Bson updateOperation = new Document("$set", updatedValue);

                    instance.getMongoDB().getCollection().updateOne(found, updateOperation);
                }

                break;
        }
    }

    public boolean get2FA() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(name + ".2fa") != null)
                    return instance.getYamlConnection().getConfig().getBoolean(name + ".2fa");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getBoolean("2fa");

                    rs.close();
                    return false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getBoolean("tfa");

                    rs.close();
                    return false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    return found.getBoolean("2fa");
                }

                return false;
        }

        return false;
    }

    public void set2FA(boolean TFA) {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(name + ".2fa", TFA);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!isInDatabase()) {
                    instance.getMySQL().update("INSERT INTO authtools (name, 2fa) VALUES ('" + name + "', " + TFA + ");");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET 2fa=" + TFA + " WHERE name='" + name + "';");

                break;

            case SQLITE:
                if (!isInDatabase()) {
                    if (TFA) {
                        instance.getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('" + name + "', " + 1 + ");");
                        break;
                    }

                    instance.getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('" + name + "', " + 0 + ");");

                    break;
                }

                if (TFA) {
                    instance.getSQLite().update("UPDATE authtools SET tfa=" + 1 + " WHERE name='" + name + "';");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET tfa=" + 0 + " WHERE name='" + name + "';");

                break;

            case MONGODB:
                create();

                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    Bson updatedValue = new Document("2fa", TFA);
                    Bson updateOperation = new Document("$set", updatedValue);

                    instance.getMongoDB().getCollection().updateOne(found, updateOperation);
                }

                break;
        }
    }

    public String get2FAsecret() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(name + ".2faSecret") != null)
                    return instance.getYamlConnection().getConfig().getString(name + ".2faSecret");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("2faSecret");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("tfaSecret");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    return found.getString("2faSecret");
                }

                return null;
        }

        return null;
    }

    public void set2FAsecret(String TFAsecret) {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(name + ".2faSecret", TFAsecret);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!isInDatabase()) {
                    instance.getMySQL().update("INSERT INTO authtools (name, 2faSecret) VALUES ('" + name + "', '" + TFAsecret + "');");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET 2faSecret='" + TFAsecret + "' WHERE name='" + name + "';");


                break;

            case SQLITE:
                if (!isInDatabase()) {
                    instance.getSQLite().update("INSERT INTO authtools (name, tfaSecret) VALUES ('" + name + "', '" + TFAsecret + "');");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET tfaSecret='" + TFAsecret + "' WHERE name='" + name + "';");

                break;

            case MONGODB:
                create();

                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    Bson updatedValue = new Document("2faSecret", TFAsecret);
                    Bson updateOperation = new Document("$set", updatedValue);

                    instance.getMongoDB().getCollection().updateOne(found, updateOperation);
                }

                break;
        }
    }

    public int getRecoveryCode() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(name + ".recoveryCode") != null)
                    return instance.getYamlConnection().getConfig().getInt(name + ".recoveryCode");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getInt("2faRecoveryCode");

                    rs.close();
                    return 0;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return 0;
                }

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getInt("tfaRecoveryCode");

                    rs.close();
                    return 0;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return 0;
                }

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    try {
                        return Integer.parseInt(found.getString("2faRecoveryCode"));
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                }

                return 0;
        }

        return 0;
    }

    public void setRecoveryCode(boolean clearRecoveryCode) {
        StringBuilder recoveryCode = new StringBuilder();
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (!clearRecoveryCode) {

                    for (int i = 1; i <= 8; i++) {
                        recoveryCode.append(new Random().nextInt(9));
                    }

                    instance.getYamlConnection().getConfig().set(name + ".recoveryCode", Integer.parseInt(recoveryCode.toString()));
                    instance.getYamlConnection().save();

                    break;
                }

                instance.getYamlConnection().getConfig().set(name + ".recoveryCode", null);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!isInDatabase()) {
                    if (!clearRecoveryCode) {

                        for (int i = 1; i <= 8; i++) {
                            recoveryCode.append(new Random().nextInt(9));
                        }

                        instance.getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('" + name + "''" + Integer.parseInt(recoveryCode.toString()) + "');");

                        break;
                    }

                    instance.getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('" + name + "','" + null + "');");

                    break;
                }

                if (!clearRecoveryCode) {

                    for (int i = 1; i <= 8; i++) {
                        recoveryCode.append(new Random().nextInt(9));
                    }

                    instance.getMySQL().update("UPDATE authtools SET 2faRecoveryCode=" + Integer.parseInt(recoveryCode.toString()) + " WHERE name='" + name + "';");

                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET 2faRecoveryCode=" + null + " WHERE name='" + name + "';");

                break;

            case SQLITE:
                if (!isInDatabase()) {
                    if (!clearRecoveryCode) {

                        for (int i = 1; i <= 8; i++) {
                            recoveryCode.append(new Random().nextInt(9));
                        }

                        instance.getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('" + name + "''" + Integer.parseInt(recoveryCode.toString()) + "');");

                        break;
                    }

                    instance.getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('" + name + "','" + null + "');");


                    break;
                }

                if (!clearRecoveryCode) {

                    for (int i = 1; i <= 8; i++) {
                        recoveryCode.append(new Random().nextInt(9));
                    }

                    instance.getSQLite().update("UPDATE authtools SET tfaRecoveryCode=" + Integer.parseInt(recoveryCode.toString()) + " WHERE name='" + name + "';");

                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET tfaRecoveryCode=" + null + " WHERE name='" + name + "';");

                break;

            case MONGODB:

                if (!clearRecoveryCode) {
                    for (int i = 1; i <= 8; i++) {
                        recoveryCode.append(new Random().nextInt(9));
                    }
                }

                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    Bson updatedValue = new Document("2faRecoveryCode", Integer.parseInt(recoveryCode.toString()));
                    Bson updateOperation = new Document("$set", updatedValue);

                    instance.getMongoDB().getCollection().updateOne(found, updateOperation);
                }

                break;
        }
    }

    public boolean getSettingUp2FA() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(name + ".settingUp2FA") != null)
                    return instance.getYamlConnection().getConfig().getBoolean(name + ".settingUp2FA");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getBoolean("2faSettingUp");

                    rs.close();
                    return false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getBoolean("tfaSettingUp");

                    rs.close();
                    return false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    return found.getBoolean("2faSettingUp");
                }

                return false;
        }

        return false;
    }

    public void setSettingUp2FA(boolean settingUp2FA) {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(name + ".settingUp2FA", settingUp2FA);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!isInDatabase()) {
                    instance.getMySQL().update("INSERT INTO authtools (name, 2faSettingUp) VALUES ('" + name + "', " + settingUp2FA + ");");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET 2faSettingUp=" + settingUp2FA + " WHERE name='" + name + "';");

                break;

            case SQLITE:
                if (!isInDatabase()) {
                    if (settingUp2FA) {
                        instance.getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('" + name + "', " + 1 + ");");
                        break;
                    }

                    instance.getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('" + name + "', " + 0 + ");");

                    break;
                }

                if (settingUp2FA) {
                    instance.getSQLite().update("UPDATE authtools SET tfaSettingUp=" + 1 + " WHERE name='" + name + "';");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET tfaSettingUp=" + 0 + " WHERE name='" + name + "';");

                break;

            case MONGODB:
                create();

                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    Bson updatedValue = new Document("2faSettingUp", settingUp2FA);
                    Bson updateOperation = new Document("$set", updatedValue);

                    instance.getMongoDB().getCollection().updateOne(found, updateOperation);
                }

                break;
        }
    }

    public String getIP() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(name + ".ip") != null)
                    return instance.getYamlConnection().getConfig().getString(name + ".ip");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("ip");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("ip");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    return found.getString("ip");
                }

                return null;
        }

        return null;
    }

    public void setIP(InetSocketAddress ip) {
        String ipEdited = ip.getHostName();

        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                instance.getYamlConnection().getConfig().set(name + ".ip", ipEdited);
                instance.getYamlConnection().save();

                break;

            case MYSQL:
                if (!isInDatabase()) {
                    instance.getMySQL().update("INSERT INTO authtools (name, ip) VALUES ('" + name + "', '" + ipEdited + "');");
                    break;
                }

                instance.getMySQL().update("UPDATE authtools SET ip='" + ipEdited + "' WHERE name='" + name + "';");

                break;

            case SQLITE:
                if (!isInDatabase()) {
                    instance.getSQLite().update("INSERT INTO authtools (name, ip) VALUES ('" + name + "', '" + ipEdited + "');");
                    break;
                }

                instance.getSQLite().update("UPDATE authtools SET ip='" + ipEdited + "' WHERE name='" + name + "';");

                break;

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    Bson updatedValue = new Document("ip", ipEdited);
                    Bson updateOperation = new Document("$set", updatedValue);

                    instance.getMongoDB().getCollection().updateOne(found, updateOperation);
                }


                break;
        }
    }

    public String getUUID() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(name + ".uuid") != null)
                    return instance.getYamlConnection().getConfig().getString(name + ".uuid");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("uuid");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("uuid");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    return found.getString("uuid");
                }

                return null;
        }

        return null;
    }

    public String getName() {
        switch (VariablesHandler.getConnectionType()) {
            case YAML:
                if (instance.getYamlConnection().getConfig().get(name + ".name") != null)
                    return instance.getYamlConnection().getConfig().getString(name + ".name");

                break;

            case MYSQL:
                try {
                    ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("name");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case SQLITE:
                try {
                    ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='" + name + "'");

                    if (rs.next())
                        return rs.getString("name");

                    rs.close();
                    return null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }

            case MONGODB:
                Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

                if (found != null) {
                    return found.getString("name");
                }

                return null;
        }

        return null;
    }

    // Storing local values
    public void needsToBeAuthenticated(boolean value) {
        if (value) {
            if (VariablesHandler.getAuthLocked().contains(name))
                return;

            VariablesHandler.getAuthLocked().add(name);
            return;
        }

        if (!VariablesHandler.getAuthLocked().contains(name))
            return;

        VariablesHandler.getAuthLocked().remove(name);
    }

    // Getting local values
    public boolean hasToBeAuthenticated() {
        return VariablesHandler.getAuthLocked().contains(name);
    }
}
