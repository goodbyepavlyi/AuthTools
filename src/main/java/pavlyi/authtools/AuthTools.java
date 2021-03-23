package pavlyi.authtools;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Sets.SetView;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import pavlyi.authtools.commands.AuthToolsCommand;
import pavlyi.authtools.commands.TFACommand;
import pavlyi.authtools.connections.MongoDB;
import pavlyi.authtools.connections.MySQL;
import pavlyi.authtools.connections.SQLite;
import pavlyi.authtools.connections.YAMLConnection;
import pavlyi.authtools.handlers.ConfigHandler;
import pavlyi.authtools.handlers.ImageRenderer;
import pavlyi.authtools.handlers.MessagesHandler;
import pavlyi.authtools.handlers.QRCreate;
import pavlyi.authtools.handlers.SpawnHandler;
import pavlyi.authtools.handlers.User;
import pavlyi.authtools.listeners.AuthMeListener;
import pavlyi.authtools.listeners.PlayerLoginListener;
import pavlyi.authtools.listeners.PlayerRegisterListener;
import pavlyi.authtools.listeners.StoreDataListener;
import pavlyi.authtools.listeners.UserSetupListener;

public class AuthTools extends JavaPlugin {
	private static AuthTools instance;
	private PluginManager pm;
	private ConfigHandler configHandler;
	private MessagesHandler messagesHandler;
	private MySQL mySQL;
	private SQLite sqlLite;
	private MongoDB mongoDB;
	private YAMLConnection yamlConnection;
	private GoogleAuthenticator googleAuthenticator;
	private SpawnHandler spawnHandler;
	private ArrayList<String> authLocked;
	private ArrayList<String> registerLocked;
	private HashMap<String, Location> spawnLocations;
	private HashMap<String, Integer> runnables;

	private String connectionType;
	private String updateURL;

	public void onEnable() {
		instance = this;
		pm = getServer().getPluginManager();
		configHandler = new ConfigHandler();
		messagesHandler = new MessagesHandler();
		mySQL = new MySQL();
		sqlLite = new SQLite();
		mongoDB = new MongoDB();
		yamlConnection = new YAMLConnection();
		googleAuthenticator = new GoogleAuthenticator();
		spawnHandler = new SpawnHandler();
		authLocked = new ArrayList<String>();
		registerLocked = new ArrayList<String>();
		spawnLocations = new HashMap<String, Location>();
		runnables = new HashMap<String, Integer>();
		updateURL = getUpdateURL();

		log("&f&m-------------------------");
		log("&r  &cAuthTools &fv"+getDescription().getVersion());
		log("&r  &cAuthor: &fPavlyi");
		log("&r  &cWebsite: &fhttp://pavlyi.eu");
		log("");

		createPluginFolders();
		getConfigHandler().createConfig();
		getMessagesHandler().createMessages();
		getSpawnHandler().createSpawnFile();

		connectionType = getConfigHandler().CONNECTION_TYPE;

		if (checkForUpdates()) {
			log("&r  &cWarning: &fYour plugin is outdated please update the plugin! &c(&f" + updateURL + "&c)");
		} else {
			log("&r  &aInfo: &fYour plugin is up-to-date!");
		}

		if (getConnectionType().equals("YAML"))
			getYamlConnection().createPlayerData();

		if (getConnectionType().equals("MYSQL"))
			getMySQL().connect();

		if (getConnectionType().equals("SQLITE"))
			getSQLite().create();

		if (getConnectionType().equals("MONGODB"))
			getMongoDB().connect();

		getCommand("authtools").setExecutor(new AuthToolsCommand());
		getCommand("authtools").setTabCompleter(new AuthToolsCommand());
		getCommand("2fa").setExecutor(new TFACommand());
		getCommand("2fa").setTabCompleter(new TFACommand());

		if (getCommand("authtools").isRegistered()) {
			log("&r  &aSuccess: &fCommand &c/authtools &fhas been registered!");
		} else {
			log("&r  &cError: &fCommand &c/authtools &couldn't get registered!");
		}

		if (getCommand("2fa").isRegistered()) {
			log("&r  &aSuccess: &fCommand &c/2fa &fhas been registered!");
		} else {
			log("&r  &cError: &fCommand &c/2fa &couldn't get registered!");
		}

		if (!isHooked()) {
			try {
				getPluginManager().registerEvents(new UserSetupListener(), this);
				log("&r  &aSuccess: &fListener &cUserSetupListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cUserSetupListener &fcouldn't get registered!");
			}

			try {
				getPluginManager().registerEvents(new StoreDataListener(), this);
				log("&r  &aSuccess: &fListener &cStoreDataListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cStoreDataListener &fcouldn't get registered!");
			}
			
			try {
				getPluginManager().registerEvents(new PlayerLoginListener(), this);
				log("&r  &aSuccess: &fListener &cPlayerLoginListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cPlayerLoginListener &fcouldn't get registered!");
			}

			try {
				getPluginManager().registerEvents(new PlayerRegisterListener(), this);
				log("&r  &aSuccess: &fListener &cPlayerRegisterListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cPlayerRegisterListener &fcouldn't get registered!");
			}

			log("&f&m-------------------------");

			for (Player p : getServer().getOnlinePlayers()) {
				User user = new User(p.getName());

				if (user.get2FA()) {
					if (getSpawnHandler().getSpawn("spawn") != null)
			    		p.teleport(getSpawnHandler().getSpawn("spawn"));

					getAuthLocked().add(p.getName());
					getSpawnLocations().put(p.getName(), p.getLocation());

					p.sendMessage(getMessagesHandler().COMMANDS_2FA_LOGIN_LOGIN_MESSAGE);

					int taskID;

					taskID = getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
						
						@Override
						public void run() {
							p.kickPlayer(getMessagesHandler().COMMANDS_2FA_LOGIN_TIMED_OUT);
						}
						
					}, 20 * getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT);

					runnables.put(p.getName(), taskID);
				} else {
					instance.getSpawnLocations().put(p.getName(), p.getLocation());

					if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN) {
						if (instance.getSpawnHandler().getSpawn("spawn") != null)
				    		p.teleport(instance.getSpawnHandler().getSpawn("spawn"));
					}

					instance.getRegisterLocked().add(p.getName());

					GoogleAuthenticatorKey secretKey = instance.getGoogleAuthenticator().createCredentials();

					user.setSettingUp2FA(true);
					user.set2FAsecret(secretKey.getKey());
					user.setRecoveryCode(false);		

					QRCreate.create(p, secretKey.getKey());

					for (String tempMessage : instance.getMessagesHandler().COMMANDS_2FA_SETUP_AUTHAPP) {
						tempMessage = tempMessage.replace("%secretkey%", secretKey.getKey());
						tempMessage = tempMessage.replace("%recoverycode%", String.valueOf(user.getRecoveryCode()));

						p.sendMessage(tempMessage);
					}

					Inventory inv = Bukkit.createInventory(p, 36);
					for (ItemStack is : p.getInventory().getContents()) {
						if (is != null)
							inv.addItem(is);
					}

					TFACommand.inventories.put(p, inv);

					ItemStack qrCodeMap = new ItemStack(Material.MAP);
					ItemMeta qrCodeMapIM = qrCodeMap.getItemMeta();
					qrCodeMapIM.setDisplayName(instance.getMessagesHandler().COMMANDS_2FA_SETUP_QRCODE_TITLE);
					qrCodeMapIM.setLore(instance.getMessagesHandler().COMMANDS_2FA_SETUP_QRCODE_LORE);
					qrCodeMap.setItemMeta(qrCodeMapIM);

					p.getInventory().clear();
					p.getInventory().setHeldItemSlot(0);
					p.getInventory().setItem(0, qrCodeMap);

					MapView view = Bukkit.getMap(p.getInventory().getItem(0).getDurability());
					Iterator<MapRenderer> iter;

					if (view.getRenderers().iterator() != null) {
						iter = view.getRenderers().iterator();

						while (iter.hasNext()) {
							view.removeRenderer(iter.next());
						}

						try {
							ImageRenderer renderer = new ImageRenderer(instance.getDataFolder().toString()+"/tempFiles/temp-qrcode-"+p.getName()+".png");
							view.addRenderer(renderer);

							new File(instance.getDataFolder().toString()+"/tempFiles/temp-qrcode-"+p.getName()+".png").delete();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					} else {
						view = Bukkit.getMap(p.getInventory().getItem(0).getDurability());
						iter = view.getRenderers().iterator();

						while (iter.hasNext()) {
							view.removeRenderer(iter.next());
						}

						try {
							ImageRenderer renderer = new ImageRenderer(instance.getDataFolder().toString()+"/tempFiles/temp-qrcode-"+p.getName()+".png");
							view.addRenderer(renderer);

							new File(instance.getDataFolder().toString()+"/tempFiles/temp-qrcode-"+p.getName()+".png").delete();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}

					if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT == 0) 
						return;

					int taskID;

					taskID = instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
						
						@Override
						public void run() {
							p.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_TIMED_OUT);
						}
						
					}, 20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT);

					instance.getRunnables().put(p.getName(), taskID);
				}

				

				user.create();
				user.setIP(p.getAddress());
				user.setUUID();
			}
		} else {
			if (!hookPlugin("AuthMe")) {
				log("&r  &cError: &fPlugin &cAuthMe &fwasn't found!");
				log("&f&m-------------------------");
				pm.disablePlugin(this);

				return;
			}

			try {
				getPluginManager().registerEvents(new AuthMeListener(), this);
				log("&r  &aSuccess: &fListener &cAuthMeListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cAuthMeListener &fcouldn't get registered!");
			}

			try {
				getPluginManager().registerEvents(new UserSetupListener(), this);
				log("&r  &aSuccess: &fListener &cUserSetupListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cUserSetupListener &fcouldn't get registered!");
			}

			try {
				getPluginManager().registerEvents(new StoreDataListener(), this);
				log("&r  &aSuccess: &fListener &cStoreDataListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cStoreDataListener &fcouldn't get registered!");
			}

			for (Player p : getServer().getOnlinePlayers()) {
				User user = new User(p.getName());

				user.create();
				user.setIP(p.getAddress());
				user.setUUID();
			}

			log("&f&m-------------------------");
		}
	}

	public void onDisable() {
		getServer().getScheduler().cancelTasks(instance);


		for (Player p : getServer().getOnlinePlayers()) {
			User user = new User(p.getName());

			if (user.getSettingUp2FA()) {
				user.setSettingUp2FA(false);
				user.set2FA(false);
				user.set2FAsecret(null);
				user.setRecoveryCode(true);

				if (TFACommand.inventories.containsKey(p)) {
					p.getInventory().clear();
					p.getInventory().setContents(TFACommand.inventories.get(p).getContents());
					TFACommand.inventories.remove(p);
				}
			}
		}

		if (getConnectionType().equals("MYSQL"))
			getMySQL().disconnect();

		if (getConnectionType().equals("SQLITE"))
			getSQLite().unload();

		if (getConnectionType().equals("MONGODB"))
			getMongoDB().disconnect();
	}

	public static void main(String[] args) {
        System.out.println("You need to run this plugin in Spigot Server!");
    }

	public static AuthTools getInstance() {
		return instance;
	}

	public String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public void log(String msg) {
		getServer().getConsoleSender().sendMessage(color("&f[&cAuthTools&f] " + msg));
	}

	public void reloadPlugin() {
		log("&f&m-------------------------");
		log("&r  &cAuthTools &fv"+getDescription().getVersion());
		log("&r  &cAuthor: &fPavlyi");
		log("&r  &cWebsite: &fhttp://pavlyi.eu");
		log("");

		createPluginFolders();
		getConfigHandler().createConfig();
		getMessagesHandler().createMessages();
		getSpawnHandler().createSpawnFile();

		connectionType = getConfigHandler().CONNECTION_TYPE;

		if (checkForUpdates()) {
			log("&r  &cWarning: &fYour plugin is outdated please update the plugin! &c(&f" + updateURL + "&c)");
		} else {
			log("&r  &aInfo: &fYour plugin is up-to-date!");
		}

		if (getConnectionType().equals("YAML"))
			getYamlConnection().createPlayerData();

		if (getConnectionType().equals("MYSQL"))
			getMySQL().connect();

		if (getConnectionType().equals("SQLITE"))
			getSQLite().create();

		if (getConnectionType().equals("MONGODB"))
			getMongoDB().connect();

		getCommand("authtools").setExecutor(new AuthToolsCommand());
		getCommand("authtools").setTabCompleter(new AuthToolsCommand());
		getCommand("2fa").setExecutor(new TFACommand());
		getCommand("2fa").setTabCompleter(new TFACommand());

		if (getCommand("authtools").isRegistered()) {
			log("&r  &aSuccess: &fCommand &c/authtools &fhas been registered!");
		} else {
			log("&r  &cError: &fCommand &c/authtools &couldn't get registered!");
		}

		if (getCommand("2fa").isRegistered()) {
			log("&r  &aSuccess: &fCommand &c/2fa &fhas been registered!");
		} else {
			log("&r  &cError: &fCommand &c/2fa &couldn't get registered!");
		}

		for (Player p : getServer().getOnlinePlayers()) {
			User user = new User(p.getName());

			user.create();
			user.setIP(p.getAddress());
			user.setUUID();
		}

		if (!isHooked()) {
			try {
				getPluginManager().registerEvents(new UserSetupListener(), this);
				log("&r  &aSuccess: &fListener &cUserSetupListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cUserSetupListener &fcouldn't get registered!");
			}

			try {
				getPluginManager().registerEvents(new StoreDataListener(), this);
				log("&r  &aSuccess: &fListener &cStoreDataListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cStoreDataListener &fcouldn't get registered!");
			}
			
			try {
				getPluginManager().registerEvents(new PlayerLoginListener(), this);
				log("&r  &aSuccess: &fListener &cPlayerLoginListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cPlayerLoginListener &fcouldn't get registered!");
			}

			try {
				getPluginManager().registerEvents(new PlayerRegisterListener(), this);
				log("&r  &aSuccess: &fListener &cPlayerRegisterListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cPlayerRegisterListener &fcouldn't get registered!");
			}

			log("&f&m-------------------------");
		} else {
			if (!hookPlugin("AuthMe")) {
				log("&r  &cError: &fPlugin &cAuthMe &fwasn't found!");
				log("&f&m-------------------------");
				pm.disablePlugin(this);

				return;
			}

			try {
				getPluginManager().registerEvents(new AuthMeListener(), this);
				log("&r  &aSuccess: &fListener &cAuthMeListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cAuthMeListener &fcouldn't get registered!");
			}

			try {
				getPluginManager().registerEvents(new UserSetupListener(), this);
				log("&r  &aSuccess: &fListener &cUserSetupListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cUserSetupListener &fcouldn't get registered!");
			}

			try {
				getPluginManager().registerEvents(new StoreDataListener(), this);
				log("&r  &aSuccess: &fListener &cStoreDataListener &fhas been registered!");
			} catch (Exception ex) {
				log("&r  &cError: &fListener &cStoreDataListener &fcouldn't get registered!");
			}

			log("&f&m-------------------------");
		}
	}
	
	public boolean hookPlugin(String pluginName) {
		return getPluginManager().getPlugin(pluginName) != null;
	}

	public boolean isHooked() {
		return getConfigHandler().SETTINGS_HOOK_INTO_AUTHME;
    }
	
	public void createPluginFolders() {
		new File(instance.getDataFolder()+"/").mkdirs();
		new File(instance.getDataFolder()+"/tempFiles/").mkdirs();
	}
	
	public String getConnectionType() {
		return this.connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public void switchConnection(String switchToConnection) {
		if (switchToConnection.equalsIgnoreCase("yaml")) {
			if (getConnectionType().equals("MYSQL")) {
				getMySQL().disconnect();
				getYamlConnection().createPlayerData();
				setConnectionType("YAML");
			}

			if (getConnectionType().equals("SQLITE")) {
				getSQLite().unload();
				getYamlConnection().createPlayerData();
				setConnectionType("YAML");
			}
		}
		
		if (switchToConnection.equalsIgnoreCase("mysql")) {
			if (getConnectionType().equals("YAML")) {
				getMySQL().connect();
				setConnectionType("MYSQL");
			}

			if (getConnectionType().equals("SQLITE")) {
				getSQLite().unload();
				getYamlConnection().createPlayerData();
				setConnectionType("MYSQL");
			}
		}

		if (switchToConnection.equalsIgnoreCase("sqlite")) {
			if (getConnectionType().equals("YAML")) {
				getSQLite().create();
				setConnectionType("SQLITE");
			}

			if (getConnectionType().equals("MYSQL")) {
				getMySQL().disconnect();
				getSQLite().create();
				setConnectionType("SQLITE");
			}
		}
	}
	
	@SuppressWarnings("resource")
	public boolean checkForUpdates() {
		URL url;
		Scanner scanner;

		try {
			url = new URL("https://pastebin.com/raw/gKLSZpfB");
			scanner = new Scanner(url.openStream());

	        if (!scanner.nextLine().equals(getDescription().getVersion())) {
	        	scanner.close();
	        	return true;
	        }
		} catch (MalformedURLException ex) {
			log("&r  &cError: &fWhile checking for an update error occured!");
			ex.printStackTrace();
		} catch (IOException ex) {
			log("&r  &cError: &fWhile checking for an update error occured!");
			ex.printStackTrace();
		}

		return false;
	}

	@SuppressWarnings("resource")
	public String getUpdateURL() {
		URL url;
		try {
			url = new URL("https://pastebin.com/raw/D8FvJXqT");
			Scanner scanner = new Scanner(url.openStream());
	        String updateURL = String.valueOf(scanner.nextLine());
	        scanner.close();

	        return updateURL;
		} catch (MalformedURLException ex) {
			log("&r  &cError: &fWhile getting an update url error occured!");
			ex.printStackTrace();
		} catch (IOException ex) {
			log("&r  &cError: &fWhile getting an update url error occured!");
			ex.printStackTrace();
		}

		return "";
	}






	public PluginManager getPluginManager() {
		return pm;
	}
	
	public ConfigHandler getConfigHandler() {
		return configHandler;
	}
	
	public MessagesHandler getMessagesHandler() {
		return messagesHandler;
	}

	public MySQL getMySQL() {
		return mySQL;
	}
	
	public SQLite getSQLite() {
		return sqlLite;
	}
	
	public MongoDB getMongoDB() {
		return mongoDB;
	}
	
	public YAMLConnection getYamlConnection() {
		return yamlConnection;
	}
	
	public GoogleAuthenticator getGoogleAuthenticator() {
		return googleAuthenticator;
	}
	
	public SpawnHandler getSpawnHandler() {
		return spawnHandler;
	}
	
	public ArrayList<String> getAuthLocked() {
		return authLocked;
	}
	
	public ArrayList<String> getRegisterLocked() {
		return registerLocked;
	}
	
	public HashMap<String, Location> getSpawnLocations() {
		return spawnLocations;
	}
	
	public HashMap<String, Integer> getRunnables() {
		return runnables;
	}
}
