package com.volmit.iris.manager.link;

import com.volmit.iris.util.IPluginLoot;
import com.volmit.iris.util.KMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

public class PluginLootRegistry {

    private Map<String, Map<String, IPluginLoot>> registry = new KMap<>();

    public void register(@NotNull Plugin plugin, @NotNull IPluginLoot handler) {
        String s = plugin.getName().toLowerCase();

        if (!registry.containsKey(s)) {
            registry.put(s, new KMap<>());
        }

        registry.get(s).put(handler.getHandlerName().toLowerCase(), handler);
    }

    @Nullable
    public IPluginLoot getLoot(@NotNull String plugin, @NotNull String handler) {
        if (registry.containsKey(plugin.toLowerCase())) {
            return registry.get(plugin.toLowerCase()).get(handler.toLowerCase());
        }

        return null;
    }
}
