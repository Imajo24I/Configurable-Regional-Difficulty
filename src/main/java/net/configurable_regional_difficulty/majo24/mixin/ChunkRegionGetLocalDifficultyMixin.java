package net.configurable_regional_difficulty.majo24.mixin;

import net.configurable_regional_difficulty.majo24.ConfigurableRegionalDifficulty;
import net.configurable_regional_difficulty.majo24.config.selection.Selection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRegion.class)
public class ChunkRegionGetLocalDifficultyMixin {
    @Inject(method = "getLocalDifficulty", at = @At("RETURN"), cancellable = true)
    public void getLocalDifficulty(BlockPos pos, CallbackInfoReturnable<LocalDifficulty> cir) {
        ChunkPos chunkPos = new ChunkPos(pos);

        for (Selection selection : ConfigurableRegionalDifficulty.configManager.getSelectionList()) {
            if (selection.containsChunk(chunkPos)) {
                cir.setReturnValue(new LocalDifficulty(Difficulty.HARD, 1512000, 3600000, 1));
                break;
            }
        }
    }
}
