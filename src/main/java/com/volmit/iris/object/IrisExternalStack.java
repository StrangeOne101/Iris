package com.volmit.iris.object;

import com.volmit.iris.Iris;
import com.volmit.iris.util.Desc;
import com.volmit.iris.util.DontObfuscate;
import com.volmit.iris.util.IPluginLoot;
import com.volmit.iris.util.KMap;
import com.volmit.iris.util.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Desc("Generates an ItemStack through a plugin")
@Data
@EqualsAndHashCode(callSuper = false)
public class IrisExternalStack extends IrisRegistrant {

    @DontObfuscate
    @Desc("The plugin ID")
    @Required
    private String plugin;

    @DontObfuscate
    @Desc("The handler that turns the item into an ItemStack")
    @Required
    private String handler;

    @DontObfuscate
    @Desc("The item name")
    @Required
    private String item;

    @DontObfuscate
    @Desc("Extra data that should be passed to the handler")
    private KMap<String, Object> extra = new KMap<>();

    public ItemStack getItemStack() {
        IPluginLoot ipl = Iris.pluginLootRegistry.getLoot(plugin, handler);

        if (ipl != null) {
            try {
                ItemStack stack = ipl.generateItem(item, extra);
                return stack;
            } catch (Exception e) {
                Iris.warn("Failed to generate External Plugin ItemStack " + plugin + ":" + handler + ":" + item);
                e.printStackTrace();
            }
        }

        return null;
    }
}
