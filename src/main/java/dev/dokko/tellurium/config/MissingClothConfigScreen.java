package dev.dokko.tellurium.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

import java.awt.*;
import java.net.URI;

public class MissingClothConfigScreen extends Screen {

    private final Screen parent;

    protected MissingClothConfigScreen(Screen parent) {
        super(Component.literal("Missing Dependency"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(
                Component.literal("Install"),
                _ -> {
                    try {
                        Util.getPlatform().openUri(new URI("https://modrinth.com/mod/cloth-config"));
                    } catch (Exception ignored) {

                    }
                }
        ).bounds(width / 2 - 110, height / 2 + 20, 100, 20).build());
        addRenderableWidget(Button.builder(
                Component.literal("Back"),
                _ -> Minecraft.getInstance().setScreen(parent)
        ).bounds(width / 2 + 10, height / 2 + 20, 100, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.fill(width/2-150, height/2-30, width/2+150, height/2, 0x90000000);
        context.centeredText(
                getFont(),
                Component.literal("Cloth Config is required to edit Tellurium's settings."),
                width / 2,
                height / 2 - 20,
                0xFFFFFFFF
        );
    }
}