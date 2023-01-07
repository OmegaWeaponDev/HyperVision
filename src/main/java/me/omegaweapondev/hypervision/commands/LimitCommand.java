package me.omegaweapondev.hypervision.commands;

import me.omegaweapondev.hypervision.HyperVision;
import me.omegaweapondev.hypervision.configs.MessageHandler;
import me.omegaweapondev.hypervision.configs.UserDataHandler;
import me.omegaweapondev.omegalibs.OmegaLibs;
import me.omegaweapondev.omegalibs.builders.TabCompleteBuilder;
import me.omegaweapondev.omegalibs.commands.GlobalCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LimitCommand extends GlobalCommand implements TabCompleter {
    private final MessageHandler messageHandler;
    private final FileConfiguration configFile;
    private final UserDataHandler userDataHandler;


    /**
     *
     * Public constructor for the class
     *
     * @param plugin (The plugin's instance)
     */
    public LimitCommand(final @NotNull HyperVision plugin) {
        messageHandler = plugin.getMessageHandler();
        configFile = plugin.getConfigHandler().getConfigFile().getConfig();
        userDataHandler = plugin.getUserDataHandler();

    }

    /**
     *
     * Handles the execution of the Limit command
     *
     * @param sender (The CommandSender)
     * @param strings (The args passed into the command)
     */
    @Override
    protected void execute(CommandSender sender, String[] strings) {
        if(strings.length == 0) {
            // Checks if the commandSender is a Player
            if(sender instanceof Player player) {
                OmegaLibs.message(player,
                    messageHandler.getPrefix() + "&#86DE0FNight Vision Limit Check Command: &#CA002E/nightvisionlimit check",
                    messageHandler.getPrefix() + "&#86DE0FNight Vision Limit Check Others Command: &#CA002E/nightvisionlimit check <player>",
                    messageHandler.getPrefix() + "&#86DE0FNight Vision Limit Reset Command: &#CA002E/nightvisionlimit reset <player>"
                );
                return;
            }
            OmegaLibs.logInfo(true,
                "Night Vision Limit Check Command: /nightvisionlimit check",
                "Night Vision Limit Check Others Command: /nightvisionlimit check <player>",
                "Night Vision Limit Reset Command: /nightvisionlimit reset <player>"
            );
            return;
        }

        if (strings[0].equalsIgnoreCase("check")) {
            checkCommand(sender, strings);
            return;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("reset")) {
            resetCommand(sender, strings);
        }
    }

    private void checkCommand(final @NotNull CommandSender commandSender, final @NotNull String[] strings ) {
        if( commandSender instanceof Player player) {
            if(strings.length == 1) {
                // Check to see if the player has permission to use the limit check command
                if(!OmegaLibs.checkPermissions(player, true, "hypervision.limit.check.self", "hypervision.limit.admin", "hypervision.admin")) {
                    OmegaLibs.message(player, messageHandler.string("No_Permission", "&#f63e3eSorry, but you don't have permission to do that."));
                    return;
                }

                // Send the player a message with their current limit status
                OmegaLibs.message(player, messageHandler.string("Night_Vision_Limit.Limit_Check", "&#1fe3e0Your limit amount currently stands at: &#f63e3e%currentLimitAmount% / %maxLimitAmount%")
                        .replace("%currentLimitAmount%", String.valueOf(userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.LIMIT)))
                        .replace("%maxLimitAmount%", String.valueOf(configFile.getInt("Night_Vision_Limit.Limit")))
                );
                return;
            }

            if (strings.length == 2) {
                // Checks if the player has permission to use the check others limit command
                if(!(OmegaLibs.checkPermissions(player, true, "hypervision.limit.check.others", "hypervision.limit.admin", "hypervision.admin"))) {
                    OmegaLibs.message(player, messageHandler.string("No_Permission", "&#f63e3eSorry, but you don't have permission to do that."));
                    return;
                }

                // Gets the target from the second arg in the command
                Player target = Bukkit.getPlayer(strings[1]);

                // Check if the player is not null
                if(target != null) {
                    // Send the player a message to inform them of the targets limit status
                    OmegaLibs.message(player, messageHandler.string("Night_Vision_Limit.Limit_Check_Others", "&#f63e3e%player%'s &#1fe3e0limit amount currently stands at: &#f63e3e%currentLimitAmount% / %maxLimitAmount%")
                            .replace("%player%", LegacyComponentSerializer.legacyAmpersand().serialize(target.displayName()))
                            .replace("%currentLimitAmount%", String.valueOf(userDataHandler.getEffectStatus(target.getUniqueId(), UserDataHandler.LIMIT)))
                            .replace("%maxLimitAmount%", String.valueOf(configFile.getInt("Night_Vision_Limit.Limit")))
                    );
                    return;
                }
                // If null, Send a message to the player informing them that the target is an invalid player
                OmegaLibs.message(player, messageHandler.string("Invalid_Player", "&#f63e3eSorry, that player cannot be found."));
                return;
            }
        }

        // Same as above, just handles the command if the CommandSender is the server's console
        if(strings.length == 2) {
            Player target = Bukkit.getPlayer(strings[1]);
            if(target != null) {
                OmegaLibs.logInfo(true, messageHandler.console("Night_Vision_Limit.Limit_Check_Others", "&#f63e3e%player%'s &#1fe3e0limit amount currently stands at: &#f63e3e%currentLimitAmount% / %maxLimitAmount%")
                        .replace("%player%", target.displayName().toString())
                        .replace("%currentLimitAmount%", String.valueOf(userDataHandler.getEffectStatus(target.getUniqueId(), UserDataHandler.LIMIT)))
                        .replace("%maxLimitAmount%", String.valueOf(configFile.getInt("Night_Vision_Limit.Limit")))
                );
                return;
            }
            OmegaLibs.logInfo(true, messageHandler.console("Invalid_Player", "&#f63e3eSorry, that player cannot be found."));
        }
    }

    private void resetCommand(final @NotNull CommandSender sender, final @NotNull String[] strings ) {
        Player target = Bukkit.getPlayer(strings[1]);


        if(sender instanceof Player player) {
            // Checks if the player has permission to use the limit reset command
            if(!OmegaLibs.checkPermissions(player, true, "hypervision.limit.reset", "hypervision.limit.admin", "hypervision.admin")) {
                OmegaLibs.message(player, messageHandler.string("No_Permission", "&#f63e3eSorry, but you don't have permission to do that."));
                return;
            }

            if(target == null) {
                OmegaLibs.message(player, messageHandler.console("Invalid_Player", "&#f63e3eSorry, that player cannot be found."));
                return;
            }

            userDataHandler.setEffectStatus(target.getUniqueId(), 0, UserDataHandler.LIMIT);
            userDataHandler.setEffectStatus(target.getUniqueId(), false, UserDataHandler.LIMIT_REACHED);
            userDataHandler.setEffectStatus(target.getUniqueId(), false, UserDataHandler.RESET_TIMER_ACTIVE);
            userDataHandler.setEffectStatus(target.getUniqueId(), 0, UserDataHandler.LIMIT_REACHED_TIME);

            OmegaLibs.message(target, messageHandler.string("Night_Vision_Limit.Limit_Reset", "&#1fe3e0Your limit's have been reset! You can use the night vision command again!"));
            OmegaLibs.message(player, messageHandler.string("Night_Vision_Limit.Limit_Reset_Other", "&#1fe3e0You have reset &#f63e3e%player%'s &#1fe3e0Nightvision Limit!")
                    .replace("%player%", LegacyComponentSerializer.legacyAmpersand().serialize(target.displayName()))
            );

            if(configFile.getBoolean("Sound_Effects.Enabled") && configFile.getBoolean("Sound_Effects.Limit_Reset.Enabled")) {
                target.playSound(target.getLocation(), Sound.valueOf(configFile.getString("Sound_Effects.Limit_Reset.Sound")), 1, 1);
            }
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            if(target == null) {
                OmegaLibs.logWarning(true, "Sorry, that player cannot be found.");
                return;
            }

            // Resets the players limit status back to 0
            userDataHandler.setEffectStatus(target.getUniqueId(), 0, UserDataHandler.LIMIT);
            userDataHandler.setEffectStatus(target.getUniqueId(), false, UserDataHandler.LIMIT_REACHED);
            userDataHandler.setEffectStatus(target.getUniqueId(), false, UserDataHandler.RESET_TIMER_ACTIVE);
            userDataHandler.setEffectStatus(target.getUniqueId(), 0, UserDataHandler.LIMIT_REACHED_TIME);
            OmegaLibs.message(target, messageHandler.string("Night_Vision_Limit.Limit_Reset", "&#1fe3e0Your limit's have been reset! You can use the night vision command again!"));
            OmegaLibs.logInfo(true, "You have reset %player%'s Nightvision Limit!".replace("%player%", target.getName()));

            if(configFile.getBoolean("Sound_Effects.Enabled") && configFile.getBoolean("Sound_Effects.Limit_Reset.Enabled")) {
                target.playSound(target.getLocation(), Sound.valueOf(configFile.getString("Sound_Effects.Limit_Reset.Sound")), 1, 1);
            }
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
        if(strings.length <= 1) {
            return new TabCompleteBuilder(commandSender)
                    .checkCommand("check", true, "hypervision.limit.check", "hypervision.limit.admin", "hypervision.admin")
                    .checkCommand("reset", true, "hypervision.limit.reset", "hypervision.limit.admin", "hypervision.admin")
                    .build(strings[0]);
        }

        if(strings.length == 2) {
            List<String> onlinePlayers = new ArrayList<>();
            for(Player onlineplayer : Bukkit.getOnlinePlayers()) {
                onlinePlayers.add(onlineplayer.getName());
            }

            return new TabCompleteBuilder(commandSender).addCommand(onlinePlayers).build(strings[1]);
        }
        return Collections.emptyList();
    }
}
