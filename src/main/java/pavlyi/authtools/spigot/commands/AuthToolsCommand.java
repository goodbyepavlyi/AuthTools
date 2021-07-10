package pavlyi.authtools.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.Updater;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.enums.ConnectionType;
import pavlyi.authtools.spigot.enums.InformationType;
import pavlyi.authtools.spigot.events.AsyncResetEvent;
import pavlyi.authtools.spigot.storages.ImportHandler;
import pavlyi.authtools.spigot.storages.Variables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AuthToolsCommand implements CommandExecutor, TabCompleter {
    public AuthTools instance = AuthTools.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0)
                for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                    sender.sendMessage(tempMessage);

            if (args.length == 1)
                switch (args[0]) {
                    case "reload":
                        instance.reloadPlugin();
                        sender.sendMessage(instance.getMessagesHandler().PLUGIN_RELOADED);

                        break;

                    case "about":
                        Updater updater = new Updater(instance.getDescription().getVersion());
                        updater.checkForUpdate();

                        boolean updateNeeded = updater.getResult().equals(Updater.Result.UPDATE_FOUND);

                        for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUT) {
                            tempMessage = tempMessage.replace("%version%", instance.getDescription().getVersion());
                            tempMessage = tempMessage.replace("%connection%", Variables.getConnectionType().toString());

                            if (updateNeeded) {
                                tempMessage = tempMessage.replace("%is_update_needed%", "(Update needed!)");
                            } else {
                                tempMessage = tempMessage.replace("%is_update_needed%", "");
                            }

                            sender.sendMessage(tempMessage);
                        }

                        break;

                    case "reset":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);

                        break;

                    case "backend":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);

                        break;

                    case "info":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);

                        break;

                    case "setspawn":
                    case "setlobby":
                        sender.sendMessage(instance.getMessagesHandler().ONLY_PLAYER_CAN_EXECUTE_COMMAND);

                        break;

                    case "import":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORTUSAGE);

                        break;

                    default:
                        for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                            sender.sendMessage(tempMessage);

                        break;
                }

            if (args.length == 2)
                switch (args[0]) {
                    case "reload":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);

                        break;

                    case "about":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);

                        break;

                    case "reset":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);

                        break;

                    case "backend":
                        if (!ConnectionType.isValid(args[1].toUpperCase())) {
                            sender.sendMessage(
                                    instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_WRONG_CONNECTION_TYPE
                                            .replace("%connectionType%", args[1]));

                            break;
                        }

                        ConnectionType enteredConnectionType = ConnectionType.valueOf(args[1].toUpperCase());
                        ConnectionType connectionType = Variables.getConnectionType();

                        switch (enteredConnectionType) {
                            case YAML:
                                if (connectionType.equals(ConnectionType.YAML)) {
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    break;
                                }

                                if (instance.getYamlConnection().connect(true)) {
                                    instance.switchConnection(enteredConnectionType);
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION
                                            .replace("%connectionType%", enteredConnectionType.toUpperCase()));
                                } else {
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                }

                                break;

                            case MYSQL:
                                if (connectionType.equals(ConnectionType.MYSQL)) {
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    break;
                                }

                                if (instance.getMySQL().connect(true)) {
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    instance.switchConnection(enteredConnectionType);
                                } else {
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                }

                                break;

                            case SQLITE:
                                if (connectionType.equals(ConnectionType.SQLITE)) {
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    break;
                                }

                                if (instance.getSQLite().connect(true)) {
                                    instance.switchConnection(enteredConnectionType);
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION
                                            .replace("%connectionType%", enteredConnectionType.toUpperCase()));
                                } else {
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                }

                                break;

                            case MONGODB:
                                if (connectionType.equals(ConnectionType.MONGODB)) {
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    break;
                                }

                                if (instance.getMongoDB().connect(true)) {
                                    instance.switchConnection(enteredConnectionType);
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION
                                            .replace("%connectionType%", enteredConnectionType.toUpperCase()));
                                } else {
                                    sender.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                }

                                break;
                        }

                        break;

                    case "info":
                        if (instance.getServer().getPlayer(args[1]) == null) {
                            sender.sendMessage(instance.getMessagesHandler().PLAYER_NOT_FOUND.replace("%player%", args[1]));
                            return false;
                        }

                        User user = Variables.getUser(instance.getServer().getPlayer(args[1]).getUniqueId());

                        if (user.exists()) {
                            ArrayList<String> messages = new ArrayList<>();

                            for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFO) {
                                tempMessage = tempMessage.replace("%player%", args[1]);

                                if (user.getIP() != null) {
                                    tempMessage = tempMessage.replace("%ip%", user.getIP());
                                } else {
                                    tempMessage = tempMessage.replace("%ip%", "Unknown");
                                }

                                if (user.getEmail() != null) {
                                    tempMessage = tempMessage.replace("%email%", user.getEmail());
                                } else {
                                    tempMessage = tempMessage.replace("%email%", "Unknown");
                                }

                                if (user.isSettingUp2FA()) {
                                    tempMessage = tempMessage.replace("%2fa%", instance.color("&eSetting up"));
                                } else {
                                    if (user.get2FA()) {
                                        tempMessage = tempMessage.replace("%2fa%", instance.color("&a✔"));
                                    } else {
                                        tempMessage = tempMessage.replace("%2fa%", instance.color("&c✖"));
                                    }
                                }

                                if (user.get2FAsecret() != null) {
                                    tempMessage = tempMessage.replace("%2fa_secret%", user.get2FAsecret());
                                } else {
                                    tempMessage = tempMessage.replace("%2fa_secret%", "Unknown");
                                }

                                if (user.get2FA()) {
                                    tempMessage = tempMessage.replace("%2fa_recoverycode%",
                                            String.valueOf(user.getRecoveryCode()));
                                } else {
                                    tempMessage = tempMessage.replace("%2fa_recoverycode%", "Unknown");
                                }

                                messages.add(tempMessage);
                            }

                            for (String tempMessage : messages)
                                sender.sendMessage(tempMessage);

                            break;
                        }

                        sender.sendMessage(instance.getMessagesHandler().PLAYER_NOT_FOUND.replace("%player%", args[1]));

                        break;

                    case "setspawn":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);

                        break;

                    case "setlobby":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);

                        break;

                    case "import":
                        String type = args[1].toUpperCase();

                        if (!ConnectionType.isValid(type)) {
                            sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORT_INCORRECT_TYPE);
                            break;
                        }

                        ImportHandler importHandler = new ImportHandler(ConnectionType.valueOf(type));

                        if (importHandler.importYAML() || importHandler.importMySQL() || importHandler.importMongoDB() || importHandler.importSQLite()) {
                            sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORT_SUCESSFULLY_IMPORTED.replace("%importedType%", type).replace("%currentBackend%", Variables.getConnectionType().toString()));
                        } else {
                            sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORT_ERROR_WHILE_IMPORTING);
                        }

                        break;

                    default:
                        for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                            sender.sendMessage(tempMessage);

                        break;
                }

            if (args.length == 3)
                switch (args[0]) {
                    case "reload":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);

                        break;

                    case "about":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);

                        break;

                    case "reset":
                        InformationType resetType;

                        try {
                            if (args[2].equalsIgnoreCase("2fa"))
                                resetType = InformationType.TFA;
                            else
                                resetType = InformationType.valueOf(args[2].toUpperCase());
                        } catch (IllegalArgumentException exception) {
                            sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
                            break;
                        }


                        if (instance.getServer().getPlayer(args[1]) == null) {
                            sender.sendMessage(instance.getMessagesHandler().PLAYER_NOT_FOUND.replace("%player%", args[1]));
                            return false;
                        }

                        User user = Variables.getUser(instance.getServer().getPlayer(args[1]).getUniqueId());

                        instance.getPluginManager().callEvent(new AsyncResetEvent(instance.getServer().getPlayer(args[1]), resetType));

                        switch (resetType) {
                            case TFA:
                                if (user.get2FA()) {
                                    user.set2FA(false);

                                    sender.sendMessage(
                                            instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_DISABLED_2FA
                                                    .replace("%player%", args[1]));

                                    break;
                                }

                                sender.sendMessage(instance
                                        .getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_2FA_ENABLED
                                        .replace("%player%", args[1]));

                                break;

                            case MAIL:
                                if (user.getEmail() != null) {
                                    user.setEmail(null);

                                    sender.sendMessage(
                                            instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_DISABLED_EMAIL.replace("%player%", args[1]));

                                    break;
                                }

                                sender.sendMessage(instance
                                        .getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_EMAIL_ENABLED
                                        .replace("%player%", args[1]));

                                break;
                        }

                        break;

                    case "backend":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);

                        break;

                    case "info":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);

                        break;

                    case "setspawn":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);

                        break;

                    case "setlobby":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);

                        break;

                    case "import":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORTUSAGE);

                        break;

                    default:
                        for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                            sender.sendMessage(tempMessage);

                        break;
                }

            if (args.length >= 4)
                switch (args[0]) {
                    case "reload":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);

                        break;

                    case "about":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);

                        break;

                    case "reset":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);

                    case "backend":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);

                        break;

                    case "info":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);

                        break;

                    case "setspawn":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);

                        break;

                    case "setlobby":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);

                        break;

                    case "import":
                        sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORTUSAGE);

                        break;

                    default:
                        for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                            sender.sendMessage(tempMessage);

                        break;
                }

            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("authtools.use")) {
            player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
            return true;
        }

        if (args.length == 0)
            for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                sender.sendMessage(tempMessage);

        if (args.length == 1)
            switch (args[0]) {
                default:
                    for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                        sender.sendMessage(tempMessage);

                    break;

                case "reload":
                    if (player.hasPermission("authtools.use.reload")) {
                        instance.reloadPlugin();
                        player.sendMessage(instance.getMessagesHandler().PLUGIN_RELOADED);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "about":
                    if (player.hasPermission("authtools.use.about")) {
                        Updater updater = new Updater(instance.getDescription().getVersion());
                        updater.checkForUpdate();

                        boolean updateNeeded = updater.getResult().equals(Updater.Result.UPDATE_FOUND);

                        for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUT) {
                            tempMessage = tempMessage.replace("%version%", instance.getDescription().getVersion());
                            tempMessage = tempMessage.replace("%connection%", Variables.getConnectionType().toString());

                            if (updateNeeded) {
                                tempMessage = tempMessage.replace("%is_update_needed%", "(Update needed!)");
                            } else {
                                tempMessage = tempMessage.replace("%is_update_needed%", "");
                            }

                            player.sendMessage(tempMessage);
                        }

                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "reset":
                    if (player.hasPermission("authtools.use.reset")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "backend":
                    if (player.hasPermission("authtools.use.backend")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "info":
                    if (player.hasPermission("authtools.use.info")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "setspawn":
                    if (player.hasPermission("authtools.use.setspawn")) {
                        Variables.createSpawn("authenticationSpawn", player.getLocation());
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWN);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "setlobby":
                    if (player.hasPermission("authtools.use.setlobby")) {
                        Variables.createSpawn("lobby", player.getLocation());
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBY);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "import":
                    if (player.hasPermission("authtools.use.import")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORTUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;
            }

        if (args.length == 2)
            switch (args[0]) {
                default:
                    for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                        sender.sendMessage(tempMessage);

                    break;

                case "reload":
                    if (player.hasPermission("authtools.use.reload")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "about":
                    if (player.hasPermission("authtools.use.about")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "reset":
                    if (player.hasPermission("authtools.use.reset")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "backend":
                    if (player.hasPermission("authtools.use.backend")) {
                        if (!ConnectionType.isValid(args[1].toUpperCase())) {
                            player.sendMessage(
                                    instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_WRONG_CONNECTION_TYPE
                                            .replace("%connectionType%", args[1]));

                            break;
                        }

                        ConnectionType enteredConnectionType = ConnectionType.valueOf(args[1].toUpperCase());
                        ConnectionType connectionType = Variables.getConnectionType();

                        switch (enteredConnectionType) {
                            case YAML:
                                if (connectionType.equals(ConnectionType.YAML)) {
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    break;
                                }

                                if (instance.getYamlConnection().connect(true)) {
                                    instance.switchConnection(enteredConnectionType);
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION
                                            .replace("%connectionType%", enteredConnectionType.toUpperCase()));
                                } else {
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                }

                                break;

                            case MYSQL:
                                if (connectionType.equals(ConnectionType.MYSQL)) {
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    break;
                                }

                                if (instance.getMySQL().connect(true)) {
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    instance.switchConnection(enteredConnectionType);
                                } else {
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                }

                                break;

                            case SQLITE:
                                if (connectionType.equals(ConnectionType.SQLITE)) {
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    break;
                                }

                                if (instance.getSQLite().connect(true)) {
                                    instance.switchConnection(enteredConnectionType);
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION
                                            .replace("%connectionType%", enteredConnectionType.toUpperCase()));
                                } else {
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                }

                                break;

                            case MONGODB:
                                if (connectionType.equals(ConnectionType.MONGODB)) {
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                    break;
                                }

                                if (instance.getMongoDB().connect(true)) {
                                    instance.switchConnection(enteredConnectionType);
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION
                                            .replace("%connectionType%", enteredConnectionType.toUpperCase()));
                                } else {
                                    player.sendMessage(instance
                                            .getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION
                                            .replace("%connectionType%",
                                                    enteredConnectionType.toUpperCase()));
                                }

                                break;
                        }

                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "info":
                    if (player.hasPermission("authtools.use.info")) {
                        if (instance.getServer().getPlayer(args[1]) == null) {
                            sender.sendMessage(instance.getMessagesHandler().PLAYER_NOT_FOUND.replace("%player%", args[1]));
                            return false;
                        }

                        User user = Variables.getUser(instance.getServer().getPlayer(args[1]).getUniqueId());
                        if (user.exists()) {
                            ArrayList<String> messages = new ArrayList<>();

                            for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFO) {
                                tempMessage = tempMessage.replace("%player%", args[1]);

                                if (instance.getServer().getPlayer(args[1]) != null)
                                    tempMessage = tempMessage.replace("%uuid%", instance.getServer().getPlayer(args[1]).getUniqueId().toString());
                                else
                                    tempMessage = tempMessage.replace("%uuid%", "Unknown");

                                if (user.getIP() != null)
                                    tempMessage = tempMessage.replace("%ip%", user.getIP());
                                else
                                    tempMessage = tempMessage.replace("%ip%", "Unknown");

                                if (user.getEmail() != null)
                                    tempMessage = tempMessage.replace("%email%", user.getEmail());
                                else
                                    tempMessage = tempMessage.replace("%email%", "Unknown");

                                if (user.isSettingUp2FA())
                                    tempMessage = tempMessage.replace("%2fa%",
                                            instance.color("&eSetting up"));
                                else if (user.get2FA())
                                    tempMessage = tempMessage.replace("%2fa%", instance.color("&a✔"));
                                else
                                    tempMessage = tempMessage.replace("%2fa%", instance.color("&c✖"));

                                if (user.get2FAsecret() != null) {
                                    tempMessage = tempMessage.replace("%2fa_secret%", user.get2FAsecret());
                                } else {
                                    tempMessage = tempMessage.replace("%2fa_secret%", "Unknown");
                                }

                                if (user.get2FA()) {
                                    tempMessage = tempMessage.replace("%2fa_recoverycode%",
                                            String.valueOf(user.getRecoveryCode()));
                                } else {
                                    tempMessage = tempMessage.replace("%2fa_recoverycode%", "Unknown");
                                }

                                messages.add(tempMessage);
                            }

                            for (String tempMessage : messages)
                                player.sendMessage(tempMessage);

                            break;
                        }

                        player.sendMessage(instance.getMessagesHandler().PLAYER_NOT_FOUND.replace("%player%", args[1]));

                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "setspawn":
                    if (player.hasPermission("authtools.use.setspawn")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "setlobby":
                    if (player.hasPermission("authtools.use.setlobby")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "import":
                    if (player.hasPermission("authtools.use.import")) {
                        String type = args[1].toUpperCase();

                        if (!ConnectionType.isValid(type)) {
                            player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORT_INCORRECT_TYPE);
                        } else {
                            ImportHandler importHandler = new ImportHandler(ConnectionType.valueOf(type));

                            if (importHandler.importYAML() || importHandler.importMySQL() || importHandler.importMongoDB() || importHandler.importSQLite()) {
                                player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORT_SUCESSFULLY_IMPORTED.replace("%importedType%", type).replace("%currentBackend%", Variables.getConnectionType().toString()));
                            } else {
                                player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORT_ERROR_WHILE_IMPORTING);
                            }
                        }

                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;
            }

        if (args.length == 3)
            switch (args[0]) {
                default:
                    for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                        sender.sendMessage(tempMessage);

                    break;

                case "reload":
                    if (player.hasPermission("authtools.use.reload")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "about":
                    if (player.hasPermission("authtools.use.about")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "reset":
                    if (player.hasPermission("authtools.use.reset")) {
                        InformationType resetType;

                        try {
                            if (args[2].equalsIgnoreCase("2fa"))
                                resetType = InformationType.TFA;
                            else
                                resetType = InformationType.valueOf(args[2].toUpperCase());
                        } catch (IllegalArgumentException exception) {
                            player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
                            return false;
                        }

                        if (instance.getServer().getPlayer(args[1]) == null) {
                            sender.sendMessage(instance.getMessagesHandler().PLAYER_NOT_FOUND.replace("%player%", args[1]));
                            return false;
                        }

                        User user = Variables.getUser(instance.getServer().getPlayer(args[1]).getUniqueId());

                        instance.getPluginManager().callEvent(new AsyncResetEvent(instance.getServer().getPlayer(args[1]), resetType));

                        switch (resetType) {
                            case TFA:
                                if (user.get2FA()) {
                                    user.set2FA(false);

                                    player.sendMessage(
                                            instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_DISABLED_2FA.replace("%player%", args[1]));

                                    break;
                                }

                                player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_2FA_ENABLED
                                        .replace("%player%", args[1]));

                                break;

                            case MAIL:
                                if (user.getEmail() != null) {
                                    user.setEmail(null);

                                    player.sendMessage(
                                            instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_DISABLED_EMAIL.replace("%player%", args[1]));

                                    break;
                                }

                                player.sendMessage(instance
                                        .getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_EMAIL_ENABLED
                                        .replace("%player%", args[1]));

                                break;
                        }

                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "backend":
                    if (player.hasPermission("authtools.use.backend")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "info":
                    if (player.hasPermission("authtools.use.info")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);

                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "setspawn":
                    if (player.hasPermission("authtools.use.setspawn")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "setlobby":
                    if (player.hasPermission("authtools.use.setlobby")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "import":
                    if (player.hasPermission("authtools.use.import")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORTUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;
            }

        if (args.length >= 4)
            switch (args[0]) {
                default:
                    for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE)
                        sender.sendMessage(tempMessage);

                    break;

                case "reload":
                    if (player.hasPermission("authtools.use.reload")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "about":
                    if (player.hasPermission("authtools.use.about")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "reset":
                    if (player.hasPermission("authtools.use.reset")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "backend":
                    if (player.hasPermission("authtools.use.backend")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "info":
                    if (player.hasPermission("authtools.use.info")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "setspawn":
                    if (player.hasPermission("authtools.use.setspawn")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "setlobby":
                    if (player.hasPermission("authtools.use.setlobby")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;

                case "import":
                    if (player.hasPermission("authtools.use.import")) {
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_IMPORTUSAGE);
                        break;
                    }

                    player.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
                    break;
            }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("authtools")) {
            if (args.length == 1) {
                List<String> mainCommands = Arrays.asList("reset", "backend", "info", "setspawn", "setlobby", "import", "about", "reload");
                List<String> possibleCommands = new ArrayList<>();

                if (!args[0].equals("")) {
                    for (String command : mainCommands) {
                        if (command.toLowerCase().startsWith(args[0].toLowerCase())) {
                            possibleCommands.add(command);
                        }
                    }
                } else {
                    possibleCommands.add("reset");
                    possibleCommands.add("backend");
                    possibleCommands.add("info");
                    possibleCommands.add("setspawn");
                    possibleCommands.add("setlobby");
                    possibleCommands.add("about");
                    possibleCommands.add("import");
                    possibleCommands.add("reload");
                }

                Collections.sort(possibleCommands);

                return possibleCommands;
            }

            if (args.length == 2) {
                List<String> mainCommands = Arrays.asList("YAML", "MYSQL", "MONGODB", "SQLITE");
                List<String> possibleCommands = new ArrayList<>();

                if (!args[0].equals("")) {
                    if (args[0].equalsIgnoreCase("reset")) {
                        for (Player all : instance.getServer().getOnlinePlayers()) {
                            possibleCommands.add(all.getName());
                        }
                    }

                    if (args[0].equalsIgnoreCase("info")) {
                        for (Player all : instance.getServer().getOnlinePlayers()) {
                            possibleCommands.add(all.getName());
                        }
                    }

                    if (args[0].equalsIgnoreCase("backend")) {
                        for (String command : mainCommands) {
                            if (command.toLowerCase().startsWith(args[1].toLowerCase())) {
                                possibleCommands.add(command);
                            }
                        }
                    }

                    if (args[0].equalsIgnoreCase("import")) {
                        for (String command : mainCommands) {
                            if (command.toLowerCase().startsWith(args[1].toLowerCase())) {
                                possibleCommands.add(command);
                            }
                        }
                    }
                } else {
                    if (args[0].equalsIgnoreCase("reset")) {
                        for (Player all : instance.getServer().getOnlinePlayers()) {
                            possibleCommands.add(all.getName());
                        }
                    }

                    if (args[0].equalsIgnoreCase("info")) {
                        for (Player all : instance.getServer().getOnlinePlayers()) {
                            possibleCommands.add(all.getName());
                        }
                    }

                    if (args[0].equalsIgnoreCase("backend")) {
                        possibleCommands.add("YAML");
                        possibleCommands.add("MYSQL");
                        possibleCommands.add("MONGODB");
                        possibleCommands.add("SQLITE");
                    }

                    if (args[0].equalsIgnoreCase("import")) {
                        possibleCommands.add("YAML");
                        possibleCommands.add("MYSQL");
                        possibleCommands.add("MONGODB");
                        possibleCommands.add("SQLITE");
                    }
                }

                Collections.sort(possibleCommands);

                return possibleCommands;
            }

            if (args.length >= 3) {
                List<String> mainCommands = Arrays.asList("2fa", "mail");
                List<String> possibleCommands = new ArrayList<>();

                if (!args[0].equals("")) {
                    if (args[0].equalsIgnoreCase("reset")) {
                        for (String command : mainCommands) {
                            if (command.toLowerCase().startsWith(args[0].toLowerCase())) {
                                possibleCommands.add(command);
                            }
                        }
                    }
                } else {
                    if (args[0].equalsIgnoreCase("reset")) {
                        possibleCommands.add("2fa");
                        possibleCommands.add("mail");
                    }
                }

                Collections.sort(possibleCommands);

                return possibleCommands;
            }
        }

        return null;
    }

}
