package pavlyi.authtools.spigot.listeners;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.apache.commons.mail.EmailException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.communication.VerifyEmail;
import pavlyi.authtools.spigot.enums.SetupPhase;
import pavlyi.authtools.spigot.enums.VersionType;
import pavlyi.authtools.spigot.storages.Variables;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class SetupListener implements Listener {
    private final AuthTools instance = AuthTools.getInstance();

    @EventHandler
    public void TfaVerify(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        User user = Variables.getUser(player.getUniqueId());
        String message = event.getMessage().replaceAll(" ", "");
        int code;

        if (user.isSettingUp2FA() && !user.hasToBeAuthenticated()) {
            event.setCancelled(true);

            try {
                code = Integer.parseInt(message);

                if (new GoogleAuthenticator().authorize(user.get2FAsecret(), code)) {
                    user.set2FA(true);
                    user.setSettingUp2FA(false);

                    if (!Variables.getVersion().equals(VersionType.ONE_NINE) && user.getPlayerInventory() != null) {
                        player.getInventory().clear();
                        player.getInventory().setContents(user.getPlayerInventory().getContents());

                        user.setPlayerInventory(null);
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
    public void TfaCancel(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = Variables.getUser(player.getUniqueId());

        if (user.isSettingUp2FA()) {
            user.setSettingUp2FA(false);
            user.set2FA(false);
            user.set2FAsecret(null);
            user.setRecoveryCode(true);

            if (!Variables.getVersion().equals(VersionType.ONE_NINE) && user.getPlayerInventory() != null) {
                player.getInventory().clear();
                player.getInventory().setContents(user.getPlayerInventory().getContents());

                user.setPlayerInventory(null);
            }
        }
    }

    @EventHandler
    public void RecoverEmailSetup(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        User user = Variables.getUser(player.getUniqueId());
        String message = event.getMessage();

        if (user.getSettingUpEmail().getPhase().equals(SetupPhase.VERIFICATION)) {
            event.setCancelled(true);

            if (!String.valueOf(user.getSettingUpEmail().getVerificationCode()).equals(message)) {
                user.getSettingUpEmail().setPhase(SetupPhase.NONE);
                player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_INVALID_VERIFICATION_CODE);
                return;
            }

            user.setEmail(user.getSettingUpEmail().getEmail());
            user.getSettingUpEmail().setPhase(SetupPhase.NONE);
            player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_SUCCESS);
        }

        if (user.getSettingUpEmail().getPhase().equals(SetupPhase.SETUP)) {
            event.setCancelled(true);

            try {
                InternetAddress emailAddr = new InternetAddress(message);
                emailAddr.validate();
            } catch (AddressException ex) {
                user.getSettingUpEmail().setPhase(SetupPhase.NONE);
                player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_INVALID_ADDRESS);
                return;
            }

            try {
                String[] hostname = message.split("@");
                InetAddress.getByName(hostname[hostname.length - 1]);
            } catch (UnknownHostException exception) {
                user.getSettingUpEmail().setPhase(SetupPhase.NONE);
                player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_INVALID_ADDRESS);
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();
            while (stringBuilder.length() <= 8)
                stringBuilder.append(new Random().nextInt(9));

            user.getSettingUpEmail().setEmail(message);
            user.getSettingUpEmail().setVerificationCode(Integer.parseInt(stringBuilder.toString()));

            VerifyEmail email = new VerifyEmail();

            String content = email.getContent();
            content = content.replace("<playername/>", player.getName());
            content = content.replace("<servername/>", instance.getServer().getServerName());
            content = content.replace("<code/>", String.valueOf(user.getSettingUpEmail().getVerificationCode()));

            email.getEmail().setSubject(email.getSubject());
            email.getEmail().setContent(content, "text/html");

            player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_SENDING);
            try {
                email.getEmail().addTo(user.getSettingUpEmail().getEmail());
                email.sendEmail();
                user.getSettingUpEmail().setPhase(SetupPhase.VERIFICATION);
                player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_SENT);
            } catch (EmailException exception) {
                user.getSettingUpEmail().setPhase(SetupPhase.NONE);
                player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_ERROR);
                exception.printStackTrace();
            }
        }
    }
}
