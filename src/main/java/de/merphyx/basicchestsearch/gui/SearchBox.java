package de.merphyx.basicchestsearch.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import de.merphyx.basicchestsearch.BasicChestSearch;
import de.merphyx.basicchestsearch.util.TextureUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class SearchBox extends EditBox {

   private static final Component PLACEHOLDER = Component.translatable("gui.basicchestsearch.placeholder")
           .withStyle(EditBox.SEARCH_HINT_STYLE);
   private static final String CLEAR_GLYPH = "x";
   private static final int CLEAR_GAP = 2;

   private final Font font;
   private final boolean useTexture;

   public SearchBox(Font font, int x, int y, int width, int height, boolean bordered) {
      super(font, x, y, width, height, Component.translatable("itemGroup.search"));
      this.font = font;
      this.useTexture = !bordered;
      this.setBordered(bordered);
      this.setTextColor(0xFFFFFFFF);
      this.setHint(PLACEHOLDER);
   }

   @Override
   public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
      if (this.isVisible() && this.useTexture) {
         TextureUtil.draw3SliceTexture(graphics, BasicChestSearch.BAR_LOCATION, this.getX() - 2, this.getY() - 2, this.getWidth() + 1, 0, 0, 90, 12);
      }

      super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);

      if (!this.isVisible()) {
         return;
      }

      if (this.getValue().isEmpty()) {
         return;
      }

      boolean hoveringClear = this.isOverClearButton(mouseX, mouseY);
      graphics.text(this.font, Component.literal(CLEAR_GLYPH), this.clearGlyphX(), this.textY(), hoveringClear ? 0xFFFFFFA0 : 0xFFFFFFFF);
      if (hoveringClear) {
         graphics.requestCursor(CursorTypes.POINTING_HAND);
      }
   }

   @Override
   public void onClick(MouseButtonEvent event, boolean doubled) {
      if (this.isOverClearButton(event.x(), event.y())) {
         this.setValue("");
         this.setFocused(true);
         return;
      }
      super.onClick(event, doubled);
   }

   @Override
   public int getInnerWidth() {
      int width = this.useTexture ? this.getWidth() - 2 : super.getInnerWidth();
      if (!this.getValue().isEmpty()) {
         width -= this.font.width(CLEAR_GLYPH) + CLEAR_GAP;
      }
      return Math.max(0, width);
   }

   private boolean isOverClearButton(double mouseX, double mouseY) {
      if (!this.isVisible() || this.getValue().isEmpty()) {
         return false;
      }
      int left = this.clearGlyphX() - 1;
      return mouseX >= left && mouseX < this.getX() + this.getWidth()
              && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
   }

   private int clearGlyphX() {
      return this.getX() + this.getWidth() - this.rightPad() - this.font.width(CLEAR_GLYPH);
   }

   private int textY() {
      return this.isBordered() ? this.getY() + (this.getHeight() - 8) / 2 : this.getY();
   }

   private int rightPad() {
      return this.isBordered() ? 4 : 2;
   }

}
