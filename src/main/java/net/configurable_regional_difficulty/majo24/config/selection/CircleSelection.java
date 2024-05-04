package net.configurable_regional_difficulty.majo24.config.selection;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;

public class CircleSelection implements Selection {
    ChunkPos center;
    int radius;

    public CircleSelection(ChunkPos center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean containsChunk(ChunkPos chunk) {
        return Math.pow((double) chunk.x - center.x, 2) + Math.pow((double) chunk.z - center.z, 2) < Math.pow(radius, 2);
    }

    @Override
    public String getSelection() {
        return "Type: circle, Center chunk: " + center.x + " " + center.z + ", Radius: " + radius;
    }

    @Override
    public void addSelectionToBuffer(PacketByteBuf buf) {
        buf.writeString("circle");
        buf.writeChunkPos(center);
        buf.writeInt(radius);
    }
}
