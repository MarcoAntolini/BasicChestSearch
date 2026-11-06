package de.merphyx.basicchestsearch.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("basicchestsearch.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static DefaultConfig handler = new DefaultConfig();

    public static void init() {
        load();
    }

    public static void load() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                handler = GSON.fromJson(Files.newBufferedReader(CONFIG_FILE), DefaultConfig.class);
            }
            if (handler == null) {
                handler = new DefaultConfig();
            }
            handler.ensureDefaults();
        } catch (Exception e) {
            handler = new DefaultConfig();
        }
    }

    public static void save() {
        try {
            Files.writeString(CONFIG_FILE, GSON.toJson(handler));
        } catch (Exception ignored) {}
    }

    public static Mode getSearchMode(AbstractContainerScreen<?> screen, int menuSize) {

        if (screen instanceof CreativeModeInventoryScreen ||
                screen instanceof InventoryScreen ||
                screen instanceof CraftingScreen) {
            return Mode.DISABLED;
        }

        String className = screen.getClass().getName();

        if (className.equals("net.minecraft.client.gui.screens.inventory.ContainerScreen") ||
                (className.startsWith("net.minecraft.") && menuSize >= 27)) {
            return handler.searchMode;
        }

        return Mode.DISABLED;
    }

    public static boolean keepSearch() { return handler.keepSearch; }
    public static boolean autoFocus() { return handler.autoFocus; }
    public static boolean detachSearchBar() { return handler.detachSearchBar; }
    public static BoundKey focusSearch() { return handler.focusSearch; }
    public static BoundKey clearSearch() { return handler.clearSearch; }

    public static DefaultConfig getRawConfig() { return handler; }

}
