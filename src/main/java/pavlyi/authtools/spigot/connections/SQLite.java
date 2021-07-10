package pavlyi.authtools.spigot.connections;

import pavlyi.authtools.spigot.AuthTools;

import java.sql.*;

public class SQLite {
    private final AuthTools instance = AuthTools.getInstance();

    public Connection connection;
    public Statement statement;

    public boolean connect(boolean silent) {
        try {
            Driver d = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
            DriverManager.registerDriver(d);

            if (!silent)
                instance.log("&r  &aSuccess: &cDriver &ffor &cSQLite &fhas been initialized!");

            try {
                String fileName = "sqlite.db";

                if (instance.getConfigHandler().CONNECTION_SQLITE_FILENAME != null && instance.getConfigHandler().CONNECTION_SQLITE_FILENAME.endsWith(".db"))
                    fileName = instance.getConfigHandler().CONNECTION_SQLITE_FILENAME;

                connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder() + "/" + fileName);
                statement = connection.createStatement();

                if (!silent)
                    instance.log("&r  &aSuccess: &cSQLite &fhas been created and loaded!");

                createTable(silent);
                return true;
            } catch (SQLException exception) {
                instance.log("&r  &cError: &cSQLite &fcouldn't create!");
                exception.printStackTrace();

                return false;
            }

        } catch (Exception exception) {
            instance.log("&r  &cError: &cDriver &ffor &cSQLite &fhasn't been found!");
            exception.printStackTrace();

            instance.getPluginManager().disablePlugin(instance);

            return false;
        }
    }

    public void createTable(boolean silent) {
        try {
            getConnection().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS authtools(name VARCHAR(255), uuid VARCHAR(255), password VARCHAR (255), ip VARCHAR(255), email VARCHAR(255), tfa boolean, tfaSecret VARCHAR(255), tfaRecoveryCode int);");
            getConnection().createStatement().executeUpdate("ALTER TABLE authtools ADD password VARCHAR (255) NOT NULL;");

            if (!silent)
                instance.log("&r  &aSuccess: &fCreated tables for &cSQLite&f!");
        } catch (SQLException exception) {
            instance.log("&r  &cError: &cSQLite &fcouldn't create a tables!");
            exception.printStackTrace();
        }
    }

    public void disconnect(boolean silent) {
        try {
            getConnection().close();

            if (!silent)
                instance.log("&r  &aSuccess: &cSQLite &fhas been unloaded!");
        } catch (SQLException exception) {
            instance.log("&r  &cError: &cSQLite &fcouldn't had been unloaded!");
            exception.printStackTrace();
        }
    }

    public void update(String qry) {
        try {
            getConnection().createStatement().executeUpdate(qry);
        } catch (SQLException exception) {
            instance.log("&r  &cError: &cSQLite &fcouldn't update data in table!");
            exception.printStackTrace();
        }
    }

    public ResultSet getResult(String qry) {
        try {
            return getConnection().createStatement().executeQuery(qry);
        } catch (SQLException exception) {
            instance.log("&r  &cError: &cSQLite &fcouldn't get a result!");
            exception.printStackTrace();

            return null;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
