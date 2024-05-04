package net.configurable_regional_difficulty.majo24.config.selection;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;

public interface Selection {
    boolean containsChunk(ChunkPos chunk);
    String getSelection();
    void addSelectionToBuffer(PacketByteBuf buf);
}
