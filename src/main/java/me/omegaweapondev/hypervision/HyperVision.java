package me.omegaweapondev.hypervision;

import me.omegaweapondev.hypervision.commands.CoreCommand;
import me.omegaweapondev.hypervision.commands.LimitCommand;
import me.omegaweapondev.hypervision.commands.ListCommand;
import me.omegaweapondev.hypervision.commands.NightVisionCommand;
import me.omegaweapondev.hypervision.configs.ConfigHandler;
import me.omegaweapondev.hypervision.configs.MessageHandler;
import me.omegaweapondev.hypervision.configs.UserDataHandler;
import me.omegaweapondev.hypervision.events.PlayerListener;
import me.omegaweapondev.hypervision.utilities.Placeholders;
import me.omegaweapondev.omegalibs.OmegaLibs;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Utility;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * The main plugin class for HyperVision that
 * handles all the enabling/disabling and a few other
 * tasks for the plugin
 *
 * @author OmegaWeaponDev
 *
 */
public class HyperVision extends JavaPlugin {
    private ConfigHandler configHandler;
    private MessageHandler messageHandler;
    private UserDataHandler userDataHandler;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        OmegaLibs.setInstance(this);

        configHandler = new ConfigHandler(this);
        configHandler.createFiles();
        configHandler.fileUpdater();

        messageHandler = new MessageHandler(this, configHandler.getMessagesFile().getConfig());
        userDataHandler = new UserDataHandler(this);


        // Print a message to console once the plugin has enabled
        OmegaLibs.logInfo(false,
            "_   _ _   _",
            "| | | | | | |",
            "| |_| | | | |  HyperVision v" + this.getDescription().getVersion() + " by OmegaWeaponDev",
            "|  _  | | | |  Running on version: " + Bukkit.getVersion(),
            "| | | \\ \\_/ /",
            "\\_| |_/\\___/",
            ""
        );

        // Check if PlaceholderAPI is installed. If so, register the plugins placeholders
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            OmegaLibs.logWarning(true,
                    "HyperVision requires PlaceholderAPI to be installed if you are wanting to use any of the placeholders",
                    "You can install PlaceholderAPI here: https://www.spigotmc.org/resources/placeholderapi.6245/ "
            );
        } else {
            new Placeholders(this).register();
        }

        registerCommands();
        registerEvents();
        setupEconomy();

        userDataHandler.populateUserDataMap();
    }

    @Override
    public void onDisable() {
       getUserDataHandler().saveUserDataToFile();
       Bukkit.getScheduler().cancelTasks(this);
    }

    public void onReload() {
        getConfigHandler().reloadFiles();
    }

    /**
     *
     * Registers all the commands for the plugin
     *
     */
    public void registerCommands() {

        try {
            OmegaLibs.logInfo(true, "HyperVision is now attempting to register it's commands...");

            OmegaLibs.setCommand().put("hypervision", new CoreCommand(this));
            OmegaLibs.setCommand().put("nightvisionlist", new ListCommand(this));
            OmegaLibs.setCommand().put("nightvisionlimit", new LimitCommand(this));
            OmegaLibs.setCommand().put("nightvision", new NightVisionCommand(this));

            OmegaLibs.registerCommands();
            OmegaLibs.logInfo(true, "HyperVision has successfully registered all of the commands.");
        } catch (Exception exception) {
            OmegaLibs.logWarning(true, "HyperVision has failed to register all of it's commands");
            exception.printStackTrace();
        }

    }

    /**
     *
     * Registers all the events for the plugin
     *
     */
    public void registerEvents() {
        OmegaLibs.registerEvents(new PlayerListener(this));
    }

    /**
     *
     * Sets up the Economy provider from vault
     * for use within the plugin
     *
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    /**
     *
     * Simple getter methods that are used throughout the plugin.
     *
     */
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public UserDataHandler getUserDataHandler() {
        return userDataHandler;
    }

    public Economy getEcon() {
        return econ;
    }
}
