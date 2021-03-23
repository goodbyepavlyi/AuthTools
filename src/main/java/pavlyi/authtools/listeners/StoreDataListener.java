package pavlyi.authtools.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import pavlyi.authtools.AuthTools;
import pavlyi.authtools.handlers.User;

public class StoreDataListener implements Listener {
	private AuthTools instance = AuthTools.getInstance();

	@EventHandler
	public void storeData(PlayerJoinEvent e) {
		User user = new User(e.getPlayer().getName());

		user.create();
		user.setIP(e.getPlayer().getAddress());
		user.setUUID();
	}
}
