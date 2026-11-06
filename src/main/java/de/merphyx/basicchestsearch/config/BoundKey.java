package de.merphyx.basicchestsearch.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.InputQuirks;
import net.minecraft.client.input.KeyEvent;

public class BoundKey {

    public String key;
    public boolean alt;
    public boolean control;
    public boolean shift;

    public boolean matches(KeyEvent event) {
        InputConstants.Key bound = this.resolve();
        if (bound.equals(InputConstants.UNKNOWN) || bound.getType() != InputConstants.Type.KEYBOARD) {
            return false;
        }
        if (bound.getValue() != event.key()) {
            return false;
        }

        // Cloth Config records Cmd as "control" on macOS. Match that same key.
        boolean controlDown = InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY
                ? event.hasControlDownWithQuirk()
                : event.hasControlDown();
        return this.alt == event.hasAltDown()
                && this.control == controlDown
                && this.shift == event.hasShiftDown();
    }

    public InputConstants.Key resolve() {
        if (this.key == null || this.key.isEmpty()) {
            return InputConstants.UNKNOWN;
        }
        try {
            return InputConstants.getKey(this.key);
        } catch (IllegalArgumentException ignored) {
            return InputConstants.UNKNOWN;
        }
    }

}
