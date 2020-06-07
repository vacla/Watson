package eu.minemania.watson.chat.command;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;

import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.config.Configs;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class HighlightCommand extends WatsonCommandBase
{
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_COLOR = (p_201404_0_, p_201404_1_) -> CommandSource.suggestMatching(FakeCommandSource.getColor(), p_201404_1_);
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_STYLE = (p_201404_0_, p_201404_1_) ->  CommandSource.suggestMatching(FakeCommandSource.getStyle(), p_201404_1_);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ClientCommandManager.addClientSideCommand("highlight");
        LiteralArgumentBuilder<ServerCommandSource> highlight = literal("highlight").executes(HighlightCommand::help)
                .then(literal("help").executes(HighlightCommand::help))
                .then(literal("list").executes(HighlightCommand::list))
                .then(literal("remove")
                        .then(argument("pattern", greedyString()).executes(HighlightCommand::remove)))
                .then(literal("add")
                        .then(argument("pattern", word())
                                .then(argument("color", word()).suggests(SUGGESTION_COLOR)
                                        .then(argument("style", word()).suggests(SUGGESTION_STYLE).executes(HighlightCommand::add)))));
        dispatcher.register(highlight);
    }

    private static int help(CommandContext<ServerCommandSource> context)
    {
        if(!Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue())
        {
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.disabled");
            return 1;
        }
        int cmdCount = 0;
        CommandDispatcher<ServerCommandSource> dispatcher = Command.commandDispatcher;
        for(CommandNode<ServerCommandSource> command : dispatcher.getRoot().getChildren())
        {
            String cmdName = command.getName();
            if(ClientCommandManager.isClientSideCommand(cmdName))
            {
                Map<CommandNode<ServerCommandSource>, String> usage = dispatcher.getSmartUsage(command, context.getSource());
                for(String u : usage.values())
                {
                    ClientCommandManager.sendFeedback(new LiteralText("/" + cmdName + " " + u));
                }
                cmdCount += usage.size();
                if(usage.size() == 0)
                {
                    ClientCommandManager.sendFeedback(new LiteralText("/" + cmdName));
                    cmdCount++;
                }
            }
        }
        return cmdCount;
    }

    private static int list(CommandContext<ServerCommandSource> context)
    {
        Highlight.listHighlights();
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> context)
    {
        String pattern = getString(context, "pattern");
        Highlight.remove(pattern);
        return 1;
    }

    private static int add(CommandContext<ServerCommandSource> context)
    {
        String pattern = getString(context, "pattern");
        String color = getString(context, "color");
        String style = getString(context, "style");
        Highlight.add(pattern, color, style);
        return 1;
    }
}