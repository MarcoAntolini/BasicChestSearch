package de.merphyx.basicchestsearch;

import de.merphyx.basicchestsearch.config.Config;
import de.merphyx.basicchestsearch.input.Keybindings;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class BasicChestSearch implements ClientModInitializer {

   public static final String MOD_ID = "chestsearchbar";
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final Identifier BAR_LOCATION = Identifier.of("chestsearchbar", "textures/gui/search_bar.png");

   @Override
   public void onInitializeClient() {
      Config.init();
      KeyBindingHelper.registerKeyBinding(Keybindings.TOGGLE_SEARCH_BAR_KEY);
   }

}