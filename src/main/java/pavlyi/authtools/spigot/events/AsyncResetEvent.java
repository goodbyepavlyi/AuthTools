package pavlyi.authtools.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.enums.InformationType;
import pavlyi.authtools.spigot.storages.Variables;

public class AsyncResetEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Player player;
    private final User user;
    private final InformationType informationType;

    public AsyncResetEvent(Player player, InformationType informationType) {
        this.player = player;
        this.user = Variables.getUser(player.getUniqueId());
        this.informationType = informationType;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() throws NullPointerException {
        return player;
    }

    public User getUser() {
        return user;
    }

    public InformationType getAccountResetType() {
        return informationType;
    }
}
