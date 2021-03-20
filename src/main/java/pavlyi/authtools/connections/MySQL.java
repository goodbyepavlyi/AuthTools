package pavlyi.authtools.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import pavlyi.authtools.AuthTools;

public class MySQL {
	private static AuthTools instance = AuthTools.getInstance();
	public Connection con;

	public boolean connect() {
		if (!isConnected()) {
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + instance.getConfigHandler().CONNECTION_MYSQL_HOSTNAME + ":" + instance.getConfigHandler().CONNECTION_MYSQL_PORT + "/" + instance.getConfigHandler().CONNECTION_MYSQL_DATABASE + "?autoReconnect=true",
						instance.getConfigHandler().CONNECTION_MYSQL_USERNAME,
						instance.getConfigHandler().CONNECTION_MYSQL_PASSWORD);
				instance.log("&r  &aSuccess: &cMySQL &fsucessfully connected!");
				createTable();

				if (isConnected()) {
					(new BukkitRunnable() {
						@Override
						public void run() {
							try {
								if (con != null && !con.isClosed()) {
									con.createStatement().execute("SELECT 1");
								}
							} catch (SQLException ex) {
								try {
									con = DriverManager.getConnection("jdbc:mysql://" + instance.getConfigHandler().CONNECTION_MYSQL_HOSTNAME + ":" + instance.getConfigHandler().CONNECTION_MYSQL_PORT + "/" + instance.getConfigHandler().CONNECTION_MYSQL_DATABASE + "?autoReconnect=true",
											instance.getConfigHandler().CONNECTION_MYSQL_USERNAME,
											instance.getConfigHandler().CONNECTION_MYSQL_PASSWORD);
									instance.log("&r  &aSuccess: &cMySQL &fhas been sucessfully reconnected!");
									createTable();
								} catch (SQLException ex1) {
									instance.log("&r  &cError: &cMySQL &fcouldn't reconnect!");
									ex.printStackTrace();
								}
							}
						}
					}).runTaskTimerAsynchronously(instance, 60 * 20, 60 * 20);
				}

				return true;
			} catch (SQLException ex) {
				instance.log("&r  &cError: &cMySQL &fcouldn't connect!");
				ex.printStackTrace();

				return false;
			}
		}

		return false;
	}

	public  void disconnect() {
		if (isConnected()) {
			try {
				con.close();
				instance.log("&r  &aSuccess: &cMySQL &fsucessfully disconnected!");
			} catch (SQLException ex) {
				instance.log("&r  &cError: &cMySQL &fcouldn't disconnect!");
				ex.printStackTrace();
			}
		}
	}

	public  boolean isConnected() {
		return con != null;
	}

	public  Connection getConnection() {
		return con;
	}

	public void update(String qry) {
		if (isConnected()) {
			try {
				con.createStatement().executeUpdate(qry);
			} catch (SQLException ex) {
				instance.log("&r  &cError: &cMySQL &fcouldn't update data in table!");
				ex.printStackTrace();
			}
		}
	}

	public ResultSet getResult(String qry) {
		if (isConnected()) {
			try {
				return con.createStatement().executeQuery(qry);
			} catch (SQLException ex) {
				instance.log("&r  &cError: &cMySQL &fcouldn't get a result!");
				ex.printStackTrace();
			}
		}

		return null;
	}

	public Object get(String whereresult, String where, String select, String database) {
		ResultSet rs = getResult("SELECT " + select + " FROM " + database + " WHERE " + where + "='" + whereresult + "'");
		try {
			if (rs.next()) {
				Object v = rs.getObject(select);
				return v;
			}
		} catch (SQLException ex) {
			instance.log("&r  &cError: &cMySQL &fcouldn't get a result!");
			ex.printStackTrace();
			return "ERROR";
		}

		return "ERROR";
	}

	public void createTable() {
		if (isConnected()) {
			try {
				con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS authtools(name VARCHAR(64), uuid VARCHAR(64), ip VARCHAR(64), email VARCHAR(64), 2fa boolean, 2faSecret VARCHAR(64), 2faRecoveryCode int, 2faSettingUp boolean);");
				instance.log("&r  &aSuccess: &fCreated tables for &cMySQL&f!");
			} catch (SQLException ex) {
				ex.printStackTrace();
				instance.log("&r  &cError: &cMySQL &fcouldn't create a tables!");
			}
		}
	}
}
