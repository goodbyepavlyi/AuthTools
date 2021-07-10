package pavlyi.authtools.spigot.connections;

import org.bukkit.scheduler.BukkitRunnable;
import pavlyi.authtools.spigot.AuthTools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {
    private static final AuthTools instance = AuthTools.getInstance();
    private static Connection con;

    public boolean connect(boolean silent) {
        if (!isConnected()) {
            try {
                String hostname = instance.getConfigHandler().CONNECTION_MYSQL_HOSTNAME;
                int port = instance.getConfigHandler().CONNECTION_MYSQL_PORT;
                String database = instance.getConfigHandler().CONNECTION_MYSQL_DATABASE;
                String options = instance.getConfigHandler().CONNECTION_MYSQL_OPTIONS;
                String username = instance.getConfigHandler().CONNECTION_MYSQL_USERNAME;
                String password = instance.getConfigHandler().CONNECTION_MYSQL_PASSWORD;

                String connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + database + options + "&user=" + username + "&password=" + password;

                con = DriverManager.getConnection(connectionString);

                if (!silent)
                    instance.log("&r  &aSuccess: &cMySQL &fsucessfully connected!");

                createTable(silent);

                if (isConnected()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                if (getConnection() != null && !getConnection().isClosed()) {
                                    getConnection().createStatement().execute("SELECT 1");
                                }
                            } catch (SQLException ex) {
                                try {

                                    String connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + database + options + "?user=" + username + "&password=" + password;

                                    con = DriverManager.getConnection(connectionString);

                                    if (!silent)
                                        instance.log("&r  &aSuccess: &cMySQL &fhas been sucessfully reconnected!");

                                    createTable(silent);
                                } catch (SQLException sqlException) {
                                    instance.log("&r  &cError: &cMySQL &fcouldn't reconnect!");
                                    sqlException.printStackTrace();
                                }
                            }
                        }
                    }.runTaskTimerAsynchronously(instance, 0, 60 * 20);
                }

                return true;
            } catch (SQLException sqlException) {
                instance.log("&r  &cError: &cMySQL &fcouldn't connect!");
                sqlException.printStackTrace();

                return false;
            }
        }

        return false;
    }

    public void createTable(boolean silent) {
        if (isConnected()) {
            try {
                getConnection().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS authtools(name VARCHAR(255), uuid VARCHAR(255), password VARCHAR (255), ip VARCHAR(255), email VARCHAR(255), 2fa boolean, 2faSecret VARCHAR(255), 2faRecoveryCode int);");
                getConnection().createStatement().executeUpdate("ALTER TABLE authtools ADD password VARCHAR (255) NOT NULL;");

                if (!silent)
                    instance.log("&r  &aSuccess: &fCreated tables for &cMySQL&f!");
            } catch (SQLException sqlException) {
                instance.log("&r  &cError: &cMySQL &fcouldn't create a tables!");
                sqlException.printStackTrace();
            }
        }
    }

    public void disconnect(boolean silent) {
        if (isConnected()) {
            try {
                getConnection().close();

                if (!silent)
                    instance.log("&r  &aSuccess: &cMySQL &fsucessfully disconnected!");
            } catch (SQLException sqlException) {
                instance.log("&r  &cError: &cMySQL &fcouldn't disconnect!");
                sqlException.printStackTrace();
            }
        }
    }

    public void update(String qry) {
        if (isConnected()) {
            try {
                getConnection().createStatement().executeUpdate(qry);
            } catch (SQLException sqlException) {
                instance.log("&r  &cError: &cMySQL &fcouldn't update data in table!");
                sqlException.printStackTrace();
            }
        }
    }

    public ResultSet getResult(String qry) {
        if (isConnected()) {
            try {
                return getConnection().createStatement().executeQuery(qry);
            } catch (SQLException sqlException) {
                instance.log("&r  &cError: &cMySQL &fcouldn't get a result!");
                sqlException.printStackTrace();
            }
        }

        return null;
    }

    public Object get(String whereresult, String where, String select, String database) {
        ResultSet rs = getResult("SELECT " + select + " FROM " + database + " WHERE " + where + "='" + whereresult + "'");

        try {
            if (rs.next())
                return rs.getObject(select);
        } catch (SQLException sqlException) {
            instance.log("&r  &cError: &cMySQL &fcouldn't get a result!");
            sqlException.printStackTrace();
            return "ERROR";
        }

        return "ERROR";
    }

    public boolean isConnected() {
        return con != null;
    }

    public Connection getConnection() {
        return con;
    }
}
