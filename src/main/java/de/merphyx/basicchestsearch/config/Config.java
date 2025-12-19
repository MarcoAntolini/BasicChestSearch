package de.merphyx.basicchestsearch.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.gui.screen.ingame.*;

public class Config {
    private static ModConfig configHolder;

    public static void init() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        configHolder = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static Mode getSearchMode(HandledScreen<?> screen, int menuSize) {
        if (screen instanceof CreativeInventoryScreen ||
                screen instanceof InventoryScreen ||
                screen instanceof CraftingScreen) {
            return Mode.DISABLED;
        }

        String className = screen.getClass().getName();

        if (className.equals("net.minecraft.client.gui.screen.ingame.GenericContainerScreen") ||
                (className.startsWith("net.minecraft.") && menuSize >= 27)) {
            return configHolder.general.searchMode;
        }

        return Mode.DISABLED;
    }

    public static boolean keepSearch() { return configHolder.general.keepSearch; }
    public static boolean autoFocus() { return configHolder.general.autoFocus; }
    public static boolean detachSearchBar() { return configHolder.general.detachSearchBar; }
}