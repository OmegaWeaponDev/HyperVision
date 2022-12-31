package me.omegaweapondev.hypervision.configs;

import me.omegaweapondev.hypervision.HyperVision;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * The UserDataHandler class that handles all the user data for the plugin
 *
 * @author OmegaWeaponDev
 */
public class UserDataHandler {
    public static final String NIGHT_VISION = "Night_Vision";
    public static final String LIMIT_REACHED = "Limit_Reached";
    public static final String LIMIT_REACHED_TIME = "Limit_Reached_Time";
    public static final String RESET_TIMER_ACTIVE = "Reset_Timer_Active";
    public static final String LIMIT = "Limit";
    private final HyperVision plugin;
    private final FileConfiguration userDataFile;
    private final Map<UUID, Map<String, Object>> userDataMap = new ConcurrentHashMap<>();

    /**
     *
     * The public constructor for the User Data Handler class
     *
     * @param plugin (The plugin's instance)
     */
    public UserDataHandler(final HyperVision plugin) {
        this.plugin = plugin;
        userDataFile = plugin.getConfigHandler().getUserDataFile().getConfig();
    }

    /**
     *
     * Grabs all the entries from the userData.yml file and
     * adds the players into the map
     *
     */
    public void populateUserDataMap() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if(userDataFile.getConfigurationSection("Users.").getKeys(false).isEmpty()) {
                return;
            }

            getUserDataMap().clear();
            for(String user : userDataFile.getConfigurationSection("Users.").getKeys(false)) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(user));

                if(player.isOnline()) {
                    getUserDataMap().put(UUID.fromString(user), new ConcurrentHashMap<>());
                }
            }
        });
    }

    /**
     *
     * Adds a specific user's data from the userData.yml and populates their map entry
     *
     */
    public void addUserToMap(@NotNull final UUID playerUUID) {
        getUserDataMap().putIfAbsent(playerUUID, new ConcurrentHashMap<>());

        setEffectStatus(playerUUID, userDataFile.getBoolean("Users." + playerUUID + "." + NIGHT_VISION, false), NIGHT_VISION);
        setEffectStatus(playerUUID, userDataFile.getBoolean("Users." + playerUUID + "." + LIMIT_REACHED, false), LIMIT_REACHED);
        setEffectStatus(playerUUID, userDataFile.getBoolean("Users." + playerUUID + "." + RESET_TIMER_ACTIVE, false), RESET_TIMER_ACTIVE);
        setEffectStatus(playerUUID, userDataFile.getLong("Users." + playerUUID + "." + LIMIT_REACHED_TIME, 0), LIMIT_REACHED_TIME);
        setEffectStatus(playerUUID, userDataFile.getInt("Users." + playerUUID + "." + LIMIT, 0), LIMIT);
    }

    /**
     *
     * Saves all the current entries from the user data map into the userData.yml
     *
     */
    public void saveUserDataToFile() {
        for (UUID userUUID : getUserDataMap().keySet()) {
            userDataFile.set("Users." + userUUID + "." + NIGHT_VISION, getUserDataMap().get(userUUID).getOrDefault(NIGHT_VISION, false));
            userDataFile.set("Users." + userUUID + "." + LIMIT_REACHED, getUserDataMap().get(userUUID).getOrDefault(LIMIT_REACHED, false));
            userDataFile.set("Users." + userUUID + "." + RESET_TIMER_ACTIVE, getUserDataMap().get(userUUID).getOrDefault(RESET_TIMER_ACTIVE, false));
            userDataFile.set("Users." + userUUID + "." + LIMIT_REACHED_TIME, getUserDataMap().get(userUUID).getOrDefault(LIMIT_REACHED_TIME, 0));
            userDataFile.set("Users." + userUUID + "." + LIMIT, getUserDataMap().get(userUUID).getOrDefault(LIMIT, 0));
        }
        plugin.getConfigHandler().getUserDataFile().saveConfig();
        userDataMap.clear();
    }

    /**
     *
     * Saves a specific players entry in the user data map into the userData.yml
     *
     * @param playerUUID (The player that has their user data saved)
     */
    public void saveUserDataToFile(@NotNull final UUID playerUUID) {
        // If the user is not in the map for whatever reason, return
        if(getUserDataMap().get(playerUUID) == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            userDataFile.set("Users." + playerUUID + "." + NIGHT_VISION, getUserDataMap().get(playerUUID).getOrDefault(NIGHT_VISION, false));
            userDataFile.set("Users." + playerUUID + "." + LIMIT_REACHED, getUserDataMap().get(playerUUID).getOrDefault(LIMIT_REACHED, false));
            userDataFile.set("Users." + playerUUID + "." + RESET_TIMER_ACTIVE, getUserDataMap().get(playerUUID).getOrDefault(RESET_TIMER_ACTIVE, false));
            userDataFile.set("Users." + playerUUID + "." + LIMIT_REACHED_TIME, getUserDataMap().get(playerUUID).getOrDefault(LIMIT_REACHED_TIME, 0));
            userDataFile.set("Users." + playerUUID + "." + LIMIT, getUserDataMap().get(playerUUID).getOrDefault(LIMIT, 0));
            plugin.getConfigHandler().getUserDataFile().saveConfig();
            userDataMap.remove(playerUUID);
        });
    }

    /**
     *
     * Sets the player's effect status to either true or false in the map
     *
     * @param uuid (The player whose entry in the map is to be modified)
     * @param status (The status effect to modify the value for)
     */
    public void setEffectStatus(@NotNull UUID uuid, Object status, String effect) {
        if(!getUserDataMap().containsKey(uuid)) {
            addUserToMap(uuid);
        }

        getUserDataMap().get(uuid).put(effect, status);
    }

    /**
     *
     * Gets a specific player's effect status
     *
     * @param uuid (The player whose data needs to be retrieved from the user data map)
     * @return (The value of the effect status)
     */
    public Object getEffectStatus(@NotNull UUID uuid, String effect) {
        if(!getUserDataMap().containsKey(uuid)) {
            addUserToMap(uuid);
        }

        return getUserDataMap().get(uuid).get(effect);
    }

    /**
     *
     * Getter for the userDataMap
     *
     * @return userDataMap
     */
    public Map<UUID, Map<String, Object>> getUserDataMap() {
        return userDataMap;
    }
}
