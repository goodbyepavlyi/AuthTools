package pavlyi.authtools.spigot.events;

import net.md_5.bungee.event.EventHandler;

public class s {
    @EventHandler
    public void onAsyncRecoverEvent(AsyncRecoverEvent e) {
        e.getPlayer().sendMessage(e.getResettedType() + " has been recovered!");
    }
}
