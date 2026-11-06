package de.merphyx.basicchestsearch.search;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContainerSearcher {

   public static List<Slot> search(String search, AbstractContainerMenu menu, int size) {
      List<Slot> slots = new ArrayList<>();
      if (search == null || search.isEmpty()) return slots;

      String query = search.toLowerCase(Locale.ROOT).trim();

      for (int i = 0; i < size; ++i) {
         Slot slot = menu.getSlot(i);
         if (checkItem(slot.getItem(), query)) {
            slots.add(slot);
         }
      }

      return slots;
   }

   public static boolean checkItem(ItemStack item, String query) {
      if (item.isEmpty()) return false;

      if (query.startsWith("#")) {
         String tagQuery = query.substring(1);
         return item.typeHolder().tags().anyMatch(tag -> tag.location().getPath().contains(tagQuery));
      }

      if (item.getHoverName().getString().toLowerCase(Locale.ROOT).contains(query)) {
         return true;
      }

      if (matchesEnchantments(item.get(DataComponents.ENCHANTMENTS), query)
              || matchesEnchantments(item.get(DataComponents.STORED_ENCHANTMENTS), query)) {
         return true;
      }

      BundleContents bundle = item.get(DataComponents.BUNDLE_CONTENTS);
      if (bundle != null) {
         for (ItemStack stack : bundle.itemCopies().toList()) {
            if (checkItem(stack, query)) return true;
         }
      }

      ItemContainerContents container = item.get(DataComponents.CONTAINER);
      if (container != null) {
         for (ItemStack stack : container.itemCopies().toList()) {
            if (checkItem(stack, query)) return true;
         }
      }

      return false;
   }

   private static boolean matchesEnchantments(ItemEnchantments enchantments, String query) {
      if (enchantments == null || enchantments.keySet().isEmpty()) return false;

      for (Holder<Enchantment> enchantment : enchantments.keySet()) {
         int level = enchantments.getLevel(enchantment);
         String enchName = enchantment.value().description().getString().toLowerCase(Locale.ROOT);

         if (enchName.contains(query) || (enchName + " " + level).contains(query)) {
            return true;
         }
      }

      return false;
   }

}
