package pavlyi.authtools.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pavlyi.authtools.spigot.handlers.User;

public class AsyncRegisterEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Player player;
    private final User user;
    private String code;
    private String secretKey;

    public AsyncRegisterEvent(Player player, String code, String secretKey) {
        this.player = player;
        this.user = new User(player.getName());
        this.code = code;
        this.secretKey = secretKey;
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

    public String getCode() {
        return code;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
