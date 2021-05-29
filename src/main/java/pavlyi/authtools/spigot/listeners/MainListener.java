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
import pavlyi.authtools.spigot.enums.HookType;
import pavlyi.authtools.spigot.handlers.AuthHandler;
import pavlyi.authtools.spigot.handlers.User;
import pavlyi.authtools.spigot.handlers.VariablesHandler;

public class MainListener implements Listener {
    private final AuthTools instance = AuthTools.getInstance();

    @EventHandler
    public void requestAuthentication(PlayerJoinEvent e) {
        if (VariablesHandler.getHookType().equals(HookType.STANDALONE)) {
            Player player = e.getPlayer();
            AuthHandler authHandler = new AuthHandler(player);

            authHandler.requestAuthentication();
        }
    }

    @EventHandler
    public void verifyCode(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        User user = new User(player.getName());

        if (user.hasToBeAuthenticated()) {
            e.setCancelled(true);

            AuthHandler authHandler = new AuthHandler(player);

            String code = e.getMessage().replaceAll(" ", "");

            if (!user.get2FA() && user.getSettingUp2FA()) {
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

            if (user.get2FA() && !user.getSettingUp2FA()) {
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
    public void storeDefaultData(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        User user = new User(player.getName());

        user.create();
        user.setIP(player.getAddress());
        user.setUUID();
    }

    @EventHandler
    public void cancelSetup(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        User user = new User(player.getName());

        if (user.getSettingUp2FA()) {
            user.setSettingUp2FA(false);
            user.set2FA(false);
            user.set2FAsecret(null);
            user.setRecoveryCode(true);

            TitleAPI.clearTitle(player);

            user.needsToBeAuthenticated(false);
        }
    }

    @EventHandler
    public void disableCommands(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated()) {
            if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_ALLOWED_COMMANDS.stream().noneMatch((s) -> e.getMessage().replaceFirst("/", "").startsWith(s))) {
                e.setCancelled(true);
                player.sendMessage(instance.getMessagesHandler().COMMANDS_2FA_LOGIN_DENIED_COMMAND);
                return;
            }

            e.setCancelled(false);
        }

    }

    @EventHandler
    public void disableArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) e.getEntity();

            if (new User(player.getName()).hasToBeAuthenticated())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void disablePlayerDamagingEntities(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.PLAYER) {
            Player player = (Player) e.getDamager();

            if (new User(player.getName()).hasToBeAuthenticated())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void disableBedEnter(PlayerBedEnterEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableBucketEmpty(PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableBucketFill(PlayerBucketFillEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableMobsTargetingPlayer(EntityTargetLivingEntityEvent e) {
        Entity entity = e.getEntity();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) e.getEntity();

            if (new User(player.getName()).hasToBeAuthenticated())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void disablePlayerDamagingMobs(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;

            if (new User(player.getName()).hasToBeAuthenticated())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void disableDeathEvent(PlayerDeathEvent e) {
        Entity entity = e.getEntity();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = e.getEntity();

            if (new User(player.getName()).hasToBeAuthenticated()) {
                e.setKeepInventory(true);
                e.setDeathMessage(null);
                player.setHealth(player.getHealthScale());
            }
        }
    }

    @EventHandler
    public void disableDropItem(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableEditBook(PlayerEditBookEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableExpChangeEvent(PlayerExpChangeEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setAmount(0);
    }

    @EventHandler
    public void disableFishEvent(PlayerFishEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableGameModeChange(PlayerGameModeChangeEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableInteractEntity(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableItemConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableItemDamage(PlayerItemDamageEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableItemHeld(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableLevelChange(PlayerLevelChangeEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            player.setLevel(e.getOldLevel());
    }

    @EventHandler
    public void disableLeashEntity(PlayerLeashEntityEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated()) {
            Location playerLocation = player.getLocation();

            int playerX = playerLocation.getBlockX();
            int playerY = playerLocation.getBlockY();
            int playerZ = playerLocation.getBlockZ();

            if (VariablesHandler.getPlayerSpawnLocations().get(player.getUniqueId()) == null) {
                player.teleport(new Location(playerLocation.getWorld(), (playerX + 0.5), playerY, (playerZ + 0.5)));
                return;
            }

            Location spawnLocation = VariablesHandler.getPlayerSpawnLocations().get(player.getUniqueId());

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
                    if (VariablesHandler.getSpawn("authenticationSpawn") == null) {
                        player.teleport(new Location(playerLocation.getWorld(), (spawnX + 0.5), playerY, (spawnZ + 0.5)));
                    } else {
                        Location location = VariablesHandler.getSpawn("authenticationSpawn");
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
    public void disablePickupItem(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disablePortal(PlayerPortalEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableShearEntity(PlayerShearEntityEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableToggleFlight(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableToggleSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableToggleSprint(PlayerToggleSprintEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableUnleashEntity(PlayerUnleashEntityEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableVelocity(PlayerVelocityEvent e) {
        Player player = e.getPlayer();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableInventoryCreative(InventoryCreativeEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableInventoryDragEvent(InventoryDragEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableInventoryInteract(InventoryInteractEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (new User(player.getName()).hasToBeAuthenticated())
            e.setCancelled(true);
    }

    @EventHandler
    public void disableSessionKick(PlayerKickEvent e) {
        if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION)
            if (e.getReason().equalsIgnoreCase("You logged in from another location"))
                e.setCancelled(true);

    }

    @EventHandler
    public void disableSessionKick1(AsyncPlayerPreLoginEvent e) {
        String p = e.getName();

        if (instance.getConfigHandler().SETTINGS_RESTRICTIONS_FORCE_SINGLE_SESSION)
            if (instance.getServer().getPlayer(p) != null && instance.getServer().getPlayer(p).isOnline())
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, instance.getMessagesHandler().COMMANDS_2FA_LOGIN_PLAYER_IS_ONLINE_KICK);
    }
}
