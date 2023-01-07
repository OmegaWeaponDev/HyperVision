package me.omegaweapondev.hypervision.configs;

import me.omegaweapondev.hypervision.HyperVision;
import me.omegaweapondev.omegalibs.OmegaLibs;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * Handles the messages for the plugin
 *
 * @author OmegaWeaponDev
 */
public class MessageHandler {
    private final HyperVision plugin;
    private final FileConfiguration messagesConfig;
    private final String configName;

    /**
     *
     * The public constructor for the MessagesHandler class
     *
     * @param plugin (The plugin's instance)
     * @param messagesConfig (The yaml file containing all the messages)
     */
    public MessageHandler(final HyperVision plugin, FileConfiguration messagesConfig) {
        this.plugin = plugin;
        this.messagesConfig = messagesConfig;
        this.configName = plugin.getConfigHandler().getMessagesFile().getFileName();
    }

    /**
     *
     * Handles String messages for players in-game
     *
     * @param message (The message from the file)
     * @param fallbackMessage (The fallback message)
     * @return (The message that is returned. Either message or fallBackMessage)
     */
    public String string(final String message, final String fallbackMessage) {
        if(messagesConfig.getString(message) == null) {
            getErrorMessage(message);
            return getPrefix() + fallbackMessage;
        }

        if(messagesConfig.getString(message).length() == 0) {
            return null;
        }
        return getPrefix() + messagesConfig.getString(message);
    }

    /**
     *
     * Handles String messages for the console
     *
     * @param message (The message from the file)
     * @param fallbackMessage (The fallback message)
     * @return (The message that is returned. Either message or fallBackMessage)
     */
    public String console(final String message, final String fallbackMessage) {
        if(messagesConfig.getString(message) == null) {
            getErrorMessage(message);
            return fallbackMessage;
        }

        if(messagesConfig.getString(message).length() == 0) {
            return null;
        }
        return messagesConfig.getString(message);
    }

    /**
     *
     * Handles the plugin's prefix
     *
     * @return (The plugin's prefix for all the messages)
     */
    public String getPrefix() {
        if(messagesConfig.getString("Plugin_Prefix") == null) {
            getErrorMessage("Plugin_Prefix");
            return "&#8c8c8c[&#2b9bbf&lHV&#8c8c8c]" + " ";
        }
        if(messagesConfig.getString("Plugin_Prefix", "none").equalsIgnoreCase("none")) {
            return "";
        }
        return messagesConfig.getString("Plugin_Prefix") + " ";
    }

    /**
     *
     * Provides a console error message if one of the messages returns the fallback message
     *
     * @param message (The message that returned the fallback message)
     */
    private void getErrorMessage(final String message) {
        OmegaLibs.logInfo(true,
            "There was an error getting the " + message + " message from the " + configName + ".",
            "I have set a fallback message to take it's place until the issue is fixed.",
            "To resolve this, please locate " + message + " in the " + configName + " and fix the issue."
        );
    }
}
