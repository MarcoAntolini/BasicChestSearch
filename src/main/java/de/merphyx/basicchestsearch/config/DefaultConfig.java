package de.merphyx.basicchestsearch.config;

import com.mojang.blaze3d.platform.InputConstants;

public class DefaultConfig {

    public int configVersion = CURRENT_VERSION;
    public Mode searchMode = Mode.SORT;
    public boolean keepSearch = true;
    public boolean autoFocus = false;
    public boolean detachSearchBar = true;
    public BoundKey focusSearch = BoundKey.ctrl(InputConstants.KEY_F);
    public BoundKey clearSearch = BoundKey.ctrl(InputConstants.KEY_D);
    public int dimOpacity = 50;
    public int highlightColor = 0xFFFF00;
    public int highlightOpacity = 100;
    public int borderThickness = 1;

    private static final int CURRENT_VERSION = 1;

    public void ensureDefaults() {
        if (this.focusSearch == null) {
            this.focusSearch = new BoundKey();
        }
        if (this.clearSearch == null) {
            this.clearSearch = new BoundKey();
        }
        // Gson writes 0 for ints that are absent from an older file, which would
        // turn the dim overlay transparent and hide the new border.
        if (this.configVersion < 1) {
            this.dimOpacity = 50;
            this.highlightColor = 0xFFFF00;
            this.highlightOpacity = 100;
            this.borderThickness = 1;
            this.configVersion = 1;
        }
        this.dimOpacity = Math.clamp(this.dimOpacity, 0, 100);
        this.highlightOpacity = Math.clamp(this.highlightOpacity, 0, 100);
        this.borderThickness = Math.clamp(this.borderThickness, 1, 4);
        this.highlightColor &= 0xFFFFFF;
    }

}