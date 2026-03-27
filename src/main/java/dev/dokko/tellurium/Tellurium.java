package dev.dokko.tellurium;

import dev.dokko.tellurium.config.Config;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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
	private void runInvertSprint(MinecraftClient client) {
		if(!manager.getConfig().isInvertSprint())return;
		if (client.player == null || client.options == null) return;

		KeyBinding sprintKey = client.options.sprintKey;

		// Get the keycode of the sprint key
		int keyCode = sprintKey.getDefaultKey().getCode();

		// Check physical key state via GLFW
		boolean physicallyPressed = InputUtil.isKeyPressed(client.getWindow().getHandle(), keyCode);

		// Invert it
		sprintKey.setPressed(!physicallyPressed);
	}

}