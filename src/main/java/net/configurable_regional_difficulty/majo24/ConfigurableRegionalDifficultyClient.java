package net.configurable_regional_difficulty.majo24;

import net.configurable_regional_difficulty.majo24.config.Config;
import net.configurable_regional_difficulty.majo24.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.WorldSavePath;

public class ConfigurableRegionalDifficultyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NetworkingHandler.initClient();
        ClientPlayConnectionEvents.JOIN.register(ConfigurableRegionalDifficultyClient::initConfigManager);
    }

    public static void initConfigManager(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient server) {
        if (!server.isIntegratedServerRunning()) {
           ConfigurableRegionalDifficulty.configManager = new ConfigManager(new Config(), null);
            NetworkingHandler.requestSelectionList();
        } else {
            ConfigurableRegionalDifficulty.configManager = new ConfigManager(new Config(), server.getServer().getSavePath(WorldSavePath.ROOT).resolve("data/" + ConfigurableRegionalDifficulty.MOD_ID + "-config.json"));
            ConfigurableRegionalDifficulty.configManager.setHasReceivedSelectionList(true);
        }
    }
}
