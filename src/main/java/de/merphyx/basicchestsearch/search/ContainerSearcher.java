package de.merphyx.basicchestsearch.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ContainerSearcher {

   public static List<Slot> search(String search, ScreenHandler menu, int size) {
      List<Slot> slots = new ArrayList<>();
      if (search == null || search.isEmpty()) return slots;

      String query = search.toLowerCase(Locale.ROOT).trim();

      for (int i = 0; i < size; ++i) {
         Slot slot = menu.getSlot(i);
         if (checkItem(slot.getStack(), query)) {
            slots.add(slot);
         }
      }

      return slots;
   }

   public static boolean checkItem(ItemStack item, String query) {
      if (item.isEmpty()) return false;

      if (query.startsWith("#")) {
         String tagQuery = query.substring(1);
         return item.streamTags().anyMatch(tag -> tag.id().getPath().contains(tagQuery));
      }

      if (item.getName().getString().toLowerCase(Locale.ROOT).contains(query)) {
         return true;
      }

      ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(item);
      if (!enchantments.isEmpty()) {
         for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
            int level = enchantments.getLevel(enchantment);

            String enchName = enchantment.value().description().getString().toLowerCase(Locale.ROOT);

            if (enchName.contains(query) || (enchName + " " + level).contains(query)) {
               return true;
            }
         }
      }

      BundleContentsComponent bundle = item.get(DataComponentTypes.BUNDLE_CONTENTS);
      if (bundle != null) {
         for (ItemStack stack : bundle.iterate()) {
            if (checkItem(stack, query)) return true;
         }
      }

      ContainerComponent container = item.get(DataComponentTypes.CONTAINER);
      if (container != null) {
         for (ItemStack stack : container.iterateNonEmpty()) {
            if (checkItem(stack, query)) return true;
         }
      }

      return false;
   }

}