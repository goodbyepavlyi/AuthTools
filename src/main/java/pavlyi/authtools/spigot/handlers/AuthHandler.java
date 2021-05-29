package pavlyi.authtools.spigot.handlers;

import com.connorlinfoot.titleapi.TitleAPI;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.apis.ActionBarAPI;
import pavlyi.authtools.spigot.apis.BungeeCordAPI;
import pavlyi.authtools.spigot.enums.AuthenticationResult;
import pavlyi.authtools.spigot.enums.VersionType;
import pavlyi.authtools.spigot.events.AsyncAuthenticateEvent;
import pavlyi.authtools.spigot.events.AsyncLoginEvent;
import pavlyi.authtools.spigot.events.AsyncRegisterEvent;
import pavlyi.authtools.spigot.qrcode.ImageRenderer;
import pavlyi.authtools.spigot.qrcode.QRCreator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@SuppressWarnings("deprecation")
public class AuthHandler {
    private final AuthTools instance = AuthTools.getInstance();

    private final Player player;
    private final User user;

    public AuthHandler(Player player) {
        this.player = player;
        this.user = new User(player.getName());
    }

    public void requestAuthentication() {
        if (isRegistered())
            requestLogin();

        if (!isRegistered())
            requestRegister();
    }

    public void requestRegister() {
        VariablesHandler.getPlayerSpawnLocations().remove(player.getUniqueId());

        VariablesHandler.getPlayerSpawnLocations().put(player.getUniqueId(), player.getLocation());

        if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN)
            if (VariablesHandler.getSpawn("authenticationSpawn") != null)
                player.teleport(VariablesHandler.getSpawn("authenticationSpawn"));

        user.needsToBeAuthenticated(true);

        GoogleAuthenticatorKey googleAuthenticatorKey = new GoogleAuthenticator().createCredentials();

        user.setSettingUp2FA(true);
        user.set2FAsecret(googleAuthenticatorKey.getKey());
        user.setRecoveryCode(false);

        QRCreator.create(player, GoogleAuthenticatorQRGenerator.getOtpAuthURL(instance.getConfigHandler().QRCODE_NAME, player.getName(), googleAuthenticatorKey));

        BungeeCordAPI.sendAuthorizationValueToBungeeCord(player, false);

        for (String setupIntroduction : instance.getMessagesHandler().COMMANDS_2FA_SETUP_AUTHAPP) {
            setupIntroduction = setupIntroduction.replace("%secretkey%", googleAuthenticatorKey.getKey());
            setupIntroduction = setupIntroduction.replace("%recoverycode%", String.valueOf(user.getRecoveryCode()));

            player.sendMessage(setupIntroduction);
        }

        if (!VariablesHandler.getVersion().equals(VersionType.ONE_NINE))
            VariablesHandler.getPlayerInventories().put(player.getUniqueId(), player.getInventory());

        ItemStack qrCodeMapItem = new ItemStack(Material.MAP);
        ItemMeta qrCodeMapItemMeta = qrCodeMapItem.getItemMeta();
        qrCodeMapItemMeta.setDisplayName(instance.getMessagesHandler().COMMANDS_2FA_SETUP_QRCODE_TITLE);
        qrCodeMapItemMeta.setLore(instance.getMessagesHandler().COMMANDS_2FA_SETUP_QRCODE_LORE);
        qrCodeMapItem.setItemMeta(qrCodeMapItemMeta);

        player.getInventory().clear();
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setItem(0, qrCodeMapItem);

        MapView mapView = Bukkit.getMap(qrCodeMapItem.getDurability());
        Iterator<MapRenderer> iterator;

        iterator = mapView.getRenderers().iterator();

        while (iterator.hasNext()) {
            mapView.removeRenderer(iterator.next());
        }

        try {
            String qrCodePath = instance.getDataFolder().toString() + File.separator + "tempFiles" + File.separator + "temp-qrcode-" + player.getName() + ".png";
            ImageRenderer imageRenderer = new ImageRenderer(qrCodePath);
            mapView.addRenderer(imageRenderer);

            new File(qrCodePath).delete();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        TitleAPI.clearTitle(player);

        if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_ENABLE
                && instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_USE_IN_REGISTER) {
            int fadeIn = instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEIN;
            int stay = 1000000000;
            int fadeOut = instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEOUT;

            TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_TITLE, null);
        }

        if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_ENABLE
                && instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_USE_IN_REGISTER) {
            int fadeIn = instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEIN;
            int stay = 1000000000;
            int fadeOut = instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEOUT;

            TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_SUBTITLE, null);
        }

        if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_ENABLE
                && instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_USE_IN_REGISTER) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!user.hasToBeAuthenticated()) {
                        TitleAPI.clearTitle(player);
                        cancel();
                        return;
                    }

                    ActionBarAPI.sendActionBar(player, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_REGISTER_ACTIONBAR);
                }
            }.runTaskTimer(instance, 0, 20);
        }

        if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT != 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (user.hasToBeAuthenticated()) {
                        player.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_TIMED_OUT);
                    }
                }
            }.runTaskLater(instance, (20L * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT));
        }
    }

    public void requestLogin() {
        VariablesHandler.getPlayerSpawnLocations().remove(player.getUniqueId());

        VariablesHandler.getPlayerSpawnLocations().put(player.getUniqueId(), player.getLocation());

        if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN)
            if (VariablesHandler.getSpawn("authenticationSpawn") != null)
                player.teleport(VariablesHandler.getSpawn("authenticationSpawn"));

        user.needsToBeAuthenticated(true);

        BungeeCordAPI.sendAuthorizationValueToBungeeCord(player, false);

        player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_LOGIN_MESSAGE);

        TitleAPI.clearTitle(player);

        if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_ENABLE
                && instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_USE_IN_LOGIN) {
            int fadeIn = instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEIN;
            int stay = 1000000000;
            int fadeOut = instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEOUT;

            TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_TITLE, null);
        }

        if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_ENABLE
                && instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_USE_IN_LOGIN) {
            int fadeIn = instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEIN;
            int stay = 1000000000;
            int fadeOut = instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEOUT;

            TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_SUBTITLE, null);
        }

        if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_ENABLE
                && instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_USE_IN_LOGIN) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!user.hasToBeAuthenticated()) {
                        TitleAPI.clearTitle(player);
                        cancel();
                        return;
                    }

                    ActionBarAPI.sendActionBar(player, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_ACTIONBAR);
                }
            }.runTaskTimer(instance, 0, 20);
        }

        if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT != 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (user.hasToBeAuthenticated()) {
                        player.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_TIMED_OUT);
                    }
                }
            }.runTaskLater(instance, (20L * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT));
        }
    }

    public AuthenticationResult register(String code) {
        if (user.get2FA()) {
            instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.TFA_ALREADY_REGISTERED, String.valueOf(code)));
            return AuthenticationResult.TFA_ALREADY_REGISTERED;
        }

        try {
            if (user.get2FAsecret() != null) {
                if (new GoogleAuthenticator().authorize(user.get2FAsecret(), Integer.parseInt(code))) {
                    TitleAPI.clearTitle(player);

                    if (VariablesHandler.getSpawn("lobby") != null)
                        player.teleport(VariablesHandler.getSpawn("lobby"));

                    if (!VariablesHandler.getVersion().equals(VersionType.ONE_NINE))
                        if (VariablesHandler.getPlayerInventories().containsKey(player.getUniqueId())) {
                            player.getInventory().clear();
                            player.getInventory().setContents(VariablesHandler.getPlayerInventories().get(player.getUniqueId()).getContents());
                            VariablesHandler.getPlayerInventories().remove(player.getUniqueId());
                        }

                    BungeeCordAPI.sendAuthorizationValueToBungeeCord(player, true);

                    if (!instance.getConfigHandler().SETTINGS_SEND_PLAYER_TO.isEmpty())
                        BungeeCordAPI.sendPlayerToServer(player, instance.getConfigHandler().SETTINGS_SEND_PLAYER_TO);

                    instance.getPluginManager().callEvent(new AsyncRegisterEvent(player, code, user.get2FAsecret()));

                    user.set2FA(true);
                    user.setSettingUp2FA(false);
                    user.needsToBeAuthenticated(false);

                    instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.TFA_REGISTERED, code));
                    return AuthenticationResult.TFA_REGISTERED;
                }

                if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE)
                    player.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_WRONG_CODE_KICK);

                instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.INVALID_CODE, code));
                return AuthenticationResult.INVALID_CODE;
            } else {
                user.set2FA(false);
                user.setRecoveryCode(true);
            }
        } catch (NumberFormatException ex) {
            if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE)
                player.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_WRONG_CODE_KICK);

            instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.INVALID_CODE, String.valueOf(code)));
            return AuthenticationResult.INVALID_CODE;
        }

        instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.FAILED, String.valueOf(code)));
        return AuthenticationResult.FAILED;
    }

    public AuthenticationResult login(String code) {
        if (!user.hasToBeAuthenticated()) {
            instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.TFA_ALREADY_LOGGED_IN, String.valueOf(code)));
            return AuthenticationResult.TFA_ALREADY_LOGGED_IN;
        }

        try {
            if (user.get2FAsecret() != null) {
                if (new GoogleAuthenticator().authorize(user.get2FAsecret(), Integer.parseInt(code))) {
                    TitleAPI.clearTitle(player);

                    if (VariablesHandler.getSpawn("lobby") != null)
                        player.teleport(VariablesHandler.getSpawn("lobby"));

                    if (!VariablesHandler.getVersion().equals(VersionType.ONE_NINE))
                        if (VariablesHandler.getPlayerInventories().containsKey(player.getUniqueId())) {
                            player.getInventory().clear();
                            player.getInventory().setContents(VariablesHandler.getPlayerInventories().get(player.getUniqueId()).getContents());
                            VariablesHandler.getPlayerInventories().remove(player.getUniqueId());
                        }

                    BungeeCordAPI.sendAuthorizationValueToBungeeCord(player, true);

                    if (!instance.getConfigHandler().SETTINGS_SEND_PLAYER_TO.isEmpty())
                        BungeeCordAPI.sendPlayerToServer(player, instance.getConfigHandler().SETTINGS_SEND_PLAYER_TO);

                    instance.getPluginManager().callEvent(new AsyncLoginEvent(player, Integer.parseInt(code)));

                    user.needsToBeAuthenticated(false);

                    instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.TFA_LOGGED, code));
                    return AuthenticationResult.TFA_LOGGED;
                }

                if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE)
                    player.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_WRONG_CODE_KICK);

                instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.INVALID_CODE, code));
                return AuthenticationResult.INVALID_CODE;
            }

        } catch (NumberFormatException ex) {
            if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE)
                player.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_WRONG_CODE_KICK);

            instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.INVALID_CODE, code));
            return AuthenticationResult.INVALID_CODE;
        }

        instance.getPluginManager().callEvent(new AsyncAuthenticateEvent(player, AuthenticationResult.FAILED, code));
        return AuthenticationResult.FAILED;
    }

    public boolean isRegistered() {
        return user.get2FA();
    }
}
