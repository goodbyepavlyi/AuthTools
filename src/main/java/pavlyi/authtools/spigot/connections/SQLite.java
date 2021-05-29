package pavlyi.authtools.spigot.connections;

import pavlyi.authtools.spigot.AuthTools;

import java.sql.*;

public class SQLite {
    private final AuthTools instance = AuthTools.getInstance();

    public Connection con;
    public Statement st;

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

                con = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder() + "/" + fileName);
                st = con.createStatement();

                if (!silent)
                    instance.log("&r  &aSuccess: &cSQLite &fhas been created and loaded!");

                createTable(silent);
                return true;
            } catch (SQLException ex) {
                instance.log("&r  &cError: &cSQLite &fcouldn't create!");
                ex.printStackTrace();

                return false;
            }

        } catch (Exception ex) {
            instance.log("&r  &cError: &cDriver &ffor &cSQLite &fhasn't been found!");
            ex.printStackTrace();

            instance.getPluginManager().disablePlugin(instance);

            return false;
        }
    }

    public void createTable(boolean silent) {
        try {
            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS authtools(name VARCHAR(64), uuid VARCHAR(64), ip VARCHAR(64), email VARCHAR(64), tfa boolean, tfaSecret VARCHAR(64), tfaRecoveryCode int, tfaSettingUp boolean);");

            if (!silent)
                instance.log("&r  &aSuccess: &fCreated tables for &cSQLite&f!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            instance.log("&r  &cError: &cSQLite &fcouldn't create a tables!");
        }
    }

    public void disconnect(boolean silent) {
        try {
            con.close();

            if (!silent)
                instance.log("&r  &aSuccess: &cSQLite &fhas been unloaded!");
        } catch (SQLException ex) {
            instance.log("&r  &cError: &cSQLite &fcouldn't had been unloaded!");
            ex.printStackTrace();
        }
    }

    public void update(String qry) {
        try {
            con.createStatement().executeUpdate(qry);
        } catch (SQLException ex) {
            instance.log("&r  &cError: &cSQLite &fcouldn't update data in table!");
            ex.printStackTrace();
        }
    }

    public ResultSet getResult(String qry) {
        try {
            return con.createStatement().executeQuery(qry);
        } catch (SQLException ex) {
            instance.log("&r  &cError: &cSQLite &fcouldn't get a result!");
            ex.printStackTrace();

            return null;
        }
    }
}
