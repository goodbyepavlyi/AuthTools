package pavlyi.authtools.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import pavlyi.authtools.AuthTools;
import pavlyi.authtools.events.AuthToolsPlayerRecovered2FAEvent;
import pavlyi.authtools.events.AuthToolsPlayerRegisteredEvent;
import pavlyi.authtools.handlers.ImageRenderer;
import pavlyi.authtools.handlers.QRCreate;
import pavlyi.authtools.handlers.User;
import pavlyi.authtools.listeners.PlayerLoginListener;
import pavlyi.authtools.listeners.UserSetupListener;

public class TFACommand implements CommandExecutor, TabCompleter {
	private AuthTools instance = AuthTools.getInstance();
	public static HashMap<Player, Inventory> inventories = new HashMap<Player, Inventory>();

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("2fa")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(instance.color("&f[&cAuthTools&f] &fOnly player can use this command!"));
			} else {
				Player p = (Player) sender;
				User user = new User(p.getName());

				if (args.length == 0) {
					if (!user.getSettingUp2FA()) {
						if (!user.get2FA()) {
							GoogleAuthenticatorKey secretKey = instance.getGoogleAuthenticator().createCredentials();

							user.setSettingUp2FA(true);
							user.set2FAsecret(secretKey.getKey());
							user.setRecoveryCode(false);		

							QRCreate.create(p, secretKey.getKey());

							for (String tempMessage : instance.getMessagesHandler().COMMANDS_2FA_SETUP_AUTHAPP) {
								tempMessage = tempMessage.replace("%secretkey%", secretKey.getKey());
								tempMessage = tempMessage.replace("%recoverycode%", String.valueOf(user.getRecoveryCode()));

								p.sendMessage(tempMessage);
							}

							Inventory inv = Bukkit.createInventory(p, 36);
							for (ItemStack is : p.getInventory().getContents()) {
								if (is != null)
									inv.addItem(is);
							}

							inventories.put(p, inv);

							ItemStack qrCodeMap = new ItemStack(Material.MAP);
							ItemMeta qrCodeMapIM = qrCodeMap.getItemMeta();
							qrCodeMapIM.setDisplayName(instance.getMessagesHandler().COMMANDS_2FA_SETUP_QRCODE_TITLE);
							qrCodeMapIM.setLore(instance.getMessagesHandler().COMMANDS_2FA_SETUP_QRCODE_LORE);
							qrCodeMap.setItemMeta(qrCodeMapIM);

							p.getInventory().clear();
							p.getInventory().setHeldItemSlot(0);
							p.getInventory().setItem(0, qrCodeMap);

							MapView view = Bukkit.getMap(p.getInventory().getItem(0).getDurability());
							Iterator<MapRenderer> iter = view.getRenderers().iterator();
							while (iter.hasNext()) {
								view.removeRenderer(iter.next());
							}

							try {
								ImageRenderer renderer = new ImageRenderer(instance.getDataFolder().toString()+"/tempFiles/temp-qrcode-"+p.getName()+".png");
								view.addRenderer(renderer);

								new File(instance.getDataFolder().toString()+"/tempFiles/temp-qrcode-"+p.getName()+".png").delete();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							user.set2FA(false);
							p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_DISABLED);
						}
					} else {
							user.setSettingUp2FA(false);

							if (inventories.containsKey(p)) {
								p.getInventory().clear();
								p.getInventory().setContents(inventories.get(p).getContents());
								inventories.remove(p);
							}

						p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_SETUP_CANCELLED);
					}
				}
				
				if (args.length == 1) {
					p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_USAGE);
				}

				if (args.length >= 2) {
					if (args.length == 2) {
						if (args[0].equalsIgnoreCase("recover")) {
							if (user.get2FA()) {
								int code = 0;

								try {
									code = Integer.parseInt(args[1]);

									if (code == user.getRecoveryCode()) {
										user.set2FA(false);
										user.set2FAsecret(null);
										user.setRecoveryCode(true);

										if (instance.getAuthLocked().contains(p.getName()))
											instance.getAuthLocked().remove(p.getName());

										instance.getPluginManager().callEvent(new AuthToolsPlayerRecovered2FAEvent(p, code, true));

										p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_RECOVERED);
										return true;
									}

									instance.getPluginManager().callEvent(new AuthToolsPlayerRecovered2FAEvent(p, code, false));
									p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE);
								} catch (NumberFormatException ex) {
									instance.getPluginManager().callEvent(new AuthToolsPlayerRecovered2FAEvent(p, code, false));
									p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_INVALID_RECOVERY_CODE);
								}
							} else {
								p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_2FA_DISABLED);
							}

							return true;
						}

						p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_USAGE);
					} else {
						p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_RECOVER_USAGE);
					}
				}
			}
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("2fa")) {
			if (args.length == 1) {
				List<String> mainCommands = Arrays.asList("recover");

                Collections.sort(mainCommands);
                
                return mainCommands;
            }
			
			if (args.length >= 2) {
				List<String> possibleCommands = new ArrayList<String>();
				
				Collections.sort(possibleCommands);
				
				return possibleCommands;
			}
		}

		return null;
	}

}
