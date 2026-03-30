package dev.dokko.tellurium;

import com.mojang.blaze3d.platform.InputConstants;
import dev.dokko.tellurium.config.Config;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.uku3lig.ukulib.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Tellurium implements ClientModInitializer {
	public static final String MOD_NAME = "Tellurium";
	public static final String MOD_ID = "tellurium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	@Getter
	private static final ConfigManager<Config> manager = ConfigManager.createDefault(Config.class, MOD_ID);

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(this::runInvertSprint);


		LOGGER.info("Loaded {}", MOD_NAME);
	}
	private void runInvertSprint(Minecraft client) {
		if(!manager.getConfig().isInvertSprint())return;
		if (client.player == null || client.options == null) return;

		KeyMapping sprintKey = client.options.keySprint;

		// Get the keycode of the sprint key
		int keyCode = sprintKey.getDefaultKey().getValue();

		// Check physical key state via GLFW
		boolean physicallyPressed = InputConstants.isKeyDown(client.getWindow(), keyCode);

		// Invert it
		sprintKey.setDown(!physicallyPressed);
	}

}