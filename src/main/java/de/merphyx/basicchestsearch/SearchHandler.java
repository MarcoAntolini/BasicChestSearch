package de.merphyx.basicchestsearch;

import de.merphyx.basicchestsearch.config.Config;
import de.merphyx.basicchestsearch.config.Mode;
import de.merphyx.basicchestsearch.search.ContainerSearcher;
import de.merphyx.basicchestsearch.util.ContainerUtil;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SearchHandler {
   public static String search = "";
   public static boolean enabled = true;
   public static boolean swallowNextChar;

   private final AbstractContainerScreen<?> screen;
   private final EditBox searchBar;
   private final Mode mode;
   private List<Slot> containerMatches = new ArrayList<>();
   private List<Slot> inventoryMatches = new ArrayList<>();

   public SearchHandler(AbstractContainerScreen<?> screen, EditBox searchBar, Mode mode) {
      this.screen = screen;
      this.searchBar = searchBar;
      this.mode = mode;

      if (Config.keepSearch()) this.searchBar.setValue(search);
      if (Config.autoFocus() && enabled) {
         this.searchBar.setFocused(true);
         screen.setFocused(this.searchBar);
      }

      this.searchBar.setVisible(enabled);
      this.searchBar.setResponder(s -> {
         SearchHandler.search = s;
         this.update(s);
      });
   }

   public void tick() {
      // The key event is polled before this tick, and the matching character event with it.
      // Anything still set here was a non-character bind and must not eat the next typed letter.
      swallowNextChar = false;
      if (this.shouldUseSearch()) {
         this.update(this.searchBar.getValue());
      }
   }

   public void update(String query) {
      int containerSize = this.getMenuSize();
      List<Slot> matches = ContainerSearcher.search(query, this.screen.getMenu(), this.screen.getMenu().slots.size());
      List<Slot> containerMatches = new ArrayList<>();
      List<Slot> inventoryMatches = new ArrayList<>();

      for (Slot slot : matches) {
         if (slot.index < containerSize) {
            containerMatches.add(slot);
         } else {
            inventoryMatches.add(slot);
         }
      }

      this.containerMatches = containerMatches;
      this.inventoryMatches = inventoryMatches;
      this.searchBar.setTextColor(matches.isEmpty() && !query.isEmpty() ? 0xFFFF6464 : 0xFFFFFFFF);
   }

   public boolean shouldUseSearch() {
      return this.searchBar.isVisible() && !this.searchBar.getValue().isEmpty();
   }

   public boolean isResult(Slot slot) {
      return this.containerMatches.contains(slot) || this.inventoryMatches.contains(slot);
   }

   public @Nullable Slot sortedSlot(Slot visual) {
      int containerSize = this.getMenuSize();
      if (visual.index < containerSize) {
         return visual.index < this.containerMatches.size() ? this.containerMatches.get(visual.index) : null;
      }

      int inventoryIndex = visual.index - containerSize;
      return inventoryIndex < this.inventoryMatches.size() ? this.inventoryMatches.get(inventoryIndex) : null;
   }

   public EditBox getSearchBar() { return this.searchBar; }
   public boolean isSortMode() { return this.mode == Mode.SORT; }
   public int getMenuSize() { return ContainerUtil.getMenuSize(this.screen.getMenu().slots.size()); }

}
