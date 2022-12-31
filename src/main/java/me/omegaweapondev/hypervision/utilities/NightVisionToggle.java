package me.omegaweapondev.hypervision.utilities;

import me.omegaweapondev.hypervision.HyperVision;
import me.omegaweapondev.hypervision.configs.MessageHandler;
import me.omegaweapondev.hypervision.configs.UserDataHandler;
import me.omegaweapondev.omegalibs.OmegaLibs;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 * The Night Vision Toggle class which handles toggling the night vision potion effect for a player
 *
 * @author OmegaWeaponDev
 */
public class NightVisionToggle {
    private final HyperVision plugin;
    private final FileConfiguration configFile;
    private final MessageHandler messagesHandler;
    private final UserDataHandler userDataHandler;

    private final boolean particleEffects;
    private final boolean particleAmbients;
    private final boolean nightVisionIcon;

    private final String nightVisionApplied;
    private final String nightVisionRemoved;


    private final CommandSender commandSender;

    /**
     *
     * The public constructor for the Night Vision Toggle class
     *
     * @param plugin (The plugin's instance)
     * @param commandSender (The player currently targeted)
     */
    public NightVisionToggle(final HyperVision plugin, final CommandSender commandSender) {
        this.plugin = plugin;
        this.commandSender = commandSender;
        configFile = plugin.getConfigHandler().getConfigFile().getConfig();
        messagesHandler = plugin.getMessageHandler();
        userDataHandler = plugin.getUserDataHandler();

        particleEffects = configFile.getBoolean("Night_Vision_Settings.Particle_Effects");
        particleAmbients = configFile.getBoolean("Night_Vision_Settings.Particle_Ambient");
        nightVisionIcon = configFile.getBoolean("Night_Vision_Settings.Night_Vision_Icon");
        nightVisionApplied = messagesHandler.string("Night_Vision_Messages.Night_Vision_Applied", "#2b9bbfNight Vision has been applied!");
        nightVisionRemoved = messagesHandler.string("Night_Vision_Messages.Night_Vision_Removed", "#f63e3eNight Vision has been removed!");
    }

    /**
     *
     * Handles toggling night vision on|off for a specific player
     *
     */
    public void nightVisionToggle() {
        if (!(commandSender instanceof Player player)) return;

        // Check if the player has permission
        if(!toggleSelfPerm(player)) {
            return;
        }

        // Check if the player currently has night vision enabled
        if((boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION)) {
            // Remove night vision from the player
            userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
            OmegaLibs.removePotionEffect(player, PotionEffectType.NIGHT_VISION);

            // Send night vision removal messages
            sendNightVisionRemovedMessages(player);
            toggleSoundEffect(player, "Night_Vision_Disabled");
            return;
        }

        // Check if the night vision cost has been enabled, and withdraw the money if it has.
        if (!withdrawNightVisionCost(player)) {
            return;
        }

        // Check if they have particle bypass perm and apply correct night vision effect
        applyNightVision(player, 60 * 60 * 24 * 100);
    }

    /**
     *
     * Handles toggling night vision on|off for a target player
     *
     * @param target (The player whose night vision status is to be modified)
     */
    public void nightVisionToggleOthers(final Player target) {
        if(!(commandSender instanceof ConsoleCommandSender)) {
            Player player = (Player) commandSender;

            // Check if the player has permission
            if(!toggleOthersPerm(player)) {
                return;
            }

            if(target.getName().equals(player.getName())) {
                if(!OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.toggle.others", "hypervision.nightvision.admin", "hypervision.admin")) {
                    OmegaLibs.message(player, messagesHandler.string("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
                    return;
                }
            }
        }

        // Check if the target currently has night vision enabled
        if((boolean) userDataHandler.getEffectStatus(target.getUniqueId(), UserDataHandler.NIGHT_VISION)) {
            // Remove night vision from the target
            userDataHandler.setEffectStatus(target.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
            OmegaLibs.removePotionEffect(target, PotionEffectType.NIGHT_VISION);

            // Send night vision removal messages
            sendNightVisionRemovedMessages(target);
            toggleSoundEffect(target, "Night_Vision_Disabled");
            return;
        }

        // Check if the target has particle bypass perm and apply correct night vision effect
        applyNightVision(target, 60 * 60 * 24 * 100);
    }

    /**
     *
     * Handles toggling night vision on for a specific amount of time
     *
     * @param target (The player whose night vision status is to be modified)
     * @param seconds (The duration in seconds for how long the night vision will last)
     */
    public void nightVisionToggleTemp(final Player target, final int seconds) {
        if(!(commandSender instanceof ConsoleCommandSender)) {
            Player player = (Player) commandSender;

            // Check if the player has permission
            if(!toggleTempPerm(player)) {
                return;
            }
        }

        // Check if the target currently has night vision enabled
        if((boolean) userDataHandler.getEffectStatus(target.getUniqueId(), UserDataHandler.NIGHT_VISION)) {
            // Remove night vision from the target
            OmegaLibs.removePotionEffect(target, PotionEffectType.NIGHT_VISION);
        }

        // Check if the target has particle bypass perm and apply correct night vision effect
        applyNightVision(target, seconds);
    }

    /**
     *
     * Handles toggling night vision on|off for all players currently online
     *
     * @param action (Either `add` | `remove`)
     */
    public void nightVisionToggleGlobal(final String action) {
        if (!(commandSender instanceof ConsoleCommandSender)) {
            Player player = (Player) commandSender;

            // Check if the player has permission
            if (!toggleGlobalPerm(player)) {
                return;
            }
        }

        if (Bukkit.getOnlinePlayers().size() == 0) {
            OmegaLibs.logWarning(true, "There are currently no players online!");
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (action.equalsIgnoreCase("remove")) {
                // Remove night vision from the target
                userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
                OmegaLibs.removePotionEffect(player, PotionEffectType.NIGHT_VISION);

                if (OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.global.alert", "hypervision.nightvision.admin", "hypervision.admin")) {
                    OmegaLibs.message(player, messagesHandler.string("Night_Vision_Messages.Night_Vision_Removed_Global", "#2b9bbfNight Vision has been removed for all players!"));
                }
                toggleSoundEffect(player, "Night_Vision_Disabled");
                continue;
            }

            if (action.equalsIgnoreCase("add")) {
                // Check if the target has particle bypass perm and apply correct night vision effect
                applyNightVisionGlobal(player);

                // Send night vision applied messages
                if (OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.global.alert", "hypervision.nightvision.admin", "hypervision.admin")) {
                    OmegaLibs.message(player, messagesHandler.string("Night_Vision_Messages.Night_Vision_Applied_Global", "#2b9bbfNight Vision has been applied for all players!"));
                }
            }
        }
    }

    /**
     *
     * Handles how the night vision effect is applied for the player
     *
     * @param player (The player that night vision is to be applied to)
     * @param duration (The duration in seconds for how long the night vision will last)
     */
    private void applyNightVision(final Player player, final int duration) {
        increaseLimitAmount(player);
        if(!hasReachedLimit(player)) {
            userDataHandler.setEffectStatus(player.getUniqueId(), true, UserDataHandler.NIGHT_VISION);
            userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.LIMIT_REACHED);
            if(OmegaLibs.checkPermissions(player, false, "hypervision.nightvision.particles.bypass", "hypervision.nightvision.admin", "hypervision.admin")) {
                OmegaLibs.addPotionEffect(player, PotionEffectType.NIGHT_VISION, duration ,1, false, false, false);
            } else {
                OmegaLibs.addPotionEffect(player, PotionEffectType.NIGHT_VISION, duration ,1, particleEffects, particleAmbients, nightVisionIcon);
            }
            toggleSoundEffect(player, "Night_Vision_Applied");
            sendNightVisionAppliedMessages(player);
            return;
        }
        OmegaLibs.message(player, messagesHandler.string("Night_Vision_Limit.Limit_Reached", "#f63e3eSorry, you have reached the limit for the night vision command!"));
        toggleSoundEffect(player, "Limit_Reached");
    }

    /**
     *
     * Handles how the night vision effect is applied for all player currently online
     *
     * @param player (The player that night vision is to be applied to)
     */
    public void applyNightVisionGlobal(final Player player) {
        userDataHandler.setEffectStatus(player.getUniqueId(), true, UserDataHandler.NIGHT_VISION);
        if(OmegaLibs.checkPermissions(player, false, "hypervision.nightvision.particles.bypass", "hypervision.nightvision.admin", "hypervision.admin")) {
            OmegaLibs.addPotionEffect(player, PotionEffectType.NIGHT_VISION, 60 * 60 * 24 * 100 ,1, false, false, false);
        } else {
            OmegaLibs.addPotionEffect(player, PotionEffectType.NIGHT_VISION, 60 * 60 * 24 * 100 ,1, particleEffects, particleAmbients, nightVisionIcon);
        }
        toggleSoundEffect(player, "Night_Vision_Applied");
    }

    /**
     *
     * Sends the player a message notifying them that night vision has been applied
     *
     * @param player (The player that the message needs to be sent to)
     */
    private void sendNightVisionAppliedMessages(final Player player) {
        OmegaLibs.message(player, nightVisionApplied);
    }

    /**
     *
     * Sends the player a message notifying them that night vision has been removed
     *
     * @param player (The player that the message needs to be sent to)
     */
    private void sendNightVisionRemovedMessages(final Player player) {
        OmegaLibs.message(player, nightVisionRemoved);
    }

    /**
     *
     * Checks a player's current Night Vision Limit status
     *
     * @param player (The player whose night vision limit status is being checked)
     * @return (The current limit status)
     */
    private boolean hasReachedLimit(@NotNull final Player player) {
        if(!configFile.getBoolean("Night_Vision_Limit.Enabled")) {
            return false;
        }

        if(OmegaLibs.checkPermissions(player, true, "hypervision.limit.bypass", "hypervision.limit.admin", "hypervision.admin")) {
            return false;
        }

        return (boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.LIMIT_REACHED);
    }

    /**
     *
     * Increase a specific players night vision limit amount
     *
     * @param player (The player whose limit is getting increased)
     */
    private void increaseLimitAmount(@NotNull final Player player) {
        if(!configFile.getBoolean("Night_Vision_Limit.Enabled")) {
            return;
        }

        if(OmegaLibs.checkPermissions(player, true, "hypervision.limit.bypass", "hypervision.limit.admin", "hypervision.admin")) {
            return;
        }
        int currentLimitCount = (int) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.LIMIT);

        if((currentLimitCount + 1) > configFile.getInt("Night_Vision_Limit.Limit")) {
            userDataHandler.setEffectStatus(player.getUniqueId(), configFile.getInt("Night_Vision_Limit.Limit"), UserDataHandler.LIMIT);
            userDataHandler.setEffectStatus(player.getUniqueId(), true, UserDataHandler.LIMIT_REACHED);
            userDataHandler.setEffectStatus(player.getUniqueId(), System.currentTimeMillis(), UserDataHandler.LIMIT_REACHED_TIME);
            limitResetTimer(player);
            return;
        }

        userDataHandler.setEffectStatus(player.getUniqueId(), currentLimitCount + 1, UserDataHandler.LIMIT);
        OmegaLibs.message(player, messagesHandler.string("Night_Vision_Limit.Limit_Amount_Increased", "#1fe3e0Your limit amount now stands at: #f63e3e%currentLimitAmount% / %maxLimitAmount%")
                .replace("%currentLimitAmount%", String.valueOf(currentLimitCount + 1))
                .replace("%maxLimitAmount%", String.valueOf(configFile.getInt("Night_Vision_Limit.Limit")))
        );
    }

    /**
     *
     * Handles resetting a players limit status after a specific timeframe
     * Is triggered once the player has reached the max limit
     *
     * @param player (The player whose night vision limit is being reset)
     */
    public void limitResetTimer(final Player player) {
        if(!(boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.LIMIT_REACHED)) {
            return;
        }

        final UUID playerUUID = player.getUniqueId();

        long configResetTimer = TimeUnit.MILLISECONDS.convert(configFile.getInt("Night_Vision_Limit.Reset_Timer"), TimeUnit.MINUTES);
        long limitTimeReached = (long) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.LIMIT_REACHED_TIME);

        if(System.currentTimeMillis() >= (limitTimeReached + configResetTimer)) {
            userDataHandler.setEffectStatus(player.getUniqueId(), 0, UserDataHandler.LIMIT);
            userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.LIMIT_REACHED);
            userDataHandler.setEffectStatus(player.getUniqueId(), 0, UserDataHandler.LIMIT_REACHED_TIME);
            userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.RESET_TIMER_ACTIVE);
            if(player.isOnline()) {
                OmegaLibs.message(player, messagesHandler.string("Night_Vision_Limit.Limit_Reset", "#1fe3e0Your night vision limits have reset! You can use the night vision command again!"));
            }
            return;
        }

        if((boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.RESET_TIMER_ACTIVE)) {
            return;
        }

        userDataHandler.setEffectStatus(player.getUniqueId(), true, UserDataHandler.RESET_TIMER_ACTIVE);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if(player.isOnline()) {
                userDataHandler.setEffectStatus(playerUUID, 0, UserDataHandler.LIMIT);
                userDataHandler.setEffectStatus(playerUUID, false, UserDataHandler.LIMIT_REACHED);
                userDataHandler.setEffectStatus(playerUUID, 0, UserDataHandler.LIMIT_REACHED_TIME);
                userDataHandler.setEffectStatus(playerUUID, false, UserDataHandler.RESET_TIMER_ACTIVE);

                OmegaLibs.message(player, messagesHandler.string("Night_Vision_Limit.Limit_Reset", "#1fe3e0Your night vision limits have reset! You can use the night vision command again!"));
            } else {
                plugin.getConfigHandler().getUserDataFile().getConfig().set("Users." + playerUUID + "." + UserDataHandler.LIMIT_REACHED, false);
                plugin.getConfigHandler().getUserDataFile().getConfig().set("Users." + playerUUID + "." + UserDataHandler.RESET_TIMER_ACTIVE, false);
                plugin.getConfigHandler().getUserDataFile().getConfig().set("Users." + playerUUID + "." + UserDataHandler.LIMIT_REACHED_TIME, 0);
                plugin.getConfigHandler().getUserDataFile().getConfig().set("Users." + playerUUID + "." + UserDataHandler.LIMIT, 0);
            }

        }, configResetTimer / 50);
    }

    /**
     *
     * Checks the player's permission for the toggle-self command
     *
     * @param player (The player who is being checked for permission)
     * @return (True | false depending on the permission)
     */
    private boolean toggleSelfPerm(final Player player) {
        if(!OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.toggle.self", "hypervision.nightvision.admin", "hypervision.admin")) {
            OmegaLibs.message(player, messagesHandler.string("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
            return false;
        }
        return true;
    }

    /**
     *
     * Checks the player's permission for the toggle-global command
     *
     * @param player (The player who is being checked for permission)
     * @return (True | false depending on the permission)
     */
    private boolean toggleGlobalPerm(final Player player) {
        if(!OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.global", "hypervision.nightvision.admin", "hypervision.admin")) {
            OmegaLibs.message(player, messagesHandler.string("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
            return false;
        }
        return true;
    }

    /**
     *
     * Checks the player's permission for the toggle-temp command
     *
     * @param player (The player who is being checked for permission)
     * @return (True | false depending on the permission)
     */
    private boolean toggleTempPerm(final Player player) {
        if(!OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.temp", "hypervision.nightvision.admin", "hypervision.admin")) {
            OmegaLibs.message(player, messagesHandler.string("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
            return false;
        }
        return true;
    }

    /**
     *
     * Checks the player's permission for the toggle-others command
     *
     * @param player (The player who is being checked for permission)
     * @return (True | false depending on the permission)
     */
    private boolean toggleOthersPerm(final Player player) {
        if(!OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.toggle.others", "hypervision.nightvision.admin", "hypervision.admin")) {
            OmegaLibs.message(player, messagesHandler.string("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
            return false;
        }
        return true;
    }

    /**
     *
     * Toggles a sound effect depending on the action taken for a specific player
     *
     * @param player (The player to play the sound effect for)
     * @param soundEffect (The specific sound effect that needs to be played)
     */
    public void toggleSoundEffect(final Player player, final String soundEffect) {

        if(!configFile.getBoolean("Sound_Effects.Enabled")) {
            return;
        }

        switch (soundEffect) {
            case "Night_Vision_Applied" -> {
                if (!configFile.getBoolean("Sound_Effects.Night_Vision_Enable.Enabled")) {
                    break;
                }
                player.playSound(player.getLocation(), Sound.valueOf(configFile.getString("Sound_Effects.Night_Vision_Enable.Sound")), 1, 1);
            }
            case "Night_Vision_Disabled" -> {
                if (!configFile.getBoolean("Sound_Effects.Night_Vision_Disable.Enabled")) {
                    break;
                }
                player.playSound(player.getLocation(), Sound.valueOf(configFile.getString("Sound_Effects.Night_Vision_Disable.Sound")), 1, 1);
            }
            case "Limit_Reached" -> {
                if (!configFile.getBoolean("Sound_Effects.Limit_Reached.Enabled")) {
                    break;
                }
                player.playSound(player.getLocation(), Sound.valueOf(configFile.getString("Sound_Effects.Limit_Reached.Sound")), 1, 1);
            }
            default -> {
            }
        }
    }

    /**
     *
     * Handles checking if the player needs to be charged a specific amount
     * when they try to enable night vision.
     *
     * @param player (The player trying to enable night vision)
     * @return (True/False depending on if withdrawal was successful)
     */
    private boolean withdrawNightVisionCost(@NotNull final Player player) {
        // Check if the night vision cost has been enabled.
        if (configFile.getBoolean("Night_Vision_Settings.Night_Vision_Cost.Enabled")) {
            double nightVisionCost = configFile.getDouble("Night_Vision_Settings.Night_Vision_Cost.Amount", 0);

            if(OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.cost.bypass", "hypervision.nightvision.admin", "hypervision.admin")) {
                return true;
            }

            // Try to remove the cost from the players balance
            EconomyResponse economyResponse = plugin.getEcon().withdrawPlayer(player, nightVisionCost);

            // Withdrawal was not successful, send players a message to inform them and return
            if (!economyResponse.transactionSuccess()) {
                OmegaLibs.message(player, messagesHandler.string("Night_Vision_Messages.Night_Vision_Cost_Denied", "#f63e3eSorry, you do not have enough money to use that command!"));
                return false;
            }
            // Withdrawal was successful so send a message to the player and toggle night vision
            OmegaLibs.message(player,
                messagesHandler.string(
                   "Night_Vision_Messages.Night_Vision_Cost_Approved",
                   "#2b9bbfYou have been charged #f63e3e$%NightVisionCost% #2b9bbfto use Night Vision!")
                   .replace("%NightVisionCost%", String.valueOf(nightVisionCost))
            );
            return true;
        }
        return true;
    }
}
