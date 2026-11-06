package de.merphyx.basicchestsearch.gui;

import de.merphyx.basicchestsearch.BasicChestSearch;
import de.merphyx.basicchestsearch.util.TextureUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SearchBox extends EditBox {

   private static final Component PLACEHOLDER = Component.translatable("gui.basicchestsearch.placeholder")
           .withStyle(EditBox.SEARCH_HINT_STYLE);

   private final boolean useTexture;

   public SearchBox(Font font, int x, int y, int width, int height, boolean bordered) {
      super(font, x, y, width, height, Component.translatable("itemGroup.search"));
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
   }

   @Override
   public int getInnerWidth() {
      return this.useTexture ? this.getWidth() - 2 : super.getInnerWidth();
   }

}
