package com.espuw.teslimat.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.UUID;

public class ItemResolver {

    private ItemResolver() {}

    public static ItemStack resolve(String input) {
        if (input == null || input.isBlank()) return new ItemStack(Material.BARRIER);

        String lower = input.toLowerCase();

        if (lower.startsWith("head-")) {
            String hash = input.substring(5);
            return buildSkull("http://textures.minecraft.net/texture/" + hash);
        }

        if (lower.startsWith("ia:")) {
            return resolveItemsAdder(input.substring(3));
        }

        try {
            return new ItemStack(Material.valueOf(input.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.BARRIER);
        }
    }

    public static boolean matches(ItemStack item, String configInput) {
        if (item == null || configInput == null) return false;
        String lower = configInput.toLowerCase();

        if (lower.startsWith("ia:")) {
            return matchesItemsAdder(item, configInput.substring(3));
        }

        if (lower.startsWith("head-")) {
            return item.getType() == Material.PLAYER_HEAD;
        }

        try {
            Material mat = Material.valueOf(configInput.toUpperCase());
            return item.getType() == mat;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static ItemStack buildSkull(String textureUrl) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) return skull;
        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "custom");
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(URI.create(textureUrl).toURL());
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
        } catch (MalformedURLException ignored) {}
        skull.setItemMeta(meta);
        return skull;
    }

    private static ItemStack resolveItemsAdder(String id) {
        try {
            Class<?> cls = Class.forName("dev.lone.itemsadder.api.CustomStack");
            Object cs = cls.getMethod("getInstance", String.class).invoke(null, id);
            if (cs != null) {
                return (ItemStack) cls.getMethod("getItemStack").invoke(cs);
            }
        } catch (Exception ignored) {}
        return new ItemStack(Material.BARRIER);
    }

    private static boolean matchesItemsAdder(ItemStack item, String id) {
        try {
            Class<?> cls = Class.forName("dev.lone.itemsadder.api.CustomStack");
            Object cs = cls.getMethod("byItemStack", ItemStack.class).invoke(null, item);
            if (cs == null) return false;
            String csId = (String) cls.getMethod("getId").invoke(cs);
            return id.equalsIgnoreCase(csId);
        } catch (Exception ignored) {}
        return false;
    }

    public static final String GEZGIN_KOYLU =
        "http://textures.minecraft.net/texture/4ce6411c8e56eddcf2fd09cfef5e8ab3a2ad3285d3c1a2c8da7b6d59fa02dad1";

    public static final String X_ISARETI =
        "http://textures.minecraft.net/texture/bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9";

    public static final String ELMAS_KASKLI_STEVE =
        "http://textures.minecraft.net/texture/a9e69f97a6b5a80898e6ad86b3e16f2b62dc0e84e96f28ac7f39b0af1ef0a8b6";
}
