package pavlyi.authtools.spigot.commands;

import com.connorlinfoot.titleapi.TitleAPI;
import org.apache.commons.mail.EmailException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.authentication.AuthHandler;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.communication.RecoverEmail;
import pavlyi.authtools.spigot.enums.InformationType;
import pavlyi.authtools.spigot.enums.VersionType;
import pavlyi.authtools.spigot.events.AsyncRecoverEvent;
import pavlyi.authtools.spigot.storages.Variables;

public class RecoverCommand implements CommandExecutor {
    private final AuthTools instance = AuthTools.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(instance.getMessagesHandler().ONLY_PLAYER_CAN_EXECUTE_COMMAND);
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1 && args.length != 2) {
            if (args.length >= 1 && args[0].equals("code")) {
                player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_USAGE_CODE);
                return false;
            }

            player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_USAGE);
            return false;
        }

        User user = Variables.getUser(player.getUniqueId());
        if (args.length == 1) {
            switch (args[0]) {
                default:
                    player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_USAGE);
                    break;

                case "email":
                    if (!user.get2FA()) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_HAVE_TO_REGISTER);
                        return false;
                    }

                    if (user.getEmail() == null) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_EMAIL_NOT_SET);
                        break;
                    }

                    RecoverEmail email = new RecoverEmail();

                    String content = email.getContent();
                    content = content.replace("<playername/>", player.getName());
                    content = content.replace("<servername/>", instance.getServer().getServerName());
                    content = content.replace("<code/>", String.valueOf(user.getRecoveryCode()));

                    email.getEmail().setSubject(email.getSubject());
                    email.getEmail().setContent(content, "text/html");

                    player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_EMAIL_SENDING);
                    try {
                        email.getEmail().addTo(user.getEmail());
                        email.sendEmail();
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_EMAIL_SENT.replace("%email%", user.getEmail()).replace("%emailMasked%", user.getEmail().replaceAll("(?<=.{3}).(?=.*@)", "*")));
                    } catch (EmailException exception) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_EMAIL_FAILED_TO_SEND);
                        instance.log("&r &cError: &fAn error occurred while sending an recovery email to &c" + player.getName() + "&f!");
                        exception.printStackTrace();
                    }

                    break;

                case "code":
                    player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_USAGE_CODE);

                    break;
            }
        }

        if (args.length == 2) {
            switch (args[0]) {
                default:
                    player.sendMessage(instance.getMessagesHandler().COMMANDS_RECOVER_USAGE);
                    break;

                case "code":
                    if (user.get2FA()) {
                        int code = 0;

                        try {
                            code = Integer.parseInt(args[1]);

                            if (code == user.getRecoveryCode()) {
                                user.set2FA(false);
                                user.set2FAsecret(null);
                                user.setRecoveryCode(true);
                                user.setSession(0);

                                user.needsToBeAuthenticated(false);

                                TitleAPI.clearTitle(player);

                                if (!Variables.getVersion().equals(VersionType.ONE_NINE) && user.getPlayerInventory() != null) {
                                    player.getInventory().clear();
                                    player.getInventory().setContents(user.getPlayerInventory().getContents());

                                    user.setPlayerInventory(null);
                                }

                                instance.getPluginManager().callEvent(new AsyncRecoverEvent(player, code, true, InformationType.TFA));

                                player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_RECOVERED);

                                new AuthHandler(player).requestAuthentication();

                                return true;
                            }

                            instance.getPluginManager().callEvent(new AsyncRecoverEvent(player, code, false, InformationType.TFA));
                            player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE);
                        } catch (NumberFormatException ex) {
                            instance.getPluginManager().callEvent(new AsyncRecoverEvent(player, code, false, InformationType.TFA));
                            player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE);
                        }

                        return false;
                    }

                    player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_2FA_DISABLED);
            }
        }

        return false;
    }
}
