package me.omegaweapondev.hypervision;

import me.omegaweapondev.hypervision.configs.ConfigHandler;
import me.omegaweapondev.hypervision.utilities.Placeholders;
import me.omegaweapondev.omegalibs.OmegaLibs;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
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
    private static Economy econ = null;

    @Override
    public void onEnable() {
        OmegaLibs.setInstance(this);

        configHandler = new ConfigHandler(this);
        configHandler.createFiles();
        configHandler.fileUpdater();

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
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void onReload() {
        getConfigHandler().reloadFiles();
    }

    public void registerCommands() {}

    public void registerEvents() {}

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

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public static Economy getEcon() {
        return econ;
    }
}
