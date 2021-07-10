package pavlyi.authtools.bungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pavlyi.authtools.bungee.AuthToolsBungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BungeeMessageListener implements Listener {
    private final AuthToolsBungee instance = AuthToolsBungee.getInstance();

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (e.isCancelled())
            return;

        // Check if the message is for a server (ignore client messages)
        if (!e.getTag().equals("BungeeCord"))
            return;

        // Check if a player is not trying to send us a fake message
        if (!(e.getSender() instanceof Server))
            return;

        // Read the plugin message
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

        in.readUTF(); // Skip ToProxy

        // Let's check the subchannel
        if (!in.readUTF().equals("AuthToolsBungee"))
            return;

        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);

        DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

        try {
            String playerName = msgin.readUTF();
            boolean removePlayer = String.valueOf(msgin.readShort()).equals("1");

            if (removePlayer)
                instance.getAuthLocked().remove(playerName);
            else
                instance.getAuthLocked().add(playerName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
