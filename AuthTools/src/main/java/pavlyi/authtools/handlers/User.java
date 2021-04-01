package pavlyi.authtools.handlers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import pavlyi.authtools.AuthTools;

public class User {
	private AuthTools instance = AuthTools.getInstance();

	public Player player;
	public String name;
	public String ip;
	public String uuid;
	public String email;
	public boolean TFA;
	public String TFAsecret;
	public boolean settingUp2FA;
	public int recoveryCode;
	

	public User(String player) {
		this.player = Bukkit.getPlayer(player);
		this.name = player;

		if (getIP() != null) {
			this.ip = getIP();
		}

		if (getUUID() != null) {
			this.uuid = getUUID();
		}

		if (getEmail() != null) {
			this.email = getEmail();
		}

		if (get2FAsecret() != null) {
			this.TFAsecret = get2FAsecret();
		}

		this.TFA = get2FA();
		this.recoveryCode = getRecoveryCode();
		this.settingUp2FA = getSettingUp2FA();
	}


	public void create() {
		if (instance.getConnectionType().equals("MONGODB")) {
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

	public void setEmail(String email) {
		if (instance.getConnectionType().equals("YAML")) {
			instance.getYamlConnection().getPlayerData().set(name+".email", email);
			this.email = email;

			instance.getYamlConnection().savePlayerData();
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			if (!isInDatabase()) {
				instance.getMySQL().update("INSERT INTO authtools (name, email) VALUES ('"+name+"', '"+email+"');");
				return;
			}
			
			instance.getMySQL().update("UPDATE authtools SET email='"+email+"' WHERE name='"+name+"';");
			
			this.email = email;
		}
		
		if (instance.getConnectionType().equals("SQLITE")) {
			if (!isInDatabase()) {
				instance.getSQLite().update("INSERT INTO authtools (name, email) VALUES ('"+name+"', '"+email+"');");
				return;
			}
			
			instance.getSQLite().update("UPDATE authtools SET email='"+email+"' WHERE name='"+name+"';");
			
			this.email = email;
		}
		
		if (instance.getConnectionType().equals("MONGODB")) {
			create();

			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				Bson updatedValue = new Document("email", email);
				Bson updateOperation = new Document("$set", updatedValue);

				instance.getMongoDB().getCollection().updateOne(found, updateOperation); 
			}

			this.email = email;
		}
	}
	
	public void set2FA(boolean TFA) {
		if (instance.getConnectionType().equals("YAML")) {
			instance.getYamlConnection().getPlayerData().set(name+".2fa", TFA);
			this.TFA = TFA;

			instance.getYamlConnection().savePlayerData();
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			if (!isInDatabase()) {
				instance.getMySQL().update("INSERT INTO authtools (name, 2fa) VALUES ('"+name+"', "+TFA+");");
				return;
			}
			
			instance.getMySQL().update("UPDATE authtools SET 2fa="+TFA+" WHERE name='"+name+"';");
			
			this.TFA = TFA;
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			if (!isInDatabase()) {
				if (TFA) {
					instance.getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('"+name+"', "+1+");");
				} else {
					instance.getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('"+name+"', "+0+");");
				}

				return;
			}

			if (TFA) {
				instance.getSQLite().update("UPDATE authtools SET tfa="+1+" WHERE name='"+name+"';");
			} else {
				instance.getSQLite().update("UPDATE authtools SET tfa="+0+" WHERE name='"+name+"';");
			}


			this.TFA = TFA;
		}
		
		if (instance.getConnectionType().equals("MONGODB")) {
			create();

			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				Bson updatedValue = new Document("2fa", TFA);
				Bson updateOperation = new Document("$set", updatedValue);

				instance.getMongoDB().getCollection().updateOne(found, updateOperation); 
			}

			this.TFA = TFA;
		}
	}
	
	public void set2FAsecret(String TFAsecret) {
		if (instance.getConnectionType().equals("YAML")) {
			instance.getYamlConnection().getPlayerData().set(name+".2faSecret", TFAsecret);
			this.TFAsecret = TFAsecret;
			
			instance.getYamlConnection().savePlayerData();
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			if (!isInDatabase()) {
				instance.getMySQL().update("INSERT INTO authtools (name, 2faSecret) VALUES ('"+name+"', '"+TFAsecret+"');");
				return;
			}
			
			instance.getMySQL().update("UPDATE authtools SET 2faSecret='"+TFAsecret+"' WHERE name='"+name+"';");
			
			this.TFAsecret = TFAsecret;
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			if (!isInDatabase()) {
				instance.getSQLite().update("INSERT INTO authtools (name, tfaSecret) VALUES ('"+name+"', '"+TFAsecret+"');");
				return;
			}

			instance.getSQLite().update("UPDATE authtools SET tfaSecret='"+TFAsecret+"' WHERE name='"+name+"';");

			this.TFAsecret = TFAsecret;
		}
		
		if (instance.getConnectionType().equals("MONGODB")) {
			create();

			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				Bson updatedValue = new Document("2faSecret", TFAsecret);
				Bson updateOperation = new Document("$set", updatedValue);

				instance.getMongoDB().getCollection().updateOne(found, updateOperation); 
			}

			this.TFAsecret = TFAsecret;
		}
	}
	
	public void setSettingUp2FA(boolean settingUp2FA) {
		if (instance.getConnectionType().equals("YAML")) {
			instance.getYamlConnection().getPlayerData().set(name+".settingUp2FA", settingUp2FA);
			this.settingUp2FA = settingUp2FA;
			
			instance.getYamlConnection().savePlayerData();
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			if (!isInDatabase()) {
				instance.getMySQL().update("INSERT INTO authtools (name, 2faSettingUp) VALUES ('"+name+"', "+settingUp2FA+");");
				return;
			}
			
			instance.getMySQL().update("UPDATE authtools SET 2faSettingUp="+settingUp2FA+" WHERE name='"+name+"';");
			
			this.settingUp2FA = settingUp2FA;
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			if (!isInDatabase()) {
				if (settingUp2FA) {
					instance.getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('"+name+"', "+1+");");
				} else {
					instance.getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('"+name+"', "+0+");");
				}

				return;
			}

			if (settingUp2FA) {
				instance.getSQLite().update("UPDATE authtools SET tfaSettingUp="+1+" WHERE name='"+name+"';");
			} else {
				instance.getSQLite().update("UPDATE authtools SET tfaSettingUp="+0+" WHERE name='"+name+"';");
			}

			this.settingUp2FA = settingUp2FA;
		}
		
		if (instance.getConnectionType().equals("MONGODB")) {
			create();

			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				Bson updatedValue = new Document("2faSettingUp", settingUp2FA);
				Bson updateOperation = new Document("$set", updatedValue);

				instance.getMongoDB().getCollection().updateOne(found, updateOperation); 
			}

			this.settingUp2FA = settingUp2FA;
		}
	}

	public void setRecoveryCode(boolean clearRecoveryCode) {
		if (instance.getConnectionType().equals("YAML")) {
			if (!clearRecoveryCode) {
				String recoveryCode = "";
				
				for (int i = 1; i <= 8; i++) {
					recoveryCode = recoveryCode + new Random().nextInt(9);
				}
				
				instance.getYamlConnection().getPlayerData().set(name+".recoveryCode", Integer.parseInt(recoveryCode));
				this.recoveryCode = Integer.parseInt(recoveryCode);
			} else {
				instance.getYamlConnection().getPlayerData().set(name+".recoveryCode", null);
				this.recoveryCode = 0;
			}

			instance.getYamlConnection().savePlayerData();
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			if (!isInDatabase()) {
				if (!clearRecoveryCode) {
					String recoveryCode = "";
					
					for (int i = 1; i <= 8; i++) {
						recoveryCode = recoveryCode + new Random().nextInt(9);
					}
					
					instance.getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('"+name+"''"+recoveryCode+"');");
					this.recoveryCode = Integer.parseInt(recoveryCode);
				} else {
					instance.getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('"+name+"','"+null+"');");
					this.recoveryCode = 0;
				}
				
				return;
			}
			
			if (!clearRecoveryCode) {
				String recoveryCode = "";
				
				for (int i = 1; i <= 8; i++) {
					recoveryCode = recoveryCode + new Random().nextInt(9);
				}
				
				instance.getMySQL().update("UPDATE authtools SET 2faRecoveryCode="+recoveryCode+" WHERE name='"+name+"';");
				this.recoveryCode = Integer.parseInt(recoveryCode);
			} else {
				instance.getMySQL().update("UPDATE authtools SET 2faRecoveryCode="+null+" WHERE name='"+name+"';");
				this.recoveryCode = 0;
			}
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			if (!isInDatabase()) {
				if (!clearRecoveryCode) {
					String recoveryCode = "";
					
					for (int i = 1; i <= 8; i++) {
						recoveryCode = recoveryCode + new Random().nextInt(9);
					}
					
					instance.getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('"+name+"''"+recoveryCode+"');");
					this.recoveryCode = Integer.parseInt(recoveryCode);
				} else {
					instance.getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('"+name+"','"+null+"');");
					this.recoveryCode = 0;
				}

				return;
			}

			if (!clearRecoveryCode) {
				String recoveryCode = "";
				
				for (int i = 1; i <= 8; i++) {
					recoveryCode = recoveryCode + new Random().nextInt(9);
				}
				
				instance.getSQLite().update("UPDATE authtools SET tfaRecoveryCode="+recoveryCode+" WHERE name='"+name+"';");
				this.recoveryCode = Integer.parseInt(recoveryCode);
			} else {
				instance.getSQLite().update("UPDATE authtools SET tfaRecoveryCode="+null+" WHERE name='"+name+"';");
				this.recoveryCode = 0;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			String recoveryCode = "";

			if (!clearRecoveryCode) {
				recoveryCode = "";
				
				for (int i = 1; i <= 8; i++) {
					recoveryCode = recoveryCode + new Random().nextInt(9);
				}
				
				this.recoveryCode = Integer.parseInt(recoveryCode);
			} else {
				this.recoveryCode = 0;
			}

			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				Bson updatedValue = new Document("2faRecoveryCode", recoveryCode);
				Bson updateOperation = new Document("$set", updatedValue);

				instance.getMongoDB().getCollection().updateOne(found, updateOperation); 
			}
		}
	}
	
	public void setIP(InetSocketAddress ip) {
		String ipEdited = ip.getHostName();

		if (instance.getConnectionType().equals("YAML")) {
			instance.getYamlConnection().getPlayerData().set(name+".ip", ipEdited);
			this.ip = ipEdited;

			instance.getYamlConnection().savePlayerData();
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			if (!isInDatabase()) {
				instance.getMySQL().update("INSERT INTO authtools (name, ip) VALUES ('"+name+"', '"+ipEdited+"');");
				return;
			}
			
			instance.getMySQL().update("UPDATE authtools SET ip='"+ipEdited+"' WHERE name='"+name+"';");
			
			this.ip = ipEdited;
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			if (!isInDatabase()) {
				instance.getSQLite().update("INSERT INTO authtools (name, ip) VALUES ('"+name+"', '"+ipEdited+"');");
				return;
			}
			
			instance.getSQLite().update("UPDATE authtools SET ip='"+ipEdited+"' WHERE name='"+name+"';");
			
			this.ip = ipEdited;
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				Bson updatedValue = new Document("ip", ipEdited);
				Bson updateOperation = new Document("$set", updatedValue);

				instance.getMongoDB().getCollection().updateOne(found, updateOperation); 
			}

			this.ip = ipEdited;
		}
	}
	
	public void setUUID() {
		if (instance.getConnectionType().equals("YAML")) {
			instance.getYamlConnection().getPlayerData().set(name+".uuid", player.getUniqueId().toString());
			this.uuid = player.getUniqueId().toString();

			instance.getYamlConnection().savePlayerData();
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			if (!isInDatabase()) {
				instance.getMySQL().update("INSERT INTO authtools (name, uuid) VALUES ('"+name+"', '"+player.getUniqueId().toString()+"');");
				return;
			}
			
			instance.getMySQL().update("UPDATE authtools SET uuid='"+player.getUniqueId().toString()+"' WHERE name='"+name+"';");
			
			this.uuid = player.getUniqueId().toString();
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			if (!isInDatabase()) {
				instance.getSQLite().update("INSERT INTO authtools (name, uuid) VALUES ('"+name+"', '"+player.getUniqueId().toString()+"');");
				return;
			}

			instance.getSQLite().update("UPDATE authtools SET uuid='"+player.getUniqueId().toString()+"' WHERE name='"+name+"';");

			this.uuid = player.getUniqueId().toString();
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				Bson updatedValue = new Document("uuid", player.getUniqueId().toString());
				Bson updateOperation = new Document("$set", updatedValue);

				instance.getMongoDB().getCollection().updateOne(found, updateOperation); 
			}

			this.uuid = player.getUniqueId().toString();
		}
	}



	public boolean isInDatabase() {
		if (instance.getConnectionType().equals("YAML")) {
			if (instance.getYamlConnection().getPlayerData().get(name) != null) {
				return true;
			}
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			try {
				ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+getName()+"'");
				
				if (rs.next())
					return true;
				
				rs.close();
				return false;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		}
		
		if (instance.getConnectionType().equals("SQLITE")) {
			try {
				ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+getName()+"'");
				
				if (rs.next())
					return true;
				
				rs.close();
				return false;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				return true;
			}

			return false;
		}

		return false;
	}







	public String getEmail() {
		if (instance.getConnectionType().equals("YAML")) {
			if (instance.getYamlConnection().getPlayerData().get(name+".email") != null) {
				return instance.getYamlConnection().getPlayerData().getString(name+".email");
			}
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			try {
				ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("email");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			try {
				ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("email");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				return found.getString("email");
			}

			return null;
		}

		return null;
	}

	public boolean get2FA() {
		if (instance.getConnectionType().equals("YAML")) {
			if (instance.getYamlConnection().getPlayerData().get(name+".2fa") != null) {
				return instance.getYamlConnection().getPlayerData().getBoolean(name+".2fa");
			}
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			try {
				ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getBoolean("2fa");
				
				rs.close();
				return false;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			try {
				ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getBoolean("tfa");
				
				rs.close();
				return false;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				return (boolean) found.getBoolean("2fa");
			}

			return false;
		}

		return false;
	}

	public String get2FAsecret() {
		if (instance.getConnectionType().equals("YAML")) {
			if (instance.getYamlConnection().getPlayerData().get(name+".2faSecret") != null) {
				return instance.getYamlConnection().getPlayerData().getString(name+".2faSecret");
			}
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			try {
				ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("2faSecret");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			try {
				ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("tfaSecret");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				return found.getString("2faSecret");
			}

			return null;
		}

		return null;
	}
	
	public boolean getSettingUp2FA() {
		if (instance.getConnectionType().equals("YAML")) {
			if (instance.getYamlConnection().getPlayerData().get(name+".settingUp2FA") != null) {
				return instance.getYamlConnection().getPlayerData().getBoolean(name+".settingUp2FA");
			}
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			try {
				ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getBoolean("2faSettingUp");
				
				rs.close();
				return false;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			try {
				ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getBoolean("tfaSettingUp");
				
				rs.close();
				return false;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				return found.getBoolean("2faSettingUp");
			}

			return false;
		}
		
		return false;
	}
	
	public int getRecoveryCode() {
		if (instance.getConnectionType().equals("YAML")) {
			if (instance.getYamlConnection().getPlayerData().get(name+".recoveryCode") != null) {
				return instance.getYamlConnection().getPlayerData().getInt(name+".recoveryCode");
			}
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			try {
				ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getInt("2faRecoveryCode");
				
				rs.close();
				return 0;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return 0;
			}
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			try {
				ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getInt("tfaRecoveryCode");
				
				rs.close();
				return 0;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return 0;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
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
	
	public String getIP() {
		if (instance.getConnectionType().equals("YAML")) {
			if (instance.getYamlConnection().getPlayerData().get(name+".ip") != null) {
				return instance.getYamlConnection().getPlayerData().getString(name+".ip");
			}
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			try {
				ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("ip");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			try {
				ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("ip");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				return found.getString("ip");
			}

			return null;
		}
		
		return null;
	}
	
	public String getUUID() {
		if (instance.getConnectionType().equals("YAML")) {
			if (instance.getYamlConnection().getPlayerData().get(name+".uuid") != null) {
				return instance.getYamlConnection().getPlayerData().getString(name+".uuid");
			}
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			try {
				ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("uuid");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			try {
				ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("uuid");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				return found.getString("uuid");
			}

			return null;
		}
		
		return null;
	}

	public String getName() {
		if (instance.getConnectionType().equals("YAML")) {
			if (instance.getYamlConnection().getPlayerData().get(name+".name") != null) {
				return instance.getYamlConnection().getPlayerData().getString(name+".name");
			}
		}
		
		if (instance.getConnectionType().equals("MYSQL")) {
			try {
				ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("name");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("SQLITE")) {
			try {
				ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name+"'");
				
				if (rs.next())
					return rs.getString("name");
				
				rs.close();
				return null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		if (instance.getConnectionType().equals("MONGODB")) {
			Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name)).first();

			if (found != null) {
				return found.getString("name");
			}

			return null;
		}

		return null;
	}

}
