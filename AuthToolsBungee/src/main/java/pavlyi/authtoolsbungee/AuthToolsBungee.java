package pavlyi.authtoolsbungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import pavlyi.authtoolsbungee.listeners.BungeeMessageListener;
import pavlyi.authtoolsbungee.listeners.BungeePlayerListener;

public class AuthToolsBungee extends Plugin {
	private static AuthToolsBungee instance;
	private PluginManager pluginManager;
	private ConfigHandler configHandler;

	private ArrayList<String> authLocked;

	public void onEnable() {
		instance = this;
		pluginManager = getProxy().getPluginManager();
		configHandler = new ConfigHandler();

		authLocked = new ArrayList<>();

		log("&f&m-------------------------");
		log("&r  &cAuthToolsBungee &fv"+getDescription().getVersion());
		log("&r  &cAuthor: &fPavlyi");
		log("&r  &cWebsite: &fhttp://pavlyi.eu");
		log("");

		getConfigHandler().createConfig();
		
		try {
			getPluginManager().registerListener(this, new BungeeMessageListener());
			log("&r  &aSuccess: &fListener &cBungeeMessageListener &fhas been registered!");
		} catch (Exception ex) {
			log("&r  &cError: &fListener &cBungeeMessageListener &fcouldn't get registered!");
		}

		try {
			getPluginManager().registerListener(this, new BungeePlayerListener());
			log("&r  &aSuccess: &fListener &cBungeePlayerListener &fhas been registered!");
		} catch (Exception ex) {
			log("&r  &cError: &fListener &cBungeePlayerListener &fcouldn't get registered!");
		}

		log("&f&m-------------------------");
	}

	public void onDisable() {
		log("&f&m-------------------------");
		log("&r  &cAuthToolsBungee &fv"+getDescription().getVersion());
		log("&r  &cAuthor: &fPavlyi");
		log("&r  &cWebsite: &fhttp://pavlyi.eu");
		log("");
		log("&r  &aSuccess: &cPlugin &fhas been disabled!");
		log("&f&m-------------------------");
	}

	public static void main(String[] args) {
		System.out.println("You need to run this plugin in BungeeCord server!");
	}

	public String color(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public void log(String message) {
		getProxy().getConsole().sendMessage(TextComponent.fromLegacyText(color("&f[&cAuthToolsBungee&f] "+message)));
	}

	public static AuthToolsBungee getInstance() {
		return instance;
	}
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public ArrayList<String> getAuthLocked() {
		return authLocked;
	}
}
