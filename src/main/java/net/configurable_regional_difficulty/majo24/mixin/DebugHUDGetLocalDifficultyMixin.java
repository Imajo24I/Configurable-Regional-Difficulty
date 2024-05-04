package net.configurable_regional_difficulty.majo24.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.configurable_regional_difficulty.majo24.ConfigurableRegionalDifficulty;
import net.configurable_regional_difficulty.majo24.NetworkingHandler;
import net.configurable_regional_difficulty.majo24.config.selection.Selection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(DebugHud.class)
public abstract class DebugHUDGetLocalDifficultyMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow protected abstract World getWorld();

    @WrapOperation(
            method = "getLeftText",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/Difficulty;JJF)Lnet/minecraft/world/LocalDifficulty;")
    )
    public LocalDifficulty testLocalDifficulty(Difficulty difficulty, long timeOfDay, long inhabitedTime, float moonSize, Operation<LocalDifficulty> original) {
        ChunkPos chunkPos = new ChunkPos(Objects.requireNonNull(client.getCameraEntity()).getBlockPos());

        if (!ConfigurableRegionalDifficulty.configManager.receivedSelectionList) {
             return original.call(difficulty, timeOfDay, inhabitedTime, moonSize);
        }

        for (Selection selection : ConfigurableRegionalDifficulty.configManager.getSelectionList()) {
            if (selection.containsChunk(chunkPos)) {
                return original.call(difficulty, timeOfDay, inhabitedTime, moonSize);
            }
        }
        return original.call(Difficulty.HARD, 1512000L, 3600000L, 1f);    }
}
