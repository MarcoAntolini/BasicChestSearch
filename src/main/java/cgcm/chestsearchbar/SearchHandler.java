package cgcm.chestsearchbar;

import cgcm.chestsearchbar.config.Config;
import cgcm.chestsearchbar.config.Mode;
import cgcm.chestsearchbar.search.ContainerSearcher;
import cgcm.chestsearchbar.util.ContainerUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.slot.Slot;

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

      if (Config.keepSearch) {
         this.searchBar.setText(search);
      }

      if (Config.autoFocus && enabled) {
         this.searchBar.setFocused(true);
         screen.setFocused(this.searchBar);
      }

      this.searchBar.setVisible(enabled);
      this.searchBar.setChangedListener(this::refreshAndUpdate);
   }

   public List<Slot> getSearchResult() {
      return this.slots;
   }

   public TextFieldWidget getSearchBar() {
      return this.searchBar;
   }

   public boolean isSearchActive() {
      return !this.searchBar.getText().isEmpty();
   }

   public boolean shouldUseSearch() {
      return this.isSearchActive() && this.searchBar.isVisible();
   }

   public int getMenuSize() {
      return ContainerUtil.getMenuSize(this.screen.getScreenHandler().slots.size());
   }

   public void tick() {
      if (this.shouldUseSearch()) {
         this.refreshResults(this.searchBar.getText());
      }
   }

   public void refreshAndUpdate(String search) {
      SearchHandler.search = search;
      this.refreshResults(search);
   }

   public void refreshResults(String search) {
      this.slots = ContainerSearcher.search(search, this.screen.getScreenHandler(), this.getMenuSize());
      if (this.slots.isEmpty() && this.isSearchActive()) {
         this.searchBar.setEditableColor(0xFFFF6464); // rot
      } else {
         this.searchBar.setEditableColor(0xFFFFFFFF); // weiß
      }
   }

   public boolean isSortMode() {
      return this.mode == Mode.SORT;
   }

}