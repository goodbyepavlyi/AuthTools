package pavlyi.authtools.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pavlyi.authtools.spigot.enums.AuthenticationResult;
import pavlyi.authtools.spigot.handlers.User;

public class AsyncAuthenticateEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private Player player;
    private User user;
    private AuthenticationResult authenticationResult;
    private String code;

    public AsyncAuthenticateEvent(Player player, AuthenticationResult authenticationResult, String code) {
        this.player = player;
        this.user = new User(player.getName());
        this.authenticationResult = authenticationResult;
        this.code = code;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public User getUser() {
        return user;
    }

    public AuthenticationResult getAuthenticationResult() {
        return authenticationResult;
    }

    public String getCode() {
        return code;
    }
}
