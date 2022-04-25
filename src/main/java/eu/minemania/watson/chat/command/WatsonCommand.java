package eu.minemania.watson.chat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import eu.minemania.watson.analysis.CoreProtectAnalysis;
import eu.minemania.watson.analysis.ServerTime;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.scheduler.SyncTaskQueue;
import eu.minemania.watson.scheduler.tasks.AddBlockEditTask;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;
import static net.minecraft.command.argument.BlockPosArgumentType.getBlockPos;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
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

public class WatsonCommand extends WatsonCommandBase
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ClientCommandManager.addClientSideCommand(Configs.Generic.WATSON_PREFIX.getStringValue());
        LiteralArgumentBuilder<ServerCommandSource> watson = literal(Configs.Generic.WATSON_PREFIX.getStringValue()).executes(WatsonCommand::help)
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
                .then(literal("tp")
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
                        .then(literal("region_info_timeout").executes(WatsonCommand::config_region_info_timeout)
                                .then(argument("seconds", doubleArg()).executes(WatsonCommand::config_region_info_timeout)))
                        .then(literal("billboard_background").executes(WatsonCommand::config_billb_background)
                                .then(argument("argb", integer()).executes(WatsonCommand::config_billb_background)))
                        .then(literal("billboard_foreground").executes(WatsonCommand::config_billb_foreground)
                                .then(argument("argb", integer()).executes(WatsonCommand::config_billb_foreground)))
                        .then(literal("group_ores_in_creative").executes(WatsonCommand::config_group_ores_creative)
                                .then(argument("enabled", bool()).executes(WatsonCommand::config_group_ores_creative)))
                        .then(literal("teleport_command").executes(WatsonCommand::config_teleport_command)
                                .then(argument("command", greedyString()).executes(WatsonCommand::config_teleport_command)))
                        .then(literal("chat_timeout").executes(WatsonCommand::config_chat_timeout)
                                .then(argument("seconds", doubleArg()).executes(WatsonCommand::config_chat_timeout)))
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
                        .then(literal("help").executes(WatsonCommand::help)))
                .then(literal("replay")
                        .then(argument("radius", integer(1))
                                .then(argument("speed", doubleArg(1))
                                        .then(argument("since", greedyString()).executes(WatsonCommand::replay))))
                        .then(literal("cancel").executes(WatsonCommand::cancelReplay)))
                .then(literal("dev")
                        .then(argument("pos", blockPos())
                                .then(argument("block", string())
                                        .then(argument("color", string())
                                                .then(argument("world", word()).executes(WatsonCommand::set_edit)))))
                        .then(literal("ledgerActions").executes(WatsonCommand::setLedgerActions)));
        dispatcher.register(watson);
    }

    private static int clear(CommandContext<ServerCommandSource> context)
    {
        DataManager.getEditSelection().clearBlockEditSet();
        CoreProtectAnalysis.reset();
        return 1;
    }

    private static int ratio(CommandContext<ServerCommandSource> context)
    {
        DataManager.getEditSelection().getBlockEditSet().getOreDB().showRatios();
        return 1;
    }

    private static int servertime(CommandContext<ServerCommandSource> context)
    {
        ServerTime.getInstance().queryServerTime(true);
        return 1;
    }

    private static int orePage(CommandContext<ServerCommandSource> context)
    {
        int page;
        try
        {
            page = getInteger(context, "page");
        }
        catch (Exception e)
        {
            page = 1;
        }
        DataManager.getEditSelection().getBlockEditSet().getOreDB().listDeposits(page);
        return 1;
    }

    private static int preCount(CommandContext<ServerCommandSource> context)
    {
        int count;
        try
        {
            count = getInteger(context, "count");
        }
        catch (Exception e)
        {
            count = Configs.Edits.PRE_COUNT.getIntegerValue();
        }
        DataManager.getEditSelection().queryPreEdits(count);
        return 1;
    }

    private static int postCount(CommandContext<ServerCommandSource> context)
    {
        int count;
        try
        {
            count = getInteger(context, "count");
        }
        catch (Exception e)
        {
            count = Configs.Edits.POST_COUNT.getIntegerValue();
        }
        DataManager.getEditSelection().queryPostEdits(count);
        return 1;
    }

    private static int display(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "displayed");
            Configs.Generic.DISPLAYED.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Generic.DISPLAYED.toggleBooleanValue();
            displayed = Configs.Generic.DISPLAYED.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.display", StringUtils.translate(strSetting));
        return 1;
    }

    private static int outline(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "displayed");
            Configs.Outlines.OUTLINE_SHOWN.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Outlines.OUTLINE_SHOWN.toggleBooleanValue();
            displayed = Configs.Outlines.OUTLINE_SHOWN.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.outline", StringUtils.translate(strSetting));
        return 1;
    }

    private static int anno(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "displayed");
            Configs.Generic.ANNOTATION_SHOWN.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Generic.ANNOTATION_SHOWN.toggleBooleanValue();
            displayed = Configs.Generic.ANNOTATION_SHOWN.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.anno", StringUtils.translate(strSetting));
        return 1;
    }

    private static int vector(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "displayed");
            Configs.Edits.VECTOR_SHOWN.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Edits.VECTOR_SHOWN.toggleBooleanValue();
            displayed = Configs.Edits.VECTOR_SHOWN.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector", StringUtils.translate(strSetting));
        return 1;
    }

    private static int vector_creat(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "displayed");
            Configs.Edits.LINKED_CREATION.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Edits.LINKED_CREATION.toggleBooleanValue();
            displayed = Configs.Edits.LINKED_CREATION.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector.creation", StringUtils.translate(strSetting));
        return 1;
    }

    private static int vector_destruct(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "displayed");
            Configs.Edits.LINKED_DESTRUCTION.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Edits.LINKED_DESTRUCTION.toggleBooleanValue();
            displayed = Configs.Edits.LINKED_DESTRUCTION.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector.destruction", StringUtils.translate(strSetting));
        return 1;
    }

    private static int vector_length(CommandContext<ServerCommandSource> context)
    {
        float length = getFloat(context, "length");
        Configs.Edits.VECTOR_LENGTH.setDoubleValue(length);
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector.length", length);
        return 1;
    }

    private static int label(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "displayed");
            Configs.Edits.LABEL_SHOWN.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Edits.LABEL_SHOWN.toggleBooleanValue();
            displayed = Configs.Edits.LABEL_SHOWN.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.label", StringUtils.translate(strSetting));
        return 1;
    }

    private static int teleport_next(CommandContext<ServerCommandSource> context)
    {
        DataManager.getEditSelection().getBlockEditSet().getOreDB().tpNext();
        return 1;
    }

    private static int teleport_prev(CommandContext<ServerCommandSource> context)
    {
        DataManager.getEditSelection().getBlockEditSet().getOreDB().tpPrev();
        return 1;
    }

    private static int teleport(CommandContext<ServerCommandSource> context)
    {
        int index = getInteger(context, "index");
        DataManager.getEditSelection().getBlockEditSet().getOreDB().tpIndex(index);
        return 1;
    }

    private static int edits_list(CommandContext<ServerCommandSource> context)
    {
        DataManager.getEditSelection().getBlockEditSet().listEdits();
        return 1;
    }

    private static int edits_hide(CommandContext<ServerCommandSource> context)
    {
        String players = getString(context, "player(s)");
        String[] playerList = players.split(" ");
        for (String player : playerList)
        {
            DataManager.getEditSelection().getBlockEditSet().setEditVisibility(player, false);
        }
        return 1;
    }

    private static int edits_show(CommandContext<ServerCommandSource> context)
    {
        String players = getString(context, "player(s)");
        String[] playerList = players.split(" ");
        for (String player : playerList)
        {
            DataManager.getEditSelection().getBlockEditSet().setEditVisibility(player, true);
        }
        return 1;
    }

    private static int edits_remove(CommandContext<ServerCommandSource> context)
    {
        String players = getString(context, "player(s)");
        String[] playerList = players.split(" ");
        for (String player : playerList)
        {
            DataManager.getEditSelection().getBlockEditSet().removeEdits(player);
        }
        return 1;
    }

    private static int set_edit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        BlockPos pos = getBlockPos(context, "pos");
        String block = getString(context, "block");
        String colorstr = getString(context, "color");
        String world = getString(context, "world");
        WatsonBlock watsonblock = WatsonBlockRegistery.getInstance().getWatsonBlockByName(block);
        int colorst = StringUtils.getColor(colorstr, 0);
        int colorTemp = MathHelper.clamp(colorst, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (colorTemp != 0)
        {
            Color4f color = Color4f.fromColor(colorTemp);
            watsonblock.setOverrideColor(color);
        }
        BlockEdit edit = new BlockEdit(1, "test edits", "test", pos.getX(), pos.getY(), pos.getZ(), watsonblock, world, 1);
        SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, true));

        return 1;
    }

    private static int setLedgerActions(CommandContext<ServerCommandSource> context)
    {
        if (!DataManager.getLedgerActions().isEmpty())
        {
            return 1;
        }
        List<String> ledgerActions = new ArrayList<>();
        ledgerActions.add("block-break");
        ledgerActions.add("block-place");
        ledgerActions.add("item-insert");
        ledgerActions.add("item-remove");
        ledgerActions.add("entity-killed");
        DataManager.setLedgerActions(ledgerActions);
        return 1;
    }

    private static int filter_list(CommandContext<ServerCommandSource> context)
    {
        DataManager.getFilters().list();
        return 1;
    }

    private static int filter_clear(CommandContext<ServerCommandSource> context)
    {
        DataManager.getFilters().clear();
        return 1;
    }

    private static int filter_add(CommandContext<ServerCommandSource> context)
    {
        String players = getString(context, "player(s)");
        String[] playerList = players.split(" ");
        for (String player : playerList)
        {
            DataManager.getFilters().addPlayer(player);
        }
        return 1;
    }

    private static int filter_remove(CommandContext<ServerCommandSource> context)
    {
        String players = getString(context, "player(s)");
        String[] playerList = players.split(" ");
        for (String player : playerList)
        {
            DataManager.getFilters().removePlayer(player);
        }
        return 1;
    }

    private static int file_list(CommandContext<ServerCommandSource> context)
    {
        String player;
        int page;
        try
        {
            player = getString(context, "player");
        }
        catch (Exception e)
        {
            player = null;
        }
        try
        {
            page = getInteger(context, "page");
        }
        catch (Exception e)
        {
            page = 1;
        }
        DataManager.listBlockEditFiles(Objects.requireNonNullElse(player, "*"), page);
        return 1;
    }

    private static int file_delete(CommandContext<ServerCommandSource> context)
    {
        String player, filename;
        try
        {
            player = getString(context, "player");
        }
        catch (Exception e)
        {
            player = null;
        }
        try
        {
            filename = getString(context, "filename");
        }
        catch (Exception e)
        {
            filename = null;
        }

        if (player != null)
        {
            DataManager.deleteBlockEditFiles(player);
        }
        else
        {
            DataManager.deleteBlockEditFiles(Objects.requireNonNullElse(filename, "*"));
        }
        return 1;
    }

    private static int file_expire(CommandContext<ServerCommandSource> context)
    {
        String date = getString(context, "YYYY-MM-DD");
        DataManager.expireBlockEditFiles(date);
        return 1;
    }

    private static int file_load(CommandContext<ServerCommandSource> context)
    {
        String player, filename;
        try
        {
            player = getString(context, "player");
        }
        catch (Exception e)
        {
            player = null;
        }
        try
        {
            filename = getString(context, "filename");
        }
        catch (Exception e)
        {
            filename = null;
        }

        if (player != null)
        {
            DataManager.loadBlockEditFile(player);
        }
        else if (filename != null)
        {
            DataManager.loadBlockEditFile(filename);
        }
        return 1;
    }

    private static int file_save(CommandContext<ServerCommandSource> context)
    {
        String filename;
        try
        {
            filename = getString(context, "filename");
        }
        catch (Exception e)
        {
            filename = null;
        }

        DataManager.saveBlockEditFile(filename);
        return 1;
    }

    private static int config_watson(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Generic.ENABLED.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Generic.ENABLED.toggleBooleanValue();
            displayed = Configs.Generic.ENABLED.getBooleanValue();
        }
        if (displayed)
        {
            InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.watson.enabled");
        }
        else
        {
            InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.watson.disabled", Configs.Generic.WATSON_PREFIX.getStringValue());
        }
        return 1;
    }

    private static int config_debug(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Generic.DEBUG.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Generic.DEBUG.toggleBooleanValue();
            displayed = Configs.Generic.DEBUG.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.debug", StringUtils.translate(strSetting));
        return 1;
    }

    private static int config_auto_page(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Plugin.AUTO_PAGE.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Plugin.AUTO_PAGE.toggleBooleanValue();
            displayed = Configs.Plugin.AUTO_PAGE.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.auto_page", StringUtils.translate(strSetting));
        return 1;
    }

    private static int config_region_info_timeout(CommandContext<ServerCommandSource> context)
    {
        double seconds;
        try
        {
            seconds = getDouble(context, "seconds");
            seconds = Math.abs(seconds);
            if (seconds < 1.0)
            {
                seconds = 1.0;
            }
            Configs.Plugin.REGION_INFO_TIMEOUT.setDoubleValue(seconds);
        }
        catch (Exception e)
        {
            seconds = Configs.Plugin.REGION_INFO_TIMEOUT.getDoubleValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.region_info_timeout", seconds);
        return 1;
    }

    private static int config_billb_background(CommandContext<ServerCommandSource> context)
    {
        int color;
        try
        {
            color = getInteger(context, "argb");
            Configs.Generic.BILLBOARD_BACKGROUND.setIntegerValue(color);
        }
        catch (Exception e)
        {
            color = Configs.Generic.BILLBOARD_BACKGROUND.getIntegerValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.billb_background", color);
        return 1;
    }

    private static int config_billb_foreground(CommandContext<ServerCommandSource> context)
    {
        int color;
        try
        {
            color = getInteger(context, "argb");
            Configs.Generic.BILLBOARD_FOREGROUND.setIntegerValue(color);
        }
        catch (Exception e)
        {
            color = Configs.Generic.BILLBOARD_FOREGROUND.getIntegerValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.billb_foreground", color);
        return 1;
    }

    private static int config_group_ores_creative(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Edits.GROUPING_ORES_IN_CREATIVE.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Edits.GROUPING_ORES_IN_CREATIVE.toggleBooleanValue();
            displayed = Configs.Edits.GROUPING_ORES_IN_CREATIVE.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.group_ores_creative", StringUtils.translate(strSetting));
        return 1;
    }

    private static int config_teleport_command(CommandContext<ServerCommandSource> context)
    {
        String command;
        try
        {
            command = getString(context, "command");
            Configs.Generic.TELEPORT_COMMAND.setValueFromString(command);
        }
        catch (Exception e)
        {
            command = Configs.Generic.TELEPORT_COMMAND.getStringValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.teleport_command", command);
        return 1;
    }

    private static int config_chat_timeout(CommandContext<ServerCommandSource> context)
    {
        double seconds;
        try
        {
            seconds = getDouble(context, "seconds");
            seconds = Math.abs(seconds);
            if (seconds < 0.0)
            {
                seconds = 0.0;
            }
            Configs.Generic.CHAT_TIMEOUT.setDoubleValue(seconds);
        }
        catch (Exception e)
        {
            seconds = Configs.Generic.CHAT_TIMEOUT.getDoubleValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.chat_timeout", seconds);
        return 1;
    }

    private static int config_max_auto_page(CommandContext<ServerCommandSource> context)
    {
        int pages;
        try
        {
            pages = getInteger(context, "pages");
            Configs.Plugin.MAX_AUTO_PAGES.setIntegerValue(pages);
        }
        catch (Exception e)
        {
            pages = Configs.Plugin.MAX_AUTO_PAGES.getIntegerValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.max_auto_page", pages);
        return 1;
    }

    private static int config_pre_count(CommandContext<ServerCommandSource> context)
    {
        int count;
        try
        {
            count = getInteger(context, "count");
            Configs.Edits.PRE_COUNT.setIntegerValue(count);
        }
        catch (Exception e)
        {
            count = Configs.Edits.PRE_COUNT.getIntegerValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.pre_count", count);
        return 1;
    }

    private static int config_post_count(CommandContext<ServerCommandSource> context)
    {
        int count;
        try
        {
            count = getInteger(context, "count");
            Configs.Edits.POST_COUNT.setIntegerValue(count);
        }
        catch (Exception e)
        {
            count = Configs.Edits.POST_COUNT.getIntegerValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.post_count", count);
        return 1;
    }

    private static int config_watson_prefix(CommandContext<ServerCommandSource> context)
    {
        String prefix;
        try
        {
            prefix = getString(context, "prefix");
            Configs.Generic.WATSON_PREFIX.setValueFromString(prefix);
        }
        catch (Exception e)
        {
            prefix = Configs.Generic.WATSON_PREFIX.getStringValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.watson.prefix", prefix);
        return 1;
    }

    private static int config_ss_player_directory(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Generic.SS_PLAYER_DIRECTORY.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Generic.SS_PLAYER_DIRECTORY.toggleBooleanValue();
            displayed = Configs.Generic.SS_PLAYER_DIRECTORY.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.ss_player_directory", StringUtils.translate(strSetting));
        return 1;
    }

    private static int config_ss_player_suffix(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Generic.SS_PLAYER_SUFFIX.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Generic.SS_PLAYER_SUFFIX.toggleBooleanValue();
            displayed = Configs.Generic.SS_PLAYER_SUFFIX.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.ss_player_suffix", StringUtils.translate(strSetting));
        return 1;
    }

    private static int config_ss_date_directory(CommandContext<ServerCommandSource> context)
    {
        String date_directory;
        try
        {
            date_directory = getString(context, "format");
            Configs.Generic.SS_DATE_DIRECTORY.setValueFromString(date_directory);
        }
        catch (Exception e)
        {
            date_directory = Configs.Generic.SS_DATE_DIRECTORY.getStringValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.ss_date_directory", date_directory);
        return 1;
    }

    private static int config_reformat_query(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Plugin.REFORMAT_QUERY_RESULTS.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Plugin.REFORMAT_QUERY_RESULTS.toggleBooleanValue();
            displayed = Configs.Plugin.REFORMAT_QUERY_RESULTS.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.reformat_query_results", StringUtils.translate(strSetting));
        return 1;
    }

    private static int config_recolor_query(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Plugin.RECOLOR_QUERY_RESULTS.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Plugin.RECOLOR_QUERY_RESULTS.toggleBooleanValue();
            displayed = Configs.Plugin.RECOLOR_QUERY_RESULTS.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.recolor_query_results", StringUtils.translate(strSetting));
        return 1;
    }

    private static int config_time_ordered(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Edits.TIME_ORDERED_DEPOSITS.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Edits.TIME_ORDERED_DEPOSITS.toggleBooleanValue();
            displayed = Configs.Edits.TIME_ORDERED_DEPOSITS.getBooleanValue();
        }
        if (displayed)
        {
            InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.time_ordered_deposits.enabled");
        }
        else
        {
            InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.time_ordered_deposits.disabled");
        }
        return 1;
    }

    private static int config_vector_length(CommandContext<ServerCommandSource> context)
    {
        float length;
        try
        {
            length = getFloat(context, "length");
            length = Math.max(0.0f, length);
            Configs.Edits.VECTOR_LENGTH.setDoubleValue(length);
        }
        catch (Exception e)
        {
            length = (float) Configs.Edits.VECTOR_LENGTH.getDoubleValue();
        }
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.vector.length", length);
        return 1;
    }

    private static int config_chat_highlights(CommandContext<ServerCommandSource> context)
    {
        boolean displayed;
        try
        {
            displayed = getBool(context, "enabled");
            Configs.Highlights.USE_CHAT_HIGHLIGHTS.setBooleanValue(displayed);
        }
        catch (Exception e)
        {
            Configs.Highlights.USE_CHAT_HIGHLIGHTS.toggleBooleanValue();
            displayed = Configs.Highlights.USE_CHAT_HIGHLIGHTS.getBooleanValue();
        }
        String strSetting = displayed ? "watson.message.setting.on" : "watson.message.setting.off";
        InfoUtils.showGuiOrInGameMessage(MessageType.INFO, "watson.message.config.chat_highlights", StringUtils.translate(strSetting));
        return 1;
    }

    private static int help(CommandContext<ServerCommandSource> context)
    {
        int cmdCount = 0;
        CommandDispatcher<ServerCommandSource> dispatcher = Command.commandDispatcher;
        for (CommandNode<ServerCommandSource> command : dispatcher.getRoot().getChildren())
        {
            String cmdName = command.getName();
            if (ClientCommandManager.isClientSideCommand(cmdName))
            {
                Map<CommandNode<ServerCommandSource>, String> usage = dispatcher.getSmartUsage(command, context.getSource());
                for (String u : usage.values())
                {
                    ClientCommandManager.sendFeedback(Text.literal("/" + cmdName + " " + u));
                }
                cmdCount += usage.size();
                if (usage.size() == 0)
                {
                    ClientCommandManager.sendFeedback(Text.literal("/" + cmdName));
                    cmdCount++;
                }
            }
        }
        return cmdCount;
    }

    private static int replay(CommandContext<ServerCommandSource> context)
    {
        String since = "";
        double speed = 0;
        int radius = 0;

        try
        {
            since = getString(context, "since");
            speed = getDouble(context, "speed");
            radius = getInteger(context, "radius");
        }
        catch (Exception e)
        {
            String error;
            if (since.isEmpty())
            {
                error = "since";
            }
            else if (speed == 0)
            {
                error = "speed";
            }
            else
            {
                error = "radius";
            }
            localErrorT(context.getSource(), "watson.error.command.replay." + error);
            return 0;
        }

        DataManager.getEditSelection().replay(since, speed, radius, context.getSource());
        return 1;
    }

    private static int cancelReplay(CommandContext<ServerCommandSource> context)
    {
        DataManager.getEditSelection().cancelReplay();
        return 1;
    }
}