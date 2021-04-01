package pavlyi.authtools.connections;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.scheduler.BukkitRunnable;

import pavlyi.authtools.AuthTools;

public class SQLite {
	private AuthTools instance = AuthTools.getInstance();

	public Connection con;
	public Statement st;
	public ResultSet rs;

	public void create(boolean silently) {
		try {
			Driver d = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
			DriverManager.registerDriver(d);

			if (!silently)
				instance.log("&r  &aSuccess: &cDriver &ffor &cSQLite &fhas been initialized!");

			try {
				String fileName = "sqlite.db";

				if (instance.getConfigHandler().CONNECTION_SQLITE_FILENAME != null && instance.getConfigHandler().CONNECTION_SQLITE_FILENAME.endsWith(".db"))
					fileName = instance.getConfigHandler().CONNECTION_SQLITE_FILENAME;

				con = DriverManager.getConnection("jdbc:sqlite:"+instance.getDataFolder()+"/"+fileName);
				st = con.createStatement();

				if (!silently)
					instance.log("&r  &aSuccess: &cSQLite &fhas been created and loaded!");

				createTable(silently);
			} catch (SQLException ex) {
				instance.log("&r  &cError: &cSQLite &fcouldn't create!");
				ex.printStackTrace();

				instance.getPluginManager().disablePlugin(instance);
			}

		} catch (Exception ex) {
			instance.log("&r  &cError: &cDriver &ffor &cSQLite &fhasn't been found!");
			ex.printStackTrace();

			instance.getPluginManager().disablePlugin(instance);
		}
	}

	public void unload(boolean silently) {
		try {
			con.close();

			if (!silently)
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

	public Object get(String whereresult, String where, String select, String database) {
		ResultSet rs = getResult("SELECT " + select + " FROM " + database + " WHERE " + where + "='" + whereresult + "'");
		try {
			if (rs.next()) {
				Object v = rs.getObject(select);
				return v;
			}
		} catch (SQLException ex) {
			instance.log("&r  &cError: &cSQLite &fcouldn't get a result!");
			ex.printStackTrace();
			return "ERROR";
		}

		return "ERROR";
	}

	public void createTable(boolean silently) {
		try {
			con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS authtools(name VARCHAR(64), uuid VARCHAR(64), ip VARCHAR(64), email VARCHAR(64), tfa boolean, tfaSecret VARCHAR(64), tfaRecoveryCode int, tfaSettingUp boolean);");

			if (!silently)
				instance.log("&r  &aSuccess: &fCreated tables for &cSQLite&f!");
		} catch (SQLException ex) {
			ex.printStackTrace();
			instance.log("&r  &cError: &cSQLite &fcouldn't create a tables!");
		}
	}

}
