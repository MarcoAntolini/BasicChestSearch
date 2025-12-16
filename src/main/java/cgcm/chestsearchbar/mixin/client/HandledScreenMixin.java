package cgcm.chestsearchbar.mixin.client;

import cgcm.chestsearchbar.SearchHandler;
import cgcm.chestsearchbar.config.Config;
import cgcm.chestsearchbar.config.Mode;
import cgcm.chestsearchbar.gui.SearchBox;
import cgcm.chestsearchbar.input.Keybindings;
import cgcm.chestsearchbar.util.ContainerUtil;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

   @Shadow
   protected T handler;
   @Shadow
   protected int backgroundWidth;
   @Shadow
   protected int x;
   @Shadow
   protected int y;
   @Shadow
   protected int titleX;
   @Shadow
   protected int titleY;
   @Shadow
   @Nullable
   protected Slot focusedSlot;
   @Unique
   private SearchHandler searchHandler;
   @Unique
   private static long lastClick;

   @Shadow
   protected abstract boolean isPointOverSlot(Slot slot, double pointX, double pointY);

   @Shadow
   protected abstract boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY);

   @Shadow
   @Nullable
   protected abstract Slot getSlotAt(double x, double y);

   protected HandledScreenMixin(T menu, Text pTitle) {
      super(pTitle);
      this.handler = menu;
   }

   @Inject(
           method = "init",
           at = @At("TAIL")
   )
   private void init(CallbackInfo ci) {
      HandledScreen<?> instance = (HandledScreen<?>) (Object) this;
      Mode mode = Config.getMode(instance, ContainerUtil.getMenuSize(this.handler.slots.size()));

      if (!(instance instanceof CreativeInventoryScreen) && !(instance instanceof InventoryScreen) && !(instance instanceof CraftingScreen) && mode != Mode.DISABLED) {
         int pY = this.titleY + this.y;
         int pX;
         int barWidth;

         if (Config.detachSearchBar) {
            barWidth = (int) (this.backgroundWidth * 0.6D);
            pX = this.width / 2 - barWidth / 2;
            pY = Math.max(pY - 20, 1);
         } else {
            pX = this.titleX + this.x + (this.title.getString().isBlank() ? 0 : this.textRenderer.getWidth(this.title.getString().replaceAll("\\p{C}", "")) + 3);
            int finalPos = this.x + this.backgroundWidth - this.titleX;
            pX = Math.min(pX, finalPos - 32);
            barWidth = finalPos - pX + 1;
         }

         TextFieldWidget searchBar = new SearchBox(this.textRenderer, pX + 1, pY, barWidth, 12, Config.detachSearchBar);
         this.searchHandler = new SearchHandler(instance, searchBar, mode);
         this.addDrawableChild(searchBar);
      }
   }

   @Inject(
           method = "renderMain",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V",
                   shift = Shift.AFTER
           )
   )
   private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.searchHandler != null) {
         if (this.searchHandler.shouldUseSearch() && this.searchHandler.isSortMode()) {
            for (int i = 0; i < this.searchHandler.getMenuSize(); ++i) {
               Slot posSlot = this.handler.getSlot(i);
               if (this.isPointWithinBounds(posSlot.x, posSlot.y, 16, 16, mouseX, mouseY) && posSlot.isEnabled()) {
                  if (i < this.searchHandler.getSearchResult().size()) {
                     this.focusedSlot = this.searchHandler.getSearchResult().get(i);
                  } else {
                     this.focusedSlot = null;
                  }
               }
            }
         }
      }
   }

   @Inject(
           method = "renderMain",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlightFront(Lnet/minecraft/client/gui/DrawContext;)V",
                   shift = Shift.AFTER
           )
   )
   private void onDrawForeground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.searchHandler != null && this.searchHandler.shouldUseSearch() && !this.searchHandler.isSortMode()) {
         for (int i = 0; i < this.searchHandler.getMenuSize(); ++i) {
            Slot posSlot = this.handler.getSlot(i);
            if (!this.searchHandler.getSearchResult().contains(posSlot)) {
               context.fill(posSlot.x, posSlot.y, posSlot.x + 16, posSlot.y + 16, 0xB4000000);
            }
         }
      }
   }

   @Redirect(
           method = "drawSlot",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"
           )
   )
   private ItemStack redirectGetItem(Slot slot) {
      int index = slot.id;
      if (this.searchHandler != null && this.searchHandler.shouldUseSearch() && this.searchHandler.isSortMode() && index < this.searchHandler.getMenuSize()) {
         return this.searchHandler.getSearchResult().size() > index ? this.searchHandler.getSearchResult().get(index).getStack() : ItemStack.EMPTY;
      } else {
         return slot.getStack();
      }
   }

   @Inject(
           method = "mouseClicked",
           at = @At("HEAD")
   )
   private void mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      if (this.searchHandler != null) {
         double mouseX = click.x();
         double mouseY = click.y();

         if (!this.searchHandler.getSearchBar().isMouseOver(mouseX, mouseY)) {
            this.searchHandler.getSearchBar().setFocused(false);
         } else {
            this.searchHandler.getSearchBar().setFocused(true);
            long time = System.currentTimeMillis();
            long d = time - lastClick;
            if (d < 200L && this.searchHandler.getSearchBar().isFocused() && !this.searchHandler.isSearchActive()) {
               this.searchHandler.getSearchBar().setText("");
               SearchHandler.search = "";
            }
            lastClick = time;
         }
      }
   }

   @Inject(
           method = "keyPressed",
           at = @At("HEAD"),
           cancellable = true
   )
   private void keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
      if (this.searchHandler != null) {
         if (this.searchHandler.getSearchBar().isFocused()) {
            if (input.key() != 256 && this.searchHandler.getSearchBar().isVisible()) {
               this.searchHandler.getSearchBar().keyPressed(input);
               cir.setReturnValue(true);
            }
         } else if (Keybindings.TOGGLE_SEARCH_BAR_KEY.matchesKey(input)) {
            boolean v = !this.searchHandler.getSearchBar().isVisible();
            this.searchHandler.getSearchBar().setVisible(v);
            SearchHandler.enabled = v;
         }
      }
   }

   @Inject(
           method = "tick",
           at = @At("TAIL")
   )
   private void tick(CallbackInfo ci) {
      if (this.searchHandler != null) {
         this.searchHandler.tick();
      }
   }

   @Redirect(
           method = "mouseDragged",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;getSlotAt(DD)Lnet/minecraft/screen/slot/Slot;"
           )
   )
   private Slot mouseDragged(HandledScreen<?> instance, double mouseX, double mouseY) {
      Slot slot = this.getSlotAt(mouseX, mouseY);
      return this.searchHandler != null && this.searchHandler.isSortMode() && this.searchHandler.shouldUseSearch() && slot != null && slot.id < this.searchHandler.getMenuSize() ? null : slot;
   }

   @Redirect(
           method = "mouseClicked",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;getSlotAt(DD)Lnet/minecraft/screen/slot/Slot;"
           )
   )
   private Slot mouseClickedSlot(HandledScreen<?> instance, double mouseX, double mouseY) {
      return this.findSlot(mouseX, mouseY);
   }

   @Redirect(
           method = "mouseReleased",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;getSlotAt(DD)Lnet/minecraft/screen/slot/Slot;"
           )
   )
   private Slot mouseReleased(HandledScreen<?> instance, double mouseX, double mouseY) {
      return this.findSlot(mouseX, mouseY);
   }

   @Unique
   private Slot findSlot(double pMouseX, double pMouseY) {
      if (this.searchHandler != null && this.searchHandler.isSortMode() && this.searchHandler.shouldUseSearch()) {
         for (int i = 0; i < this.searchHandler.getMenuSize(); ++i) {
            Slot slot = this.handler.getSlot(i);
            if (this.isPointOverSlot(slot, pMouseX, pMouseY) && slot.isEnabled()) {
               if (i < this.searchHandler.getSearchResult().size()) {
                  return this.searchHandler.getSearchResult().get(i);
               }
               return null;
            }
         }
      }
      return this.getSlotAt(pMouseX, pMouseY);
   }

}