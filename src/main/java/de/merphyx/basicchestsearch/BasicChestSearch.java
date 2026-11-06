package de.merphyx.basicchestsearch;

import de.merphyx.basicchestsearch.config.Config;
import de.merphyx.basicchestsearch.input.Keybindings;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class BasicChestSearch implements ClientModInitializer {

   public static final String MOD_ID = "basicchestsearch";
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final Identifier BAR_LOCATION = Identifier.fromNamespaceAndPath("basicchestsearch", "textures/gui/search_bar.png");

   @Override
   public void onInitializeClient() {
      Config.init();
      KeyMappingHelper.registerKeyMapping(Keybindings.TOGGLE_SEARCH_BAR_KEY);
   }

}
