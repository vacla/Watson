package eu.minemania.watson.chat.command;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextComponentString;

public class HighlightCommand extends WatsonCommandBase {
	private static final SuggestionProvider<CommandSource> SUGGESTION_COLOR = (p_201404_0_, p_201404_1_) -> {
		return ISuggestionProvider.suggest(FakeCommandSource.getColor(), p_201404_1_);
	};
	private static final SuggestionProvider<CommandSource> SUGGESTION_STYLE = (p_201404_0_, p_201404_1_) -> {
		return ISuggestionProvider.suggest(FakeCommandSource.getStyle(), p_201404_1_);
	};

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		ClientCommandManager.addClientSideCommand("highlight");
		LiteralArgumentBuilder<CommandSource> highlight = literal("highlight").executes(HighlightCommand::help)
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

	private static int help(CommandContext<CommandSource> context) {
		if(!Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue()) {
			InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.disabled");
			return 1;
		}
		int cmdCount = 0;
		CommandDispatcher<CommandSource> dispatcher = Command.commandDispatcher;
		for(CommandNode<CommandSource> command : dispatcher.getRoot().getChildren()) {
			String cmdName = command.getName();
			if(ClientCommandManager.isClientSideCommand(cmdName)) {
				Map<CommandNode<CommandSource>, String> usage = dispatcher.getSmartUsage(command, context.getSource());
				for(String u : usage.values()) {
					ClientCommandManager.sendFeedback(new TextComponentString("/" + cmdName + " " + u));
				}
				cmdCount += usage.size();
				if(usage.size() == 0) {
					ClientCommandManager.sendFeedback(new TextComponentString("/" + cmdName));
					cmdCount++;
				}
			}
		}
		return cmdCount;
	}

	private static int list(CommandContext<CommandSource> context) {
		Highlight.listHighlights();
		return 1;
	}

	private static int remove(CommandContext<CommandSource> context) {
		String pattern = getString(context, "pattern");
		Highlight.remove(pattern);
		return 1;
	}

	private static int add(CommandContext<CommandSource> context) {
		String pattern = getString(context, "pattern");
		String color = getString(context, "color");
		String style = getString(context, "style");
		Highlight.add(pattern, color, style);
		return 1;
	}
}
