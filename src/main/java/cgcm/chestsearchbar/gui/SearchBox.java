package cgcm.chestsearchbar.gui;

import cgcm.chestsearchbar.ChestSearchBar;
import cgcm.chestsearchbar.util.TextureUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SearchBox extends TextFieldWidget {

   private final boolean useTexture;

   public SearchBox(TextRenderer font, int x, int y, int width, int height, boolean bordered) {
      super(font, x, y, width, height, Text.translatable("itemGroup.search"));
      this.useTexture = !bordered;
      this.setDrawsBackground(bordered);
      this.setEditableColor(0xFFFFFFFF);
   }

   @Override
   public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
      if (this.isVisible() && this.useTexture) {
         TextureUtil.draw3SliceTexture(context, ChestSearchBar.BAR_LOCATION, this.getX() - 2, this.getY() - 2, this.getWidth() + 1, 0, 0, 90, 12);
      }

      super.renderWidget(context, mouseX, mouseY, delta);
   }

   @Override
   public int getInnerWidth() {
      return this.useTexture ? this.width - 2 : super.getInnerWidth();
   }

}