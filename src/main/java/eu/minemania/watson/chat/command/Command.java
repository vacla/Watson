package eu.minemania.watson.chat.command;

import com.mojang.brigadier.CommandDispatcher;

import eu.minemania.watson.interfaces.ICommandRemover;
import fi.dy.masa.malilib.config.options.ConfigString;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;

public class Command
{
    public static CommandDispatcher<CommandSource> commandDispatcher;

    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher)
    {
        ClientCommandManager.clearClientSideCommands();
        WatsonCommand.register(dispatcher);
        RefreshCommand.register(dispatcher);
        AnnoCommand.register(dispatcher);
        CalcCommand.register(dispatcher);
        HighlightCommand.register(dispatcher);

        if (Minecraft.getInstance().isIntegratedServerRunning())
        {

        }

        commandDispatcher = dispatcher;
    }

    public static void reregisterWatsonCommand(CommandDispatcher<CommandSource> dispatcher, ConfigString command)
    {
        ClientCommandManager.getClientSideCommands().remove(command.getOldStringValue());
        ((ICommandRemover) dispatcher.getRoot()).removeChild(command.getOldStringValue());
        WatsonCommand.register(dispatcher);

        if (Minecraft.getInstance().isIntegratedServerRunning())
        {

        }
    }
}