package de.merphyx.basicchestsearch;

import de.merphyx.basicchestsearch.config.Config;
import de.merphyx.basicchestsearch.config.Mode;
import de.merphyx.basicchestsearch.search.ContainerSearcher;
import de.merphyx.basicchestsearch.util.ContainerUtil;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class SearchHandler {
   public static String search = "";
   public static boolean enabled = true;

   private final AbstractContainerScreen<?> screen;
   private final EditBox searchBar;
   private final Mode mode;
   private List<Slot> slots = new ArrayList<>();

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
      if (this.shouldUseSearch()) {
         this.update(this.searchBar.getValue());
      }
   }

   public void update(String query) {
      int size = ContainerUtil.getMenuSize(this.screen.getMenu().slots.size());
      this.slots = ContainerSearcher.search(query, this.screen.getMenu(), size);

      this.searchBar.setTextColor(this.slots.isEmpty() && !query.isEmpty() ? 0xFFFF6464 : 0xFFFFFFFF);
   }

   public boolean shouldUseSearch() {
      return this.searchBar.isVisible() && !this.searchBar.getValue().isEmpty();
   }

   public boolean isResult(Slot slot) {
      return this.slots.contains(slot);
   }

   public List<Slot> getSearchResult() { return this.slots; }
   public EditBox getSearchBar() { return this.searchBar; }
   public boolean isSortMode() { return this.mode == Mode.SORT; }
   public int getMenuSize() { return ContainerUtil.getMenuSize(this.screen.getMenu().slots.size()); }

}
