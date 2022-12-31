package me.omegaweapondev.hypervision.commands;

import me.omegaweapondev.hypervision.HyperVision;
import me.omegaweapondev.hypervision.configs.MessageHandler;
import me.omegaweapondev.omegalibs.OmegaLibs;
import me.omegaweapondev.omegalibs.commands.GlobalCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListCommand extends GlobalCommand implements TabCompleter {
    private final HyperVision plugin;
    private final MessageHandler messagesHandler;

    /**
     *
     * The public constructor for the list command
     *
     * @param plugin (The plugin's instance)
     */
    public ListCommand(final HyperVision plugin) {
        this.plugin = plugin;
        messagesHandler = plugin.getMessageHandler();
    }

    /**
     *
     * Handles the execution of the list command
     *
     * @param commandSender (The CommandSender that is trying to execute the command)
     * @param strings (The arguments that were passed into the command)
     */
    @Override
    protected void execute(final CommandSender commandSender, final String[] strings) {
        // Checks if the CommandSender is a player
        if(commandSender instanceof Player player) {

            // Check if the player has permission to use the list command
            if(!OmegaLibs.checkPermissions(player, true, "hypervision.nightvision.list", "hypervision.nightvision.admin", "hypervision.admin")) {
                OmegaLibs.message(player, messagesHandler.string("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
                return;
            }

            // Send a list of the players who currently have night vision enabled.
            OmegaLibs.message(player, messagesHandler.getPrefix() + "#00D4FFThe following players have night vision enabled:", getPlayerList());
            return;
        }

        OmegaLibs.logInfo(true, "The following players have night vision enabled:", getPlayerList());
    }

    /**
     *
     * Finds all the players that currently have night vision enabled and adds their name to a string.
     *
     * @return (A list of all the players who currently have night vision enabled.)
     */
    private String getPlayerList() {
        if(Bukkit.getOnlinePlayers().size() == 0)
        {
            return "There are currently no players online!";
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                break;
            }
            return "No players currently have night vision enabled!";
        }

        // Add all the players who have nightvision to a list
        List<String> nightVisionList = new ArrayList<>();
        for(Player playerName : Bukkit.getOnlinePlayers()) {
            if(playerName.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                nightVisionList.add(playerName.getName());
            }
        }

        // Loop through the list created above and add the names into a string
        StringBuilder playerList = new StringBuilder();
        for(String playerName : nightVisionList) {
            playerList.append(playerName).append(", ");
        }

        return playerList.toString();
    }

    /**
     *
     * The tab completion for the list command
     *
     * @return (Returns an empty list as there is no need for any tab completion)
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }
}
