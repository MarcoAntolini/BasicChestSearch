package de.merphyx.basicchestsearch.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Keybindings {

   public static KeyMapping.Category KEY_CATEGORY =
           KeyMapping.Category.register(
                   Identifier.fromNamespaceAndPath("basicchestsearch", "title")
           );

   public static KeyMapping TOGGLE_SEARCH_BAR_KEY =
           new KeyMapping(
                   "key.basicchestsearch.toggleBar",
                   InputConstants.Type.KEYBOARD,
                   InputConstants.KEY_T,
                   KEY_CATEGORY
           );

}
