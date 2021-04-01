package pavlyi.authtools.listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.connorlinfoot.titleapi.TitleAPI;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import pavlyi.authtools.AuthTools;
import pavlyi.authtools.commands.TFACommand;
import pavlyi.authtools.events.AuthToolsPlayerLoggedEvent;
import pavlyi.authtools.handlers.ActionBarAPI;
import pavlyi.authtools.handlers.ImageRenderer;
import pavlyi.authtools.handlers.QRCreate;
import pavlyi.authtools.handlers.SpawnHandler;
import pavlyi.authtools.handlers.User;

public class PlayerLoginListener implements Listener {
	private AuthTools instance = AuthTools.getInstance();

	@EventHandler
	public void authLogin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		User user = new User(p.getName());
		
		if (!user.get2FA())
			return;

		instance.getSpawnLocations().put(p.getName(), p.getLocation());

		if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TELEPORT_UNAUTHED_TO_SPAWN) {
			if (instance.getSpawnHandler().getSpawn("spawn") != null)
	    		p.teleport(instance.getSpawnHandler().getSpawn("spawn"));
		}

		instance.getAuthLocked().add(p.getName());

		instance.getBungeeCordAPI().sendAuthorizationValueToBungeeCord(p, 0);

		p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_LOGIN_MESSAGE);
		
		TitleAPI.clearTitle(p);
		if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_ENABLE) {
			if (!instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_USE_IN_LOGIN)
				return;

			TitleAPI.sendTitle(p, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEIN, 20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_TITLE_FADEOUT, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_TITLE);
		}
		
		if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_ENABLE) {
			if (!instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_USE_IN_LOGIN)
				return;

			TitleAPI.sendSubtitle(p, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEIN, 20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_SUBTITLE_FADEOUT, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_SUBTITLE);
		}

		if (instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_ENABLE) {
			if (!instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_ACTIONBAR_USE_IN_LOGIN)
				return;

			int taskID;

			taskID = instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
				
				@Override
				public void run() {
					ActionBarAPI.sendActionBar(p, instance.getConfigHandler().SETTINGS_TITLE_ANNOUNCEMENT_LOGIN_ACTIONBAR);
				}
			}, 0, 20);

			instance.getActionBarRunnables().put(p.getName(), taskID);
		}

		if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT != 0) {
			int taskID;

			taskID = instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

				@Override
				public void run() {
					p.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_TIMED_OUT);
				}

			}, 20 * instance.getConfigHandler().SETTINGS_RESTRICTIONS_TIMEOUT);

			instance.getRunnables().put(p.getName(), taskID);
		}
	}

	@EventHandler
	public void removePlayer(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		if (instance.getAuthLocked().contains(p.getName())) {
			TitleAPI.clearTitle(p);
			if (instance.getActionBarRunnables().get(p.getName()) != null) {
    			instance.getServer().getScheduler().cancelTask(instance.getActionBarRunnables().get(p.getName()));
				instance.getActionBarRunnables().remove(p.getName(), instance.getActionBarRunnables().get(p.getName()));							
			}

			instance.getRegisterLocked().remove(p.getName());
			instance.getAuthLocked().remove(p.getName());

			if (instance.getRunnables().get(p.getName()) != null)
        		instance.getServer().getScheduler().cancelTask(instance.getRunnables().get(p.getName()));

			instance.getBungeeCordAPI().sendAuthorizationValueToBungeeCord(p, 1);
		}
	}
	
	@EventHandler
	public void verifyCode(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		User user = new User(p.getName());
		String message = e.getMessage().replaceAll("\\s", "");
		int code = 0;

		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);

			try {
	        	code = Integer.parseInt(message);

	        	if (user.get2FAsecret() != null) {
	        		if (instance.getGoogleAuthenticator().authorize(user.get2FAsecret(), code)) {
	        			TitleAPI.clearTitle(p);

						if (instance.getActionBarRunnables().get(p.getName()) != null) {
		        			instance.getServer().getScheduler().cancelTask(instance.getActionBarRunnables().get(p.getName()));
							instance.getActionBarRunnables().remove(p.getName(), instance.getActionBarRunnables().get(p.getName()));							
						}

						instance.getRegisterLocked().remove(p.getName());
						instance.getAuthLocked().remove(p.getName());

			        	if (instance.getSpawnHandler().getSpawn("lobby") != null)
			        		p.teleport(instance.getSpawnHandler().getSpawn("lobby"));

			        	if (instance.getRunnables().get(p.getName()) != null)
			        		instance.getServer().getScheduler().cancelTask(instance.getRunnables().get(p.getName()));

			        	instance.getPluginManager().callEvent(new AuthToolsPlayerLoggedEvent(p, code));

			        	instance.getBungeeCordAPI().sendAuthorizationValueToBungeeCord(p, 1);

			        	instance.getBungeeCordAPI().sendPlayerToServer(p);

			        	p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_LOGGED_IN);

			            return;
			        }
	        	} else {
					instance.getRegisterLocked().remove(p.getName());
					instance.getAuthLocked().remove(p.getName());
	        	}

		        p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_INVALID_CODE);

				if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE)
					instance.getServer().getScheduler().runTask(instance, new Runnable() {
						public void run() {
							p.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_WRONG_CODE_KICK);
						}
					});
	        } catch (NumberFormatException ex) {
	        	p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_INVALID_CODE);

		        if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_KICK_ON_WRONG_2FA_CODE)
		        	p.kickPlayer(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_WRONG_CODE_KICK);
	        }
		}
	}










	@EventHandler
	public void disableCommands(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			if (!instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_COMMANDS.stream().anyMatch((s) -> e.getMessage().replaceFirst("/", "").startsWith(s))) {
				e.setCancelled(true);
				p.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_DENIED_COMMAND);
				return;
			}
			
			e.setCancelled(false);
		}
		
	}
	
	@EventHandler
	public void disableArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}

	@EventHandler
	public void disableDamage(EntityDamageEvent e) {
		if (e.getEntityType().equals(EntityType.PLAYER)) {
			Player p = (Player) e.getEntity();
			
			if (instance.getAuthLocked().contains(p.getName())) {
				e.setCancelled(true);
			}
		}
		
	}

	@EventHandler
	public void disableBedEnter(PlayerBedEnterEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}

	@EventHandler
	public void disableBucketEmpty(PlayerBucketEmptyEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableBucketFill(PlayerBucketFillEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}

	@EventHandler
	public void disableDeathEvent(PlayerDeathEvent e) {
		Player p = e.getEntity();

		if (instance.getAuthLocked().contains(p.getName())) {
			e.setKeepInventory(true);
			e.setDeathMessage(null);
			p.setHealth(p.getMaxHealth());
		}
		
	}

	
	@EventHandler
	public void disableDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableEditBook(PlayerEditBookEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableExpChangeEvent(PlayerExpChangeEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setAmount(0);
		}
		
	}
	
	@EventHandler
	public void disableFishEvent(PlayerFishEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disablePlayerDamagingMobs(EntityDamageByEntityEvent e) {
		Entity entity = e.getEntity();

		if (entity.getType() == EntityType.PLAYER) {
			Player p = (Player) e.getEntity();
			
			if (instance.getAuthLocked().contains(p.getName())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void disableGameModeChange(PlayerGameModeChangeEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableInteractAtEntity(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableItemConsume(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableItemDamage(PlayerItemDamageEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableItemHeld(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableLevelChange(PlayerLevelChangeEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			p.setLevel(e.getOldLevel());
		}
		
	}
	
	@EventHandler
	public void disableLeashEntity(PlayerLeashEntityEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableMoveEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			Location spawnLocation = instance.getSpawnLocations().get(p.getName());
			Location playerLocation = p.getLocation();

			int spawnX = spawnLocation.getBlockX();
			int spawnY = spawnLocation.getBlockY();
			int spawnZ = spawnLocation.getBlockZ();

			int playerX = playerLocation.getBlockX();
			int playerY = playerLocation.getBlockY();
			int playerZ = playerLocation.getBlockZ();

			if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOW_MOVEMENT) {
				if (playerX == (spawnX-instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS)) {
					p.teleport(new Location(playerLocation.getWorld(), (playerX+1), playerY, playerZ));
				}

				if (playerX == (spawnX+instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS)) {
					p.teleport(new Location(playerLocation.getWorld(), (playerX-1), playerY, playerZ));
				}

				if (playerZ == (spawnZ-instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS)) {
					p.teleport(new Location(playerLocation.getWorld(), playerX, playerY, (playerZ+1)));
				}

				if (playerZ == (spawnZ+instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS)) {
					p.teleport(new Location(playerLocation.getWorld(), playerX, playerY, (playerZ-1)));
				}

				return;
			}

			if (playerX != spawnX || playerY != spawnY || playerZ != spawnZ) {
				p.teleport(new Location(spawnLocation.getWorld(), spawnX, spawnY, spawnZ));
			}
		}
	}

	@EventHandler
	public void disablePickupItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}

	}
	
	@EventHandler
	public void disableMobsTargetingPlayer(EntityTargetLivingEntityEvent e) {
		Entity entity = e.getEntity();

		if (entity.getType() == EntityType.PLAYER) {
			Player p = (Player) e.getEntity();
			
			if (instance.getAuthLocked().contains(p.getName())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void disablePortal(PlayerPortalEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableShearEntity(PlayerShearEntityEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableToggleFlight(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableToggleSneak(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableToggleSprint(PlayerToggleSprintEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableUnleashEntity(PlayerUnleashEntityEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableVelocity(PlayerVelocityEvent e) {
		Player p = e.getPlayer();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableInventoryCreative(InventoryCreativeEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableInventoryDragEvent(InventoryDragEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableInventoryInteract(InventoryInteractEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if (instance.getAuthLocked().contains(p.getName())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void disableSessionKick(PlayerKickEvent e) {
		if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION) {
			if (e.getReason().equalsIgnoreCase("You logged in from another location")) {
				e.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void disableSessionKick1(AsyncPlayerPreLoginEvent e) {
		String p = e.getName();
		
		if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION) {
			if (Bukkit.getPlayer(p) != null && Bukkit.getPlayer(p).isOnline())
				e.disallow(Result.KICK_WHITELIST, instance.getMessagesHandler().COMMANDS_2FA_LOGIN_PLAYER_IS_ONLINE_KICK);
		}
		
	}

}
