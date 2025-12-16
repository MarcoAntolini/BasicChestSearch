package cgcm.chestsearchbar.search;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class ContainerSearcher {

   public static List<Slot> search(String search, ScreenHandler menu, int size) {
      List<Slot> slots = new ArrayList<>();

      for (int i = 0; i < size; ++i) {
         Slot slot = menu.getSlot(i);
         ItemStack item = slot.getStack();
         if (checkItem(item, search)) {
            slots.add(slot);
         }
      }

      return slots;
   }

   public static boolean checkItem(ItemStack item, String search) {
      if (!item.isEmpty() && !search.isEmpty()) {
         if (checkName(item.getName().getString(), search)) {
            return true;
         } else {

            if (item.hasEnchantments() || item.isOf(Items.ENCHANTED_BOOK)) {
               ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(item);
               for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                  String name = Text.translatable(enchantment.value().description().getString()).getString()
                          + " " + enchantments.getLevel(enchantment);
                  if (checkName(name, search)) {
                     return true;
                  }
               }
            }

            if (item.contains(DataComponentTypes.BUNDLE_CONTENTS)) {
               BundleContentsComponent bundleContents = item.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
               for (ItemStack stack : bundleContents.iterate()) {
                  if (checkItem(stack, search)) {
                     return true;
                  }
               }
            }

            if (item.contains(DataComponentTypes.CONTAINER)) {
               ContainerComponent container = item.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
               for (ItemStack stack : container.iterateNonEmpty()) {
                  if (checkItem(stack, search)) {
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean checkName(String item, String search) {
      return item.toLowerCase().contains(search.toLowerCase());
   }

}