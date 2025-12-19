package de.merphyx.basicchestsearch.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClothConfig {
    public static Screen create(Screen parent) {

        DefaultConfig config = Config.getRawConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.basicchestsearch.config"));

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.basicchestsearch.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startEnumSelector(
                        Text.translatable("option.basicchestsearch.searchMode"), Mode.class, config.searchMode)
                .setDefaultValue(Mode.SORT)
                .setTooltip(Text.translatable("option.basicchestsearch.searchMode.tooltip"))
                .setSaveConsumer(newValue -> config.searchMode = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("option.basicchestsearch.keepSearch"), config.keepSearch)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.basicchestsearch.keepSearch.tooltip"))
                .setSaveConsumer(newValue -> config.keepSearch = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("option.basicchestsearch.autoFocus"), config.autoFocus)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("option.basicchestsearch.autoFocus.tooltip"))
                .setSaveConsumer(newValue -> config.autoFocus = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("option.basicchestsearch.detachSearchBar"), config.detachSearchBar)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("option.basicchestsearch.detachSearchBar.tooltip"))
                .setSaveConsumer(newValue -> config.detachSearchBar = newValue)
                .build());

        builder.setSavingRunnable(Config::save);

        return builder.build();
    }
}