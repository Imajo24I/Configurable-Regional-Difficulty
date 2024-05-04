package net.configurable_regional_difficulty.majo24;

import net.configurable_regional_difficulty.majo24.config.selection.CircleSelection;
import net.configurable_regional_difficulty.majo24.config.selection.RectangleSelection;
import net.configurable_regional_difficulty.majo24.config.selection.Selection;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static net.configurable_regional_difficulty.majo24.ConfigurableRegionalDifficulty.configManager;

public class NetworkingHandler {
    public static final Identifier CRD_ADD_SELECTION_ID = new Identifier(ConfigurableRegionalDifficulty.MOD_ID, "crd_circle_selection");
    public static final Identifier CRD_REMOVE_SELECTION_ID = new Identifier(ConfigurableRegionalDifficulty.MOD_ID, "crd_remove_selection");
    public static final Identifier CRD_REQUEST_SELECTION_ID = new Identifier(ConfigurableRegionalDifficulty.MOD_ID, "crd_request_selection");
    public static final Identifier CRD_RECEIVED_REQUEST_FEEDBACK = new Identifier(ConfigurableRegionalDifficulty.MOD_ID, "crd_received_request_feedback");

    private NetworkingHandler() {}

    public static void initMain() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkingHandler.CRD_REQUEST_SELECTION_ID, ((server, player, handler, buf, responseSender) -> NetworkingHandler.sendSelections(player)));
    }

    public static void initClient() {
            ClientPlayNetworking.registerGlobalReceiver(NetworkingHandler.CRD_ADD_SELECTION_ID, (client, server, buf, responseSender) -> NetworkingHandler.addSelection(buf));
            ClientPlayNetworking.registerGlobalReceiver(NetworkingHandler.CRD_REMOVE_SELECTION_ID, (client, server, buf, responseSender) -> NetworkingHandler.removeSelection(buf));
            ClientPlayNetworking.registerGlobalReceiver(NetworkingHandler.CRD_RECEIVED_REQUEST_FEEDBACK, (client, server, buf, responseSender) -> configManager.receivedSelectionList = true);

    }

    public static void addSelection(PacketByteBuf buf) {
        if (buf.readString().equals("circle")) {
            configManager.addSelection(new CircleSelection(buf.readChunkPos(), buf.readInt()));
        } else {
            configManager.addSelection(new RectangleSelection(buf.readChunkPos(), buf.readChunkPos()));
        }
    }

    private static void removeSelection(PacketByteBuf buf) {
        if (configManager != null) {
            if (buf.readString().equals("circle")) {
                configManager.removeSelection(new CircleSelection(buf.readChunkPos(), buf.readInt()));
            } else {
                configManager.removeSelection(new RectangleSelection(buf.readChunkPos(), buf.readChunkPos()));
            }
        }
    }

    /** Send all Selections to a specific player*/
    public static void sendSelections(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, CRD_RECEIVED_REQUEST_FEEDBACK, PacketByteBufs.create());

		for (Selection selection : configManager.getSelectionList()) {
			PacketByteBuf buf = PacketByteBufs.create();
			selection.addSelectionToBuffer(buf);
            ServerPlayNetworking.send(player, CRD_ADD_SELECTION_ID, buf);
		}
	}

    /** Send a Selection to all players to remove */
    public static void sendRemoveSelection(Selection selection, MinecraftServer server) {
        if (!server.isRemote()) {return;}
        PacketByteBuf buf = PacketByteBufs.create();
        selection.addSelectionToBuffer(buf);
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, CRD_REMOVE_SELECTION_ID, buf);
        }
    }
    /** Send a Selection to all players to add */
    public static void sendAddSelection(Selection selection, MinecraftServer server) {
        if (!server.isRemote()) {return;}
        PacketByteBuf buf = PacketByteBufs.create();
        selection.addSelectionToBuffer(buf);
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, CRD_ADD_SELECTION_ID, buf);
        }
    }

    public static void requestSelectionList() {
        ClientPlayNetworking.send(CRD_REQUEST_SELECTION_ID, PacketByteBufs.empty());
    }
}
