package com.horizen.economy.gui;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
public final class MenuHolder implements InventoryHolder {
    private final String menu;
    private Inventory inventory;
    public MenuHolder(String menu){this.menu=menu;}
    public String menu(){return menu;}
    public void setInventory(Inventory inventory){this.inventory=inventory;}
    @Override public Inventory getInventory(){return inventory;}
}
