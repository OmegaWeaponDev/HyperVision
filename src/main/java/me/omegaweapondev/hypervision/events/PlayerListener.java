package me.omegaweapondev.hypervision.events;

import me.omegaweapondev.hypervision.HyperVision;
import me.omegaweapondev.hypervision.configs.MessageHandler;
import me.omegaweapondev.hypervision.configs.UserDataHandler;
import me.omegaweapondev.hypervision.utilities.NightVisionToggle;
import me.omegaweapondev.omegalibs.OmegaLibs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {
    private final HyperVision plugin;
    private final FileConfiguration configFile;
    private final MessageHandler messagesHandler;

    private final UserDataHandler userDataHandler;
    private final boolean particleEffects;
    private final boolean ambientEffects;
    private final boolean nightvisionIcon;

    /**
     *
     * The public constructor for the player listener class
     *
     * @param plugin (The plugin's instance)
     */
    public PlayerListener(final HyperVision plugin) {
        this.plugin = plugin;
        configFile = plugin.getConfigHandler().getConfigFile().getConfig();
        userDataHandler = plugin.getUserDataHandler();
        messagesHandler = plugin.getMessageHandler();

        particleEffects = configFile.getBoolean("Night_Vision_Settings.Particle_Effects");
        ambientEffects = configFile.getBoolean("Night_Vision_Settings.Particle_Ambient");
        nightvisionIcon = configFile.getBoolean("Night_Vision_Settings.Night_Vision_Icon");
    }

    /**
     *
     * Listens for the Player Join Event
     * @see org.bukkit.event.player.PlayerJoinEvent
     *
     * @param playerJoinEvent (The player join event that was triggered)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();

        // Add the user to the user data map if they aren't currently in it.
        if(player.getFirstPlayed() == System.currentTimeMillis()) {
            userDataHandler.getUserDataMap().putIfAbsent(player.getUniqueId(), new ConcurrentHashMap<>());
        } else {
            userDataHandler.addUserToMap(player.getUniqueId());
        }

        // Check if the Night Vision Login setting was enabled and the player has a
        // true night vision status in the map and permission for night vision long.
        // If so, Apply night vision to them once they have logged in. Otherwise, remove it.
        if(configFile.getBoolean("Night_Vision_Settings.Night_Vision_Login") && (boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION) && OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.login", "hypervision.nightvision.admin", "hypervision.admin")) {
            OmegaLibs.addPotionEffect(player, PotionEffectType.NIGHT_VISION, 60 * 60 * 24 * 100 ,1, particleEffects, ambientEffects, nightvisionIcon);
        } else {
            userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
            OmegaLibs.removePotionEffect(player, PotionEffectType.NIGHT_VISION);
        }

        if(configFile.getBoolean("Night_Vision_Limit.Enabled")) {
            if((boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.LIMIT_REACHED)) {
                NightVisionToggle nightVisionToggle = new NightVisionToggle(plugin, player);
                nightVisionToggle.limitResetTimer(player);
            }
        }
    }

    /**
     *
     * Listens for the Player Changed World Event
     * @see org.bukkit.event.player.PlayerChangedWorldEvent
     *
     * @param playerChangedWorldEvent (The player changed world event that was triggered)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent playerChangedWorldEvent) {
        final Player player = playerChangedWorldEvent.getPlayer();

        // Checks if the world disabled setting has been enabled.
        if(!configFile.getBoolean("World_Disable.Enabled")) {
            return;
        }

        // Checks if the player has permission to bypass the night vision change world feature
        if(OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.world.bypass", "hypervision.nightvision.admin", "hypervision.admin")) {
            return;
        }

        // Checks the players new world against the list of worlds in the config
        for(String worldName : configFile.getStringList("World_Disable.Worlds")) {
            if(!(boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION)) {
                return;
            }

            // If the world name is in the config world list, remove night vision from the player
            if(player.getWorld().getName().equalsIgnoreCase(worldName)) {
                userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
                OmegaLibs.removePotionEffect(player, PotionEffectType.NIGHT_VISION);
                return;
            }
        }
    }

    /**
     *
     * Listens for the Player Quit Event
     * @see org.bukkit.event.player.PlayerQuitEvent
     *
     * @param playerQuitEvent (The Player Quit Event that was triggered)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        // Save the user's data to the file
        userDataHandler.saveUserDataToFile(playerQuitEvent.getPlayer().getUniqueId());
    }

    /**
     *
     * Listens for the Player Respawn Event
     *
     * @param playerRespawnEvent (The Player Respawn Event that was triggered)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        Player player = playerRespawnEvent.getPlayer();

        // Wait 1 full second to allow for respawn to finish before checking night vision status
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Checks if the keep on death feature has been enabled
            if(!configFile.getBoolean("Night_Vision_Settings.Keep_Night_Vision_On_Death")) {
                return;
            }

            // Checks if the player has permission to keep their night vision when they die
            if(!OmegaLibs.checkPermissions(player, false, "hypervision.nightvision.keepondeath", "hypervision.nightvision.admin", "hypervision.admin")) {
                userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
                OmegaLibs.removePotionEffect(player, PotionEffectType.NIGHT_VISION);
                return;
            }

            if(!((boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION))) {
                return;
            }

            // Re-applies night vision to the player after they respawn
            if(OmegaLibs.checkPermissions(player, false, "hypervision.nightvision.particles.bypass", "hypervision.nightvision.admin", "hypervision.admin")) {
                OmegaLibs.addPotionEffect(player, PotionEffectType.NIGHT_VISION, 60 * 60 * 24 * 100 ,1, false, false, false);
            } else {
                OmegaLibs.addPotionEffect(player, PotionEffectType.NIGHT_VISION, 60 * 60 * 24 * 100 ,1, particleEffects, ambientEffects, nightvisionIcon);
            }
            NightVisionToggle nightVisionToggle = new NightVisionToggle(plugin, player);
            nightVisionToggle.toggleSoundEffect(player, "Night_Vision_Applied");
        }, 20);
    }

    /**
     *
     * Listens for the Player Item Consume Event
     * @see org.bukkit.event.player.PlayerItemConsumeEvent
     *
     * @param playerItemConsumeEvent (The Player Item Consume Event that was triggered)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketUse(PlayerItemConsumeEvent playerItemConsumeEvent) {
        final Player player = playerItemConsumeEvent.getPlayer();
        final ItemStack item = playerItemConsumeEvent.getItem();
        playerItemConsumeEvent.setCancelled(true);

        // Check if the bucket usage feature was enabled.
        if(!configFile.getBoolean("Night_Vision_Settings.Bucket_Usage")) {
            playerItemConsumeEvent.setCancelled(false);
            return;
        }

        // Check if the item consumed was a milk bucket
        if(!item.getType().equals(Material.MILK_BUCKET)) {
            playerItemConsumeEvent.setCancelled(false);
            return;
        }

        // Check if the player currently has night vision
        if(!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
            playerItemConsumeEvent.setCancelled(false);
            return;
        }

        // Check if the player has permission to use the bucket feature
        if(!OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.bucket", "hypervision.nightvision.admin", "hypervision.admin")) {
            userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
            playerItemConsumeEvent.setCancelled(false);
            return;
        }

        // Removes all the potion effects from the player then re-applies night vision without the particle effects
        for(PotionEffect activeEffects : player.getActivePotionEffects()) {
            OmegaLibs.removePotionEffect(player, activeEffects.getType());
        }
        userDataHandler.setEffectStatus(player.getUniqueId(), true, UserDataHandler.NIGHT_VISION);
        OmegaLibs.addPotionEffect(player, PotionEffectType.NIGHT_VISION, 60 * 60 * 24 * 100, 1, false, false, false);
        OmegaLibs.message(player, messagesHandler.string("Night_Vision_Messages.Bucket_Message", "#2b9bbfThe particle effects and the icon have been removed!"));

        // Checks if the bucket empty setting has been enabled
        if(!configFile.getBoolean("Night_Vision_Settings.Bucket_Empty")) {
            return;
        }

        // Replace the milk bucket with an empty bucket
        player.getInventory().getItemInMainHand().setType(Material.BUCKET);
    }
}
