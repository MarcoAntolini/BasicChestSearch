package de.merphyx.basicchestsearch.mixin;

import de.merphyx.basicchestsearch.SearchHandler;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.CharacterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

   @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
   private void swallowSearchHotkeyChar(long window, CharacterEvent event, CallbackInfo ci) {
      if (!SearchHandler.swallowNextChar) {
         return;
      }
      SearchHandler.swallowNextChar = false;
      ci.cancel();
   }

}
