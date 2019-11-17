package eu.minemania.watson.chat.command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;

import eu.minemania.watson.analysis.ServerTime;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentString;

import static net.minecraft.command.Commands.literal;

import java.util.Map;

import static net.minecraft.command.Commands.argument;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;

public class WatsonCommandCopy extends WatsonCommandBase {
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		ClientCommandManager.addClientSideCommand(Configs.Generic.WATSON_PREFIX.getStringValue());
		LiteralArgumentBuilder<CommandSource> watson = literal(Configs.Generic.WATSON_PREFIX.getStringValue()).executes(WatsonCommandCopy::help) //done
				.then(literal("help").executes(WatsonCommandCopy::help)) //TODO done
				.then(literal("clear").executes(WatsonCommandCopy::clear)) //TODO done
				.then(literal("ratio").executes(WatsonCommandCopy::ratio)) //TODO done
				.then(literal("servertime").executes(WatsonCommandCopy::servertime)) //TODO done
				.then(literal("ore").executes(WatsonCommandCopy::orePage) //TODO done
					.then(argument("page", integer(1)).executes(WatsonCommandCopy::orePage))) //TODO done
				.then(literal("pre").executes(WatsonCommandCopy::preCount) //TODO donee
					.then(argument("count", integer(1)).executes(WatsonCommandCopy::preCount))) //TODO done
				.then(literal("post").executes(WatsonCommandCopy::postCount) //TODO done
					.then(argument("count", integer(1)).executes(WatsonCommandCopy::postCount))) //TODO done
				.then(literal("display").executes(WatsonCommandCopy::display) //TODO done
					.then(argument("displayed", bool()).executes(WatsonCommandCopy::display))) //TODO done
				.then(literal("outline").executes(WatsonCommandCopy::outline) //TODO done
					.then(argument("displayed", bool()).executes(WatsonCommandCopy::outline))) //TODO done
				.then(literal("anno").executes(WatsonCommandCopy::anno) //TODO done
					.then(argument("displayed", bool()).executes(WatsonCommandCopy::anno))) //TODO done
				.then(literal("vector").executes(WatsonCommandCopy::vector) //TODO done
					.then(argument("displayed", bool()).executes(WatsonCommandCopy::vector)) //TODO done
					.then(literal("creations").executes(WatsonCommandCopy::vector)) //TODO done
						.then(argument("displayed", bool()).executes(WatsonCommandCopy::vector)) //TODO done
					.then(literal("destructions").executes(WatsonCommandCopy::vector) //TODO done
						.then(argument("displayed", bool()).executes(WatsonCommandCopy::vector))) //TODO done
					.then(literal("length") //TODO done
						.then(argument("length", floatArg()).executes(WatsonCommandCopy::vector)))) //TODO done
				.then(literal("label").executes(WatsonCommandCopy::label) //TODO done
					.then(argument("displayed", bool()).executes(WatsonCommandCopy::label))) //TODO done
				.then(literal("tp").executes(WatsonCommandCopy::teleport) //TODO done
					.then(literal("next").executes(WatsonCommandCopy::teleport)) //TODO done
					.then(literal("previous").executes(WatsonCommandCopy::teleport)) //TODO done
					.then(argument("index", integer()).executes(WatsonCommandCopy::teleport))) //TODO done
				.then(literal("edits").executes(WatsonCommandCopy::edits) //TODO done
					.then(literal("list").executes(WatsonCommandCopy::edits)) //TODO done
					.then(literal("hide") //TODO done
						.then(argument("player(s)", greedyString()).executes(WatsonCommandCopy::edits))) //TODO done
					.then(literal("show") //TODO done
						.then(argument("player(s)", greedyString()).executes(WatsonCommandCopy::edits))) //TODO done
					.then(literal("remove") //TODO done
						.then(argument("player(s)", greedyString()).executes(WatsonCommandCopy::edits)))) //TODO done
				.then(literal("filter").executes(WatsonCommandCopy::filter) //TODO done
					.then(literal("list").executes(WatsonCommandCopy::filter)) //TODO done
					.then(literal("clear").executes(WatsonCommandCopy::filter)) //TODO done
					.then(literal("add") //TODO done
						.then(argument("player(s)", greedyString()).executes(WatsonCommandCopy::filter))) //TODO done
					.then(literal("remove") //TODO done
						.then(argument("player(s)", greedyString()).executes(WatsonCommandCopy::filter)))) //TODO done
				.then(literal("file") //TODO done
					.then(literal("list").executes(WatsonCommandCopy::file) //gives * //TODO done
						.then(literal("*").executes(WatsonCommandCopy::file) //TODO done
							.then(argument("page", integer(1)).executes(WatsonCommandCopy::file))) //TODO done
						.then(argument("player", word()).executes(WatsonCommandCopy::file) //TODO done
							.then(argument("page", integer(1)).executes(WatsonCommandCopy::file)))) //TODO done
					.then(literal("delete") //TODO done
						.then(literal("*").executes(WatsonCommandCopy::file)) //TODO done
						.then(argument("player", word()).executes(WatsonCommandCopy::file)) //TODO done
						.then(argument("filename", greedyString()).executes(WatsonCommandCopy::file))) //TODO done
					.then(literal("expire") //TODO done
						.then(argument("YYYY-MM-DD", greedyString()).executes(WatsonCommandCopy::file))) //TODO done
					.then(literal("load") //TODO done
						.then(argument("player", word()).executes(WatsonCommandCopy::file)) //TODO done
						.then(argument("filename", greedyString()).executes(WatsonCommandCopy::file))) //TODO done
					.then(literal("save").executes(WatsonCommandCopy::file) //TODO done
						.then(argument("filename", greedyString()).executes(WatsonCommandCopy::file)))) //TODO done
				.then(literal("config").executes(WatsonCommandCopy::config) //TODO done
					.then(literal("watson").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("debug").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("auto_page").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("region_info_timeout").executes(WatsonCommandCopy::config) //get //TODO done
						.then(argument("seconds", doubleArg()).executes(WatsonCommandCopy::config))) //set //TODO done
					.then(literal("billboard_background").executes(WatsonCommandCopy::config) //get //TODO done
						.then(argument("argb", integer()).executes(WatsonCommandCopy::config))) //set //TODO done
					.then(literal("billboard_foreground").executes(WatsonCommandCopy::config) //get //TODO done
						.then(argument("argb", integer()).executes(WatsonCommandCopy::config))) //set //TODO done
					.then(literal("group_ores_in_creative").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("teleport_command").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("command", greedyString()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("chat_timeout").executes(WatsonCommandCopy::config) //get //TODO done
						.then(argument("seconds", doubleArg()).executes(WatsonCommandCopy::config))) //set //TODO done
					.then(literal("max_auto_page").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("pages", integer(1)).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("pre_count").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("count", integer(1)).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("post_count").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("count", integer(1)).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("watson_prefix").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("prefix", word()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("ss_player_directory").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("ss_player_prefix").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("ss_date_directory").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("format", greedyString()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("reformat_query_results").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("recolor_query_results").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("time_ordered_deposits").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("vector_length").executes(WatsonCommandCopy::config) //TODO done
						.then(argument("length", floatArg(0)).executes(WatsonCommandCopy::config))) //TODO done
					.then(literal("chat_highlights").executes(WatsonCommandCopy::config)
						.then(argument("enabled", bool()).executes(WatsonCommandCopy::config)))
					.then(literal("help").executes(WatsonCommandCopy::help)));
		//FIXME done
		dispatcher.register(watson);
    }
	
	/*@FunctionalInterface
    interface SupplierWithCommandSyntaxException<T> {
        T get() throws CommandSyntaxException;
    }

    private static <T> T tryGetArg(SupplierWithCommandSyntaxException<T> a, T value) throws CommandSyntaxException {
        try {
            return a.get();
        } catch (IllegalArgumentException e) {
            return value;
        }
    }
	
	/*private static int help(CommandContext<CommandSource> context) {
		String prefix = Configs.Generic.WATSON_PREFIX.getStringValue();
		localOutput(context.getSource(), "Usage:");
		localOutput(context.getSource(), " /" + prefix + " help");
		return 1;
	}*/
	
	private static int clear(CommandContext<CommandSource> context) {
		localOutput(context.getSource(), "cleared");
		DataManager.getEditSelection().clearBlockEditSet();
		return 1;
	}
	
	private static int ratio(CommandContext<CommandSource> context) {
		DataManager.getEditSelection().getBlockEditSet().getOreDB().showRatios();
		return 1;
	}
	
	private static int servertime(CommandContext<CommandSource> context) {
		ServerTime.getInstance().queryServerTime(true);
		return 1;
	}
	
	private static int orePage(CommandContext<CommandSource> context) {
		Integer page;
		try {
			page = getInteger(context, "page");
		} catch (Exception e) {
			page = 1;
		}
		System.out.println(page);
		DataManager.getEditSelection().getBlockEditSet().getOreDB().listDeposits(page);
		return 1;
	}
	
	private static int preCount(CommandContext<CommandSource> context) {
		Integer count;
		try {
			count = getInteger(context, "count");
		} catch (Exception e) {
			count = Configs.Generic.PRE_COUNT.getIntegerValue();
		}
		System.out.println(count);
		DataManager.getEditSelection().queryPreEdits(count);
		return 1;
	}
	
	private static int postCount(CommandContext<CommandSource> context) {
		Integer count;
		try {
			count = getInteger(context, "count");
		} catch (Exception e) {
			count = Configs.Generic.POST_COUNT.getIntegerValue();
		}
		System.out.println(count);
		DataManager.getEditSelection().queryPostEdits(count);
		return 1;
	}
	
	private static int display(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			System.out.println(displayed);
			Configs.Generic.DISPLAYED.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.DISPLAYED.toggleBooleanValue();
		}
		return 1;
	}
	
	private static int outline(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			System.out.println(displayed);
			Configs.Generic.OUTLINE_SHOWN.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.OUTLINE_SHOWN.toggleBooleanValue();
			System.out.println("toggled " + Configs.Generic.OUTLINE_SHOWN.getBooleanValue());
		}
		return 1;
	}
	
	private static int anno(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			System.out.println(displayed);
			Configs.Generic.ANNOTATION_SHOWN.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.ANNOTATION_SHOWN.toggleBooleanValue();
			System.out.println("toggled " + Configs.Generic.ANNOTATION_SHOWN.getBooleanValue());
		}
		return 1;
	}
	
	private static int vector(CommandContext<CommandSource> context) {
		
		return 1;
	}
	
	private static int label(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			System.out.println(displayed);
			Configs.Generic.LABEL_SHOWN.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.LABEL_SHOWN.toggleBooleanValue();
			System.out.println("toggled " + Configs.Generic.LABEL_SHOWN.getBooleanValue());
		}
		return 1;
	}
	
	private static int teleport(CommandContext<CommandSource> context) {
		return 1;
	}
	
	private static int edits(CommandContext<CommandSource> context) {
		return 1;
	}
	
	private static int filter(CommandContext<CommandSource> context) {
		return 1;
	}
	
	private static int file(CommandContext<CommandSource> context) {
		return 1;
	}
	
	private static int config(CommandContext<CommandSource> context) {
		return 1;
	}
	
	private static int help(CommandContext<CommandSource> context) {
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
}
