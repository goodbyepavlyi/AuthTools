package pavlyi.authtools.handlers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import pavlyi.authtools.AuthTools;

public class BungeeCordAPI {
	private AuthTools instance = AuthTools.getInstance();

	public void sendAuthorizationValueToBungeeCord(Player p, int value) {
		if (instance.getConfigHandler().SETTINGS_BUNGEECORD) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);

			out.writeUTF("ToProxy");
			out.writeUTF("AuthToolsBungee");

			try {
				msgout.writeUTF(p.getName());
				msgout.writeShort(value);
			} catch (IOException exception) {
				exception.printStackTrace();
			}

			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());

			p.sendPluginMessage(instance, "BungeeCord", out.toByteArray());
		}
	}

	public void sendPlayerToServer(Player p) {
		if (instance.getConfigHandler().SETTINGS_BUNGEECORD && !instance.getConfigHandler().SETTINGS_SEND_PLAYER_TO.isEmpty()) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();

			out.writeUTF("Connect");
			out.writeUTF(instance.getConfigHandler().SETTINGS_SEND_PLAYER_TO);

			p.sendPluginMessage(instance, "BungeeCord", out.toByteArray());
		}
	}
}
