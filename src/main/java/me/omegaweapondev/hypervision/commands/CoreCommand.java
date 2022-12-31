package me.omegaweapondev.hypervision.commands;

import me.omegaweapondev.hypervision.HyperVision;
import me.omegaweapondev.hypervision.configs.MessageHandler;
import me.omegaweapondev.omegalibs.OmegaLibs;
import me.omegaweapondev.omegalibs.builders.TabCompleteBuilder;
import me.omegaweapondev.omegalibs.commands.GlobalCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 *
 * The main command for the plugin `/hypervision`
 *
 * @author OmegaWeaponDev
 */
public class CoreCommand extends GlobalCommand implements TabCompleter {
    private final HyperVision plugin;
    private final MessageHandler messageHandler;

    /**
     *
     * The public constructor for the main plugin command
     *
     * @param plugin (The plugin's instance)
     */
    public CoreCommand(final HyperVision plugin) {
        this.plugin = plugin;
        messageHandler = plugin.getMessageHandler();
    }

    /**
     *
     * Handles the execution of the main plugin command
     *
     * @param commandSender (The CommandSender who is trying to execute the command)
     * @param strings (The arguments that were passed into the command)
     */
    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        if(strings.length != 1) {
            helpCommand(commandSender);
            return;
        }

        switch (strings[0]) {
            case "version" -> versionCommand(commandSender);
            case "reload" -> reloadCommand(commandSender);
            case "debug" -> debugCommand(commandSender);
            default -> helpCommand(commandSender);
        }
    }

    /**
     *
     * Simple method to send the user the version info about HyperVision
     *
     * @param sender (The CommandSender who is trying to execute the command)
     */
    private void versionCommand(final CommandSender sender) {
        if(sender instanceof Player player) {
            if (OmegaLibs.checkPermissions(player, true, "hypervision.version", "hypervision.admin")) {
                OmegaLibs.message(player, messageHandler.getPrefix() + "#86DE0FHyperVision #CA002Ev" + plugin.getDescription().getVersion() + " #86DE0FBy OmegaWeaponDev");
                return;
            }
            OmegaLibs.message(player, messageHandler.string("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            OmegaLibs.logInfo(true, "#86DE0FHyperVision #CA002Ev" + plugin.getDescription().getVersion() + " #86DE0FBy OmegaWeaponDev");
        }
    }

    /**
     *
     * Simple method to run the onReload() method from a command
     *
     * @param sender (The CommandSender who is trying to execute the command)
     */
    private void reloadCommand(final CommandSender sender) {
        if(sender instanceof Player player) {
           if (OmegaLibs.checkPermissions(player, true, "hypervision.reload","hypervision.admin")) {
                OmegaLibs.message(player, messageHandler.string("Plugin_Reload", "#f63e3eHyperVision has successfully been reloaded."));
                plugin.onReload();
                return;
            }
           OmegaLibs.message(player, messageHandler.string("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
           return;
        }
        if(sender instanceof ConsoleCommandSender) {
            plugin.onReload();
            OmegaLibs.logInfo(true, messageHandler.console("Plugin_Reload", "#f63e3eSorry, but you don't have permission to do that."));
        }
    }

    /**
     *
     * Prints some useful debugging information
     *
     * @param sender (The CommandSender who is trying to execute the command)
     */
    private void debugCommand(final CommandSender sender) {
        StringBuilder plugins = new StringBuilder();

        if(sender instanceof Player player) {
            for(Plugin installedPlugins : Bukkit.getPluginManager().getPlugins()) {
                plugins.append("#FF4A4A").append(installedPlugins.getName()).append(" ").append(installedPlugins.getDescription().getVersion()).append("#14ABC9, ");
            }

            OmegaLibs.message(player,
                    "#14abc9===========================================",
                    " #6928f7HyperVision #ff4a4av" + plugin.getDescription().getVersion() + " #14abc9By OmegaWeaponDev",
                    "#14abc9===========================================",
                    " #14abc9Server Brand: #ff4a4a" + Bukkit.getName(),
                    " #14abc9Server Version: #ff4a4a" + Bukkit.getServer().getVersion(),
                    " #14abc9Online Mode: #ff4a4a" + Bukkit.getOnlineMode(),
                    " #14abc9Players Online: #ff4a4a" + Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers(),
                    " #14abc9HyperVision Commands: #ff4a4a" + OmegaLibs.setCommand().size() + " / 4 #14abc9registered",
                    " #14abc9Currently Installed Plugins...",
                    " " + plugins,
                    "#14abc9==========================================="
            );
            return;
        }

        for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            plugins.append(plugin.getName()).append(" ").append(plugin.getDescription().getVersion()).append(", ");
        }

        OmegaLibs.logInfo(true,
                "===========================================",
                " HyperVision v" + plugin.getDescription().getVersion() + " By OmegaWeaponDev",
                "===========================================",
                " Server Brand: " + Bukkit.getName(),
                " Server Version: " + Bukkit.getServer().getVersion(),
                " Online Mode: " + Bukkit.getOnlineMode(),
                " Players Online: " + Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers(),
                " HyperVision Commands: " + OmegaLibs.setCommand().size() + " / 4 registered",
                " Currently Installed Plugins...",
                " " + plugins,
                "==========================================="
        );
    }

    /**
     *
     * Prints a list of the plugins command usages.
     *
     * @param sender (The CommandSender who is trying to execute the command)
     */
    private void helpCommand(final CommandSender sender) {
        versionCommand(sender);
        if(sender instanceof Player player) {

            OmegaLibs.message(player,
                    messageHandler.getPrefix() + "#86DE0FReload Command: #CA002E/hypervision reload",
                    messageHandler.getPrefix() + "#86DE0FVersion Command: #CA002E/hypervision version",
                    messageHandler.getPrefix() + "#86DE0FHelp Command: #CA002E/hypervision help",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Toggle Command: #CA002E/nightvision",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Toggle Others Command: #CA002E/nightvision <player>",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Global Command: #CA002E/nightvision global add|remove",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Temp Command: #CA002E/nightvision <player> <time>",
                    messageHandler.getPrefix() + "#86DE0FNight Vision List Command: #CA002E/nightvisionlist",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Limit Check Command: #CA002E/nightvisionlimit check",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Limit Check Others Command: #CA002E/nightvisionlimit check <player>",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Limit Reset Command: #CA002E/nightvisionlimit reset <player>"
            );
            return;
        }

        if(sender instanceof ConsoleCommandSender) {

            OmegaLibs.logInfo(true,
                    "Reload Command: /hypervision reload",
                    "Version Command: /hypervision version",
                    "Help Command: /hypervision help",
                    "Night Vision Toggle Others Command: /nightvision <player>",
                    "Night Vision Global Command: /nightvision global add|remove",
                    "Night Vision Temp Command: /nightvision <player> <time>",
                    "Night Vision List Command: /nightvisionlist",
                    "Night Vision Limit Check Others Command: /nightvisionlimit check <player>",
                    "Night Vision Limit Reset Command: /nightvisionlimit reset <player>"
            );
        }
    }

    /**
     *
     * Sets up the command tab completion based on player's permissions
     *
     * @param sender (Who sent the command)
     * @param command (The argument to add into the tab completion list)
     * @param args (The command arguments)
     * @return (The completed tab completion list)
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length <= 1) {
            return new TabCompleteBuilder(sender)
                .checkCommand("version", true, "hypervision.version, hypervision.admin")
                .checkCommand("reload", true, "hypervision.reload", "hypervision.admin")
                .checkCommand("debug", true, "hypervision.admin")
                .build(args[0]);
        }
        return Collections.emptyList();
    }
}
