package cgcm.chestsearchbar.util;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class TextureUtil {

   public static void draw3SliceTexture(DrawContext graphics, Identifier texture, int x, int y, int totalWidth, int uStart, int vStart, int textureWidth, int textureHeight) {
      int sectionWidth = textureWidth / 3;
      int middleWidth = totalWidth - 2 * sectionWidth;
      graphics.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, (float)uStart, (float)vStart, sectionWidth, textureHeight, textureWidth, textureHeight);

      int drawWidth;
      for(int drawnWidth = 0; drawnWidth < middleWidth; drawnWidth += drawWidth) {
         drawWidth = Math.min(sectionWidth, middleWidth - drawnWidth);
         graphics.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x + sectionWidth + drawnWidth, y, (float)(uStart + sectionWidth), (float)vStart, drawWidth, textureHeight, textureWidth, textureHeight);
      }

      graphics.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x + totalWidth - sectionWidth, y, (float)(uStart + 2 * sectionWidth), (float)vStart, sectionWidth, textureHeight, textureWidth, textureHeight);
   }

}