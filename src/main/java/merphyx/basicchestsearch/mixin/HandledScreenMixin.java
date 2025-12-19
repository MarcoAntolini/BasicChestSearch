package merphyx.basicchestsearch.mixin;

import merphyx.basicchestsearch.SearchHandler;
import merphyx.basicchestsearch.config.Config;
import merphyx.basicchestsearch.config.Mode;
import merphyx.basicchestsearch.gui.SearchBox;
import merphyx.basicchestsearch.input.Keybindings;
import merphyx.basicchestsearch.util.ContainerUtil;
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

import java.util.List;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

   @Shadow protected T handler;
   @Shadow protected int backgroundWidth;
   @Shadow protected int x;
   @Shadow protected int y;
   @Shadow protected int titleX;
   @Shadow protected int titleY;
   @Shadow @Nullable protected Slot focusedSlot;

   @Shadow protected abstract boolean isPointOverSlot(Slot slot, double pointX, double pointY);
   @Shadow @Nullable protected abstract Slot getSlotAt(double x, double y);

   @Unique private SearchHandler searchHandler;
   @Unique private static long lastClick;
   @Unique private double currentMouseX;
   @Unique private double currentMouseY;

   protected HandledScreenMixin(T menu, Text pTitle) {
      super(pTitle);
      this.handler = menu;
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void init(CallbackInfo ci) {
      HandledScreen<?> instance = (HandledScreen<?>) (Object) this;
      int size = ContainerUtil.getMenuSize(this.handler.slots.size());

      Mode mode = Config.getSearchMode(instance, size);

      if (mode == Mode.DISABLED) {
         return;
      }

      int pY = this.titleY + this.y;
      int pX;
      int barWidth;

      if (Config.detachSearchBar()) {
         barWidth = (int) (this.backgroundWidth * 0.6D);
         pX = this.width / 2 - barWidth / 2;
         pY = Math.max(pY - 20, 1);
      } else {
         String titleStr = this.title.getString().replaceAll("\\p{C}", "");
         pX = this.titleX + this.x + (titleStr.isBlank() ? 0 : this.textRenderer.getWidth(titleStr) + 3);
         int maxX = this.x + this.backgroundWidth - this.titleX - 32;
         pX = Math.min(pX, maxX);
         barWidth = (this.x + this.backgroundWidth - this.titleX) - pX + 1;
      }

      TextFieldWidget searchBar = new SearchBox(this.textRenderer, pX + 1, pY, barWidth, 12, Config.detachSearchBar());
      this.searchHandler = new SearchHandler(instance, searchBar, mode);
      this.addDrawableChild(searchBar);
   }

   @Redirect(
           method = {"renderMain", "mouseClicked", "mouseReleased", "mouseDragged"},
           at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;getSlotAt(DD)Lnet/minecraft/screen/slot/Slot;")
   )
   private Slot universalSlotRedirect(HandledScreen<?> instance, double x, double y) {
      return this.findSlot(x, y);
   }

   @Inject(method = {"drawSlotHighlightBack", "drawSlotHighlightFront"}, at = @At("HEAD"), cancellable = true)
   private void cancelHighlightInSortMode(DrawContext context, CallbackInfo ci) {
      if (this.isSearchAndSortActive()) {
         ci.cancel();
      }
   }

   @Inject(
           method = "renderMain",
           at = @At("HEAD")
   )
   private void captureMousePosition(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
      this.currentMouseX = mouseX;
      this.currentMouseY = mouseY;
   }

   @Inject(
           method = "drawSlot",
           at = @At("HEAD")
   )
   private void drawSlotHighlightBackground(DrawContext context, Slot slot, CallbackInfo ci) {
      if (this.searchHandler != null && this.searchHandler.shouldUseSearch() && this.searchHandler.isSortMode()) {
         if (this.focusedSlot != null) {
            Slot hoverSlot = this.getSlotAt(this.currentMouseX, this.currentMouseY);
            if (slot.equals(hoverSlot)) {
               context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80FFFFFF);
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
   private void onDrawForeground(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
      if (this.searchHandler != null && this.searchHandler.shouldUseSearch()) {
         if (!this.searchHandler.isSortMode()) {
            int menuSize = this.searchHandler.getMenuSize();
            for (int i = 0; i < menuSize; ++i) {
               Slot posSlot = this.handler.getSlot(i);
               if (!this.searchHandler.isResult(posSlot)) {
                  context.fill(posSlot.x, posSlot.y, posSlot.x + 16, posSlot.y + 16, 0xB4000000);
               }
            }
         }
      }
   }

   @Redirect(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"))
   private ItemStack redirectedStack(Slot slot) {
      if (this.isSearchAndSortActive() && slot.id < this.searchHandler.getMenuSize()) {
         List<Slot> results = this.searchHandler.getSearchResult();
         return slot.id < results.size() ? results.get(slot.id).getStack() : ItemStack.EMPTY;
      }
      return slot.getStack();
   }

   @Inject(
           method = "mouseClicked",
           at = @At("HEAD")
   )
   private void mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      if (this.searchHandler == null) return;

      TextFieldWidget searchBar = this.searchHandler.getSearchBar();
      boolean isMouseOver = searchBar.isMouseOver(click.x(), click.y());

      searchBar.setFocused(isMouseOver);

      if (isMouseOver) {
         long time = System.currentTimeMillis();
         if ((doubled || time - lastClick < 200L) && !searchBar.getText().isEmpty()) {
            searchBar.setText("");
         }
         lastClick = time;
      }
   }

   @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
   private void onKeyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
      if (this.searchHandler == null) return;

      TextFieldWidget bar = this.searchHandler.getSearchBar();
      if (bar.isFocused()) {
         if (input.key() == 256) { // ESC
            bar.setFocused(false);
         } else if (bar.isVisible()) {
            bar.keyPressed(input);
         }
         cir.setReturnValue(true);
      } else if (Keybindings.TOGGLE_SEARCH_BAR_KEY.matchesKey(input)) {
         boolean visible = !bar.isVisible();
         bar.setVisible(visible);
         SearchHandler.enabled = visible;
         cir.setReturnValue(true);
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

   @Unique
   private Slot findSlot(double mouseX, double mouseY) {
      if (this.isSearchAndSortActive()) {
         int menuSize = this.searchHandler.getMenuSize();
         for (int i = 0; i < menuSize; ++i) {
            Slot slot = this.handler.getSlot(i);
            if (this.isPointOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
               List<Slot> results = this.searchHandler.getSearchResult();
               return i < results.size() ? results.get(i) : null;
            }
         }
      }
      return this.getSlotAt(mouseX, mouseY);
   }

   @Unique
   private boolean isSearchAndSortActive() {
      return this.searchHandler != null && this.searchHandler.shouldUseSearch() && this.searchHandler.isSortMode();
   }

}