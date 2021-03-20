package pavlyi.authtools.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import pavlyi.authtools.AuthTools;
import pavlyi.authtools.connections.MySQL;
import pavlyi.authtools.handlers.User;

public class AuthToolsCommand implements CommandExecutor, TabCompleter {
	public AuthTools instance = AuthTools.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("authtools")) {
			if (!(sender instanceof Player)) {
				if (args.length == 0) {
					for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE) {
						sender.sendMessage(tempMessage);
					}
				}

				if (args.length == 1) {
					if (!(args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("backend") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setlobby"))) {
						for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE) {
							sender.sendMessage(tempMessage);
						}
					} else {
						if (args[0].equalsIgnoreCase("reload")) {
							instance.reloadPlugin();
							sender.sendMessage(instance.getMessagesHandler().PLUGIN_RELOADED);
						}

						if (args[0].equalsIgnoreCase("about")) {
							boolean updateNeeded = instance.checkForUpdates();
							for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUT) {
								tempMessage = tempMessage.replace("%version%", instance.getDescription().getVersion());
								tempMessage = tempMessage.replace("%connection%", instance.getConnectionType());

								if (updateNeeded) {
									tempMessage = tempMessage.replace("%is_update_needed%", "(Update needed!)");
								} else {
									tempMessage = tempMessage.replace("%is_update_needed%", "");
								}

								sender.sendMessage(tempMessage);
							}
						}
						
						if (args[0].equalsIgnoreCase("reset")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("backend")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("info")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("setspawn")) {
							sender.sendMessage(instance.getMessagesHandler().ONLY_PLAYER_CAN_EXECUTE_COMMAND);
						}

						if (args[0].equalsIgnoreCase("setlobby")) {
							sender.sendMessage(instance.getMessagesHandler().ONLY_PLAYER_CAN_EXECUTE_COMMAND);
						}
					}
				}

				if (args.length == 2) {
					if (!(args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("backend") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setlobby"))) {
						for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE) {
							sender.sendMessage(tempMessage);
						}
					} else {
						if (args[0].equalsIgnoreCase("reload")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);
						}

						if (args[0].equalsIgnoreCase("about")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("reset")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("backend")) {
							String enteredConnectionType = args[1];
							String connectionType = instance.getConnectionType();

							if (enteredConnectionType.equalsIgnoreCase("yaml") || enteredConnectionType.equalsIgnoreCase("mysql") || enteredConnectionType.equalsIgnoreCase("sqlite")) {
								if (enteredConnectionType.equalsIgnoreCase("yaml")) {
									if (connectionType.equals("YAML")) {
										sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED.replace("%connectionType%", enteredConnectionType.toUpperCase()));
										return true;
									}
									
									instance.switchConnection(enteredConnectionType);
									sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION.replace("%connectionType%", enteredConnectionType.toUpperCase()));
								}
								
								if (enteredConnectionType.equalsIgnoreCase("mysql")) {
									if (connectionType.equals("MYSQL")) {
										sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED.replace("%connectionType%", enteredConnectionType.toUpperCase()));
										return true;
									}

									if (instance.getMySQL().connect()) {
										sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION.replace("%connectionType%", enteredConnectionType.toUpperCase()));
										instance.switchConnection(enteredConnectionType);
									} else {
										sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION.replace("%connectionType%", enteredConnectionType.toUpperCase()));
									}
								}

								if (enteredConnectionType.equalsIgnoreCase("sqlite")) {
									if (connectionType.equals("SQLITE")) {
										sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED.replace("%connectionType%", enteredConnectionType.toUpperCase()));
										return true;
									}

									instance.switchConnection(enteredConnectionType);
									sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION.replace("%connectionType%", enteredConnectionType.toUpperCase()));
								}

								return true;
							}

							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_WRONG_CONNECTION_TYPE.replace("%connectionType%", enteredConnectionType.toUpperCase()));
						}

						if (args[0].equalsIgnoreCase("info")) {
							User user = new User(args[1]);

							if (user.isInDatabase()) {
								ArrayList<String> messages = new ArrayList<String>();

								for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFO) {
									tempMessage = tempMessage.replace("%player%", args[1]);

									if (user.getUUID() != null) {
										tempMessage = tempMessage.replace("%uuid%", user.getUUID());
									} else {
										tempMessage = tempMessage.replace("%uuid%", "Unknown");
									}
									
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
									
									if (user.getSettingUp2FA()) {
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
										tempMessage = tempMessage.replace("%2fa_recoverycode%", String.valueOf(user.getRecoveryCode()));
									} else {
										tempMessage = tempMessage.replace("%2fa_recoverycode%", "Unknown");
									}

									messages.add(tempMessage);
								}

								for (String tempMessage : messages) {
									sender.sendMessage(tempMessage);
								}
							} else {
								sender.sendMessage(instance.getMessagesHandler().PLAYER_NOT_FOUND.replace("%player%", args[1]));
							}
						}

						if (args[0].equalsIgnoreCase("setspawn")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);
						}

						if (args[0].equalsIgnoreCase("setlobby")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);
						}
					}
				}
				
				if (args.length == 3) {
					if (!(args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("backend") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setlobby"))) {
						for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE) {
							sender.sendMessage(tempMessage);
						}
					} else {
						if (args[0].equalsIgnoreCase("reload")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("about")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("reset")) {
							String type = args[2];
							User user = new User(args[1]);

							if (type.equalsIgnoreCase("2fa") || type.equalsIgnoreCase("mail")) {
								if (type.equalsIgnoreCase("2fa")) {
									if (user.get2FA()) {
										user.set2FA(false);

										sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_DISABLED_2FA.replace("%player%", args[1]));

										return true;
									}

									sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_2FA_ENABLED.replace("%player%", args[1]));
								}

								if (type.equalsIgnoreCase("mail")) {
									sender.sendMessage(instance.color("&f[&cAuthTools&f] &cEmail &fis in progress! Keep watching for an &cupdate&f!"));
								}

								return true;
							}

							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("backend")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("info")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);
						}

						if (args[0].equalsIgnoreCase("setspawn")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);
						}

						if (args[0].equalsIgnoreCase("setlobby")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);
						}
					}
				}
				
				if (args.length >= 4) {
					if (!(args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("backend") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setlobby"))) {
						for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE) {
							sender.sendMessage(tempMessage);
						}
					} else {
						if (args[0].equalsIgnoreCase("reload")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("about")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("reset")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("backend")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);
						}
						
						if (args[0].equalsIgnoreCase("info")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);
						}

						if (args[0].equalsIgnoreCase("setspawn")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);
						}

						if (args[0].equalsIgnoreCase("setlobby")) {
							sender.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);
						}
					}
				}

			} else {
				Player p = (Player) sender;
				
				if (!p.hasPermission("authtools.use") || !p.hasPermission("authtools.*")) {
					p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
				} else {
					if (args.length == 0) {
						instance.getMessagesHandler().sendMessage(p, instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE);
					}

					if (args.length == 1) {
						if (!(args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("backend") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setlobby"))) {
							instance.getMessagesHandler().sendMessage(p, instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE);
						} else {
							if (args[0].equalsIgnoreCase("reload")) {
								if (p.hasPermission("authtools.use.reload") || p.hasPermission("authtools.*")) {
									instance.reloadPlugin();
									p.sendMessage(instance.getMessagesHandler().PLUGIN_RELOADED);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("about")) {
								if (p.hasPermission("authtools.use.about") || p.hasPermission("authtools.*")) {
									boolean updateNeeded = instance.checkForUpdates();
									for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUT) {
										tempMessage = tempMessage.replace("%version%",
												instance.getDescription().getVersion());
										tempMessage = tempMessage.replace("%connection%", instance.getConnectionType());

										if (updateNeeded) {
											tempMessage = tempMessage.replace("%is_update_needed%", "(Update needed!)");
										} else {
											tempMessage = tempMessage.replace("%is_update_needed%", "");
										}

										p.sendMessage(tempMessage);
									}
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("reset")) {
								if (p.hasPermission("authtools.use.reset") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("backend")) {
								if (p.hasPermission("authtools.use.backend") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("info")) {
								if (p.hasPermission("authtools.use.info") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("setspawn")) {
								if (p.hasPermission("authtools.use.setspawn") || p.hasPermission("authtools.*")) {
									instance.getSpawnHandler().createSpawn("spawn", p.getLocation());
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWN);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("setlobby")) {
								if (p.hasPermission("authtools.use.setlobby") || p.hasPermission("authtools.*")) {
									instance.getSpawnHandler().createSpawn("lobby", p.getLocation());
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBY);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
						}
					}

					if (args.length == 2) {
						if (!(args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("backend") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setlobby"))) {
							instance.getMessagesHandler().sendMessage(p, instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE);
						} else {
							if (args[0].equalsIgnoreCase("reload")) {
								if (p.hasPermission("authtools.use.reload") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("about")) {
								if (p.hasPermission("authtools.use.about") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("reset")) {
								if (p.hasPermission("authtools.use.reset") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("backend")) {
								if (p.hasPermission("authtools.use.backend") || p.hasPermission("authtools.*")) {
									String enteredConnectionType = args[1];
									String connectionType = instance.getConnectionType();
	
									if (enteredConnectionType.equalsIgnoreCase("yaml") || enteredConnectionType.equalsIgnoreCase("mysql") || enteredConnectionType.equalsIgnoreCase("sqlite")) {
										if (enteredConnectionType.equalsIgnoreCase("yaml")) {
											if (connectionType.equals("YAML")) {
												p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED.replace("%connectionType%", enteredConnectionType.toUpperCase()));
												return true;
											}
											
											instance.switchConnection(enteredConnectionType);
											p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION.replace("%connectionType%", enteredConnectionType.toUpperCase()));
										}
										
										if (enteredConnectionType.equalsIgnoreCase("mysql")) {
											if (connectionType.equals("MYSQL")) {
												p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED.replace("%connectionType%", enteredConnectionType.toUpperCase()));
												return true;
											}
	
											if (instance.getMySQL().connect()) {
												p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION.replace("%connectionType%", enteredConnectionType.toUpperCase()));
												instance.switchConnection(enteredConnectionType);
											} else {
												p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_COULDNT_SWTICH_CONNECTION.replace("%connectionType%", enteredConnectionType.toUpperCase()));
											}
										}
	
										if (enteredConnectionType.equalsIgnoreCase("sqlite")) {
											if (connectionType.equals("SQLITE")) {
												p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_ALREADY_CONNECTED.replace("%connectionType%", enteredConnectionType.toUpperCase()));
												return true;
											}
	
											instance.switchConnection(enteredConnectionType);
											p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_SWITCHED_TO_CONNECTION.replace("%connectionType%", enteredConnectionType.toUpperCase()));
										}
	
										return true;
									}
	
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKEND_WRONG_CONNECTION_TYPE.replace("%connectionType%", enteredConnectionType.toUpperCase()));
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("info")) {
								if (p.hasPermission("authtools.use.info") || p.hasPermission("authtools.*")) {
									User user = new User(args[1]);
	
									if (user.isInDatabase()) {
										ArrayList<String> messages = new ArrayList<String>();
	
										for (String tempMessage : instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFO) {
											tempMessage = tempMessage.replace("%player%", args[1]);
	
											if (user.getUUID() != null) {
												tempMessage = tempMessage.replace("%uuid%", user.getUUID());
											} else {
												tempMessage = tempMessage.replace("%uuid%", "Unknown");
											}
											
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
											
											if (user.getSettingUp2FA()) {
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
												tempMessage = tempMessage.replace("%2fa_recoverycode%", String.valueOf(user.getRecoveryCode()));
											} else {
												tempMessage = tempMessage.replace("%2fa_recoverycode%", "Unknown");
											}
	
											messages.add(tempMessage);
										}
	
										for (String tempMessage : messages) {
											p.sendMessage(tempMessage);
										}
									} else {
										p.sendMessage(instance.getMessagesHandler().PLAYER_NOT_FOUND.replace("%player%", args[1]));
									}
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("setspawn")) {
								if (p.hasPermission("authtools.use.setspawn") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("setlobby")) {
								if (p.hasPermission("authtools.use.setlobby") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
						}
					}
					
					if (args.length == 3) {
						if (!(args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("backend") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setlobby"))) {
							instance.getMessagesHandler().sendMessage(p, instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE);
						} else {
							if (args[0].equalsIgnoreCase("reload")) {
								if (p.hasPermission("authtools.use.reload") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("about")) {
								if (p.hasPermission("authtools.use.about") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("reset")) {
								if (p.hasPermission("authtools.use.reset") || p.hasPermission("authtools.*")) {
									String type = args[2];
									User user = new User(args[1]);
	
									if (type.equalsIgnoreCase("2fa") || type.equalsIgnoreCase("mail")) {
										if (type.equalsIgnoreCase("2fa")) {
											if (user.get2FA()) {
												user.set2FA(false);
	
												p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_DISABLED_2FA.replace("%player%", p.getName()));
	
												return true;
											}
	
											p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESET_PLAYER_DOESNT_HAVE_2FA_ENABLED.replace("%player%", p.getName()));
										}
	
										if (type.equalsIgnoreCase("mail")) {
											p.sendMessage(instance.color("&f[&cAuthTools&f] &cEmail &fis in progress! Keep watching for an &cupdate&f!"));
										}
	
										return true;
									}
	
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("backend")) {
								if (p.hasPermission("authtools.use.backend") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("info")) {
								if (p.hasPermission("authtools.use.info") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("setspawn")) {
								if (p.hasPermission("authtools.use.setspawn") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("setlobby")) {
								if (p.hasPermission("authtools.use.setlobby") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
						}
					}
					
					if (args.length >= 4) {
						if (!(args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("backend") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setlobby"))) {
							instance.getMessagesHandler().sendMessage(p, instance.getMessagesHandler().COMMANDS_AUTHTOOLS_HELPUSAGE);
						} else {
							if (args[0].equalsIgnoreCase("reload")) {
								if (p.hasPermission("authtools.use.reload") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RELOADUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("about")) {
								if (p.hasPermission("authtools.use.about") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_ABOUTUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("reset")) {
								if (p.hasPermission("authtools.use.reset") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_RESETUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("backend")) {
								if (p.hasPermission("authtools.use.backend") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_BACKENDUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
							
							if (args[0].equalsIgnoreCase("info")) {
								if (p.hasPermission("authtools.use.info") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_INFOUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("setspawn")) {
								if (p.hasPermission("authtools.use.setspawn") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETSPAWNUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}

							if (args[0].equalsIgnoreCase("setlobby")) {
								if (p.hasPermission("authtools.use.setlobby") || p.hasPermission("authtools.*")) {
									p.sendMessage(instance.getMessagesHandler().COMMANDS_AUTHTOOLS_SETLOBBYUSAGE);
								} else {
									p.sendMessage(instance.getMessagesHandler().NO_PERMISSIONS);
								}
							}
						}
					}
				}

			}
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("authtools")) {
			if (args.length == 1) {
				List<String> mainCommands = Arrays.asList("reset", "backend", "info", "setspawn", "setlobby", "about", "reload");
				List<String> possibleCommands = new ArrayList<String>();
                
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
                	possibleCommands.add("reload");
                }
                
                Collections.sort(possibleCommands);
                
                return possibleCommands;
            }
			
			if (args.length == 2) {
				List<String> mainCommands = Arrays.asList("YAML", "MYSQL", "SQLITE");
				List<String> possibleCommands = new ArrayList<String>();
				
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
	                		if (command.toLowerCase().startsWith(args[0].toLowerCase())) {
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
						possibleCommands.add("SQLITE");
					}
				}
				
				Collections.sort(possibleCommands);
				
				return possibleCommands;
			}

			if (args.length >= 3) {
				List<String> mainCommands = Arrays.asList("2fa", "mail");
				List<String> possibleCommands = new ArrayList<String>();
                
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
