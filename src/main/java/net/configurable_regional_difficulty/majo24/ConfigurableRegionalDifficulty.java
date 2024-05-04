package net.configurable_regional_difficulty.majo24;

import net.configurable_regional_difficulty.majo24.config.Commands;
import net.configurable_regional_difficulty.majo24.config.ConfigManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ConfigurableRegionalDifficulty implements ModInitializer {
	public static final String MOD_ID = "configurable-regional-difficulty";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ConfigManager configManager = null;

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register(ConfigurableRegionalDifficulty::loadConfig);
		Commands.registerCommand();
		NetworkingHandler.initMain();
	}

	public static void loadConfig(MinecraftServer world, ServerWorld serverWorld) {
		Path path = world.getSavePath(WorldSavePath.ROOT).resolve("data/" + MOD_ID + "-config.json");
		configManager = new ConfigManager(ConfigManager.getConfigFromFile(path), path);
	}
}