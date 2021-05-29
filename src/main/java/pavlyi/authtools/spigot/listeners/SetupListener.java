package pavlyi.authtools.spigot.listeners;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.enums.VersionType;
import pavlyi.authtools.spigot.handlers.User;
import pavlyi.authtools.spigot.handlers.VariablesHandler;

public class SetupListener implements Listener {
    private final AuthTools instance = AuthTools.getInstance();

    @EventHandler
    public void checkCode(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        User user = new User(player.getName());
        String message = e.getMessage().replaceAll(" ", "");
        int code;

        if (user.getSettingUp2FA() && !user.hasToBeAuthenticated()) {
            e.setCancelled(true);

            try {
                code = Integer.parseInt(message);

                if (new GoogleAuthenticator().authorize(user.get2FAsecret(), code)) {
                    user.set2FA(true);
                    user.setSettingUp2FA(false);

                    if (!VariablesHandler.getVersion().equals(VersionType.ONE_NINE))
                        if (VariablesHandler.getPlayerInventories().containsKey(player.getUniqueId())) {
                            player.getInventory().clear();
                            player.getInventory().setContents(VariablesHandler.getPlayerInventories().get(player.getUniqueId()).getContents());

                            VariablesHandler.getPlayerInventories().remove(player.getUniqueId());
                        }

                    player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_ENABLED);
                    return;
                }

                player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_INVALID_CODE);
            } catch (NumberFormatException ex) {
                player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_INVALID_CODE);
            }
        }
    }

    @EventHandler
    public void disableSetup(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        User user = new User(player.getName());

        if (user.getSettingUp2FA()) {
            user.setSettingUp2FA(false);
            user.set2FA(false);
            user.set2FAsecret(null);
            user.setRecoveryCode(true);

            if (!VariablesHandler.getVersion().equals(VersionType.ONE_NINE))
                if (VariablesHandler.getPlayerInventories().containsKey(player.getUniqueId())) {
                    player.getInventory().clear();
                    player.getInventory().setContents(VariablesHandler.getPlayerInventories().get(player.getUniqueId()).getContents());

                    VariablesHandler.getPlayerInventories().remove(player.getUniqueId());
                }
        }
    }
}
