package de.merphyx.basicchestsearch.config;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfig {

    public static Screen create(Screen parent) {

        DefaultConfig config = Config.getRawConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.basicchestsearch.config"));

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.basicchestsearch.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startEnumSelector(
                        Component.translatable("option.basicchestsearch.searchMode"), Mode.class, config.searchMode)
                .setDefaultValue(Mode.SORT)
                .setTooltip(Component.translatable("option.basicchestsearch.searchMode.tooltip"))
                .setSaveConsumer(newValue -> config.searchMode = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("option.basicchestsearch.keepSearch"), config.keepSearch)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.basicchestsearch.keepSearch.tooltip"))
                .setSaveConsumer(newValue -> config.keepSearch = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("option.basicchestsearch.autoFocus"), config.autoFocus)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("option.basicchestsearch.autoFocus.tooltip"))
                .setSaveConsumer(newValue -> config.autoFocus = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("option.basicchestsearch.detachSearchBar"), config.detachSearchBar)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.basicchestsearch.detachSearchBar.tooltip"))
                .setSaveConsumer(newValue -> config.detachSearchBar = newValue)
                .build());

        general.addEntry(entryBuilder.startModifierKeyCodeField(
                        Component.translatable("option.basicchestsearch.focusSearch"), readKey(config.focusSearch))
                .setDefaultValue(readKey(BoundKey.ctrl(InputConstants.KEY_F)))
                .setAllowMouse(false)
                .setTooltip(Component.translatable("option.basicchestsearch.focusSearch.tooltip"))
                .setModifierSaveConsumer(value -> writeKey(config.focusSearch, value))
                .build());

        general.addEntry(entryBuilder.startModifierKeyCodeField(
                        Component.translatable("option.basicchestsearch.clearSearch"), readKey(config.clearSearch))
                .setDefaultValue(readKey(BoundKey.ctrl(InputConstants.KEY_D)))
                .setAllowMouse(false)
                .setTooltip(Component.translatable("option.basicchestsearch.clearSearch.tooltip"))
                .setModifierSaveConsumer(value -> writeKey(config.clearSearch, value))
                .build());

        ConfigCategory appearance = builder.getOrCreateCategory(Component.translatable("category.basicchestsearch.appearance"));

        appearance.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("option.basicchestsearch.dimOpacity"), config.dimOpacity, 0, 100)
                .setDefaultValue(50)
                .setTextGetter(value -> Component.literal(value + "%"))
                .setTooltip(Component.translatable("option.basicchestsearch.dimOpacity.tooltip"))
                .setSaveConsumer(newValue -> config.dimOpacity = newValue)
                .build());

        appearance.addEntry(entryBuilder.startColorField(
                        Component.translatable("option.basicchestsearch.highlightColor"), config.highlightColor)
                .setDefaultValue(0xFFFF00)
                .setTooltip(Component.translatable("option.basicchestsearch.highlightColor.tooltip"))
                .setSaveConsumer(newValue -> config.highlightColor = newValue & 0xFFFFFF)
                .build());

        appearance.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("option.basicchestsearch.highlightOpacity"), config.highlightOpacity, 0, 100)
                .setDefaultValue(100)
                .setTextGetter(value -> Component.literal(value + "%"))
                .setTooltip(Component.translatable("option.basicchestsearch.highlightOpacity.tooltip"))
                .setSaveConsumer(newValue -> config.highlightOpacity = newValue)
                .build());

        appearance.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("option.basicchestsearch.borderThickness"), config.borderThickness, 1, 4)
                .setDefaultValue(1)
                .setTooltip(Component.translatable("option.basicchestsearch.borderThickness.tooltip"))
                .setSaveConsumer(newValue -> config.borderThickness = newValue)
                .build());

        builder.setSavingRunnable(Config::save);

        return builder.build();
    }

    private static ModifierKeyCode readKey(BoundKey source) {
        return ModifierKeyCode.of(source.resolve(), Modifier.of(source.alt, source.control, source.shift));
    }

    private static void writeKey(BoundKey target, ModifierKeyCode value) {
        InputConstants.Key key = value.getKeyCode();
        Modifier modifier = value.getModifier();
        target.key = key == null || value.isUnknown() ? null : key.getName();
        target.alt = modifier != null && modifier.hasAlt();
        target.control = modifier != null && modifier.hasControl();
        target.shift = modifier != null && modifier.hasShift();
    }

}
