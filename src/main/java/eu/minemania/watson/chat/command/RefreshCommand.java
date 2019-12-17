package eu.minemania.watson.chat.command;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import eu.minemania.watson.config.Configs;
import net.minecraft.server.command.ServerCommandSource;

public class RefreshCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		ClientCommandManager.addClientSideCommand("refresh");
		LiteralArgumentBuilder<ServerCommandSource> refresh = literal("refresh").executes(RefreshCommand::refresh);
		dispatcher.register(refresh);
	}

	private static int refresh(CommandContext<ServerCommandSource> context) {
		Command.reregisterWatsonCommand(Command.commandDispatcher, Configs.Generic.WATSON_PREFIX);
		return 1;
	}
}
