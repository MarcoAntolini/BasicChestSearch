package cgcm.chestsearchbar.config;

import cgcm.chestsearchbar.api.ISearchable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class Config {

   public static boolean keepSearch = true;
   public static boolean autoFocus = false;
   public static boolean detachSearchBar = false;
   public static Mode vanillaContainerMode;
   public static Map<String, Integer> modePerScreenMap;
   public static final JsonConfig CLIENT_CONFIG;
   private static final JsonConfig.BooleanConfig KEEP_SEARCH;
   private static final JsonConfig.BooleanConfig AUTO_FOCUS;
   private static final JsonConfig.BooleanConfig DETACH_BAR;
   private static final JsonConfig.IntConfig VANILLA_MODE;
   private static final JsonConfig.HashMapConfig MODE_PER_SCREEN;

   public static Mode getMode(HandledScreen<?> screen, int menuSize) {
      String className = screen.getClass().getName();
      if (className.equals("net.minecraft.client.gui.screen.ingame.GenericContainerScreen") || className.startsWith("net.minecraft.") && menuSize >= 27) {
         return vanillaContainerMode;
      } else if (modePerScreenMap.containsKey(className)) {
         return Mode.values()[modePerScreenMap.get(className)];
      } else if (screen instanceof ISearchable searchable) {
          return searchable.getMode();
      } else {
         return Mode.DISABLED;
      }
   }

   public static void load(Path p) {
      CLIENT_CONFIG.load(p.resolve("chestsearchbar.json").toFile());
      keepSearch = KEEP_SEARCH.get();
      autoFocus = AUTO_FOCUS.get();
      detachSearchBar = DETACH_BAR.get();
      vanillaContainerMode = Mode.values()[VANILLA_MODE.get()];
      modePerScreenMap = MODE_PER_SCREEN.get();
   }

   static {
      vanillaContainerMode = Mode.SORT;
      modePerScreenMap = new HashMap<>() {
          {
              this.put("compasses.expandedstorage.impl.client.gui.SingleScreen", 1);
              this.put("compasses.expandedstorage.impl.client.gui.PageScreen", 1);
              this.put("compasses.expandedstorage.impl.client.gui.ScrollScreen", 1);
              this.put("fuzs.netherchested.client.gui.screens.inventory.NetherChestScreen", 2);
          }
      };
      CLIENT_CONFIG = new JsonConfig();
      KEEP_SEARCH = CLIENT_CONFIG.booleanBuilder("keepSearch", true);
      AUTO_FOCUS = CLIENT_CONFIG.booleanBuilder("autoFocus", false);
      DETACH_BAR = CLIENT_CONFIG.booleanBuilder("detachSearchBar", false);
      VANILLA_MODE = CLIENT_CONFIG.intBuilder("vanillaContainerMode", Mode.SORT.ordinal(), 0, 2).description("Mode for minecraft containers. 0 = Disabled.");
      MODE_PER_SCREEN = CLIENT_CONFIG.HashMapConfigBuilder("modePerScreen", modePerScreenMap, 0, 2).description("Mode for each screen. 0 = Disabled.");
   }

}