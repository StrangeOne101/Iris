package com.volmit.iris.util;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface IPluginLoot {

    /**
     * Get the name of this handler that produces custom items
     * @return The handler name
     */
    String getHandlerName();

    /**
     * Generate an item when a loot table generates an item using this handler
     * @param item The name of the item to generate
     * @param extra Extra data provided to generate with
     * @return The ItemStack
     */
    ItemStack generateItem(String item, Map<String, Object> extra);
}
