package eu.minemania.watson.chat.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class Command
{
    public static CommandDispatcher<FabricClientCommandSource> commandDispatcher;

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)
    {
        WatsonCommand.register(dispatcher);
        AnnoCommand.register(dispatcher);
        CalcCommand.register(dispatcher);
        HighlightCommand.register(dispatcher);

        commandDispatcher = dispatcher;
    }

    public static void reregisterWatsonCommand(String newValue, String oldValue)
    {
        CommandDispatcher<FabricClientCommandSource> dispatcher = Command.commandDispatcher;
        CommandRemoval.removeCommand(dispatcher.getRoot(), oldValue);
        WatsonCommand.register(dispatcher);
    }
}