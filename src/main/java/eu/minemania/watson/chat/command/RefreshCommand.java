package eu.minemania.watson.chat.command;

import static net.minecraft.command.Commands.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import eu.minemania.watson.config.Configs;
import net.minecraft.command.CommandSource;

public class RefreshCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        ClientCommandManager.addClientSideCommand("refresh");
        LiteralArgumentBuilder<CommandSource> refresh = literal("refresh").executes(RefreshCommand::refresh);
        dispatcher.register(refresh);
    }

    private static int refresh(CommandContext<CommandSource> context)
    {
        Command.reregisterWatsonCommand(Command.commandDispatcher, Configs.Generic.WATSON_PREFIX);
        return 1;
    }
}