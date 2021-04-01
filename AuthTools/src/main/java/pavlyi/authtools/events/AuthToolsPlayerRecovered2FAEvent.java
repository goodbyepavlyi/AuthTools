package pavlyi.authtools.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import pavlyi.authtools.handlers.User;

public class AuthToolsPlayerRecovered2FAEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private Player player;
	private User user;
	private int recoveryCode;
	private boolean recovered2FA;

	public AuthToolsPlayerRecovered2FAEvent(Player player, int recoveryCode, boolean recovered2FA) {
		this.player = player;
		this.user = new User(player.getName());
		this.recoveryCode = recoveryCode;
		this.recovered2FA = recovered2FA;
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
	
	public int getRecoveryCode() {
		return recoveryCode;
	}

	public boolean hasRecovered2FA() {
		return recovered2FA;
	}
}
