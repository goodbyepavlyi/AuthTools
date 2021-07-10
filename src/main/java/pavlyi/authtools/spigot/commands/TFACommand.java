package pavlyi.authtools.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.authentication.AuthHandler;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.enums.VersionType;
import pavlyi.authtools.spigot.storages.Variables;

public class TFACommand implements CommandExecutor {
    private final AuthTools instance = AuthTools.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(instance.getMessagesHandler().ONLY_PLAYER_CAN_EXECUTE_COMMAND);
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 0) {
            player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_USAGE);
            return false;
        }

        User user = Variables.getUser(player.getUniqueId());
        AuthHandler authHandler = new AuthHandler(player);

        if (user.get2FA() && !user.isSettingUp2FA()) {
            user.set2FA(false);
            player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_DISABLED);
            return false;
        }

        if (!(user.isSettingUp2FA() && user.get2FA())) {
            authHandler.requestRegister();
        } else {
            user.setSettingUp2FA(false);

            if (!Variables.getVersion().equals(VersionType.ONE_NINE) && user.getPlayerInventory() != null) {
                player.getInventory().clear();
                player.getInventory().setContents(user.getPlayerInventory().getContents());

                user.setPlayerInventory(null);
            }

            player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_SETUP_CANCELLED);
        }

        return false;
    }
}
