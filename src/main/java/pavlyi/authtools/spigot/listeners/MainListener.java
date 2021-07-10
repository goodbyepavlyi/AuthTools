package pavlyi.authtools.spigot.listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import pavlyi.authtools.spigot.AuthTools;
import pavlyi.authtools.spigot.authentication.AuthHandler;
import pavlyi.authtools.spigot.authentication.User;
import pavlyi.authtools.spigot.storages.Variables;

public class MainListener implements Listener {
    private final AuthTools instance = AuthTools.getInstance();

    @EventHandler
    public void requestAuthentication(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = Variables.getUser(player.getUniqueId());
        AuthHandler authHandler = new AuthHandler(player);

        if (user.getSession() != null)
            if (!user.getSession().getIP().getHostName().equals(player.getAddress().getHostName()) || System.currentTimeMillis() >= user.getSession().getTime())
                user.setSession(0);

        authHandler.requestAuthentication();
    }

    @EventHandler
    public void verifyCode(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        User user = Variables.getUser(player.getUniqueId());

        if (user.hasToBeAuthenticated()) {
            event.setCancelled(true);

            AuthHandler authHandler = new AuthHandler(player);

            String code = event.getMessage().replaceAll(" ", "");

            if (!user.get2FA() && user.isSettingUp2FA()) {
                switch (authHandler.register(code)) {
                    case FAILED:
                    case INVALID_CODE:
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_INVALID_CODE);

                        break;

                    case TFA_REGISTERED:
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_ENABLED);

                        break;

                    case TFA_ALREADY_REGISTERED:
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_ALREADY_REGISTERED);

                        break;

                }

                return;
            }

            if (user.get2FA() && !user.isSettingUp2FA()) {
                switch (authHandler.login(code)) {
                    case TFA_LOGGED:
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_LOGGED_IN);

                        break;

                    case TFA_ALREADY_LOGGED_IN:
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_ALREADY_LOGGED_IN);

                        break;

                    case INVALID_CODE:
                        player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_SETUP_INVALID_CODE);

                        break;
                }
            }
        }
    }

    @EventHandler
    public void storeDefaultData(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = Variables.getUser(player.getUniqueId());

        user.create();
        user.setIP(player.getAddress());
    }

    @EventHandler
    public void cancelSetup(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = Variables.getUser(player.getUniqueId());

        if (user.isSettingUp2FA()) {
            user.setSettingUp2FA(false);
            user.set2FA(false);
            user.set2FAsecret(null);
            user.setRecoveryCode(true);

            TitleAPI.clearTitle(player);

            user.needsToBeAuthenticated(false);
        }
    }

    @EventHandler
    public void disableCommands(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated()) {
            if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_COMMANDS.stream().noneMatch((s) -> event.getMessage().replaceFirst("/", "").startsWith(s))) {
                event.setCancelled(true);
                player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_DENIED_COMMAND);
                return;
            }

            event.setCancelled(false);
        }

    }

    @EventHandler
    public void disableArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();

            if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void disablePlayerDamagingEntities(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getDamager();

            if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void disableBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableMobsTargetingPlayer(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();

            if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void disablePlayerDamagingMobs(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;

            if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void disableDeathEvent(PlayerDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = event.getEntity();

            if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated()) {
                event.setKeepInventory(true);
                event.setDeathMessage(null);
                player.setHealth(player.getHealthScale());
            }
        }
    }

    @EventHandler
    public void disableDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableEditBook(PlayerEditBookEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableExpChangeEvent(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setAmount(0);
    }

    @EventHandler
    public void disableFishEvent(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            player.setLevel(event.getOldLevel());
    }

    @EventHandler
    public void disableLeashEntity(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = Variables.getUser(player.getUniqueId());

        if (user.hasToBeAuthenticated()) {
            Location playerLocation = player.getLocation();

            int playerX = playerLocation.getBlockX();
            int playerY = playerLocation.getBlockY();
            int playerZ = playerLocation.getBlockZ();

            if (user.getSpawnLocation() == null) {
                player.teleport(new Location(playerLocation.getWorld(), (playerX + 0.5), playerY, (playerZ + 0.5)));
                return;
            }

            Location spawnLocation = user.getSpawnLocation();

            int spawnX = spawnLocation.getBlockX();
            int spawnZ = spawnLocation.getBlockZ();

            if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOW_MOVEMENT) {
                if (playerX == (spawnX - instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS))
                    player.teleport(new Location(playerLocation.getWorld(), (playerX + 1 + 0.5), playerY, (playerZ + 0.5)));

                if (playerX == (spawnX + instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS))
                    player.teleport(new Location(playerLocation.getWorld(), (playerX - 1 + 0.5), playerY, (playerZ + 0.5)));

                if (playerZ == (spawnZ - instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS))
                    player.teleport(new Location(playerLocation.getWorld(), (playerX + 0.5), playerY, (playerZ + 1 + 0.5)));

                if (playerZ == (spawnZ + instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_MOVEMENT_RADIUS))
                    player.teleport(new Location(playerLocation.getWorld(), (playerX + 0.5), playerY, (playerZ - 1 + 0.5)));

                return;
            }

            if (!instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOW_MOVEMENT) {
                if (playerX != spawnX || playerZ != spawnZ) {
                    if (Variables.getSpawn("authenticationSpawn") == null) {
                        player.teleport(new Location(playerLocation.getWorld(), (spawnX + 0.5), playerY, (spawnZ + 0.5)));
                    } else {
                        Location location = Variables.getSpawn("authenticationSpawn");
                        assert location != null;
                        location.setYaw(playerLocation.getYaw());
                        location.setPitch(playerLocation.getPitch());
                        player.teleport(location);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void disablePickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disablePortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableShearEntity(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableUnleashEntity(PlayerUnleashEntityEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableInventoryCreative(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableInventoryDragEvent(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableInventoryInteract(InventoryInteractEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (Variables.getUser(player.getUniqueId()).hasToBeAuthenticated())
            event.setCancelled(true);
    }

    @EventHandler
    public void disableSessionKick(PlayerKickEvent event) {
        if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION)
            if (event.getReason().equalsIgnoreCase("You logged in from another location"))
                event.setCancelled(true);

    }

    @EventHandler
    public void disableSessionKick1(AsyncPlayerPreLoginEvent event) {
        String p = event.getName();

        if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION)
            if (instance.getServer().getPlayer(p) != null && instance.getServer().getPlayer(p).isOnline())
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, instance.getMessagesHandler().COMMANDS_2FA_LOGIN_PLAYER_IS_ONLINE_KICK);
    }
}
