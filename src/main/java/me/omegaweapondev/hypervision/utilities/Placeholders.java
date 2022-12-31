package me.omegaweapondev.hypervision.utilities;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.omegaweapondev.hypervision.HyperVision;
import me.omegaweapondev.hypervision.configs.UserDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholders extends PlaceholderExpansion {
    private final HyperVision plugin;
    private final UserDataHandler userDataHandler;

    public Placeholders(final HyperVision plugin) {
        this.plugin = plugin;
        this.userDataHandler = plugin.getUserDataHandler();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        // %hypervision_hasnightvision%
        if(params.equals("hasnightvision")) {
            return String.valueOf(userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION));
        }

        // %hypervision_nightvision_expiry%
        if(params.equals("nightvision_expiry")) {
            if(!(boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION)) {
                return "";
            }
            return String.valueOf(player.getPotionEffect(PotionEffectType.NIGHT_VISION).getDuration());
        }

        // %hypervision_limit_count%
        if(params.equalsIgnoreCase("limit_count")) {
            return String.valueOf(userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.LIMIT));
        }

        // %hypervision_limit_reached%
        if(params.equalsIgnoreCase("limit_reached")) {
            return String.valueOf(userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.LIMIT_REACHED));
        }
        return null;
    }
}
