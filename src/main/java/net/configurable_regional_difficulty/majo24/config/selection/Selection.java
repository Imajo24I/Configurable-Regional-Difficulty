package net.configurable_regional_difficulty.majo24.config.selection;

import net.minecraft.util.math.ChunkPos;

public interface Selection {
    boolean containsChunk(ChunkPos chunk);
    String getSelection();
}
