package com.espuw.teslimat.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Teslimat menülerini güvenle tanımak için özel InventoryHolder.
 */
public class TeslimatHolder implements InventoryHolder {

    private final String menuId;
    private Inventory inventory;

    public TeslimatHolder(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
