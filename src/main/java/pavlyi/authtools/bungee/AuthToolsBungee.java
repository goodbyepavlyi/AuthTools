package pavlyi.authtools.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import pavlyi.authtools.bungee.listeners.BungeeMessageListener;
import pavlyi.authtools.bungee.listeners.BungeePlayerListener;
import pavlyi.authtools.spigot.Updater;

import java.util.ArrayList;

public class AuthToolsBungee extends Plugin {
    private static AuthToolsBungee instance;
    boolean printDisableMessage;
    private PluginManager pluginManager;
    private ConfigHandler configHandler;
    private ArrayList<String> authLocked;

    public static void main(String[] args) {
        System.out.println("You need to run this plugin in BungeeCord server!");
    }

    public static AuthToolsBungee getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        pluginManager = getProxy().getPluginManager();
        configHandler = new ConfigHandler();

        authLocked = new ArrayList<>();

        log("&f&m-------------------------");
        log("&r  &cAuthToolsBungee &fv" + getDescription().getVersion());
        log("&r  &cAuthor: &f" + getDescription().getAuthor());
        log("&r  &cWebsite: &fhttps://pavlyi.eu");
        log("");

        Updater updater = new Updater(getDescription().getVersion());
        updater.checkForUpdate();
        switch (updater.getResult()) {
            case UPDATE_FOUND:
                String downloadUrl = updater.getDownloadURL();

                if (downloadUrl == null)
                    downloadUrl = "Link not found";

                log("&r  &cWarning: &fYour plugin is outdated please update the plugin! &c(&f" + downloadUrl + "&c)");

                break;

            case BAD_ID:
                log("&r  &cError: &fAn error occurred while looking for a resource on Spigot!");
                break;

            case NO_UPDATE:
                log("&r  &aInfo: &fYour plugin is up-to-date!");
                break;
        }

        getConfigHandler().create();

        String configCheck = getConfigHandler().check();
        if (configCheck != null) {
            log("&r  &cError: &fInvalid config!");
            log("&r         &fThe plugin has been &cdisabled&f, because there is missing content in the messages,");
            log("&r         &fmissing line \"&c" + configCheck + "&f\"");
            log("&f&m-------------------------");

            printDisableMessage = true;
            onDisable();

            return;
        }

        getConfigHandler().load();

        try {
            getPluginManager().registerListener(this, new BungeeMessageListener());
        } catch (Exception ex) {
            log("&r  &cError: &fListener &cBungeeMessageListener &fcouldn't get registered!");
            ex.printStackTrace();
        }

        try {
            getPluginManager().registerListener(this, new BungeePlayerListener());
        } catch (Exception ex) {
            log("&r  &cError: &fListener &cBungeePlayerListener &fcouldn't get registered!");
            ex.printStackTrace();
        }

        log("&f&m-------------------------");
    }

    public void onDisable() {
        if (!printDisableMessage) {
            log("&f&m-------------------------");
            log("&r  &cAuthToolsBungee &fv" + getDescription().getVersion());
            log("&r  &cAuthor: &f" + getDescription().getAuthor());
            log("&r  &cWebsite: &fhttps://pavlyi.eu");
            log("");

            Updater updater = new Updater(getDescription().getVersion());
            updater.checkForUpdate();
            switch (updater.getResult()) {
                case UPDATE_FOUND:
                    String downloadUrl = updater.getDownloadURL();

                    if (downloadUrl == null)
                        downloadUrl = "Link not found";

                    log("&r  &cWarning: &fYour plugin is outdated please update the plugin! &c(&f" + downloadUrl + "&c)");

                    break;

                case BAD_ID:
                    log("&r  &cError: &fAn error occurred while looking for a resource on Spigot!");
                    break;

                case NO_UPDATE:
                    log("&r  &aInfo: &fYour plugin is up-to-date!");
                    break;
            }

            log("&r  &aSuccess: &cPlugin &fhas been disabled!");
            log("&f&m-------------------------");
        }
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void log(String message) {
        getProxy().getConsole().sendMessage(TextComponent.fromLegacyText(color("&f[&cAuthToolsBungee&f] " + message)));
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
