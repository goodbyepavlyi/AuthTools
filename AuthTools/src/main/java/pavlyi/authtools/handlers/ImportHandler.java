package pavlyi.authtools.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import pavlyi.authtools.AuthTools;
import pavlyi.authtools.connections.MongoDB;
import pavlyi.authtools.connections.MySQL;
import pavlyi.authtools.connections.SQLite;
import pavlyi.authtools.connections.YAMLConnection;

public class ImportHandler {
	private AuthTools instance = AuthTools.getInstance();

	private String currentBackend;
	private String importFrom;

	private ArrayList<String> name;
	private HashMap<String, String> uuid;
	private HashMap<String, String> ip;
	private HashMap<String, String> email;
	private HashMap<String, Boolean> tfa;
	private HashMap<String, String> tfaSecret;
	private HashMap<String, Integer> tfaRecoveryCode;
	private HashMap<String, Boolean> tfaSettingUp;
	

	public ImportHandler(String importFrom) {
		this.currentBackend = instance.getConnectionType();
		this.importFrom = importFrom;

		this.name = new ArrayList<String>();
		this.uuid = new HashMap<String, String>();
		this.ip = new HashMap<String, String>();
		this.email = new HashMap<String, String>();
		this.tfa = new HashMap<String, Boolean>();
		this.tfaSecret = new HashMap<String, String>();
		this.tfaRecoveryCode = new HashMap<String, Integer>();
		this.tfaSettingUp = new HashMap<String, Boolean>();
	}
	
	public boolean importYAML() {
		if (importFrom.equalsIgnoreCase("yaml"))
			return false;

		if (currentBackend.equalsIgnoreCase("yaml")) {
			getYAML().deletePlayerData(true);
			getYAML().createPlayerData(true);

			if (importFrom.equalsIgnoreCase("mysql")) {
				if (!getMySQL().isConnected())
					getMySQL().connect(true);

				try {
					ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools");

				    while (rs.next()) {
				        this.name.add(rs.getString("name"));
				    }

				    rs.close();	
				} catch (SQLException ex) {
				    ex.printStackTrace();
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.uuid.put(name.get(i), rs.getString("uuid"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.ip.put(name.get(i), rs.getString("ip"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.email.put(name.get(i), rs.getString("email"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfa.put(name.get(i), rs.getBoolean("2fa"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaSecret.put(name.get(i), rs.getString("2faSecret"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaRecoveryCode.put(name.get(i), rs.getInt("2faRecoveryCode"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");

					    while (rs.next()) {
					        this.tfaSettingUp.put(name.get(i), rs.getBoolean("2faSettingUp"));
					    }

					    rs.close();	
					} catch (SQLException ex) {
					    ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					getYAML().getPlayerData().set(name.get(i)+".uuid", uuid.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".ip", ip.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".email", email.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2fa", tfa.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2faSecret", tfaSecret.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2faRecoveryCode", tfaRecoveryCode.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2faSettingUp", tfaSettingUp.get(name.get(i)));

					getYAML().savePlayerData();
				}

				getMySQL().disconnect(true);

				return true;
			}

			if (importFrom.equalsIgnoreCase("sqlite")) {
				getSQLite().create(true);

				try {
					ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools");

				    while (rs.next()) {
				        this.name.add(rs.getString("name"));
				    }

				    rs.close();	
				} catch (SQLException ex) {
				    ex.printStackTrace();
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.uuid.put(name.get(i), rs.getString("uuid"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.ip.put(name.get(i), rs.getString("ip"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.email.put(name.get(i), rs.getString("email"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfa.put(name.get(i), rs.getBoolean("tfa"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaSecret.put(name.get(i), rs.getString("tfaSecret"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaRecoveryCode.put(name.get(i), rs.getInt("tfaRecoveryCode"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");

					    while (rs.next()) {
					        this.tfaSettingUp.put(name.get(i), rs.getBoolean("tfaSettingUp"));
					    }

					    rs.close();	
					} catch (SQLException ex) {
					    ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					getYAML().getPlayerData().set(name.get(i)+".uuid", uuid.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".ip", ip.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".email", email.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2fa", tfa.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2faSecret", tfaSecret.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2faRecoveryCode", tfaRecoveryCode.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2faSettingUp", tfaSettingUp.get(name.get(i)));

					getYAML().savePlayerData();
				}

				getSQLite().unload(true);

				return true;
			}

			if (importFrom.equalsIgnoreCase("mongodb")) {
				getMongoDB().connect(true);

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						name.add(found.getString("name"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						uuid.put(name.get(i), found.getString("uuid"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						ip.put(name.get(i), found.getString("ip"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						email.put(name.get(i), found.getString("email"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfa.put(name.get(i), found.getBoolean("2fa"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfaSecret.put(name.get(i), found.getString("2faSecret"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfaRecoveryCode.put(name.get(i), found.getInteger("2faRecoveryCode"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfaSettingUp.put(name.get(i), found.getBoolean("2faSettingUp"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					getYAML().getPlayerData().set(name.get(i)+".uuid", uuid.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".ip", ip.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".email", email.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2fa", tfa.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2faSecret", tfaSecret.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2faRecoveryCode", tfaRecoveryCode.get(name.get(i)));
					getYAML().getPlayerData().set(name.get(i)+".2faSettingUp", tfaSettingUp.get(name.get(i)));

					getYAML().savePlayerData();
				}

				getMongoDB().disconnect(true);

				return true;
			}
		}

		return false;
	}
	
	public boolean importMySQL() {
		if (importFrom.equalsIgnoreCase("mysql"))
			return false;

		if (currentBackend.equalsIgnoreCase("mysql")) {
			if (!getMySQL().isConnected())
				getMySQL().connect(true);

			getMySQL().update("DROP TABLE authtools;");
			getMySQL().createTable(true);
			
			if (importFrom.equalsIgnoreCase("yaml")) {
				getYAML().createPlayerData(true);
				getYAML().loadPlayerData(true);
				
				for (String f : getYAML().getPlayerData().getKeys(false)) {
					name.add(f);
				}
				
				for (int i = 0; i < name.size(); i++) {
					uuid.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".uuid"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					ip.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".ip"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					email.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".email"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfa.put(name.get(i), getYAML().getPlayerData().getBoolean(name.get(i)+".2fa"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfaSecret.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".2faSecret"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfaRecoveryCode.put(name.get(i), getYAML().getPlayerData().getInt(name.get(i)+".2faRecoveryCode"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfaSettingUp.put(name.get(i), getYAML().getPlayerData().getBoolean(name.get(i)+".2faSettingUp"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					instance.getMySQL().update("INSERT INTO authtools (name, uuid) VALUES ('"+name.get(i)+"', '"+uuid.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, ip) VALUES ('"+name.get(i)+"', '"+ip.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, email) VALUES ('"+name.get(i)+"', '"+email.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2fa) VALUES ('"+name.get(i)+"', '"+tfa.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2faSecret) VALUES ('"+name.get(i)+"', '"+tfaSecret.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('"+name.get(i)+"', '"+tfaRecoveryCode.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2faSettingUp) VALUES ('"+name.get(i)+"', '"+tfaSettingUp.get(name.get(i))+"');");
				}
				
				return true;
			}

			if (importFrom.equalsIgnoreCase("sqlite")) {
				getSQLite().create(true);

				try {
					ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools");

				    while (rs.next()) {
				        this.name.add(rs.getString("name"));
				    }

				    rs.close();	
				} catch (SQLException ex) {
				    ex.printStackTrace();
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.uuid.put(name.get(i), rs.getString("uuid"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.ip.put(name.get(i), rs.getString("ip"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.email.put(name.get(i), rs.getString("email"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfa.put(name.get(i), rs.getBoolean("tfa"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaSecret.put(name.get(i), rs.getString("tfaSecret"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaRecoveryCode.put(name.get(i), rs.getInt("tfaRecoveryCode"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");

					    while (rs.next()) {
					        this.tfaSettingUp.put(name.get(i), rs.getBoolean("tfaSettingUp"));
					    }

					    rs.close();	
					} catch (SQLException ex) {
					    ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					instance.getMySQL().update("INSERT INTO authtools (name, uuid) VALUES ('"+name.get(i)+"', '"+uuid.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, ip) VALUES ('"+name.get(i)+"', '"+ip.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, email) VALUES ('"+name.get(i)+"', '"+email.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2fa) VALUES ('"+name.get(i)+"', '"+tfa.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2faSecret) VALUES ('"+name.get(i)+"', '"+tfaSecret.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('"+name.get(i)+"', '"+tfaRecoveryCode.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2faSettingUp) VALUES ('"+name.get(i)+"', '"+tfaSettingUp.get(name.get(i))+"');");
				}

				getSQLite().unload(true);

				return true;
			}

			if (importFrom.equalsIgnoreCase("mongodb")) {
				getMongoDB().connect(true);

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						name.add(found.getString("name"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						uuid.put(name.get(i), found.getString("uuid"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						ip.put(name.get(i), found.getString("ip"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						email.put(name.get(i), found.getString("email"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfa.put(name.get(i), found.getBoolean("2fa"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfaSecret.put(name.get(i), found.getString("2faSecret"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfaRecoveryCode.put(name.get(i), found.getInteger("2faRecoveryCode"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfaSettingUp.put(name.get(i), found.getBoolean("2faSettingUp"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					instance.getMySQL().update("INSERT INTO authtools (name, uuid) VALUES ('"+name.get(i)+"', '"+uuid.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, ip) VALUES ('"+name.get(i)+"', '"+ip.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, email) VALUES ('"+name.get(i)+"', '"+email.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2fa) VALUES ('"+name.get(i)+"', '"+tfa.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2faSecret) VALUES ('"+name.get(i)+"', '"+tfaSecret.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2faRecoveryCode) VALUES ('"+name.get(i)+"', '"+tfaRecoveryCode.get(name.get(i))+"');");
					instance.getMySQL().update("INSERT INTO authtools (name, 2faSettingUp) VALUES ('"+name.get(i)+"', '"+tfaSettingUp.get(name.get(i))+"');");
				}

				getMongoDB().disconnect(true);

				return true;
			}
		}

		return false;
	}
	
	public boolean importMongoDB() {
		if (importFrom.equalsIgnoreCase("mongodb"))
			return false;

		if (currentBackend.equalsIgnoreCase("mongodb")) {
			getMongoDB().connect(true);

			getMongoDB().getCollection().drop();

			if (importFrom.equalsIgnoreCase("mysql")) {
				if (!getMySQL().isConnected())
					getMySQL().connect(true);

				try {
					ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools");

				    while (rs.next()) {
				        this.name.add(rs.getString("name"));
				    }

				    rs.close();	
				} catch (SQLException ex) {
				    ex.printStackTrace();
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.uuid.put(name.get(i), rs.getString("uuid"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.ip.put(name.get(i), rs.getString("ip"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.email.put(name.get(i), rs.getString("email"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfa.put(name.get(i), rs.getBoolean("2fa"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaSecret.put(name.get(i), rs.getString("2faSecret"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaRecoveryCode.put(name.get(i), rs.getInt("2faRecoveryCode"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");

					    while (rs.next()) {
					        this.tfaSettingUp.put(name.get(i), rs.getBoolean("2faSettingUp"));
					    }

					    rs.close();	
					} catch (SQLException ex) {
					    ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document playerDoc = new Document("name", name.get(i));

					playerDoc.append("uuid", uuid.get(name.get(i)));
					playerDoc.append("ip", ip.get(name.get(i)));
					playerDoc.append("email", email.get(name.get(i)));
					playerDoc.append("2fa", tfa.get(name.get(i)));
					playerDoc.append("2faSecret", tfaSecret.get(name.get(i)));
					playerDoc.append("2faRecoveryCode", tfaRecoveryCode.get(name.get(i)));
					playerDoc.append("2faSettingUp", tfaSettingUp.get(name.get(i)));

		            getMongoDB().getCollection().insertOne(playerDoc);
				}

				getMySQL().disconnect(true);

				return true;
			}

			if (importFrom.equalsIgnoreCase("yaml")) {
				getYAML().createPlayerData(true);
				getYAML().loadPlayerData(true);
				
				for (String f : getYAML().getPlayerData().getKeys(false)) {
					name.add(f);
				}
				
				for (int i = 0; i < name.size(); i++) {
					uuid.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".uuid"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					ip.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".ip"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					email.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".email"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfa.put(name.get(i), getYAML().getPlayerData().getBoolean(name.get(i)+".2fa"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfaSecret.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".2faSecret"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfaRecoveryCode.put(name.get(i), getYAML().getPlayerData().getInt(name.get(i)+".2faRecoveryCode"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfaSettingUp.put(name.get(i), getYAML().getPlayerData().getBoolean(name.get(i)+".2faSettingUp"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					Document playerDoc = new Document("name", name.get(i));

					playerDoc.append("uuid", uuid.get(name.get(i)));
					playerDoc.append("ip", ip.get(name.get(i)));
					playerDoc.append("email", email.get(name.get(i)));
					playerDoc.append("2fa", tfa.get(name.get(i)));
					playerDoc.append("2faSecret", tfaSecret.get(name.get(i)));
					playerDoc.append("2faRecoveryCode", tfaRecoveryCode.get(name.get(i)));
					playerDoc.append("2faSettingUp", tfaSettingUp.get(name.get(i)));

		            getMongoDB().getCollection().insertOne(playerDoc);
				}
				
				return true;
			}

			if (importFrom.equalsIgnoreCase("sqlite")) {
				getSQLite().create(true);

				try {
					ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools");

				    while (rs.next()) {
				        this.name.add(rs.getString("name"));
				    }

				    rs.close();	
				} catch (SQLException ex) {
				    ex.printStackTrace();
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.uuid.put(name.get(i), rs.getString("uuid"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.ip.put(name.get(i), rs.getString("ip"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.email.put(name.get(i), rs.getString("email"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfa.put(name.get(i), rs.getBoolean("tfa"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaSecret.put(name.get(i), rs.getString("tfaSecret"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaRecoveryCode.put(name.get(i), rs.getInt("tfaRecoveryCode"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getSQLite().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");

					    while (rs.next()) {
					        this.tfaSettingUp.put(name.get(i), rs.getBoolean("tfaSettingUp"));
					    }

					    rs.close();	
					} catch (SQLException ex) {
					    ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document playerDoc = new Document("name", name.get(i));

					playerDoc.append("uuid", uuid.get(name.get(i)));
					playerDoc.append("ip", ip.get(name.get(i)));
					playerDoc.append("email", email.get(name.get(i)));
					playerDoc.append("2fa", tfa.get(name.get(i)));
					playerDoc.append("2faSecret", tfaSecret.get(name.get(i)));
					playerDoc.append("2faRecoveryCode", tfaRecoveryCode.get(name.get(i)));
					playerDoc.append("2faSettingUp", tfaSettingUp.get(name.get(i)));

		            getMongoDB().getCollection().insertOne(playerDoc);
				}

				getSQLite().unload(true);

				return true;
			}
		}

		return false;
	}

	public boolean importSQLite() {
		if (importFrom.equalsIgnoreCase("sqlite"))
			return false;

		if (currentBackend.equalsIgnoreCase("sqlite")) {
			if (importFrom.equalsIgnoreCase("mysql")) {
				if (!getMySQL().isConnected())
					getMySQL().connect(true);

				try {
					ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools");

				    while (rs.next()) {
				        this.name.add(rs.getString("name"));
				    }

				    rs.close();	
				} catch (SQLException ex) {
				    ex.printStackTrace();
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.uuid.put(name.get(i), rs.getString("uuid"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.ip.put(name.get(i), rs.getString("ip"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.email.put(name.get(i), rs.getString("email"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfa.put(name.get(i), rs.getBoolean("2fa"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaSecret.put(name.get(i), rs.getString("2faSecret"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");
						
						while (rs.next()) {
							this.tfaRecoveryCode.put(name.get(i), rs.getInt("2faRecoveryCode"));
						}
						
						rs.close();	
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					try {
						ResultSet rs = instance.getMySQL().getResult("SELECT * FROM authtools WHERE name='"+name.get(i)+"'");

					    while (rs.next()) {
					        this.tfaSettingUp.put(name.get(i), rs.getBoolean("2faSettingUp"));
					    }

					    rs.close();	
					} catch (SQLException ex) {
					    ex.printStackTrace();
					}
				}

				for (int i = 0; i < name.size(); i++) {
					instance.getSQLite().update("INSERT INTO authtools (name, uuid) VALUES ('"+name.get(i)+"', '"+uuid.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, ip) VALUES ('"+name.get(i)+"', '"+ip.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, email) VALUES ('"+name.get(i)+"', '"+email.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('"+name.get(i)+"', '"+tfa.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfaSecret) VALUES ('"+name.get(i)+"', '"+tfaSecret.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('"+name.get(i)+"', '"+tfaRecoveryCode.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('"+name.get(i)+"', '"+tfaSettingUp.get(name.get(i))+"');");
				}

				getMySQL().disconnect(true);

				return true;
			}

			if (importFrom.equalsIgnoreCase("yaml")) {
				getYAML().createPlayerData(true);
				getYAML().loadPlayerData(true);
				
				for (String f : getYAML().getPlayerData().getKeys(false)) {
					name.add(f);
				}
				
				for (int i = 0; i < name.size(); i++) {
					uuid.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".uuid"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					ip.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".ip"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					email.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".email"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfa.put(name.get(i), getYAML().getPlayerData().getBoolean(name.get(i)+".2fa"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfaSecret.put(name.get(i), getYAML().getPlayerData().getString(name.get(i)+".2faSecret"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfaRecoveryCode.put(name.get(i), getYAML().getPlayerData().getInt(name.get(i)+".2faRecoveryCode"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					tfaSettingUp.put(name.get(i), getYAML().getPlayerData().getBoolean(name.get(i)+".2faSettingUp"));
				}
				
				for (int i = 0; i < name.size(); i++) {
					instance.getSQLite().update("INSERT INTO authtools (name, uuid) VALUES ('"+name.get(i)+"', '"+uuid.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, ip) VALUES ('"+name.get(i)+"', '"+ip.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, email) VALUES ('"+name.get(i)+"', '"+email.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('"+name.get(i)+"', '"+tfa.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfaSecret) VALUES ('"+name.get(i)+"', '"+tfaSecret.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('"+name.get(i)+"', '"+tfaRecoveryCode.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('"+name.get(i)+"', '"+tfaSettingUp.get(name.get(i))+"');");
				}
				
				return true;
			}

			if (importFrom.equalsIgnoreCase("mongodb")) {
				getMongoDB().connect(true);

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						name.add(found.getString("name"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						uuid.put(name.get(i), found.getString("uuid"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						ip.put(name.get(i), found.getString("ip"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						email.put(name.get(i), found.getString("email"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfa.put(name.get(i), found.getBoolean("2fa"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfaSecret.put(name.get(i), found.getString("2faSecret"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfaRecoveryCode.put(name.get(i), found.getInteger("2faRecoveryCode"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					Document found = (Document) instance.getMongoDB().getCollection().find(new Document("name", name.get(i))).first();

					if (found != null) {
						tfaSettingUp.put(name.get(i), found.getBoolean("2faSettingUp"));
					}
				}

				for (int i = 0; i < name.size(); i++) {
					instance.getSQLite().update("INSERT INTO authtools (name, uuid) VALUES ('"+name.get(i)+"', '"+uuid.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, ip) VALUES ('"+name.get(i)+"', '"+ip.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, email) VALUES ('"+name.get(i)+"', '"+email.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfa) VALUES ('"+name.get(i)+"', '"+tfa.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfaSecret) VALUES ('"+name.get(i)+"', '"+tfaSecret.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfaRecoveryCode) VALUES ('"+name.get(i)+"', '"+tfaRecoveryCode.get(name.get(i))+"');");
					instance.getSQLite().update("INSERT INTO authtools (name, tfaSettingUp) VALUES ('"+name.get(i)+"', '"+tfaSettingUp.get(name.get(i))+"');");
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
