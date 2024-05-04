package net.configurable_regional_difficulty.majo24.config;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.configurable_regional_difficulty.majo24.NetworkingHandler;
import net.configurable_regional_difficulty.majo24.config.selection.CircleSelection;
import net.configurable_regional_difficulty.majo24.config.selection.RectangleSelection;
import net.configurable_regional_difficulty.majo24.config.selection.Selection;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

import static net.configurable_regional_difficulty.majo24.ConfigurableRegionalDifficulty.configManager;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
	private Commands() {}

	public static final Text describeCommandText = Text.literal(
		"""
		Using this command you can configure the Configurable Regional Difficulty Mod
		There are following arguments to this command, to configure this mod:
		- list: List all selections
		- add: Add a new selection
		- remove: Remove a selection""");
	public static final Text describeAddCommand = Text.literal("""
		Add a new Selection with this
		Please add the selection type and the selection as arguments to this command
		Selection Types:
		- circle: Everything outside of circle has max Regional Difficulty
		- rectangle: Everything outside of rectangle has max Regional Difficulty""");
	public static final Text describeRemoveCommand = Text.literal("""
		Remove a selection with this
		Please add the selection type and the selection as arguments to this command to remove the selection""");

    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

			final LiteralArgumentBuilder<ServerCommandSource> command = literal("crd")
					.requires(source -> environment.integrated || source.hasPermissionLevel(2))
					.executes(context -> {
						context.getSource().sendFeedback(() -> describeCommandText, false);
						return 1;
					})

					.then(literal("list").executes(Commands::listSelections))

					.then(literal("add").executes(context -> {
						context.getSource().sendFeedback(() -> describeAddCommand, false);
						return 1;
					})
							.then(literal("circle")
									.then(argument("Center chunk x", IntegerArgumentType.integer())
									.then(argument("Center chunk z", IntegerArgumentType.integer())
									.then(argument("Radius", IntegerArgumentType.integer())
									.executes(Commands::addCircleSelection)))))

							.then(literal("rectangle")
									.then(argument("Bottom left chunk x", IntegerArgumentType.integer())
									.then(argument("Bottom left chunk z", IntegerArgumentType.integer())
									.then(argument("Top right chunk x", IntegerArgumentType.integer())
									.then(argument("Top right chunk z", IntegerArgumentType.integer())
									.executes(Commands::addRectangleSelection)))))))

					.then(literal("remove").executes(context -> {
						context.getSource().sendFeedback(() -> describeRemoveCommand, false);
						return 1;
					})
							.then(literal("circle")
									.then(argument("Center chunk x", IntegerArgumentType.integer())
									.then(argument("Center chunk z", IntegerArgumentType.integer())
									.then(argument("Radius", IntegerArgumentType.integer())
									.executes(Commands::removeCircleSelection)))))

							.then(literal("rectangle")
									.then(argument("Bottom left chunk x", IntegerArgumentType.integer())
									.then(argument("Bottom left chunk z", IntegerArgumentType.integer())
									.then(argument("Top right chunk x", IntegerArgumentType.integer())
									.then(argument("Top right chunk z", IntegerArgumentType.integer())
									.executes(Commands::removeRectangleSelection)))))));

			dispatcher.register(command);
		});
    }

	public static int listSelections(CommandContext<ServerCommandSource> context) {
		for (Selection selection : configManager.getSelectionList()) {
			context.getSource().sendFeedback(() -> Text.literal("- " + selection.getSelection()), false);
		}
		return 1;
	}

	public static int addCircleSelection(CommandContext<ServerCommandSource> context) {
		int x = IntegerArgumentType.getInteger(context, "Center chunk x");
		int z = IntegerArgumentType.getInteger(context, "Center chunk z");
		int radius = IntegerArgumentType.getInteger(context, "Radius");
		CircleSelection selection = new CircleSelection(new ChunkPos(x, z), radius);

		configManager.addSelection(selection);
		context.getSource().sendFeedback(() -> Text.literal("Added circle selection at Chunk " + x + " " + z + " with radius " + radius), true);
		configManager.saveConfig();
		NetworkingHandler.sendAddSelection(selection, context.getSource().getServer());
		return 1;
	}

	private static int addRectangleSelection(CommandContext<ServerCommandSource> context) {
		int startChunkX = IntegerArgumentType.getInteger(context, "Bottom left chunk x");
		int startChunkZ = IntegerArgumentType.getInteger(context, "Bottom left chunk z");
		int endChunkX = IntegerArgumentType.getInteger(context, "Top right chunk x");
		int endChunkZ = IntegerArgumentType.getInteger(context, "Top right chunk z");
		RectangleSelection selection = new RectangleSelection(new ChunkPos(startChunkX, startChunkZ), new ChunkPos(endChunkX, endChunkZ));

		configManager.addSelection(selection);
		context.getSource().sendFeedback(() -> Text.literal("Added rectangle selection from Chunk " + startChunkX + " " + startChunkZ + " to Chunk " + endChunkX + " " + endChunkZ), true);
		configManager.saveConfig();
		NetworkingHandler.sendAddSelection(selection, context.getSource().getServer());
		return 1;
	}

	private static int removeCircleSelection(CommandContext<ServerCommandSource> context) {
		int x = IntegerArgumentType.getInteger(context, "Center chunk x");
		int z = IntegerArgumentType.getInteger(context, "Center chunk z");
		int radius = IntegerArgumentType.getInteger(context, "Radius");
		CircleSelection selection = new CircleSelection(new ChunkPos(x, z), radius);

		boolean successfullyRemoved = configManager.removeSelection(selection);
		if (successfullyRemoved) {
			context.getSource().sendFeedback(() -> Text.literal("Successfully removed circle selection at Chunk " + x + " " + z + " with radius " + radius), true);
			configManager.saveConfig();
			NetworkingHandler.sendRemoveSelection(selection, context.getSource().getServer());
		} else {
			context.getSource().sendFeedback(() -> Text.literal("Failed to remove circle selection at Chunk " + x + " " + z + " with radius " + radius + "\nMost likely caused by the selection not existing"), false);
		}
		return 1;
	}

	private static int removeRectangleSelection(CommandContext<ServerCommandSource> context) {
		int startChunkX = IntegerArgumentType.getInteger(context, "Bottom left chunk x");
		int startChunkZ = IntegerArgumentType.getInteger(context, "Bottom left chunk z");
		int endChunkX = IntegerArgumentType.getInteger(context, "Top right chunk x");
		int endChunkZ = IntegerArgumentType.getInteger(context, "Top right chunk z");
		RectangleSelection selection = new RectangleSelection(new ChunkPos(startChunkX, startChunkZ), new ChunkPos(endChunkX, endChunkZ));

		boolean successfullyRemoved = configManager.removeSelection(selection);
		if (successfullyRemoved) {
			context.getSource().sendFeedback(() -> Text.literal("Successfully removed rectangle selection from Chunk " + startChunkX + " " + startChunkZ + " to Chunk " + endChunkX + " " + endChunkZ), true);
			configManager.saveConfig();
			NetworkingHandler.sendRemoveSelection(selection, context.getSource().getServer());
		} else {
			context.getSource().sendFeedback(() -> Text.literal("Failed to remove rectangle selection from Chunk " + startChunkX + " " + startChunkZ + " to Chunk " + endChunkX + " " + endChunkZ + "\nMost likely caused by selection not existing"), false);
		}
		return 1;
	}
}
