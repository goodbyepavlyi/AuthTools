package pavlyi.authtools.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pavlyi.authtools.spigot.handlers.User;

public class AsyncLoginEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Player player;
    private final User user;
    private final int code;

    public AsyncLoginEvent(Player player, int code) {
        this.player = player;
        this.user = new User(player.getName());
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

    public int getCode() {
        return code;
    }
}
