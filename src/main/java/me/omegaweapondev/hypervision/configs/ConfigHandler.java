package me.omegaweapondev.hypervision.configs;

import com.tchristofferson.configupdater.ConfigUpdater;
import me.omegaweapondev.hypervision.HyperVision;
import me.omegaweapondev.omegalibs.OmegaLibs;
import me.omegaweapondev.omegalibs.configs.FileManager;

import java.io.IOException;
import java.util.List;

/**
 *
 * The Storage Manager class that handles all the plugins files
 *
 * @author OmegaWeaponDev
 */
public class ConfigHandler {
    private final HyperVision plugin;
    private final FileManager configFile;
    private final FileManager messagesFile;
    private final FileManager useDataFile;

    /**
     *
     * The public constructor for the Storage Manager class
     *
     */
    public ConfigHandler(final HyperVision plugin) {
        this.plugin = plugin;

        configFile = new FileManager("config.yml");
        messagesFile = new FileManager("messages.yml");
        useDataFile = new FileManager("userData.yml");
    }

    /**
     *
     * Handles creating all the files and the data folder for HyperVision
     *
     */
    public void createFiles() {
        // Creates the config.yml, messages.yml & userData.yml if they don't already exist.
        getConfigFile().createConfig();
        getMessagesFile().createConfig();
        getUseDataFile().createConfig();
    }

    /**
     *
     * Handles making sure all the files are up-to-date against the default in the resources folder
     *
     */
    public void fileUpdater() {
        OmegaLibs.logInfo(true, "Attempting to update config files....");

        try {
            // Checks the file version for the config.yml
            if(getConfigFile().getConfig().getDouble("Config_Version") != 1.0) {
                // Updates the file version for the config.yml then saves the file
                getConfigFile().getConfig().set("Config_Version", 1.0);
                getConfigFile().saveConfig();
                // Updates the keys and values in the config.yml if they have been changed.
                ConfigUpdater.update(plugin, "config.yml", getConfigFile().getFile(), List.of("null"));
                OmegaLibs.logInfo(true, "The config.yml has been successfully updated.");
            }

            // Checks the file version for the config.yml
            if(getMessagesFile().getConfig().getDouble("Config_Version") != 1.0) {
                // Updates the file version for the config.yml then saves the file
                getMessagesFile().getConfig().set("Config_Version", 1.0);
                getMessagesFile().saveConfig();
                // Updates the keys and values in the config.yml if they have been changed.
                ConfigUpdater.update(plugin, "messages.yml", getConfigFile().getFile(), List.of("null"));
                OmegaLibs.logInfo(true, "The messages.yml has been successfully updated.");
            }
            plugin.onReload();
        } catch (IOException ioException) {
            OmegaLibs.logWarning(true, "There was an issue trying to update the one or more of the files.");
            ioException.printStackTrace();
        }
    }

    /**
     *
     * Handles reloading all the files
     *
     */
    public void reloadFiles() {
        // Reloads the config.yml and messages.yml
        getConfigFile().reloadConfig();
        getMessagesFile().reloadConfig();
    }

    /**
     *
     * A getter for the configuration file
     *
     * @return configFile
     */
    public FileManager getConfigFile() {
        return configFile;
    }

    /**
     *
     * A getter for the messages file
     *
     * @return messagesFile
     */
    public FileManager getMessagesFile() {
        return messagesFile;
    }

    /**
     *
     * A getter for the user data file
     *
     * @return userDataFile
     */
    public FileManager getUseDataFile() {
        return useDataFile;
    }
}
