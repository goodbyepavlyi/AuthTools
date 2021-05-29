package pavlyi.authtools.spigot.commands;

import com.connorlinfoot.titleapi.TitleAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.enums.InformationType;
import pavlyi.authtools.spigot.enums.VersionType;
import pavlyi.authtools.spigot.events.AsyncRecoverEvent;
import pavlyi.authtools.spigot.handlers.AuthHandler;
import pavlyi.authtools.spigot.handlers.User;
import pavlyi.authtools.spigot.handlers.VariablesHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TFACommand implements CommandExecutor, TabCompleter {
    private final AuthTools instance = AuthTools.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(instance.color("&f[&cAuthTools&f] &fOnly player can use this command!"));
            return true;
        }

        Player player = (Player) sender;
        User user = new User(player.getName());
        AuthHandler authHandler = new AuthHandler(player);

        if (args.length == 0) {
            if (user.get2FA() && !user.getSettingUp2FA()) {
                user.set2FA(false);
                player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_DISABLED);
                return true;
            }

            if (!(user.getSettingUp2FA() && user.get2FA())) {
                authHandler.requestRegister();
            } else {
                user.setSettingUp2FA(false);

                if (!VariablesHandler.getVersion().equals(VersionType.ONE_NINE))
                    if (VariablesHandler.getPlayerInventories().containsKey(player.getUniqueId())) {
                        player.getInventory().clear();
                        player.getInventory().setContents(VariablesHandler.getPlayerInventories().get(player.getUniqueId()).getContents());

                        VariablesHandler.getPlayerInventories().remove(player.getUniqueId());
                    }

                player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_SETUP_CANCELLED);
            }
        }

        if (args.length == 1)
            player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_USAGE);

        if (args.length == 2) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "recover":
                    if (user.get2FA()) {
                        int code = 0;

                        try {
                            code = Integer.parseInt(args[1]);

                            if (code == user.getRecoveryCode()) {
                                user.set2FA(false);
                                user.set2FAsecret(null);
                                user.setRecoveryCode(true);

                                user.needsToBeAuthenticated(false);

                                TitleAPI.clearTitle(player);

                                if (!VariablesHandler.getVersion().equals(VersionType.ONE_NINE))
                                    if (VariablesHandler.getPlayerInventories().containsKey(player.getUniqueId())) {
                                        player.getInventory().clear();
                                        player.getInventory().setContents(VariablesHandler.getPlayerInventories().get(player.getUniqueId()).getContents());

                                        VariablesHandler.getPlayerInventories().remove(player.getUniqueId());
                                    }

                                instance.getPluginManager().callEvent(new AsyncRecoverEvent(player, code, true, InformationType.TFA));

                                player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_RECOVERED);

                                authHandler.requestAuthentication();

                                return true;
                            }

                            instance.getPluginManager().callEvent(new AsyncRecoverEvent(player, code, false, InformationType.TFA));
                            player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE);
                        } catch (NumberFormatException ex) {
                            instance.getPluginManager().callEvent(new AsyncRecoverEvent(player, code, false, InformationType.TFA));
                            player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE);
                        }

                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_2FA_DISABLED);

                    break;

                default:
                    player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_USAGE);

                    break;
            }
        }

        if (args.length >= 3)
            player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_USAGE);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("2fa")) {
            if (args.length == 1) {
                List<String> mainCommands = new ArrayList<>(Collections.singletonList("recover"));

                Collections.sort(mainCommands);

                return mainCommands;
            }

            if (args.length >= 2) {
                List<String> possibleCommands = new ArrayList<>();

                Collections.sort(possibleCommands);

                return possibleCommands;
            }
        }

        return null;
    }

}
