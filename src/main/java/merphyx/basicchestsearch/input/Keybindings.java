package merphyx.basicchestsearch.input;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Keybindings {

   public static KeyBinding.Category KEY_CATEGORY =
           KeyBinding.Category.create(
                   Identifier.of("chestsearchbar", "title")
           );

   public static KeyBinding TOGGLE_SEARCH_BAR_KEY =
           new KeyBinding(
                   "key.chestsearchbar.toggleSearchBarKey",
                   InputUtil.Type.KEYSYM,
                   GLFW.GLFW_KEY_T,
                   KEY_CATEGORY
           );

}