package de.merphyx.basicchestsearch.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
                return ClothConfig.create(parent);
            } else {
                return new NoticeScreen(
                        () -> MinecraftClient.getInstance().setScreen(parent),
                        Text.translatable("title.basicchestsearch.cloth_missing").formatted(Formatting.UNDERLINE),
                        Text.translatable("message.basicchestsearch.cloth_missing")
                );
            }
        };
    }
}