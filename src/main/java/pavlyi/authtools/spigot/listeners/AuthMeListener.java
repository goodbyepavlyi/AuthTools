package pavlyi.authtools.spigot.listeners;

import fr.xephi.authme.events.AuthMeAsyncPreLoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.handlers.AuthHandler;
import pavlyi.authtools.spigot.handlers.User;

public class AuthMeListener implements Listener {
    private final AuthTools instance = AuthTools.getInstance();

    @EventHandler
    public void requestAuthentication(AuthMeAsyncPreLoginEvent e) {
        Player player = e.getPlayer();
        User user = new User(player.getName());
        AuthHandler authHandler = new AuthHandler(player);

        if (user.get2FA()) {
            authHandler.requestLogin();
            return;
        }

        if (instance.getConfigHandler().HOOK_REGISTER_AFTER_AUTHENTICATION)
            if (!user.get2FA())
                authHandler.requestRegister();
    }
}