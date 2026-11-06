package de.merphyx.basicchestsearch.mixin;

import de.merphyx.basicchestsearch.SearchHandler;
import de.merphyx.basicchestsearch.config.Config;
import de.merphyx.basicchestsearch.config.Mode;
import de.merphyx.basicchestsearch.gui.SearchBox;
import de.merphyx.basicchestsearch.input.Keybindings;
import de.merphyx.basicchestsearch.util.ContainerUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
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

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin<T extends AbstractContainerMenu> extends Screen {

   @Shadow protected int imageWidth;
   @Shadow protected int titleLabelY;
   @Shadow protected int inventoryLabelX;
   @Shadow protected int inventoryLabelY;
   @Shadow protected int leftPos;
   @Shadow protected int topPos;
   @Shadow protected Component playerInventoryTitle;
   @Shadow @Nullable protected Slot hoveredSlot;

   @Shadow @Nullable private Slot getHoveredSlot(double x, double y) { return null; }
   @Shadow private boolean isHovering(Slot slot, double xm, double ym) { return false; }
   @Shadow public abstract T getMenu();

   @Unique private SearchHandler searchHandler;
   @Unique private static long lastClick;
   @Unique private double currentMouseX;
   @Unique private double currentMouseY;

   protected HandledScreenMixin(Component title) {
      super(title);
   }

   @Inject(method = "init()V", at = @At("TAIL"))
   private void init(CallbackInfo ci) {
      AbstractContainerScreen<?> instance = (AbstractContainerScreen<?>) (Object) this;
      int size = ContainerUtil.getMenuSize(this.getMenu().slots.size());

      Mode mode = Config.getSearchMode(instance, size);

      if (mode == Mode.DISABLED) {
         return;
      }

      int pY = this.titleLabelY + this.topPos;
      int pX;
      int barWidth;

      if (Config.detachSearchBar()) {
         barWidth = this.imageWidth;
         pX = this.leftPos - 1;
         pY = Math.max(pY - 20, 1);
      } else {
         // The bar texture is drawn 2px left of the widget and 1px wider than it.
         barWidth = this.imageWidth - 1;
         pX = this.leftPos + 1;
      }

      EditBox searchBar = new SearchBox(this.font, pX + 1, pY, barWidth, 12, Config.detachSearchBar());
      this.searchHandler = new SearchHandler(instance, searchBar, mode);
      this.addRenderableWidget(searchBar);
   }

   @Redirect(
           method = {"extractContents", "mouseClicked", "mouseReleased", "mouseDragged"},
           at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getHoveredSlot(DD)Lnet/minecraft/world/inventory/Slot;")
   )
   private Slot universalSlotRedirect(AbstractContainerScreen<?> instance, double x, double y) {
      return this.findSlot(x, y);
   }

   @Inject(method = "extractLabels", at = @At("HEAD"), cancellable = true)
   private void hideContainerTitle(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
      if (Config.detachSearchBar() || this.searchHandler == null || !this.searchHandler.getSearchBar().isVisible()) {
         return;
      }

      graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
      ci.cancel();
   }

   @Inject(method = {"extractSlotHighlightBack", "extractSlotHighlightFront"}, at = @At("HEAD"), cancellable = true)
   private void cancelHighlightInSortMode(GuiGraphicsExtractor graphics, CallbackInfo ci) {
      if (this.isSearchAndSortActive()) {
         ci.cancel();
      }
   }

   @Inject(
           method = "extractContents",
           at = @At("HEAD")
   )
   private void captureMousePosition(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      this.currentMouseX = mouseX;
      this.currentMouseY = mouseY;
   }

   @Inject(
           method = "extractSlot",
           at = @At("HEAD")
   )
   private void drawSlotHighlightBackground(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
      if (this.searchHandler != null && this.searchHandler.shouldUseSearch() && this.searchHandler.isSortMode()) {
         if (this.hoveredSlot != null) {
            Slot hoverSlot = this.getHoveredSlot(this.currentMouseX, this.currentMouseY);
            if (slot.equals(hoverSlot)) {
               graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80FFFFFF);
            }
         }
      }
   }

   @Inject(
           method = "extractContents",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlotHighlightFront(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V",
                   shift = Shift.AFTER
           )
   )
   private void onDrawForeground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (this.searchHandler != null && this.searchHandler.shouldUseSearch()) {
         if (!this.searchHandler.isSortMode()) {
            int menuSize = this.searchHandler.getMenuSize();
            for (int i = 0; i < menuSize; ++i) {
               Slot posSlot = this.getMenu().getSlot(i);
               if (!this.searchHandler.isResult(posSlot)) {
                  graphics.fill(posSlot.x, posSlot.y, posSlot.x + 16, posSlot.y + 16, 0xB4000000);
               }
            }
         }
      }
   }

   @Redirect(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;"))
   private ItemStack redirectedStack(Slot slot) {
      if (this.isSearchAndSortActive() && slot.index < this.searchHandler.getMenuSize()) {
         List<Slot> results = this.searchHandler.getSearchResult();
         return slot.index < results.size() ? results.get(slot.index).getItem() : ItemStack.EMPTY;
      }
      return slot.getItem();
   }

   @Inject(
           method = "mouseClicked",
           at = @At("HEAD")
   )
   private void mouseClicked(MouseButtonEvent event, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      if (this.searchHandler == null) return;

      EditBox searchBar = this.searchHandler.getSearchBar();
      boolean isMouseOver = searchBar.isMouseOver(event.x(), event.y());

      searchBar.setFocused(isMouseOver);

      if (isMouseOver) {
         long time = System.currentTimeMillis();
         if ((doubled || time - lastClick < 200L) && !searchBar.getValue().isEmpty()) {
            searchBar.setValue("");
         }
         lastClick = time;
      }
   }

   @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
   private void onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
      if (this.searchHandler == null) return;

      EditBox bar = this.searchHandler.getSearchBar();
      if (Config.focusSearch().matches(event)) {
         this.focusSearchBar(bar);
         cir.setReturnValue(true);
         return;
      }
      if (Config.clearSearch().matches(event)) {
         bar.setValue("");
         SearchHandler.swallowNextChar = true;
         cir.setReturnValue(true);
         return;
      }
      if (bar.isFocused()) {
         if (event.isEscape()) {
            bar.setFocused(false);
            if (this.getFocused() == bar) {
               this.setFocused(null);
            }
         } else if (bar.isVisible()) {
            bar.keyPressed(event);
         }
         cir.setReturnValue(true);
      } else if (Keybindings.TOGGLE_SEARCH_BAR_KEY.matches(event)) {
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
   private void focusSearchBar(EditBox bar) {
      if (!bar.isVisible()) {
         bar.setVisible(true);
         SearchHandler.enabled = true;
      }
      bar.setFocused(true);
      this.setFocused(bar);
      SearchHandler.swallowNextChar = true;
   }

   @Unique
   private Slot findSlot(double mouseX, double mouseY) {
      if (this.isSearchAndSortActive()) {
         int menuSize = this.searchHandler.getMenuSize();
         for (int i = 0; i < menuSize; ++i) {
            Slot slot = this.getMenu().getSlot(i);
            if (this.isHovering(slot, mouseX, mouseY) && slot.isActive()) {
               List<Slot> results = this.searchHandler.getSearchResult();
               return i < results.size() ? results.get(i) : null;
            }
         }
      }
      return this.getHoveredSlot(mouseX, mouseY);
   }

   @Unique
   private boolean isSearchAndSortActive() {
      return this.searchHandler != null && this.searchHandler.shouldUseSearch() && this.searchHandler.isSortMode();
   }

}
