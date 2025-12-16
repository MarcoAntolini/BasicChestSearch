package cgcm.chestsearchbar.api;

import cgcm.chestsearchbar.config.Mode;

public interface ISearchable {

   default Mode getMode() {
      return Mode.SHADOW;
   }

}
