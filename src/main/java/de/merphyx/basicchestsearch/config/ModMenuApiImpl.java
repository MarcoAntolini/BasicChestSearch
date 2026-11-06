package de.merphyx.basicchestsearch.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;

public class ModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
                return ClothConfig.create(parent);
            } else {
                return new AlertScreen(
                        () -> Minecraft.getInstance().setScreenAndShow(parent),
                        Component.translatable("title.basicchestsearch.cloth_missing").withStyle(ChatFormatting.UNDERLINE),
                        Component.translatable("message.basicchestsearch.cloth_missing")
                );
            }
        };
    }

}
