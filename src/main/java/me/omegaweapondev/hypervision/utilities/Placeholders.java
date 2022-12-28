package me.omegaweapondev.hypervision.utilities;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.omegaweapondev.hypervision.HyperVision;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholders extends PlaceholderExpansion {
    private final HyperVision plugin;

    public Placeholders(final HyperVision plugin) {
        this.plugin = plugin;
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
        return "hypervision";
    }

    @Override
    public @NotNull String getAuthor() {
        return "OmegaWeaponDev";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        return super.onPlaceholderRequest(player, params);
    }
}
