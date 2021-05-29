package pavlyi.authtools.spigot.apis;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import pavlyi.authtools.spigot.AuthTools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeCordAPI {
    private static final AuthTools instance = AuthTools.getInstance();

    public static void sendAuthorizationValueToBungeeCord(Player player, boolean loggedIn) {
        send(player, "AuthToolsBungee", player.getName(), (loggedIn ? 1 : 0));
    }

    public static void sendPlayerToServer(Player p, String serverName) {
        if (instance.getConfigHandler().HOOK_BUNGEECORD) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("Connect");
            out.writeUTF(serverName);

            p.sendPluginMessage(instance, "BungeeCord", out.toByteArray());
        }
    }

    public static void send(Player player, String subChannel, String data, int value) {
        if (instance.getConfigHandler().HOOK_BUNGEECORD) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);

            out.writeUTF("ToProxy");
            out.writeUTF(subChannel);

            try {
                msgout.writeUTF(data);
                msgout.writeShort(value);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());

            player.sendPluginMessage(instance, "BungeeCord", out.toByteArray());
        }
    }
}
