package pavlyi.authtools.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import pavlyi.authtools.AuthTools;

public class MessagesHandler {
	private AuthTools instance = AuthTools.getInstance();
	private File f = new File(instance.getDataFolder()+"/messages.yml");
	private FileConfiguration c = YamlConfiguration.loadConfiguration(f);


	public String NO_PERMISSIONS;
	public String PLUGIN_RELOADED;
	public String PLAYER_NOT_FOUND;
	public String ONLY_PLAYER_CAN_EXECUTE_COMMAND;

	public List<String> COMMANDS_AUTHTOOLS_HELPUSAGE = new ArrayList<String>();
	public List<String> COMMANDS_AUTHTOOLS_ABOUT = new ArrayList<String>();
	public List<String> COMMANDS_AUTHTOOLS_INFO = new ArrayList<String>();

	public String COMMANDS_AUTHTOOLS_RELOADUSAGE;
	public String COMMANDS_AUTHTOOLS_ABOUTUSAGE;
	public String COMMANDS_AUTHTOOLS_RESETUSAGE;
	public String COMMANDS_AUTHTOOLS_BACKENDUSAGE;
	public String COMMANDS_AUTHTOOLS_INFOUSAGE;
	public String COMMANDS_AUTHTOOLS_IMPORTUSAGE;
	public String COMMANDS_AUTHTOOLS_SETSPAWNUSAGE;
	public String COMMANDS_AUTHTOOLS_SETLOBBYUSAGE;

	public String COMMANDS_AUTHTOOLS_SETSPAWN;
	public String COMMANDS_AUTHTOOLS_SETLOBBY;

	public String COMMANDS_AUTHTOOLS_IMPORT_SUCESSFULLY_IMPORTED;
	public String COMMANDS_AUTHTOOLS_IMPORT_ERROR_WHILE_IMPORTING;
	public String COMMANDS_AUTHTOOLS_IMPORT_INCORRECT_TYPE;

	public String COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_2FA_ENABLED;
	public String COMMANDS_AUTHTOOLS_RESET_DISABLED_2FA;

	public String COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED;
	public String COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION;
	public String COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION;
	public String COMMANDS_AUTHTOOLS_BACKEND_WRONG_CONNECTION_TYPE;

	public List<String> COMMANDS_2FA_SETUP_AUTHAPP = new ArrayList<String>();
	public String COMMANDS_2FA_SETUP_ENABLED;
	public String COMMANDS_2FA_SETUP_DISABLED;
	public String COMMANDS_2FA_SETUP_SETUP_CANCELLED;
	public String COMMANDS_2FA_SETUP_INVALID_CODE;
	public String COMMANDS_2FA_SETUP_QRCODE_TITLE;
	public List<String> COMMANDS_2FA_SETUP_QRCODE_LORE = new ArrayList<String>();

	public String COMMANDS_2FA_RECOVER_USAGE;
	public String COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE;
	public String COMMANDS_2FA_RECOVER_RECOVERED;
	public String COMMANDS_2FA_RECOVER_2FA_DISABLED;

	public String COMMANDS_2FA_LOGIN_LOGIN_MESSAGE;
	public String COMMANDS_2FA_LOGIN_LOGGED_IN;
	public String COMMANDS_2FA_LOGIN_TIMED_OUT;
	public String COMMANDS_2FA_LOGIN_DENIED_COMMAND;
	public String COMMANDS_2FA_LOGIN_WRONG_CODE_KICK;
	public String COMMANDS_2FA_LOGIN_PLAYER_IS_ONLINE_KICK;







	public void createMessages(boolean silently) {
		if (!f.exists()) {
			instance.saveResource("messages.yml", false);

			if (!silently)
				instance.log("&r  &aSuccess: &cMessages &fhas been created!");
		}

		loadMessages(silently);
	}

	public void saveMessages() {
		try {
			c.save(f);
		} catch (IOException ex) {
			instance.log("&r  &cError: &fWhile saving &cmessages &fan error ocurred!");
			ex.printStackTrace();
			instance.getPluginManager().disablePlugin(instance);
		}
	}

	public void loadMessages(boolean silently) {
		try {
			getConfig().load(f);

			if (!silently)
				instance.log("&r  &aSuccess: &cMessages &fhas been loaded!");
		} catch (IOException | org.bukkit.configuration.InvalidConfigurationException ex) {
			instance.log("&r  &cError: &fWhile saving &cmessages &fan error ocurred!");
			ex.printStackTrace();
			instance.getPluginManager().disablePlugin(instance);
		}

		NO_PERMISSIONS = instance.color(getConfig().getString("noPermissions"));
		PLUGIN_RELOADED = instance.color(getConfig().getString("pluginReloaded"));
		PLAYER_NOT_FOUND = instance.color(getConfig().getString("playerNotFound"));
		ONLY_PLAYER_CAN_EXECUTE_COMMAND = instance.color(getConfig().getString("onlyPlayerCanExecuteCommand"));
		
		for (String tempMessage : getConfig().getStringList("commands.authtools.helpusage")) {
			COMMANDS_AUTHTOOLS_HELPUSAGE.add(instance.color(tempMessage));
		}
		
		for (String tempMessage : getConfig().getStringList("commands.authtools.about")) {
			COMMANDS_AUTHTOOLS_ABOUT.add(instance.color(tempMessage));
		}

		for (String tempMessage : getConfig().getStringList("commands.authtools.info")) {
			COMMANDS_AUTHTOOLS_INFO.add(instance.color(tempMessage));
		}

		COMMANDS_AUTHTOOLS_RELOADUSAGE = instance.color(getConfig().getString("commands.authtools.reloadusage"));
		COMMANDS_AUTHTOOLS_ABOUTUSAGE = instance.color(getConfig().getString("commands.authtools.aboutusage"));
		COMMANDS_AUTHTOOLS_RESETUSAGE = instance.color(getConfig().getString("commands.authtools.resetusage"));
		COMMANDS_AUTHTOOLS_BACKENDUSAGE = instance.color(getConfig().getString("commands.authtools.backendusage"));
		COMMANDS_AUTHTOOLS_INFOUSAGE = instance.color(getConfig().getString("commands.authtools.infousage"));
		COMMANDS_AUTHTOOLS_IMPORTUSAGE = instance.color(getConfig().getString("commands.authtools.importusage"));
		COMMANDS_AUTHTOOLS_SETSPAWNUSAGE = instance.color(getConfig().getString("commands.authtools.setspawnusage"));
		COMMANDS_AUTHTOOLS_SETLOBBYUSAGE = instance.color(getConfig().getString("commands.authtools.setlobbyusage"));

		COMMANDS_AUTHTOOLS_SETSPAWN = instance.color(getConfig().getString("commands.authtools.setspawn"));
		COMMANDS_AUTHTOOLS_SETLOBBY = instance.color(getConfig().getString("commands.authtools.setlobby"));

		COMMANDS_AUTHTOOLS_IMPORT_SUCESSFULLY_IMPORTED = instance.color(getConfig().getString("commands.authtools.import.sucessfullyImported"));
		COMMANDS_AUTHTOOLS_IMPORT_ERROR_WHILE_IMPORTING = instance.color(getConfig().getString("commands.authtools.import.errorWhileImporting"));
		COMMANDS_AUTHTOOLS_IMPORT_INCORRECT_TYPE = instance.color(getConfig().getString("commands.authtools.import.incorrectType"));

		COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_2FA_ENABLED = instance.color(getConfig().getString("commands.authtools.reset.playerDoesntHave2FAenabled"));
		COMMANDS_AUTHTOOLS_RESET_DISABLED_2FA = instance.color(getConfig().getString("commands.authtools.reset.disabled2FA"));

		COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED = instance.color(getConfig().getString("commands.authtools.backend.alreadyConnected"));
		COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION = instance.color(getConfig().getString("commands.authtools.backend.switchedToConnection"));
		COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION = instance.color(getConfig().getString("commands.authtools.backend.couldntSwitchConnection"));
		COMMANDS_AUTHTOOLS_BACKEND_WRONG_CONNECTION_TYPE = instance.color(getConfig().getString("commands.authtools.backend.wrongConnectionType"));

		for (String tempMessage : getConfig().getStringList("commands.2fa.setup.authApp")) {
			COMMANDS_2FA_SETUP_AUTHAPP.add(instance.color(tempMessage));
		}

		COMMANDS_2FA_SETUP_ENABLED = instance.color(getConfig().getString("commands.2fa.setup.enabled"));
		COMMANDS_2FA_SETUP_DISABLED = instance.color(getConfig().getString("commands.2fa.setup.disabled"));
		COMMANDS_2FA_SETUP_SETUP_CANCELLED = instance.color(getConfig().getString("commands.2fa.setup.setupCancelled"));
		COMMANDS_2FA_SETUP_INVALID_CODE = instance.color(getConfig().getString("commands.2fa.setup.invalidCode"));
		COMMANDS_2FA_SETUP_QRCODE_TITLE = instance.color(getConfig().getString("commands.2fa.setup.qrcode.title"));

		for (String tempMessage : getConfig().getStringList("commands.2fa.setup.qrcode.lore")) {
			COMMANDS_2FA_SETUP_QRCODE_LORE.add(instance.color(tempMessage));
		}

		COMMANDS_2FA_RECOVER_USAGE = instance.color(getConfig().getString("commands.2fa.recover.usage"));
		COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE = instance.color(getConfig().getString("commands.2fa.recover.invalidRecoveryCode"));
		COMMANDS_2FA_RECOVER_RECOVERED = instance.color(getConfig().getString("commands.2fa.recover.recovered"));
		COMMANDS_2FA_RECOVER_2FA_DISABLED = instance.color(getConfig().getString("commands.2fa.recover.2faDisabled"));

		COMMANDS_2FA_LOGIN_LOGIN_MESSAGE = instance.color(getConfig().getString("commands.2fa.login.loginMessage"));
		COMMANDS_2FA_LOGIN_LOGGED_IN = instance.color(getConfig().getString("commands.2fa.login.loggedIn"));
		COMMANDS_2FA_LOGIN_TIMED_OUT = instance.color(getConfig().getString("commands.2fa.login.timedOut"));
		COMMANDS_2FA_LOGIN_DENIED_COMMAND = instance.color(getConfig().getString("commands.2fa.login.deniedCommand"));
		COMMANDS_2FA_LOGIN_WRONG_CODE_KICK = instance.color(getConfig().getString("commands.2fa.login.wrongCodeKick"));
		COMMANDS_2FA_LOGIN_PLAYER_IS_ONLINE_KICK = instance.color(getConfig().getString("commands.2fa.login.playerIsOnlineKick"));
	}


	
	public void sendMessage(Player p, List<String> message) {
		for (String listMessage : message) {
			p.sendMessage(instance.color(listMessage));
		}
	}

	public void sendMessageConsole(CommandSender sender, List<String> message) {
		for (String listMessage : message) {
			sender.sendMessage(instance.color(listMessage));
		}
	}





	public File getFile() {
		return f;
	}

	public FileConfiguration getConfig() {
		return c;
	}
}

