package net.configurable_regional_difficulty.majo24.config.selection;

import net.minecraft.util.math.ChunkPos;

public class RectangleSelection implements Selection {
    public ChunkPos startingChunkPos;
    public ChunkPos endingChunkPos;

    public RectangleSelection(ChunkPos startingChunkPos, ChunkPos endingChunkPos) {
        this.startingChunkPos = startingChunkPos;
        this.endingChunkPos = endingChunkPos;
    }

    @Override
    public boolean containsChunk(ChunkPos chunk) {
        return ((chunk.x >= startingChunkPos.x && chunk.x <= endingChunkPos.x) && (chunk.z >= startingChunkPos.z && chunk.z <= endingChunkPos.z));
    }

    @Override
    public String getSelection() {
        return "Type: rectangle, from Chunk " + startingChunkPos.x +  " " + startingChunkPos.z + " to Chunk " + endingChunkPos.x + " " + endingChunkPos.z;
    }
}
