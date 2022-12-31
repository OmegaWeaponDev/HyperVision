package me.omegaweapondev.hypervision.commands;

import me.omegaweapondev.hypervision.HyperVision;
import me.omegaweapondev.hypervision.configs.MessageHandler;
import me.omegaweapondev.hypervision.utilities.NightVisionToggle;
import me.omegaweapondev.omegalibs.OmegaLibs;
import me.omegaweapondev.omegalibs.builders.TabCompleteBuilder;
import me.omegaweapondev.omegalibs.commands.GlobalCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NightVisionCommand extends GlobalCommand implements TabCompleter {
    private final HyperVision plugin;
    private final FileConfiguration configFile;
    private final MessageHandler messagesHandler;

    /**
     *
     * The public constructor for the Night Vision command
     *
     * @param plugin (The plugin's instance)
     */
    public NightVisionCommand(final HyperVision plugin) {
        this.plugin = plugin;
        configFile = plugin.getConfigHandler().getConfigFile().getConfig();
        messagesHandler = plugin.getMessageHandler();
    }

    /**
     *
     * Handles the execution of the Night Vision Command
     *
     * @param commandSender (The Executor for the command that trying to execute the night vision command)
     * @param strings (The arguments passed into the command)
     */
    @Override
    protected void execute(final CommandSender commandSender, final String[] strings) {

        NightVisionToggle nightVisionToggle = new NightVisionToggle(plugin, commandSender);

        // If there are no args, simply call the night vision toggle method
        if(strings.length == 0) {
            nightVisionToggle.nightVisionToggle();
            return;
        }

        if(strings.length == 1) {
            // If there is 1 arg in the command, check if that player is valid
            Player target = Bukkit.getPlayer(strings[0]);

            if(target == null) {
                return;
            }

            // Call to the toggle others method
            nightVisionToggle.nightVisionToggleOthers(target);
        }

        if(strings.length == 2) {
            // Checks if the first arg in the command is `global`
            if(!strings[0].equalsIgnoreCase("global")) {
                return;
            }

            if(!strings[1].equalsIgnoreCase("add") && !strings[1].equalsIgnoreCase("remove")) {
                if(commandSender instanceof final Player player) {
                    OmegaLibs.message(player,
                            "#2b9bbfNight Vision Global Command: #f63e3e/nightvision global add #2b9bbf- Adds night vision to add online players",
                            "#2b9bbfNight Vision Global Command: #f63e3e/nightvision global remove #2b9bbf- Removes night vision from all online players"
                    );
                } else {
                    OmegaLibs.logWarning(true,
                            "Night Vision Global Command: /nightvision global add - Adds night vision to add online players",
                            "Night Vision Global Command: /nightvision global remove - Removes night vision from all online players"
                    );
                }

            }
            // Call the night vision global method and pass it the second arg in the command
            nightVisionToggle.nightVisionToggleGlobal(strings[1]);
            return;
        }

        if(strings.length == 3) {
            // Checks if the first arg in the command is `temp`
            if(!strings[0].equalsIgnoreCase("temp")) {
                return;
            }
            // Call to the night vision temp method and pass in the second and third args
            nightVisionToggle.nightVisionToggleTemp(Bukkit.getPlayer(strings[1]), Integer.parseInt(strings[2]));
        }
    }

    /**
     *
     * Sets up the command tab completion based on player's permissions
     *
     * @param commandSender (Who sent the command)
     * @param command (The argument to add into the tab completion list)
     * @param strings (The command arguments)
     * @return (The completed tab completion list)
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 0) {
            return Collections.emptyList();
        }

        List<String> onlinePlayers = new ArrayList<>();
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(onlinePlayer.getName());
        }

        if(strings.length == 1) {

            return new TabCompleteBuilder(commandSender)
                    .checkCommand("global", true, "hypervision.nightvision.global", "hypervision.nightvision.admin", "hypervision.admin")
                    .checkCommand("temp", true, "hypervision.nightvision.temp", "hypervision.nightvision.admin", "hypervision.admin")
                    .checkCommand(onlinePlayers, true, "hypervision.nightvision.toggle.others", "hypervision.nightvision.admin", "hypervision.admin")
                    .build(strings[0]);
        }

        if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("temp")) {
                return new TabCompleteBuilder(commandSender)
                        .checkCommand(onlinePlayers, true, "hypervision.nightvision.temp", "hypervision.nightvision.admin", "hypervision.admin")
                        .build(strings[1]);
            }

            if(strings[0].equalsIgnoreCase("global")) {
                return new TabCompleteBuilder(commandSender)
                        .checkCommand("add", true, "hypervision.nightvision.global", "hypervision.nightvision.admin", "hypervision.admin")
                        .checkCommand("remove", true, "hypervision.nightvision.global", "hypervision.nightvision.admin", "hypervision.admin")
                        .build(strings[1]);
            }
        }

        return Collections.emptyList();
    }
}
