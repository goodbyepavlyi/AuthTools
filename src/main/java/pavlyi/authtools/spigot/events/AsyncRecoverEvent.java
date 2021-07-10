package pavlyi.authtools.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.enums.InformationType;
import pavlyi.authtools.spigot.storages.Variables;

public class AsyncRecoverEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Player player;
    private final User user;
    private final boolean sucessfullRecover;
    private final InformationType resettedType;
    private int recoveryCode;

    public AsyncRecoverEvent(Player player, int recoveryCode, boolean sucessfullRecover, InformationType resettedType) {
        this.player = player;
        this.user = Variables.getUser(player.getUniqueId());
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
        return resettedType;
    }
}
