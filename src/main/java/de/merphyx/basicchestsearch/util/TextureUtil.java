package de.merphyx.basicchestsearch.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class TextureUtil {

   public static void draw3SliceTexture(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int totalWidth, int uStart, int vStart, int textureWidth, int textureHeight) {
      int sectionWidth = textureWidth / 3;
      int middleWidth = totalWidth - 2 * sectionWidth;
      graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, (float) uStart, (float) vStart, sectionWidth, textureHeight, textureWidth, textureHeight);

      int drawWidth;
      for (int drawnWidth = 0; drawnWidth < middleWidth; drawnWidth += drawWidth) {
         drawWidth = Math.min(sectionWidth, middleWidth - drawnWidth);
         graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + sectionWidth + drawnWidth, y, (float) (uStart + sectionWidth), (float) vStart, drawWidth, textureHeight, textureWidth, textureHeight);
      }

      graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + totalWidth - sectionWidth, y, (float) (uStart + 2 * sectionWidth), (float) vStart, sectionWidth, textureHeight, textureWidth, textureHeight);
   }

}
