package pavlyi.authtools.spigot.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.enums.SetupPhase;
import pavlyi.authtools.spigot.storages.Variables;

public class AuthCommand implements CommandExecutor {
    private final AuthTools instance = AuthTools.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(instance.getMessagesHandler().ONLY_PLAYER_CAN_EXECUTE_COMMAND);
            return false;
        }

        Player player = (Player) sender;
        User user = Variables.getUser(player.getUniqueId());

        if (!(args.length == 0 || args.length == 1)) {
            player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_USAGE);
            return false;
        }

        if (args.length == 1) {
            switch (args[0]) {
                default:
                    player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_USAGE);
                    return false;

                case "setupEmail":
                    if (user.getEmail() != null) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_ALREADY_SETTED_UP);
                        return false;
                    }

                    if (!user.getSettingUpEmail().getPhase().equals(SetupPhase.NONE)) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_ALREADY_SETTING_UP);
                        return false;
                    }

                    user.getSettingUpEmail().setPhase(SetupPhase.SETUP);
                    player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_ENTER_ADDRESS);
                    return false;

                case "changeEmail":
                    if (!user.getSettingUpEmail().getPhase().equals(SetupPhase.NONE)) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_ALREADY_SETTING_UP);
                        return false;
                    }

                    user.setEmail(null);
                    user.getSettingUpEmail().setPhase(SetupPhase.SETUP);
                    player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTH_EMAIL_ENTER_ADDRESS);
                    return false;
            }
        }


        for (String message : instance.getMessagesHandler().COMMANDS_AUTH_INFO) {
//            if (!(message.contains("%emailStatus%") || message.contains("%discordStatus%"))) {
            if (!(message.contains("%emailStatus%") || message.contains("%changeEmail%"))) {
                if (user.getEmail() != null) {
                    message = message.replace("%email%", user.getEmail());
                    message = message.replace("%emailMasked%", user.getEmail().replaceAll("(?<=.{3}).(?=.*@)", "*"));
                } else {
                    message = message.replace("%email%", instance.getMessagesHandler().COMMANDS_AUTH_PLACEHOLDERS_EMAIL_ADDRESS_NOT_SETUP);
                    message = message.replace("%emailMasked%", instance.getMessagesHandler().COMMANDS_AUTH_PLACEHOLDERS_EMAIL_ADDRESS_NOT_SETUP);
                }

                player.sendMessage(instance.color(message));
            } else {
                if (message.contains("%emailStatus%")) {
                    String[] splittedMessage = message.split("%emailStatus%");
                    String message1 = "";
                    String message2 = "";

                    for (String messageSplitted : splittedMessage) {
                        if (splittedMessage.length == 2 && !message1.isEmpty() && message2.isEmpty())
                            message2 = instance.color(messageSplitted);

                        if (message1.isEmpty())
                            message1 = instance.color(messageSplitted);
                    }

                    TextComponent messageComponent = new TextComponent();
                    TextComponent result = new TextComponent();

                    result.setText(instance.getMessagesHandler().COMMANDS_AUTH_PLACEHOLDERS_EMAIL_STATUS_ENABLED);
                    if (user.getEmail() == null) {
                        result.setText(instance.getMessagesHandler().COMMANDS_AUTH_PLACEHOLDERS_EMAIL_STATUS_SETUP);
                        result.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/auth setupEmail"));
                    }

                    if (!user.getSettingUpEmail().getPhase().equals(SetupPhase.NONE))
                        result.setText(instance.getMessagesHandler().COMMANDS_AUTH_PLACEHOLDERS_EMAIL_STATUS_SETTING_UP);

                    messageComponent.addExtra(message1);
                    messageComponent.addExtra(result);
                    messageComponent.addExtra(message2);

                    player.spigot().sendMessage(messageComponent);
                }

                if (message.contains("%changeEmail%")) {
                    if (user.getEmail() != null) {
                        String[] splittedMessage = message.split("%changeEmail%");
                        String message1 = "";
                        String message2 = "";

                        for (String messageSplitted : splittedMessage) {
                            if (splittedMessage.length == 2 && !message1.isEmpty() && message2.isEmpty())
                                message2 = instance.color(messageSplitted);

                            if (message1.isEmpty())
                                message1 = instance.color(messageSplitted);
                        }

                        TextComponent messageComponent = new TextComponent();
                        TextComponent result = new TextComponent();

                        result.setText(instance.getMessagesHandler().COMMANDS_AUTH_PLACEHOLDERS_EMAIL_CHANGE_EMAIL);
                        result.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/auth changeEmail"));

                        messageComponent.addExtra(message1);
                        messageComponent.addExtra(result);
                        messageComponent.addExtra(message2);

                        player.spigot().sendMessage(messageComponent);
                    }
                }

//                if (message.contains("%discordStatus%")) {
//                    String[] splittedMessage = message.split("%discordStatus%");
//                    String message1 = "";
//                    String message2 = "";
//
//                    for (String messageSplitted : splittedMessage) {
//                        if (splittedMessage.length == 2 && !message1.isEmpty() && message2.isEmpty())
//                            message2 = instance.color(messageSplitted);
//
//                        if (message1.isEmpty())
//                            message1 = instance.color(messageSplitted);
//                    }
//
//                    TextComponent messageComponent = new TextComponent();
//                    TextComponent result = new TextComponent();
//
//                    result.setText("Enabled");
//                    if (user.getEmail() == null) {
//                        result.setText("Setup");
//                        result.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/auth setupDiscord"));
//                    }
//
//                    messageComponent.addExtra(message1);
//                    messageComponent.addExtra(result);
//                    messageComponent.addExtra(message2);
//
//                    player.spigot().sendMessage(messageComponent);
//                }
            }
        }

        return false;
    }
}
