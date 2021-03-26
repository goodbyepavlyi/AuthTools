package pavlyi.authtools;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.connorlinfoot.titleapi.TitleAPI;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import pavlyi.authtools.commands.TFACommand;
import pavlyi.authtools.connections.MongoDB;
import pavlyi.authtools.connections.MySQL;
import pavlyi.authtools.connections.YAMLConnection;
import pavlyi.authtools.handlers.ActionBarAPI;
import pavlyi.authtools.handlers.ConfigHandler;
import pavlyi.authtools.handlers.ImageRenderer;
import pavlyi.authtools.handlers.MessagesHandler;
import pavlyi.authtools.handlers.QRCreate;
import pavlyi.authtools.handlers.SpawnHandler;
import pavlyi.authtools.handlers.User;

public class AuthToolsApi {
	private static AuthTools instance = AuthTools.getInstance();

	public static AuthTools getInstance() {
		return instance;
	}


	// Classes
	public static ConfigHandler getConfig() {
		return instance.getConfigHandler();
	}

	public static MessagesHandler getMessages() {
		return instance.getMessagesHandler();
	}

	public static MySQL getMySQL() {
		return instance.getMySQL();
	}

	public static MongoDB getMongoDB() {
		return instance.getMongoDB();
	}

	public static YAMLConnection getYAMLConnection() {
		return instance.getYamlConnection();
	}
	
	public static GoogleAuthenticator getGoogleAuthenticator() {
		return instance.getGoogleAuthenticator();
	}

	public static SpawnHandler getSpawnHandler() {
		return instance.getSpawnHandler();
	}


	// Misc
	public static boolean hasAnUpdate() {
		return instance.checkForUpdates();
	}

	public static boolean isHookedIntoAuthMe() {
		return instance.isHooked();
	}

	public static User getUser(String playerName) {
		return new User(playerName);
	}


	// Set
	public static void setSpawn(Location spawn) {
		instance.getSpawnHandler().createSpawn("spawn", spawn);
	}

	public static void setLobby(Location lobby) {
		instance.getSpawnHandler().createSpawn("lobby", lobby);
	}


	// Login & Register
	public static void requireLogin(Player player) {
		User user = new User(player.getName());
		
		if (!user.get2FA())
			return;

		instance.getSpawnLocations().put(player.getName(), player.getLocation());

		if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN) {
			if (instance.getSpawnHandler().getSpawn("spawn") != null)
	    		player.teleport(instance.getSpawnHandler().getSpawn("spawn"));
		}

		instance.getAuthLocked().add(player.getName());

		player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_LOGIN_MESSAGE);
		
		TitleAPI.clearTitle(player);
		if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_ENABLE) {
			if (!instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_USE_IN_LOGIN)
				return;

			TitleAPI.sendTitle(player, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEIN, 20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEOUT, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_TITLE);
		}
		
		if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_ENABLE) {
			if (!instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_USE_IN_LOGIN)
				return;

			TitleAPI.sendSubtitle(player, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEIN, 20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEOUT, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_SUBTITLE);
		}

		if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_ENABLE) {
			if (!instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_USE_IN_LOGIN)
				return;

			int taskID;

			taskID = instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
				
				@Override
				public void run() {
					ActionBarAPI.sendActionBar(player, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_ACTIONBAR);
				}
			}, 0, 20);

			instance.getActionBarRunnables().put(player.getName(), taskID);
		}

		if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT != 0) {
			int taskID;

			taskID = instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

				@Override
				public void run() {
					player.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_TIMED_OUT);
				}

			}, 20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT);

			instance.getRunnables().put(player.getName(), taskID);
		}
	}

	public static void requireRegister(Player player) {
		User user = new User(player.getName());

		if (user.get2FA()) {
			return;
		}

		instance.getSpawnLocations().put(player.getName(), player.getLocation());

		if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN) {
			if (instance.getSpawnHandler().getSpawn("spawn") != null)
				player.teleport(instance.getSpawnHandler().getSpawn("spawn"));
		}

		instance.getRegisterLocked().add(player.getName());

		GoogleAuthenticatorKey secretKey = instance.getGoogleAuthenticator().createCredentials();

		user.setSettingUp2FA(true);
		user.set2FAsecret(secretKey.getKey());
		user.setRecoveryCode(false);

		QRCreate.create(player, secretKey.getKey());

		for (String tempMessage : instance.getMessagesHandler().COMMANDS_2FA_SETUP_AUTHAPP) {
			tempMessage = tempMessage.replace("%secretkey%", secretKey.getKey());
			tempMessage = tempMessage.replace("%recoverycode%", String.valueOf(user.getRecoveryCode()));

			player.sendMessage(tempMessage);
		}

		Inventory inv = Bukkit.createInventory(player, 36);
		for (ItemStack is : player.getInventory().getContents()) {
			if (is != null)
				inv.addItem(is);
		}

		TFACommand.inventories.put(player, inv);

		ItemStack qrCodeMap = new ItemStack(Material.MAP);
		ItemMeta qrCodeMapIM = qrCodeMap.getItemMeta();
		qrCodeMapIM.setDisplayName(instance.getMessagesHandler().COMMANDS_2FA_SETUP_QRCODE_TITLE);
		qrCodeMapIM.setLore(instance.getMessagesHandler().COMMANDS_2FA_SETUP_QRCODE_LORE);
		qrCodeMap.setItemMeta(qrCodeMapIM);

		player.getInventory().clear();
		player.getInventory().setHeldItemSlot(0);
		player.getInventory().setItem(0, qrCodeMap);

		MapView view = Bukkit.getMap(player.getInventory().getItem(0).getDurability());
		Iterator<MapRenderer> iter;

		if (view.getRenderers().iterator() != null) {
			iter = view.getRenderers().iterator();

			while (iter.hasNext()) {
				view.removeRenderer(iter.next());
			}

			try {
				ImageRenderer renderer = new ImageRenderer(
						instance.getDataFolder().toString() + "/tempFiles/temp-qrcode-" + player.getName() + ".png");
				view.addRenderer(renderer);

				new File(instance.getDataFolder().toString() + "/tempFiles/temp-qrcode-" + player.getName() + ".png")
						.delete();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			view = Bukkit.getMap(player.getInventory().getItem(0).getDurability());
			iter = view.getRenderers().iterator();

			while (iter.hasNext()) {
				view.removeRenderer(iter.next());
			}

			try {
				ImageRenderer renderer = new ImageRenderer(
						instance.getDataFolder().toString() + "/tempFiles/temp-qrcode-" + player.getName() + ".png");
				view.addRenderer(renderer);

				new File(instance.getDataFolder().toString() + "/tempFiles/temp-qrcode-" + player.getName() + ".png")
						.delete();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		TitleAPI.clearTitle(player);
		if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_ENABLE) {
			if (!instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_USE_IN_REGISTER)
				return;

			TitleAPI.sendTitle(player, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEIN,
					20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT,
					instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEOUT,
					instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_TITLE);
		}

		if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_ENABLE) {
			if (!instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_USE_IN_REGISTER)
				return;

			TitleAPI.sendSubtitle(player, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEIN,
					20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT,
					instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEOUT,
					instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_SUBTITLE);
		}

		if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_ENABLE) {
			if (!instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_USE_IN_REGISTER)
				return;

			int taskID;

			taskID = instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {

				@Override
				public void run() {
					ActionBarAPI.sendActionBar(player, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_ACTIONBAR);
				}
			}, 0, 20);

			instance.getActionBarRunnables().put(player.getName(), taskID);
		}

		if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT != 0) {
			int taskID;

			taskID = instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

				@Override
				public void run() {
					player.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_TIMED_OUT);
				}

			}, 20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT);

			instance.getRunnables().put(player.getName(), taskID);
		}
	}
	
	public static boolean isLogged(Player player) {
		return instance.getAuthLocked().contains(player.getName());
	}

	public static boolean isRegistered(Player player) {
		return instance.getRegisterLocked().contains(player.getName());
	}
}
