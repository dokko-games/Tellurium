package dev.dokko.tellurium.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class Implementation implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
                return TelluriumConfigScreen.create(parent);
            } else {
                return new MissingClothConfigScreen(parent);
            }
        };
    }

}
