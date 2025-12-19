package merphyx.basicchestsearch.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;

public class ContainerUtil {

   public static int getMenuSize(int totalSlotsSize) {
      return totalSlotsSize - (MinecraftClient.getInstance().player.getInventory().size() - PlayerInventory.EQUIPMENT_SLOTS.size());
   }

}