package pavlyi.authtools.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import pavlyi.authtools.AuthTools;
import pavlyi.authtools.commands.TFACommand;
import pavlyi.authtools.handlers.User;

public class UserSetupListener implements Listener {
	private AuthTools instance = AuthTools.getInstance();

	@EventHandler
	public void checkCode(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		User user = new User(p.getName());
		String message = e.getMessage().replaceAll("\\s", "");
		int code = 0;

		if (user.getSettingUp2FA() && !instance.getRegisterLocked().contains(p.getName())) {
			e.setCancelled(true);

	        try {
	        	code = Integer.parseInt(message);

		        if (instance.getGoogleAuthenticator().authorize(user.get2FAsecret(), code)) {
		            user.set2FA(true);
		            user.setSettingUp2FA(false);

		            if (TFACommand.inventories.containsKey(p)) {
						p.getInventory().clear();
						p.getInventory().setContents(TFACommand.inventories.get(p).getContents());
						TFACommand.inventories.remove(p);
					}

		            p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_ENABLED);
		            return;
		        }

		        p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_INVALID_CODE);
	        } catch (NumberFormatException ex) {
	        	p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_INVALID_CODE);
	        }
		}
	}

	@EventHandler
	public void disableSetup(PlayerQuitEvent e) {
		Player p = e.getPlayer();
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
}
