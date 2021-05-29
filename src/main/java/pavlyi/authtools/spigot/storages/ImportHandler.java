package pavlyi.authtools.spigot.storages;

import org.bson.Document;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.connections.MongoDB;
import pavlyi.authtools.spigot.connections.MySQL;
import pavlyi.authtools.spigot.connections.SQLite;
import pavlyi.authtools.spigot.connections.YAMLConnection;
import pavlyi.authtools.spigot.enums.ConnectionType;
import pavlyi.authtools.spigot.handlers.VariablesHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ImportHandler {
    private final AuthTools instance = AuthTools.getInstance();
    private final ArrayList<String> name;
    private final HashMap<String, String> uuid;
    private final HashMap<String, String> ip;
    private final HashMap<String, String> email;
    private final HashMap<String, Boolean> tfa;
    private final HashMap<String, String> tfaSecret;
    private final HashMap<String, Integer> tfaRecoveryCode;
    private final HashMap<String, Boolean> tfaSettingUp;
    private final ConnectionType connectionType;
    private final ConnectionType importFrom;


    public ImportHandler(ConnectionType importFrom) {
        this.connectionType = VariablesHandler.getConnectionType();
        this.importFrom = importFrom;

        this.name = new ArrayList<>();
        this.uuid = new HashMap<>();
        this.ip = new HashMap<>();
        this.email = new HashMap<>();
        this.tfa = new HashMap<>();
        this.tfaSecret = new HashMap<>();
        this.tfaRecoveryCode = new HashMap<>();
        this.tfaSettingUp = new HashMap<>();
    }

    public boolean importYAML() {
        if (importFrom.equals(ConnectionType.YAML))
            return false;

        if (connectionType.equals(ConnectionType.YAML)) {
            switch (importFrom) {
                case MYSQL:
                    if (!getMySQL().isConnected())
                        getMySQL().connect(true);

                    try {
                        ResultSet rs = getMySQL().getResult("SELECT * FROM authtools");

                        while (rs.next()) {
                            this.name.add(rs.getString("name"));
                        }

                        rs.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return false;
                    }

                    for (String value : name) {
                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.uuid.put(value, rs.getString("uuid"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.ip.put(value, rs.getString("ip"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.email.put(value, rs.getString("email"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfa.put(value, rs.getBoolean("2fa"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSecret.put(value, rs.getString("2faSecret"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaRecoveryCode.put(value, rs.getInt("2faRecoveryCode"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSettingUp.put(value, rs.getBoolean("2faSettingUp"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        getYAML().getConfig().set(value + ".uuid", uuid.get(value));
                        getYAML().getConfig().set(value + ".ip", ip.get(value));
                        getYAML().getConfig().set(value + ".email", email.get(value));
                        getYAML().getConfig().set(value + ".2fa", tfa.get(value));
                        getYAML().getConfig().set(value + ".2faSecret", tfaSecret.get(value));
                        getYAML().getConfig().set(value + ".2faRecoveryCode", tfaRecoveryCode.get(value));
                        getYAML().getConfig().set(value + ".2faSettingUp", tfaSettingUp.get(value));

                        getYAML().save();
                    }


                    getMySQL().disconnect(true);

                    return true;

                case SQLITE:
                    getSQLite().connect(true);

                    try {
                        ResultSet rs = getSQLite().getResult("SELECT * FROM authtools");

                        while (rs.next()) {
                            this.name.add(rs.getString("name"));
                        }

                        rs.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return false;
                    }

                    for (String value : name) {
                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.uuid.put(value, rs.getString("uuid"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.ip.put(value, rs.getString("ip"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.email.put(value, rs.getString("email"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfa.put(value, rs.getBoolean("tfa"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSecret.put(value, rs.getString("tfaSecret"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSecret.put(value, rs.getString("tfaSecret"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaRecoveryCode.put(value, rs.getInt("tfaRecoveryCode"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSettingUp.put(value, rs.getBoolean("tfaSettingUp"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        getYAML().getConfig().set(value + ".uuid", uuid.get(value));
                        getYAML().getConfig().set(value + ".ip", ip.get(value));
                        getYAML().getConfig().set(value + ".email", email.get(value));
                        getYAML().getConfig().set(value + ".2fa", tfa.get(value));
                        getYAML().getConfig().set(value + ".2faSecret", tfaSecret.get(value));
                        getYAML().getConfig().set(value + ".2faRecoveryCode", tfaRecoveryCode.get(value));
                        getYAML().getConfig().set(value + ".2faSettingUp", tfaSettingUp.get(value));

                        getYAML().save();
                    }

                    getSQLite().disconnect(true);

                    return true;

                case MONGODB:
                    getMongoDB().connect(true);

                    for (String value : name) {
                        Document found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            name.add(found.getString("name"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            ip.put(value, found.getString("ip"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            email.put(value, found.getString("email"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfa.put(value, found.getBoolean("2fa"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfaSecret.put(value, found.getString("2faSecret"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfaRecoveryCode.put(value, found.getInteger("2faRecoveryCode"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfaSettingUp.put(value, found.getBoolean("2faSettingUp"));
                        }

                        getYAML().getConfig().set(value + ".uuid", uuid.get(value));
                        getYAML().getConfig().set(value + ".ip", ip.get(value));
                        getYAML().getConfig().set(value + ".email", email.get(value));
                        getYAML().getConfig().set(value + ".2fa", tfa.get(value));
                        getYAML().getConfig().set(value + ".2faSecret", tfaSecret.get(value));
                        getYAML().getConfig().set(value + ".2faRecoveryCode", tfaRecoveryCode.get(value));
                        getYAML().getConfig().set(value + ".2faSettingUp", tfaSettingUp.get(value));

                        getYAML().save();
                    }

                    getMongoDB().disconnect(true);

                    return true;
            }
        }

        return false;
    }

    public boolean importMySQL() {
        if (importFrom.equals(ConnectionType.MYSQL))
            return false;

        if (connectionType.equals(ConnectionType.MYSQL)) {
            switch (importFrom) {
                case YAML:
                    getYAML().connect(true);

                    name.addAll(getYAML().getConfig().getKeys(false));

                    for (String value : name) {
                        uuid.put(value, getYAML().getConfig().getString(value + ".uuid"));
                        ip.put(value, getYAML().getConfig().getString(value + ".ip"));
                        email.put(value, getYAML().getConfig().getString(value + ".email"));
                        tfa.put(value, getYAML().getConfig().getBoolean(value + ".2fa"));
                        tfaSecret.put(value, getYAML().getConfig().getString(value + ".2faSecret"));
                        tfaRecoveryCode.put(value, getYAML().getConfig().getInt(value + ".2faRecoveryCode"));
                        tfaSettingUp.put(value, getYAML().getConfig().getBoolean(value + ".2faSettingUp"));

                        getMySQL().update("INSERT INTO authtools (name, uuid) VALUES ('" + value + "', '" + uuid.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, ip) VALUES ('" + value + "', '" + ip.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, email) VALUES ('" + value + "', '" + email.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2fa) VALUES ('" + value + "', '" + tfa.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2faSecret) VALUES ('" + value + "', '" + tfaSecret.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('" + value + "', '" + tfaRecoveryCode.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2faSettingUp) VALUES ('" + value + "', '" + tfaSettingUp.get(value) + "');");
                    }

                    return true;

                case SQLITE:
                    getSQLite().connect(true);

                    try {
                        ResultSet rs = getSQLite().getResult("SELECT * FROM authtools");

                        while (rs.next()) {
                            this.name.add(rs.getString("name"));
                        }

                        rs.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return false;
                    }

                    for (String value : name) {
                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.uuid.put(value, rs.getString("uuid"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.ip.put(value, rs.getString("ip"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.email.put(value, rs.getString("email"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfa.put(value, rs.getBoolean("tfa"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSecret.put(value, rs.getString("tfaSecret"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaRecoveryCode.put(value, rs.getInt("tfaRecoveryCode"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSettingUp.put(value, rs.getBoolean("tfaSettingUp"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        getMySQL().update("INSERT INTO authtools (name, uuid) VALUES ('" + value + "', '" + uuid.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, ip) VALUES ('" + value + "', '" + ip.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, email) VALUES ('" + value + "', '" + email.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2fa) VALUES ('" + value + "', '" + tfa.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2faSecret) VALUES ('" + value + "', '" + tfaSecret.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('" + value + "', '" + tfaRecoveryCode.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2faSettingUp) VALUES ('" + value + "', '" + tfaSettingUp.get(value) + "');");
                    }

                    getSQLite().disconnect(true);

                    return true;

                case MONGODB:
                    getMongoDB().connect(true);

                    for (String value : name) {
                        Document found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            name.add(found.getString("name"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            uuid.put(value, found.getString("uuid"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            ip.put(value, found.getString("ip"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            email.put(value, found.getString("email"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfa.put(value, found.getBoolean("2fa"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfaSecret.put(value, found.getString("2faSecret"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfaRecoveryCode.put(value, found.getInteger("2faRecoveryCode"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfaSettingUp.put(value, found.getBoolean("2faSettingUp"));
                        }

                        getMySQL().update("INSERT INTO authtools (name, uuid) VALUES ('" + value + "', '" + uuid.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, ip) VALUES ('" + value + "', '" + ip.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, email) VALUES ('" + value + "', '" + email.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2fa) VALUES ('" + value + "', '" + tfa.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2faSecret) VALUES ('" + value + "', '" + tfaSecret.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('" + value + "', '" + tfaRecoveryCode.get(value) + "');");
                        getMySQL().update("INSERT INTO authtools (name, 2faSettingUp) VALUES ('" + value + "', '" + tfaSettingUp.get(value) + "');");
                    }

                    getMongoDB().disconnect(true);

                    return true;
            }
        }

        return false;
    }

    public boolean importMongoDB() {
        if (importFrom.equals(ConnectionType.MONGODB))
            return false;

        if (connectionType.equals(ConnectionType.MONGODB)) {
            switch (importFrom) {
                case MYSQL:
                    if (!getMySQL().isConnected())
                        getMySQL().connect(true);

                    try {
                        ResultSet rs = getMySQL().getResult("SELECT * FROM authtools");

                        while (rs.next()) {
                            this.name.add(rs.getString("name"));
                        }

                        rs.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return false;
                    }

                    for (String value : name) {
                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.uuid.put(value, rs.getString("uuid"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.ip.put(value, rs.getString("ip"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.email.put(value, rs.getString("email"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfa.put(value, rs.getBoolean("2fa"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSecret.put(value, rs.getString("2faSecret"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaRecoveryCode.put(value, rs.getInt("2faRecoveryCode"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSettingUp.put(value, rs.getBoolean("2faSettingUp"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        Document playerDoc = new Document("name", value);

                        playerDoc.append("uuid", uuid.get(value));
                        playerDoc.append("ip", ip.get(value));
                        playerDoc.append("email", email.get(value));
                        playerDoc.append("2fa", tfa.get(value));
                        playerDoc.append("2faSecret", tfaSecret.get(value));
                        playerDoc.append("2faRecoveryCode", tfaRecoveryCode.get(value));
                        playerDoc.append("2faSettingUp", tfaSettingUp.get(value));

                        getMongoDB().getCollection().insertOne(playerDoc);
                    }

                    getMySQL().disconnect(true);

                    return true;

                case YAML:
                    getYAML().connect(true);

                    name.addAll(getYAML().getConfig().getKeys(false));

                    for (String value : name) {
                        uuid.put(value, getYAML().getConfig().getString(value + ".uuid"));
                        ip.put(value, getYAML().getConfig().getString(value + ".ip"));
                        email.put(value, getYAML().getConfig().getString(value + ".email"));
                        tfa.put(value, getYAML().getConfig().getBoolean(value + ".2fa"));
                        tfaSecret.put(value, getYAML().getConfig().getString(value + ".2faSecret"));
                        tfaRecoveryCode.put(value, getYAML().getConfig().getInt(value + ".2faRecoveryCode"));
                        tfaSettingUp.put(value, getYAML().getConfig().getBoolean(value + ".2faSettingUp"));

                        Document playerDoc = new Document("name", value);

                        playerDoc.append("uuid", uuid.get(value));
                        playerDoc.append("ip", ip.get(value));
                        playerDoc.append("email", email.get(value));
                        playerDoc.append("2fa", tfa.get(value));
                        playerDoc.append("2faSecret", tfaSecret.get(value));
                        playerDoc.append("2faRecoveryCode", tfaRecoveryCode.get(value));
                        playerDoc.append("2faSettingUp", tfaSettingUp.get(value));

                        getMongoDB().getCollection().insertOne(playerDoc);
                    }

                    return true;

                case SQLITE:
                    getSQLite().connect(true);

                    try {
                        ResultSet rs = getSQLite().getResult("SELECT * FROM authtools");

                        while (rs.next()) {
                            this.name.add(rs.getString("name"));
                        }

                        rs.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return false;
                    }

                    for (String value : name) {
                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.uuid.put(value, rs.getString("uuid"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.ip.put(value, rs.getString("ip"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.email.put(value, rs.getString("email"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfa.put(value, rs.getBoolean("tfa"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSecret.put(value, rs.getString("tfaSecret"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaRecoveryCode.put(value, rs.getInt("tfaRecoveryCode"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getSQLite().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSettingUp.put(value, rs.getBoolean("tfaSettingUp"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        Document playerDoc = new Document("name", value);

                        playerDoc.append("uuid", uuid.get(value));
                        playerDoc.append("ip", ip.get(value));
                        playerDoc.append("email", email.get(value));
                        playerDoc.append("2fa", tfa.get(value));
                        playerDoc.append("2faSecret", tfaSecret.get(value));
                        playerDoc.append("2faRecoveryCode", tfaRecoveryCode.get(value));
                        playerDoc.append("2faSettingUp", tfaSettingUp.get(value));

                        getMongoDB().getCollection().insertOne(playerDoc);
                    }

                    getSQLite().disconnect(true);

                    return true;
            }
        }

        return false;
    }

    public boolean importSQLite() {
        if (importFrom.equals(ConnectionType.SQLITE))
            return false;

        if (connectionType.equals(ConnectionType.SQLITE)) {
            switch (importFrom) {
                case MYSQL:
                    if (!getMySQL().isConnected())
                        getMySQL().connect(true);

                    try {
                        ResultSet rs = getMySQL().getResult("SELECT * FROM authtools");

                        while (rs.next()) {
                            this.name.add(rs.getString("name"));
                        }

                        rs.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return false;
                    }

                    for (String value : name) {
                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.uuid.put(value, rs.getString("uuid"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.ip.put(value, rs.getString("ip"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.email.put(value, rs.getString("email"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfa.put(value, rs.getBoolean("2fa"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSecret.put(value, rs.getString("2faSecret"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaRecoveryCode.put(value, rs.getInt("2faRecoveryCode"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        try {
                            ResultSet rs = getMySQL().getResult("SELECT * FROM authtools WHERE name='" + value + "'");

                            while (rs.next()) {
                                this.tfaSettingUp.put(value, rs.getBoolean("2faSettingUp"));
                            }

                            rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        }

                        getSQLite().update("INSERT INTO authtools (name, uuid) VALUES ('" + value + "', '" + uuid.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, ip) VALUES ('" + value + "', '" + ip.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, email) VALUES ('" + value + "', '" + email.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('" + value + "', '" + tfa.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfaSecret) VALUES ('" + value + "', '" + tfaSecret.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('" + value + "', '" + tfaRecoveryCode.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('" + value + "', '" + tfaSettingUp.get(value) + "');");
                    }

                    getMySQL().disconnect(true);

                    return true;

                case YAML:
                    getYAML().connect(true);

                    name.addAll(getYAML().getConfig().getKeys(false));

                    for (String value : name) {
                        uuid.put(value, getYAML().getConfig().getString(value + ".uuid"));
                        ip.put(value, getYAML().getConfig().getString(value + ".ip"));
                        email.put(value, getYAML().getConfig().getString(value + ".email"));
                        tfa.put(value, getYAML().getConfig().getBoolean(value + ".2fa"));
                        tfaSecret.put(value, getYAML().getConfig().getString(value + ".2faSecret"));
                        tfaRecoveryCode.put(value, getYAML().getConfig().getInt(value + ".2faRecoveryCode"));
                        tfaSettingUp.put(value, getYAML().getConfig().getBoolean(value + ".2faSettingUp"));

                        getSQLite().update("INSERT INTO authtools (name, uuid) VALUES ('" + value + "', '" + uuid.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, ip) VALUES ('" + value + "', '" + ip.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, email) VALUES ('" + value + "', '" + email.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('" + value + "', '" + tfa.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfaSecret) VALUES ('" + value + "', '" + tfaSecret.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('" + value + "', '" + tfaRecoveryCode.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('" + value + "', '" + tfaSettingUp.get(value) + "');");
                    }

                    return true;

                case MONGODB:
                    getMongoDB().connect(true);

                    for (String value : name) {
                        Document found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            name.add(found.getString("name"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            uuid.put(value, found.getString("uuid"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            ip.put(value, found.getString("ip"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            email.put(value, found.getString("email"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfa.put(value, found.getBoolean("2fa"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfaSecret.put(value, found.getString("2faSecret"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfaRecoveryCode.put(value, found.getInteger("2faRecoveryCode"));
                        }

                        found = (Document) getMongoDB().getCollection().find(new Document("name", value)).first();

                        if (found != null) {
                            tfaSettingUp.put(value, found.getBoolean("2faSettingUp"));
                        }

                        getSQLite().update("INSERT INTO authtools (name, uuid) VALUES ('" + value + "', '" + uuid.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, ip) VALUES ('" + value + "', '" + ip.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, email) VALUES ('" + value + "', '" + email.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('" + value + "', '" + tfa.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfaSecret) VALUES ('" + value + "', '" + tfaSecret.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('" + value + "', '" + tfaRecoveryCode.get(value) + "');");
                        getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('" + value + "', '" + tfaSettingUp.get(value) + "');");
                    }

                    getMongoDB().disconnect(true);

                    return true;
            }
        }

        return false;
    }


    public YAMLConnection getYAML() {
        return instance.getYamlConnection();
    }

    public MySQL getMySQL() {
        return instance.getMySQL();
    }

    public MongoDB getMongoDB() {
        return instance.getMongoDB();
    }

    public SQLite getSQLite() {
        return instance.getSQLite();
    }

}
