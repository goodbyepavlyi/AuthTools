package pavlyi.authtools.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import pavlyi.authtools.handlers.User;

public class AuthToolsPlayerLoggedEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private Player player;
	private User user;
	private int verificationCode;

	public AuthToolsPlayerLoggedEvent(Player player, int verificationCode) {
		this.player = player;
		this.user = new User(player.getName());
		this.verificationCode = verificationCode;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}





	
	public Player getPlayer() {
		return player;
	}

	public User getUser() {
		return user;
	}

	public int getVerificationCode() {
		return verificationCode;
	}
}
