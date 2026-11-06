package de.merphyx.basicchestsearch.config;

public class DefaultConfig {

    public Mode searchMode = Mode.SORT;
    public boolean keepSearch = true;
    public boolean autoFocus = false;
    public boolean detachSearchBar = false;
    public BoundKey focusSearch = new BoundKey();
    public BoundKey clearSearch = new BoundKey();

    public void ensureDefaults() {
        if (this.focusSearch == null) {
            this.focusSearch = new BoundKey();
        }
        if (this.clearSearch == null) {
            this.clearSearch = new BoundKey();
        }
    }

}