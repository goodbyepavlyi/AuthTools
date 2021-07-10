package pavlyi.authtools.bungee.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pavlyi.authtools.bungee.AuthToolsBungee;

public class BungeePlayerListener implements Listener {
    private final AuthToolsBungee instance = AuthToolsBungee.getInstance();

    private boolean isAuthServer(ServerInfo serverInfo) {
        return instance.getConfigHandler().AUTH_SERVERS.contains(serverInfo.getName());
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent e) {
        instance.getAuthLocked().add(e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        instance.getAuthLocked().remove(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(ChatEvent e) {
        if (e.isCancelled())
            return;

        if (e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();

            if (instance.getAuthLocked().contains(p.getName())) {
                if (e.getMessage().startsWith("/")) {
                    boolean enableCommand = false;

                    for (int i = 0; i < instance.getConfigHandler().WHITELISTED_COMMANDS.size(); i++) {
                        if (e.getMessage().toLowerCase().startsWith(instance.getConfigHandler().WHITELISTED_COMMANDS.get(i))) {
                            enableCommand = true;
                            break;
                        }
                    }

                    if (!enableCommand) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConnectingToServer(ServerConnectEvent e) {
        if (e.isCancelled())
            return;

        ProxiedPlayer p = e.getPlayer();
        boolean isAuthenticated = instance.getAuthLocked().contains(p.getName());

        // Skip logged users
        if (isAuthenticated)
            return;

        // Only check non auth servers
        if (isAuthServer(e.getTarget()))
            return;

        // If the player is not logged in and serverSwitchRequiresAuth is enabled,
        // cancel the connection
        if (instance.getConfigHandler().SERVER_SWITCH_REQUIRES_AUTH) {
            e.setCancelled(true);

            TextComponent reasonMessage = new TextComponent(
                    instance.color(instance.getConfigHandler().SERVER_SWITCH_REQUIRES_AUTH_KICK_MESSAGE));

            if (p.getServer() == null) {
                p.disconnect(reasonMessage);
            } else {
                p.sendMessage(reasonMessage);
            }
        }
    }
}
