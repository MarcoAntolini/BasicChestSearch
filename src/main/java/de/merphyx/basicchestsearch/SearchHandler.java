package de.merphyx.basicchestsearch;

import de.merphyx.basicchestsearch.config.Config;
import de.merphyx.basicchestsearch.config.Mode;
import de.merphyx.basicchestsearch.search.ContainerSearcher;
import de.merphyx.basicchestsearch.util.ContainerUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class SearchHandler {
   public static String search = "";
   public static boolean enabled = true;

   private final HandledScreen<?> screen;
   private final TextFieldWidget searchBar;
   private final Mode mode;
   private List<Slot> slots = new ArrayList<>();

   public SearchHandler(HandledScreen<?> screen, TextFieldWidget searchBar, Mode mode) {
      this.screen = screen;
      this.searchBar = searchBar;
      this.mode = mode;

      if (Config.keepSearch()) this.searchBar.setText(search);
      if (Config.autoFocus() && enabled) {
         this.searchBar.setFocused(true);
         screen.setFocused(this.searchBar);
      }

      this.searchBar.setVisible(enabled);
      this.searchBar.setChangedListener(s -> {
         SearchHandler.search = s;
         this.update(s);
      });
   }

   public void tick() {
      if (this.shouldUseSearch()) {
         this.update(this.searchBar.getText());
      }
   }

   public void update(String query) {
      int size = ContainerUtil.getMenuSize(this.screen.getScreenHandler().slots.size());
      this.slots = ContainerSearcher.search(query, this.screen.getScreenHandler(), size);

      this.searchBar.setEditableColor(this.slots.isEmpty() && !query.isEmpty() ? 0xFFFF6464 : 0xFFFFFFFF);
   }

   public boolean shouldUseSearch() {
      return this.searchBar.isVisible() && !this.searchBar.getText().isEmpty();
   }

   public boolean isResult(Slot slot) {
      return this.slots.contains(slot);
   }

   public List<Slot> getSearchResult() { return this.slots; }
   public TextFieldWidget getSearchBar() { return this.searchBar; }
   public boolean isSortMode() { return this.mode == Mode.SORT; }
   public int getMenuSize() { return ContainerUtil.getMenuSize(this.screen.getScreenHandler().slots.size()); }

}