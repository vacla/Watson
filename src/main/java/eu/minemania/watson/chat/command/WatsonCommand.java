package eu.minemania.watson.chat.command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;

import eu.minemania.watson.analysis.ServerTime;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentString;

import java.util.Map;

import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.Commands.argument;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;

public class WatsonCommand extends WatsonCommandBase {
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		ClientCommandManager.addClientSideCommand(Configs.Generic.WATSON_PREFIX.getStringValue());
		LiteralArgumentBuilder<CommandSource> watson = literal(Configs.Generic.WATSON_PREFIX.getStringValue()).executes(WatsonCommand::help)
				.then(literal("help").executes(WatsonCommand::help))
				.then(literal("clear").executes(WatsonCommand::clear))
				.then(literal("ratio").executes(WatsonCommand::ratio))
				.then(literal("servertime").executes(WatsonCommand::servertime))
				.then(literal("ore").executes(WatsonCommand::orePage)
					.then(argument("page", integer(1)).executes(WatsonCommand::orePage)))
				.then(literal("pre").executes(WatsonCommand::preCount)
					.then(argument("count", integer(1)).executes(WatsonCommand::preCount)))
				.then(literal("post").executes(WatsonCommand::postCount)
					.then(argument("count", integer(1)).executes(WatsonCommand::postCount)))
				.then(literal("display").executes(WatsonCommand::display)
					.then(argument("displayed", bool()).executes(WatsonCommand::display)))
				.then(literal("outline").executes(WatsonCommand::outline)
					.then(argument("displayed", bool()).executes(WatsonCommand::outline)))
				.then(literal("anno").executes(WatsonCommand::anno)
					.then(argument("displayed", bool()).executes(WatsonCommand::anno)))
				.then(literal("vector").executes(WatsonCommand::vector)
					.then(argument("displayed", bool()).executes(WatsonCommand::vector))
					.then(literal("creations").executes(WatsonCommand::vector_creat))
						.then(argument("displayed", bool()).executes(WatsonCommand::vector_creat))
					.then(literal("destructions").executes(WatsonCommand::vector_destruct)
						.then(argument("displayed", bool()).executes(WatsonCommand::vector_destruct)))
					.then(literal("length")
						.then(argument("length", floatArg()).executes(WatsonCommand::vector_length))))
				.then(literal("label").executes(WatsonCommand::label)
					.then(argument("displayed", bool()).executes(WatsonCommand::label)))
				.then(literal("tp").executes(WatsonCommand::teleport_next)
					.then(literal("next").executes(WatsonCommand::teleport_next))
					.then(literal("previous").executes(WatsonCommand::teleport_prev))
					.then(argument("index", integer()).executes(WatsonCommand::teleport)))
				.then(literal("edits").executes(WatsonCommand::edits_list)
					.then(literal("list").executes(WatsonCommand::edits_list))
					.then(literal("hide")
						.then(argument("player(s)", greedyString()).executes(WatsonCommand::edits_hide)))
					.then(literal("show")
						.then(argument("player(s)", greedyString()).executes(WatsonCommand::edits_show)))
					.then(literal("remove")
						.then(argument("player(s)", greedyString()).executes(WatsonCommand::edits_remove))))
				.then(literal("filter").executes(WatsonCommand::filter_list)
					.then(literal("list").executes(WatsonCommand::filter_list))
					.then(literal("clear").executes(WatsonCommand::filter_clear))
					.then(literal("add")
						.then(argument("player(s)", greedyString()).executes(WatsonCommand::filter_add)))
					.then(literal("remove")
						.then(argument("player(s)", greedyString()).executes(WatsonCommand::filter_remove))))
				.then(literal("file")
					.then(literal("list").executes(WatsonCommand::file_list)
						.then(literal("*").executes(WatsonCommand::file_list)
							.then(argument("page", integer(1)).executes(WatsonCommand::file_list)))
						.then(argument("player", word()).executes(WatsonCommand::file_list)
							.then(argument("page", integer(1)).executes(WatsonCommand::file_list))))
					.then(literal("delete")
						.then(literal("*").executes(WatsonCommand::file_delete))
						.then(argument("player", word()).executes(WatsonCommand::file_delete))
						.then(argument("filename", greedyString()).executes(WatsonCommand::file_delete)))
					.then(literal("expire")
						.then(argument("YYYY-MM-DD", greedyString()).executes(WatsonCommand::file_expire)))
					.then(literal("load")
						.then(argument("player", word()).executes(WatsonCommand::file_load))
						.then(argument("filename", greedyString()).executes(WatsonCommand::file_load)))
					.then(literal("save").executes(WatsonCommand::file_save)
						.then(argument("filename", greedyString()).executes(WatsonCommand::file_save))))
				.then(literal("config").executes(WatsonCommand::help)
					.then(literal("watson").executes(WatsonCommand::config_watson)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_watson)))
					.then(literal("debug").executes(WatsonCommand::config_debug)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_debug)))
					.then(literal("auto_page").executes(WatsonCommand::config_auto_page)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_auto_page)))
					.then(literal("region_info_timeout").executes(WatsonCommand::config_region_info_timeout) //get
						.then(argument("seconds", doubleArg()).executes(WatsonCommand::config_region_info_timeout))) //set
					.then(literal("billboard_background").executes(WatsonCommand::config_billb_background) //get
						.then(argument("argb", integer()).executes(WatsonCommand::config_billb_background))) //set
					.then(literal("billboard_foreground").executes(WatsonCommand::config_billb_foreground) //get
						.then(argument("argb", integer()).executes(WatsonCommand::config_billb_foreground))) //set
					.then(literal("group_ores_in_creative").executes(WatsonCommand::config_group_ores_creative)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_group_ores_creative)))
					.then(literal("teleport_command").executes(WatsonCommand::config_teleport_command)
						.then(argument("command", greedyString()).executes(WatsonCommand::config_teleport_command)))
					.then(literal("chat_timeout").executes(WatsonCommand::config_chat_timeout) //get
						.then(argument("seconds", doubleArg()).executes(WatsonCommand::config_chat_timeout))) //set
					.then(literal("max_auto_page").executes(WatsonCommand::config_max_auto_page)
						.then(argument("pages", integer(1)).executes(WatsonCommand::config_max_auto_page)))
					.then(literal("pre_count").executes(WatsonCommand::config_pre_count)
						.then(argument("count", integer(1)).executes(WatsonCommand::config_pre_count)))
					.then(literal("post_count").executes(WatsonCommand::config_post_count)
						.then(argument("count", integer(1)).executes(WatsonCommand::config_post_count)))
					.then(literal("watson_prefix").executes(WatsonCommand::config_watson_prefix)
						.then(argument("prefix", word()).executes(WatsonCommand::config_watson_prefix)))
					.then(literal("ss_player_directory").executes(WatsonCommand::config_ss_player_directory)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_ss_player_directory)))
					.then(literal("ss_player_suffix").executes(WatsonCommand::config_ss_player_suffix)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_ss_player_suffix)))
					.then(literal("ss_date_directory").executes(WatsonCommand::config_ss_date_directory)
						.then(argument("format", greedyString()).executes(WatsonCommand::config_ss_date_directory)))
					.then(literal("reformat_query_results").executes(WatsonCommand::config_reformat_query)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_reformat_query)))
					.then(literal("recolor_query_results").executes(WatsonCommand::config_recolor_query)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_recolor_query)))
					.then(literal("time_ordered_deposits").executes(WatsonCommand::config_time_ordered)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_time_ordered)))
					.then(literal("vector_length").executes(WatsonCommand::config_vector_length)
						.then(argument("length", floatArg(0)).executes(WatsonCommand::config_vector_length)))
					.then(literal("chat_highlights").executes(WatsonCommand::config_chat_highlights)
						.then(argument("enabled", bool()).executes(WatsonCommand::config_chat_highlights)))
					.then(literal("help").executes(WatsonCommand::help)));
		dispatcher.register(watson);
    }
	
	private static int clear(CommandContext<CommandSource> context) {
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
		DataManager.getEditSelection().queryPostEdits(count);
		return 1;
	}
	
	private static int display(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			Configs.Generic.DISPLAYED.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.DISPLAYED.toggleBooleanValue();
			displayed = Configs.Generic.DISPLAYED.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.display", displayed);
		return 1;
	}
	
	private static int outline(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			Configs.Generic.OUTLINE_SHOWN.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.OUTLINE_SHOWN.toggleBooleanValue();
			displayed = Configs.Generic.OUTLINE_SHOWN.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.outline", displayed);
		return 1;
	}
	
	private static int anno(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			Configs.Generic.ANNOTATION_SHOWN.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.ANNOTATION_SHOWN.toggleBooleanValue();
			displayed = Configs.Generic.ANNOTATION_SHOWN.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.anno", displayed);
		return 1;
	}
	
	private static int vector(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			Configs.Generic.VECTOR_SHOWN.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.VECTOR_SHOWN.toggleBooleanValue();
			displayed = Configs.Generic.VECTOR_SHOWN.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector", displayed);
		return 1;
	}
	
	private static int vector_creat(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			Configs.Generic.LINKED_CREATION.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.LINKED_CREATION.toggleBooleanValue();
			displayed = Configs.Generic.LINKED_CREATION.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector.creation", displayed);
		return 1;
	}
	
	private static int vector_destruct(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			Configs.Generic.LINKED_DESTRUCTION.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.LINKED_DESTRUCTION.toggleBooleanValue();
			displayed = Configs.Generic.LINKED_DESTRUCTION.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector.destruction", displayed);
		return 1;
	}
	
	private static int vector_length(CommandContext<CommandSource> context) {
		float length = getFloat(context, "length");
		Configs.Generic.VECTOR_LENGTH.setDoubleValue(length);
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector.length", length);
		return 1;
	}
	
	private static int label(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "displayed");
			Configs.Generic.LABEL_SHOWN.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.LABEL_SHOWN.toggleBooleanValue();
			displayed = Configs.Generic.LABEL_SHOWN.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.label", displayed);
		return 1;
	}
	
	private static int teleport_next(CommandContext<CommandSource> context) {
		DataManager.getEditSelection().getBlockEditSet().getOreDB().tpNext();
		return 1;
	}
	
	private static int teleport_prev(CommandContext<CommandSource> context) {
		DataManager.getEditSelection().getBlockEditSet().getOreDB().tpNext();
		return 1;
	}
	
	private static int teleport(CommandContext<CommandSource> context) {
		Integer index = getInteger(context, "index");
		DataManager.getEditSelection().getBlockEditSet().getOreDB().tpIndex(index);
		return 1;
	}
	
	private static int edits_list(CommandContext<CommandSource> context) {
		DataManager.getEditSelection().getBlockEditSet().listEdits();
		return 1;
	}
	
	private static int edits_hide(CommandContext<CommandSource> context) {
		String players = getString(context, "player(s)");
		String[] playerList = players.split(" ");
		for (String player : playerList) {
			DataManager.getEditSelection().getBlockEditSet().setEditVisibility(player, false);
		}
		return 1;
	}
	
	private static int edits_show(CommandContext<CommandSource> context) {
		String players = getString(context, "player(s)");
		String[] playerList = players.split(" ");
		for (String player : playerList) {
			DataManager.getEditSelection().getBlockEditSet().setEditVisibility(player, true);
		}
		return 1;
	}
	
	private static int edits_remove(CommandContext<CommandSource> context) {
		String players = getString(context, "player(s)");
		String[] playerList = players.split(" ");
		for (String player : playerList) {
			DataManager.getEditSelection().getBlockEditSet().removeEdits(player);
		}
		return 1;
	}
	
	private static int filter_list(CommandContext<CommandSource> context) {
		DataManager.getFilters().list();
		return 1;
	}
	
	private static int filter_clear(CommandContext<CommandSource> context) {
		DataManager.getFilters().clear();
		return 1;
	}
	
	private static int filter_add(CommandContext<CommandSource> context) {
		String players = getString(context, "player(s)");
		String[] playerList = players.split(" ");
		for (String player : playerList) {
			DataManager.getFilters().addPlayer(player);
		}
		return 1;
	}
	
	private static int filter_remove(CommandContext<CommandSource> context) {
		String players = getString(context, "player(s)");
		String[] playerList = players.split(" ");
		for (String player : playerList) {
			DataManager.getFilters().removePlayer(player);
		}
		return 1;
	}
	
	private static int file_list(CommandContext<CommandSource> context) {
		String player;
		int page;
		try {
			player = getString(context, "player");
		} catch (Exception e) {
			player = null;
		}
		try {
			page = getInteger(context, "page");
		} catch (Exception e) {
			page = 1;
		}
		if(player == null) {
			DataManager.listBlockEditFiles("*", page);
		} else {
			DataManager.listBlockEditFiles(player, page);
		}
		return 1;
	}
	
	private static int file_delete(CommandContext<CommandSource> context) {
		String player, filename;
		try {
			player = getString(context, "player");
		} catch (Exception e) {
			player = null;
		}
		try {
			filename = getString(context, "filename");
		} catch (Exception e) {
			filename = null;
		}
		
		if(player != null) {
			DataManager.deleteBlockEditFiles(player);
		} else if(filename != null) {
			DataManager.deleteBlockEditFiles(filename);
		} else {
			DataManager.deleteBlockEditFiles("*");
		}
		return 1;
	}
	
	private static int file_expire(CommandContext<CommandSource> context) {
		String date = getString(context, "YYYY-MM-DD");
		DataManager.expireBlockEditFiles(date);
		return 1;
	}
	
	private static int file_load(CommandContext<CommandSource> context) {
		String player, filename;
		try {
			player = getString(context, "player");
		} catch (Exception e) {
			player = null;
		}
		try {
			filename = getString(context, "filename");
		} catch (Exception e) {
			filename = null;
		}
		
		if(player != null) {
			DataManager.loadBlockEditFile(player);
		} else if(filename != null) {
			DataManager.loadBlockEditFile(filename);
		}
		return 1;
	}
	
	private static int file_save(CommandContext<CommandSource> context) {
		String filename;
		try {
			filename = getString(context, "filename");
		} catch (Exception e) {
			filename = null;
		}
		
		DataManager.saveBlockEditFile(filename);
		return 1;
	}
	
	private static int config_watson(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.ENABLED.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.ENABLED.toggleBooleanValue();
			displayed = Configs.Generic.ENABLED.getBooleanValue();
		}
		if(displayed) {
			InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.watson.enabled");
		} else {
			InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.watson.disabled", Configs.Generic.WATSON_PREFIX.getStringValue());
		}
		return 1;
	}
	
	private static int config_debug(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.DEBUG.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.DEBUG.toggleBooleanValue();
			displayed = Configs.Generic.DEBUG.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.debug", displayed);
		return 1;
	}
	
	private static int config_auto_page(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.AUTO_PAGE.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.AUTO_PAGE.toggleBooleanValue();
			displayed = Configs.Generic.AUTO_PAGE.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.auto_page", displayed);
		return 1;
	}
	
	private static int config_region_info_timeout(CommandContext<CommandSource> context) {
		double seconds;
		try {
			seconds = getDouble(context, "seconds");
			seconds = Math.abs(seconds);
			if(seconds < 1.0) {
				seconds = 1.0;
			}
			Configs.Generic.REGION_INFO_TIMEOUT.setDoubleValue(seconds);
		} catch (Exception e) {
			seconds = Configs.Generic.REGION_INFO_TIMEOUT.getDoubleValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.region_info_timeout", seconds);
		return 1;
	}
	
	private static int config_billb_background(CommandContext<CommandSource> context) {
		Integer color;
		try {
			color = getInteger(context, "argb");
			Configs.Generic.BILLBOARD_BACKGROUND.setIntegerValue(color);
		} catch (Exception e) {
			color = Configs.Generic.BILLBOARD_BACKGROUND.getIntegerValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.billb_background", color);
		return 1;
	}
	
	private static int config_billb_foreground(CommandContext<CommandSource> context) {
		Integer color;
		try {
			color = getInteger(context, "argb");
			Configs.Generic.BILLBOARD_FOREGROUND.setIntegerValue(color);
		} catch (Exception e) {
			color = Configs.Generic.BILLBOARD_FOREGROUND.getIntegerValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.billb_foreground", color);
		return 1;
	}
	
	private static int config_group_ores_creative(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.GROUPING_ORES_IN_CREATIVE.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.GROUPING_ORES_IN_CREATIVE.toggleBooleanValue();
			displayed = Configs.Generic.GROUPING_ORES_IN_CREATIVE.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.group_ores_creative", displayed);
		return 1;
	}
	
	private static int config_teleport_command(CommandContext<CommandSource> context) {
		String command;
		try {
			command = getString(context, "command");
			Configs.Generic.TELEPORT_COMMAND.setValueFromString(command);
		} catch (Exception e) {
			command = Configs.Generic.TELEPORT_COMMAND.getStringValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.teleport_command", command);
		return 1;
	}
	
	private static int config_chat_timeout(CommandContext<CommandSource> context) {
		double seconds;
		try {
			seconds = getDouble(context, "seconds");
			seconds = Math.abs(seconds);
			if(seconds < 0.0) {
				seconds = 0.0;
			}
			Configs.Generic.CHAT_TIMEOUT.setDoubleValue(seconds);
		} catch (Exception e) {
			seconds = Configs.Generic.CHAT_TIMEOUT.getDoubleValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.chat_timeout", seconds);
		return 1;
	}
	
	private static int config_max_auto_page(CommandContext<CommandSource> context) {
		Integer pages;
		try {
			pages = getInteger(context, "pages");
			Configs.Generic.MAX_AUTO_PAGES.setIntegerValue(pages);
		} catch (Exception e) {
			pages = Configs.Generic.MAX_AUTO_PAGES.getIntegerValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.max_auto_page", pages);
		return 1;
	}
	
	private static int config_pre_count(CommandContext<CommandSource> context) {
		Integer count;
		try {
			count = getInteger(context, "count");
			Configs.Generic.PRE_COUNT.setIntegerValue(count);
		} catch (Exception e) {
			count = Configs.Generic.PRE_COUNT.getIntegerValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.pre_count", count);
		return 1;
	}
	
	private static int config_post_count(CommandContext<CommandSource> context) {
		Integer count;
		try {
			count = getInteger(context, "count");
			Configs.Generic.POST_COUNT.setIntegerValue(count);
		} catch (Exception e) {
			count = Configs.Generic.POST_COUNT.getIntegerValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.post_count", count);
		return 1;
	}
	
	private static int config_watson_prefix(CommandContext<CommandSource> context) {
		String prefix;
		try {
			prefix = getString(context, "prefix");
			Configs.Generic.WATSON_PREFIX.setValueFromString(prefix);
		} catch (Exception e) {
			prefix = Configs.Generic.WATSON_PREFIX.getStringValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.watson_prefix", prefix);
		return 1;
	}
	
	private static int config_ss_player_directory(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.SS_PLAYER_DIRECTORY.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.SS_PLAYER_DIRECTORY.toggleBooleanValue();
			displayed = Configs.Generic.SS_PLAYER_DIRECTORY.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.ss_player_directory", displayed);
		return 1;
	}
	
	private static int config_ss_player_suffix(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.SS_PLAYER_SUFFIX.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.SS_PLAYER_SUFFIX.toggleBooleanValue();
			displayed = Configs.Generic.SS_PLAYER_SUFFIX.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.ss_player_suffix", displayed);
		return 1;
	}
	
	private static int config_ss_date_directory(CommandContext<CommandSource> context) {
		String date_directory;
		try {
			date_directory = getString(context, "format");
			Configs.Generic.SS_DATE_DIRECTORY.setValueFromString(date_directory);;
		} catch (Exception e) {
			date_directory = Configs.Generic.SS_DATE_DIRECTORY.getStringValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.ss_date_directory", date_directory);
		return 1;
	}
	
	private static int config_reformat_query(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.REFORMAT_QUERY_RESULTS.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.REFORMAT_QUERY_RESULTS.toggleBooleanValue();
			displayed = Configs.Generic.REFORMAT_QUERY_RESULTS.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.reformat_query_results", displayed);
		return 1;
	}
	
	private static int config_recolor_query(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.RECOLOR_QUERY_RESULTS.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.RECOLOR_QUERY_RESULTS.toggleBooleanValue();
			displayed = Configs.Generic.RECOLOR_QUERY_RESULTS.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.recolor_query_results", displayed);
		return 1;
	}
	
	private static int config_time_ordered(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.TIME_ORDERED_DEPOSITS.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.TIME_ORDERED_DEPOSITS.toggleBooleanValue();
			displayed = Configs.Generic.TIME_ORDERED_DEPOSITS.getBooleanValue();
		}
		if(displayed) {
			InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.time_ordered_deposits.enabled");
		} else {
			InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.time_ordered_deposits.disabled");
		}
		return 1;
	}
	
	private static int config_vector_length(CommandContext<CommandSource> context) {
		float length;
		try {
			length = getFloat(context, "length");
			length = Math.max(0.0f, length);
			Configs.Generic.VECTOR_LENGTH.setDoubleValue(length);
		} catch (Exception e) {
			length = (float) Configs.Generic.VECTOR_LENGTH.getDoubleValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector.length", length);
		return 1;
	}
	
	private static int config_chat_highlights(CommandContext<CommandSource> context) {
		boolean displayed;
		try {
			displayed = getBool(context, "enabled");
			Configs.Generic.USE_CHAT_HIGHLIGHTS.setBooleanValue(displayed);
		} catch (Exception e) {
			Configs.Generic.USE_CHAT_HIGHLIGHTS.toggleBooleanValue();
			displayed = Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue();
		}
		InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.chat_highlights", displayed);
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