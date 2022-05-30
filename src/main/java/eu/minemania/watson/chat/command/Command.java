package eu.minemania.watson.chat.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;

public class Command
{
    public static CommandDispatcher<ServerCommandSource> commandDispatcher;

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ClientCommandManager.clearClientSideCommands();
        WatsonCommand.register(dispatcher);
        AnnoCommand.register(dispatcher);
        CalcCommand.register(dispatcher);
        HighlightCommand.register(dispatcher);

        commandDispatcher = dispatcher;
    }

    public static void reregisterWatsonCommand(String newValue, String oldValue)
    {
        CommandDispatcher<ServerCommandSource> dispatcher = Command.commandDispatcher;
        ClientCommandManager.getClientSideCommands().remove(oldValue);
        CommandRemoval.removeCommand(dispatcher.getRoot(), oldValue);
        WatsonCommand.register(dispatcher);
    }
}