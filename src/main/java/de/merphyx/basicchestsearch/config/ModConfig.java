package de.merphyx.basicchestsearch.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "basicchestsearch")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public General general = new General();

    public static class General {

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public Mode searchMode = Mode.SORT;

        @ConfigEntry.Gui.Tooltip
        public boolean keepSearch = true;

        @ConfigEntry.Gui.Tooltip
        public boolean autoFocus = false;

        @ConfigEntry.Gui.Tooltip
        public boolean detachSearchBar = false;
    }

}