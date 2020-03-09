package eu.minemania.watson.chat.command;

import com.mojang.brigadier.CommandDispatcher;
import fi.dy.masa.malilib.config.options.ConfigString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;

public class Command
{
	public static CommandDispatcher<ServerCommandSource> commandDispatcher;

	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		ClientCommandManager.clearClientSideCommands();
		WatsonCommand.register(dispatcher);
		RefreshCommand.register(dispatcher);
		AnnoCommand.register(dispatcher);
		CalcCommand.register(dispatcher);
		HighlightCommand.register(dispatcher);

		if (MinecraftClient.getInstance().isIntegratedServerRunning())
		{

		}

		commandDispatcher = dispatcher;
	}

	public static void reregisterWatsonCommand(CommandDispatcher<ServerCommandSource> dispatcher, ConfigString command)
	{
		ClientCommandManager.getClientSideCommands().remove(command.getOldStringValue());
		CommandRemoval.removeCommand(dispatcher.getRoot(), command.getOldStringValue());
		WatsonCommand.register(dispatcher);

		if (MinecraftClient.getInstance().isIntegratedServerRunning())
		{

		}
	}
}