package eu.minemania.watson.chat.command;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;

import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.config.Configs;
import malilib.overlay.message.MessageDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class HighlightCommand extends WatsonCommandBase
{
    private static final SuggestionProvider<FabricClientCommandSource> SUGGESTION_COLOR = (p_201404_0_, p_201404_1_) -> CommandSource.suggestMatching(FakeCommandSource.getColor(), p_201404_1_);
    private static final SuggestionProvider<FabricClientCommandSource> SUGGESTION_STYLE = (p_201404_0_, p_201404_1_) -> CommandSource.suggestMatching(FakeCommandSource.getStyle(), p_201404_1_);

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        dispatcher.register(literal("highlight"));
        dispatcher.register(literal("highlight")
                .then(literal("list").executes(HighlightCommand::list))
                .then(literal("remove")
                        .then(argument("pattern", greedyString()).executes(HighlightCommand::remove)))
                .then(literal("add")
                        .then(argument("color", word()).suggests(SUGGESTION_COLOR)
                                .then(argument("style", word()).suggests(SUGGESTION_STYLE)
                                        .then(argument("pattern", greedyString()).executes(HighlightCommand::add))))));
    }

    private static int list(CommandContext<FabricClientCommandSource> context)
    {
        Highlight.listHighlights();
        return 1;
    }

    private static int remove(CommandContext<FabricClientCommandSource> context)
    {
        String pattern = getString(context, "pattern");
        Highlight.remove(pattern);
        return 1;
    }

    private static int add(CommandContext<FabricClientCommandSource> context)
    {
        String color = getString(context, "color");
        String style = getString(context, "style");
        String pattern = getString(context, "pattern");
        Highlight.add(pattern, color, style);
        return 1;
    }
}