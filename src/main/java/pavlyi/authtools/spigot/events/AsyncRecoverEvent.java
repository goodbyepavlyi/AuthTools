package pavlyi.authtools.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pavlyi.authtools.spigot.enums.InformationType;
import pavlyi.authtools.spigot.handlers.User;

public class AsyncRecoverEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private Player player;
    private User user;
    private int recoveryCode;
    private boolean sucessfullRecover;
    private InformationType resettedType;

    public AsyncRecoverEvent(Player player, int recoveryCode, boolean sucessfullRecover, InformationType resettedType) {
        this.player = player;
        this.user = new User(player.getName());
        this.sucessfullRecover = sucessfullRecover;
        this.resettedType = resettedType;
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

    public int getRecoveryCode() {
        return recoveryCode;
    }

    public boolean hasSucessfullyRecovered() {
        return sucessfullRecover;
    }

    public InformationType getResettedType() {
        return sucessfullRecover;
    }
}
