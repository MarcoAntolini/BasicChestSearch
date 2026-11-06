package de.merphyx.basicchestsearch.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;

public class ContainerUtil {

   public static int getMenuSize(int totalSlotsSize) {
      Inventory inventory = Minecraft.getInstance().player.getInventory();
      return totalSlotsSize - (inventory.getContainerSize() - Inventory.EQUIPMENT_SLOT_MAPPING.size());
   }

}
