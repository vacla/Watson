package eu.minemania.watson.config;

import eu.minemania.watson.data.Actions;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.config.IConfigHandler;

import java.io.File;
import java.util.*;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.minemania.watson.Reference;
import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.db.WatsonBlockRegistery;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class Configs implements IConfigHandler
{
    /**
     * Config file for mod.
     */
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    private static final Map<Item, String> DEFAULT_COLORS = new HashMap<>();

    /**
     * Default Generic configuration.
     */
    public static class Generic
    {
        public static final ConfigBoolean ACTION_REVERSE = new ConfigBoolean("actionReverse", false, "watson.description.config.action_reverse");
        public static final ConfigBoolean ANNOTATION_SHOWN = new ConfigBoolean("annotationShown", true, "watson.description.config.annotation_shown");
        public static final ConfigColor BILLBOARD_BACKGROUND = new ConfigColor("billboardBackground", "0xA8000000", "watson.description.config.billboard_background");
        public static final ConfigColor BILLBOARD_FOREGROUND = new ConfigColor("billboardForeground", "0x7FFFFFFF", "watson.description.config.billboard_foreground");
        public static final ConfigDouble CHAT_TIMEOUT = new ConfigDouble("chatTimeoutSeconds", 1, 0.1, 5, "watson.description.config.chat_timeout");
        public static final ConfigBoolean DEBUG = new ConfigBoolean("debugWatson", false, "watson.description.config.debug");
        public static final ConfigBoolean DISPLAYED = new ConfigBoolean("displayed", true, "watson.description.config.displayed");
        public static final ConfigBoolean ENABLED = new ConfigBoolean("enabled", true, "watson.description.config.enabled");
        public static final ConfigString SS_DATE_DIRECTORY = new ConfigString("ssDateDirectory", "yyyy-MM-dd HH:mm:ss", "watson.description.config.ss_date_directory");
        public static final ConfigBoolean SS_KEY_CUSTOM = new ConfigBoolean("ssKeyCustom", false, "watson.description.config.ss_key_custom");
        public static final ConfigBoolean SS_PLAYER_DIRECTORY = new ConfigBoolean("ssPlayerDirectory", true, "watson.description.config.ss_player_directory");
        public static final ConfigBoolean SS_PLAYER_SUFFIX = new ConfigBoolean("ssPlayerSuffix", true, "watson.description.config.ss_player_suffix");
        public static final ConfigString TELEPORT_COMMAND = new ConfigString("teleportCommand", "tppos {x:d} {y:d} {z:d} {world}", "watson.description.config.teleport_command");
        public static final ConfigString WATSON_PREFIX = new ConfigString("watsonPrefix", "watson", "watson.description.config.watson_prefix");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ACTION_REVERSE,
                ANNOTATION_SHOWN,
                BILLBOARD_BACKGROUND,
                BILLBOARD_FOREGROUND,
                CHAT_TIMEOUT,
                DEBUG,
                DISPLAYED,
                ENABLED,
                SS_DATE_DIRECTORY,
                SS_KEY_CUSTOM,
                SS_PLAYER_DIRECTORY,
                SS_PLAYER_SUFFIX,
                TELEPORT_COMMAND,
                WATSON_PREFIX
        );
    }

    public static class Messages
    {
        public static final ConfigBoolean DISABLE_CP_MESSAGES = new ConfigBoolean("disableCPMessages", false, "watson.description.config.coreprotect_messages");
        public static final ConfigBoolean DISABLE_JOIN_MESSAGES = new ConfigBoolean("disableJoinMessages", false, "watson.description.config.watson_join_messages");
        public static final ConfigBoolean DISABLE_LB_MESSAGES = new ConfigBoolean("disableLBMessages", false, "watson.description.config.logblock_messages");
        public static final ConfigBoolean DISABLE_PR_MESSAGES = new ConfigBoolean("disablePRMessages", false, "watson.description.config.prism_messages");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                DISABLE_CP_MESSAGES,
                DISABLE_JOIN_MESSAGES,
                DISABLE_LB_MESSAGES,
                DISABLE_PR_MESSAGES
        );
    }

    public static class Outlines
    {
        public static final ConfigBoolean ONLY_ORE_BLOCK = new ConfigBoolean("onlyOreBlock", false, "watson.description.config.only_ore_block");
        public static final ConfigInteger ORE_LINEWIDTH = new ConfigInteger("oreLinewidth", 3, 1, 10, "watson.description.config.ore_linewidth");
        public static final ConfigBoolean ORE_OUTLINE_THICKER = new ConfigBoolean("oreOutlineThicker", false, "watson.description.config.ore_outline_thicker");
        public static final ConfigBoolean OUTLINE_SHOWN = new ConfigBoolean("outlineshown", true, "watson.description.config.outline_shown");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ONLY_ORE_BLOCK,
                ORE_LINEWIDTH,
                ORE_OUTLINE_THICKER,
                OUTLINE_SHOWN
        );
    }

    public static class Plugin
    {
        public static final ConfigInteger AMOUNT_ROWS = new ConfigInteger("amountRowes", 5, "watson.description.config.amount_rows");
        public static final ConfigBoolean AUTO_PAGE = new ConfigBoolean("autopage", true, "watson.description.config.auto_page");
        public static final ConfigOptionList COREPROTECT_COMMAND = new ConfigOptionList("coreprotectCommand", CoreprotectCommand.CO, "watson.description.config.coreprotect_command");
        public static final ConfigInteger MAX_AUTO_PAGES = new ConfigInteger("maxAutoPages", 100, "watson.description.config.max_auto_pages");
        public static final ConfigInteger MAX_AUTO_PAGES_LOOP = new ConfigInteger("maxAutoPagesLoop", 100, "watson.description.config.max_auto_pages_loop");
        public static final ConfigInteger PAGE_LINES = new ConfigInteger("pagelines", 50, "watson.description.config.page_lines");
        public static final ConfigOptionList PLUGIN = new ConfigOptionList("plugin", Plugins.NULL, "watson.description.config.plugin");
        public static final ConfigBoolean RECOLOR_QUERY_RESULTS = new ConfigBoolean("recolourQueryResults", true, "watson.description.config.recolor_query_results");
        public static final ConfigBoolean REFORMAT_QUERY_RESULTS = new ConfigBoolean("reformatQueryResults", true, "watson.description.config.reformat_query_results");
        public static final ConfigDouble REGION_INFO_TIMEOUT = new ConfigDouble("regionInfoTimeoutSeconds", 5.0, 1.0, 9.0, "watson.description.config.region_info_timeout");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                AMOUNT_ROWS,
                AUTO_PAGE,
                COREPROTECT_COMMAND,
                MAX_AUTO_PAGES,
                MAX_AUTO_PAGES_LOOP,
                PAGE_LINES,
                PLUGIN,
                RECOLOR_QUERY_RESULTS,
                REFORMAT_QUERY_RESULTS,
                REGION_INFO_TIMEOUT
        );
    }

    public static class Highlights
    {
        public static final ConfigBoolean HIGHLIGHT_CASE_SENSITIVE = new ConfigBoolean("highlightCaseSensitive", true, "watson.description.config.highlight_case_sensitive");
        public static final ConfigString HIGHLIGHT_SOUND = new ConfigString("highlightSound", "", "watson.description.config.highlight_sound");
        public static final ConfigBoolean HIGHLIGHT_SOUND_ENABLE = new ConfigBoolean("highlightSoundEnable", false, "watson.description.config.highlight_sound_enable");
        public static final ConfigDouble HIGHLIGHT_SOUND_VOLUME = new ConfigDouble("highlightSoundVolume", 1, 0, 1, "watson.description.config.highlight_sound_volume");
        public static final ConfigBoolean USE_CHAT_HIGHLIGHTS = new ConfigBoolean("useChatHighlights", false, "watson.description.config.use_chat_highlights");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                HIGHLIGHT_CASE_SENSITIVE,
                HIGHLIGHT_SOUND,
                HIGHLIGHT_SOUND_ENABLE,
                HIGHLIGHT_SOUND_VOLUME,
                USE_CHAT_HIGHLIGHTS
        );
    }

    public static class Edits
    {
        public static final ConfigBoolean GROUPING_ORES_IN_CREATIVE = new ConfigBoolean("groupingOresInCreative", true, "watson.description.config.grouping_ores_in_creative");
        public static final ConfigBoolean LABEL_SHOWN = new ConfigBoolean("labelshown", true, "watson.description.config.label_shown");
        public static final ConfigBoolean LINKED_CREATION = new ConfigBoolean("linkedcreation", false, "watson.description.config.linked_creation");
        public static final ConfigBoolean LINKED_DESTRUCTION = new ConfigBoolean("linkeddestruction", false, "watson.description.config.linked_description");
        public static final ConfigInteger POST_COUNT = new ConfigInteger("postCount", 45, "watson.description.config.post_count");
        public static final ConfigInteger PRE_COUNT = new ConfigInteger("precount", 45, "watson.description.config.pre_count");
        public static final ConfigBoolean SELECTION_SHOWN = new ConfigBoolean("selectionShown", true, "watson.description.config.selection_shown");
        public static final ConfigBoolean TIME_ORDERED_DEPOSITS = new ConfigBoolean("timeOrderedDeposits", false, "watson.description.config.time_ordered_deposits");
        public static final ConfigDouble VECTOR_LENGTH = new ConfigDouble("vectorFloat", 4.0f, 0.0f, 10.0f, "watson.description.config.vector_length");
        public static final ConfigBoolean VECTOR_SHOWN = new ConfigBoolean("vectorShown", true, "watson.description.config.vector_shown");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                GROUPING_ORES_IN_CREATIVE,
                LABEL_SHOWN,
                LINKED_CREATION,
                LINKED_DESTRUCTION,
                POST_COUNT,
                PRE_COUNT,
                SELECTION_SHOWN,
                TIME_ORDERED_DEPOSITS,
                VECTOR_LENGTH,
                VECTOR_SHOWN
        );
    }

    /**
     * Default Lists configuration.
     */
    public static class Lists
    {
        public static final ConfigStringList HIGHLIGHT = new ConfigStringList("highlight", ImmutableList.of(), "watson.description.config.highlight");
        public static final ConfigStringList OVERRIDING_ACTIONS = new ConfigStringList("overriding actions", ImmutableList.of(), "watson.description.config.overriding_actions");
        public static final ConfigStringList SMALLER_RENDER_BOX = new ConfigStringList("Smaller render box", ImmutableList.of("minecraft:stone", "minecraft:gravel", "minecraft:dirt", "minecraft:diorite", "minecraft:sand", "minecraft:andesite", "minecraft:granite"), "watson.description.config.smaller_render_box");
        public static final ConfigStringList WATSON_BLOCKS = new ConfigStringList("watson blocks", setWatsonBlockData(), "watson.description.config.watson_blocks");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                HIGHLIGHT,
                OVERRIDING_ACTIONS,
                SMALLER_RENDER_BOX,
                WATSON_BLOCKS
        );
    }

    public static class Analysis
    {
        public static final ConfigStringExt CP_BUSY = new ConfigStringExt("cp busy", "^CoreProtect - Database busy. Please try again later.$", "watson.description.config.analysis").setCommentArgs("cp busy");
        public static final ConfigStringExt CP_DETAILS = new ConfigStringExt("cp details", "^(?:\\s+)?(\\d+[.,]\\d+\\/[mhd] ago|\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{2}:\\d{2}) [-+] #?(\\w+) ((?!.*logged).*?) ((?:x(\\d+) )?\\w+(?::\\w+)?)\\.$", "watson.description.config.analysis").setCommentArgs("cp details");
        public static final ConfigStringExt CP_DETAILS_SESSION = new ConfigStringExt("cp details session", "^(\\d+[.,]\\d+\\/[mhd] ago|\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{2}:\\d{2}) - (\\w+) (logged \\w+)\\.$", "watson.description.config.analysis").setCommentArgs("cp details session");
        public static final ConfigStringExt CP_DETAILS_SIGN = new ConfigStringExt("cp details sign", "^(\\d+[.,]\\d+\\/[mhd] ago|\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{2}:\\d{2}) - (\\w+): ([\\s\\w+\\W]+)", "watson.description.config.analysis").setCommentArgs("cp details sign");
        public static final ConfigStringExt CP_INSPECTOR_COORDS = new ConfigStringExt("cp inspector coords", "^-{5} \\w+(?:\\s\\w+)* -{5} \\(x(-?\\d+)\\/y(-?\\d+)\\/z(-?\\d+)\\)$", "watson.description.config.analysis").setCommentArgs("cp inspector coords");
        public static final ConfigStringExt CP_LOOKUP_COORDS = new ConfigStringExt("cp lookup coords", "^ +\\^ \\(x(-?\\d+)\\/y(-?\\d+)\\/z(-?\\d+)\\/([^\\)]+)\\)(?: \\(.+\\))?$", "watson.description.config.analysis").setCommentArgs("cp lookup coords");
        public static final ConfigStringExt CP_LOOKUP_HEADER = new ConfigStringExt("cp lookup header", "^----- CoreProtect Lookup Results -----$", "watson.description.config.analysis").setCommentArgs("cp lookup header");
        public static final ConfigStringExt CP_NO_RESULT = new ConfigStringExt("cp no result", "^CoreProtect - No results found.$", "watson.description.config.analysis").setCommentArgs("cp no result");
        public static final ConfigStringExt CP_PAGE = new ConfigStringExt("cp page", "^(?:.\\s)*Page (\\d+)\\/(\\d+) (?:.\\s)*", "watson.description.config.analysis").setCommentArgs("cp page");
        public static final ConfigStringExt CP_SEARCH = new ConfigStringExt("cp search", "^CoreProtect - Lookup searching. Please wait...$", "watson.description.config.analysis").setCommentArgs("cp search");
        public static final ConfigStringExt DUTYMODE_DISABLE = new ConfigStringExt("duty mode disable", "^\\[Duties\\] Duty mode disabled.*", "watson.description.config.analysis").setCommentArgs("duty mode disable");
        public static final ConfigStringExt DUTYMODE_ENABLE = new ConfigStringExt("duty mode enable", "^\\[Duties\\] Duty mode enabled.*", "watson.description.config.analysis").setCommentArgs("duty mode enable");
        public static final ConfigStringExt LB_POSITION = new ConfigStringExt("lb position", "^(?:[\\w ]+) in the last \\d+ \\w+ (?:at (-?\\d+):(-?\\d+):(-?\\d+) |within .+ blocks of location )?in (.+):$", "watson.description.config.analysis").setCommentArgs("lb position");
        public static final ConfigStringExt LB_DATA = new ConfigStringExt("lb data", "(?:\\((\\d+)\\) )?\\[((?:\\d{2,4}-)?\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})?] (\\w+) (\\w+\\s?\\w) ((?:(\\d+)x )?[A-Z_]+)(?:(?: with)? (\\w+[A-Z]))?(?:(?: to )? \\[(.*[^\\[\\]])] \\[(.*[^\\[\\]])] \\[(.*[^\\[\\]])] \\[(.*[^\\[\\]])])?(?: at (-?\\d+), (-?\\d+), (-?\\d+)| (?:from|into) \\w+)?(?: with (\\w+))?", "watson.description.config.analysis").setCommentArgs("lb data");
        public static final ConfigStringExt LB_TP = new ConfigStringExt("lb tp", "^Teleported to (-?\\d+):(\\d+):(-?\\d+)$", "watson.description.config.analysis").setCommentArgs("lb tp");
        public static final ConfigStringExt LB_PAGE = new ConfigStringExt("lb page", "^Page (\\d+)/(\\d+)$", "watson.description.config.analysis").setCommentArgs("lb page");
        public static final ConfigStringExt LB_HEADER_NO_RESULTS = new ConfigStringExt("lb header no results", "^No results found\\.$", "watson.description.config.analysis").setCommentArgs("lb header no results");
        public static final ConfigStringExt LB_HEADER_CHANGES = new ConfigStringExt("lb header changes", "^\\d+ changes? found\\.$", "watson.description.config.analysis").setCommentArgs("lb header changes");
        public static final ConfigStringExt LB_HEADER_BLOCKS = new ConfigStringExt("lb header blocks", "^\\d+ blocks? found\\.$", "watson.description.config.analysis").setCommentArgs("lb header blocks");
        public static final ConfigStringExt LB_HEADER_SUM_BLOCKS = new ConfigStringExt("lb header sum blocks", "^Created - Destroyed - Block$", "watson.description.config.analysis").setCommentArgs("lb header sum blocks");
        public static final ConfigStringExt LB_HEADER_SUM_PLAYERS = new ConfigStringExt("lb header sum players", "^Created - Destroyed - Player$", "watson.description.config.analysis").setCommentArgs("lb header sum players");
        public static final ConfigStringExt LB_HEADER_SEARCHING = new ConfigStringExt("lb header searching", "^Searching Block changes from player \\w+ in the last \\d+ minutes (?:within \\d+ blocks of you )?in .+:$", "watson.description.config.analysis").setCommentArgs("lb header searching");
        public static final ConfigStringExt LB_HEADER_RATIO = new ConfigStringExt("lb header ratio", "^STONE and DIAMOND_ORE changes from player \\w+ between (\\d+) and (\\d+) minutes ago in .+ summed up by blocks:$", "watson.description.config.analysis").setCommentArgs("lb header ratio");
        public static final ConfigStringExt LB_HEADER_RATIO_CURRENT = new ConfigStringExt("lb header ratio current", "^Stone and diamond ore changes from player \\w+ in the last (\\d+) minutes in .+ summed up by blocks:$", "watson.description.config.analysis").setCommentArgs("lb header ratio current");
        public static final ConfigStringExt LB_HEADER_TIME_CHECK = new ConfigStringExt("lb header time check", "Block changes from player \\w+ between (\\d+) and \\d+ minutes ago in .+:", "watson.description.config.analysis").setCommentArgs("lb header time check");
        public static final ConfigStringExt LB_HEADER_BLOCK = new ConfigStringExt("lb header block", "^(?!STONE and DIAMOND_ORE)(?: |,|\\w)+ from player \\w+ (?:in the last \\d+ minutes |between \\d+ and \\d+ minutes ago |more than -?\\d+ minutes ago )?(?:within \\d+ blocks of you )?in .+ summed up by (?:players|blocks):$", "watson.description.config.analysis").setCommentArgs("lb header block");
        public static final ConfigStringExt LB_SUM = new ConfigStringExt("lb sum", "^(\\d+)[ ]{6,}(\\d+)[ ]{6,}((?:\\w| )+)$", "watson.description.config.analysis").setCommentArgs("lb sum");
        public static final ConfigStringExt MODMODE_DISABLE = new ConfigStringExt("modmode disable", "^You are no longer in ModMode!$", "watson.description.config.analysis").setCommentArgs("modmode disable");
        public static final ConfigStringExt MODMODE_ENABLE = new ConfigStringExt("modmode enable", "^You are now in ModMode!$", "watson.description.config.analysis").setCommentArgs("modmode enable");
        public static final ConfigStringExt PRISM_DATA = new ConfigStringExt("prism data", ".*?[-+] \\[([0-9]+)\\]\\s+(?<instigator>.*) (?<cause>grew|killed|picked up|placed|grew|ignited|set a fire|used|threw potion|sheared|dispensed|blew up|formed|poured|broke|filled a|accessed|ate|(?:un)?leashed|launched|hung|wrote|entered|exited|removed|dropped|inserted|ran command|said|spawned|quit|joined)\\s+(?<target>.*)\\s+(?<when>just now|(?:\\d+d)?(?:\\d+h)?(?:\\d+m)?\\sago) \\(a:(?<action>.*)\\)\\s-\\d+- (?<date>\\d+\\/\\d+\\/\\d+) (?<time>\\d+:\\d+:\\d+\\w+) - (?<world>\\w+) @ (?<x>-?\\d+) (?<y>-?\\d+) (?<z>-?\\d+).*?", "watson.description.config.analysis").setCommentArgs("prism data");
        public static final ConfigStringExt PRISM_PAGE = new ConfigStringExt("prism page", "Showing\\s+\\d+ results\\. Page\\s+(?<current>\\d+) of\\s+(?<max>\\d+)", "watson.description.config.analysis").setCommentArgs("prism page");
        public static final ConfigStringExt PRISM_PAGINATION = new ConfigStringExt("prism pagination", "(?:\\s+\\[<< Prev] \\|)?\\s+\\[Next >>\\]", "watson.description.config.analysis").setCommentArgs("prism pagination");
        public static final ConfigStringExt WG_REGIONS = new ConfigStringExt("wg regions", "^Applicable regions: ([a-zA-Z0-9_-]+(?:, [a-zA-Z0-9_-]+)*)$", "watson.description.config.analysis").setCommentArgs("wg regions");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CP_BUSY,
                CP_DETAILS,
                CP_DETAILS_SESSION,
                CP_DETAILS_SIGN,
                CP_INSPECTOR_COORDS,
                CP_LOOKUP_COORDS,
                CP_LOOKUP_HEADER,
                CP_NO_RESULT,
                CP_PAGE,
                CP_SEARCH,
                DUTYMODE_DISABLE,
                DUTYMODE_ENABLE,
                LB_DATA,
                LB_HEADER_BLOCK,
                LB_HEADER_BLOCKS,
                LB_HEADER_CHANGES,
                LB_HEADER_NO_RESULTS,
                LB_HEADER_RATIO,
                LB_HEADER_RATIO_CURRENT,
                LB_HEADER_SEARCHING,
                LB_HEADER_SUM_BLOCKS,
                LB_HEADER_SUM_PLAYERS,
                LB_HEADER_TIME_CHECK,
                LB_PAGE,
                LB_POSITION,
                LB_SUM,
                LB_TP,
                MODMODE_DISABLE,
                MODMODE_ENABLE,
                PRISM_DATA,
                PRISM_PAGE,
                PRISM_PAGINATION,
                WG_REGIONS
        );
    }

    /**
     * Loads configurations from configuration file.
     */
    public static void loadFromFile()
    {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Messages", Messages.OPTIONS);
                ConfigUtils.readConfigBase(root, "Outlines", Outlines.OPTIONS);
                ConfigUtils.readConfigBase(root, "Plugin", Plugin.OPTIONS);
                ConfigUtils.readConfigBase(root, "Highlights", Highlights.OPTIONS);
                ConfigUtils.readConfigBase(root, "Edits", Edits.OPTIONS);
                ConfigUtils.readConfigBase(root, "Analysis", Analysis.OPTIONS);
                ConfigUtils.readConfigBase(root, "Lists", Lists.OPTIONS);
                ConfigUtils.readConfigBase(root, "Hotkeys", Hotkeys.HOTKEY_LIST);
            }
        }

        Highlight.setHighlightList(Lists.HIGHLIGHT.getStrings());
        WatsonBlockRegistery.setWatsonBlockList(Lists.WATSON_BLOCKS.getStrings());
        Actions.setActionsList(Lists.OVERRIDING_ACTIONS.getStrings());
    }

    private static ImmutableList<String> setWatsonBlockData()
    {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        ArrayList<String> list = new ArrayList<>();
        String color = "";

        for (String name : DataManager.getAllItemEntitiesStringIdentifiers())
        {
            Optional<Block> optionalBlock = Registries.BLOCK.getOrEmpty(new Identifier(name));
            Optional<Item> optionalItem = Registries.ITEM.getOrEmpty(optionalBlock.map(block -> Registries.ITEM.getId(block.asItem())).orElseGet(() -> new Identifier(name)));

            if (optionalItem.isEmpty())
            {
                color = setCustomColorOres(Registries.ENTITY_TYPE.get(new Identifier(name)));
            }
            if (color.isEmpty() && optionalItem.isPresent())
            {
                color = setCustomColorOres(optionalItem.get());
            }

            list.add(name + ";2;" + color);
            color = "";
        }

        builder.addAll(list);

        return builder.build();
    }

    private static String setCustomColorOres(Object object)
    {
        if (object instanceof EntityType)
        {
            return "#CC780E22";
        }
        if (DEFAULT_COLORS.isEmpty())
        {
            DEFAULT_COLORS.put(Items.DIAMOND_ORE, "#CC5DECF5");
            DEFAULT_COLORS.put(Items.IRON_ORE, "#CCE68C3F");
            DEFAULT_COLORS.put(Items.LAPIS_ORE, "#CC1846B2");
            DEFAULT_COLORS.put(Items.GOLD_ORE, "#CCFCEE4B");
            DEFAULT_COLORS.put(Items.REDSTONE_ORE, "#CCA00000");
            DEFAULT_COLORS.put(Items.COAL_ORE, "#CC191611");
            DEFAULT_COLORS.put(Items.EMERALD_ORE, "#CC17DD62");
            DEFAULT_COLORS.put(Items.NETHER_QUARTZ_ORE, "#CCEBE9E3");

            DEFAULT_COLORS.put(Items.ANCIENT_DEBRIS, "#CC332120");
            DEFAULT_COLORS.put(Items.GILDED_BLACKSTONE, "#CCFCEE4B");
            DEFAULT_COLORS.put(Items.NETHER_GOLD_ORE, "#CCFCEE4B");
            DEFAULT_COLORS.put(Items.COPPER_ORE, "#CCE48149");
            DEFAULT_COLORS.put(Items.DEEPSLATE_DIAMOND_ORE, "#CC5DECF5");
            DEFAULT_COLORS.put(Items.DEEPSLATE_EMERALD_ORE, "#CC17DD62");
            DEFAULT_COLORS.put(Items.DEEPSLATE_IRON_ORE, "#CCE68C3F");
            DEFAULT_COLORS.put(Items.DEEPSLATE_GOLD_ORE, "#CCFCEE4B");
            DEFAULT_COLORS.put(Items.DEEPSLATE_LAPIS_ORE, "#CC1846B2");
            DEFAULT_COLORS.put(Items.DEEPSLATE_REDSTONE_ORE, "#CCA00000");
            DEFAULT_COLORS.put(Items.DEEPSLATE_COAL_ORE, "#CC191611");
            DEFAULT_COLORS.put(Items.DEEPSLATE_COPPER_ORE, "#CCE48149");
            DEFAULT_COLORS.put(Items.SMALL_AMETHYST_BUD, "#CC6532B8");
            DEFAULT_COLORS.put(Items.MEDIUM_AMETHYST_BUD, "#CC6532B8");
            DEFAULT_COLORS.put(Items.LARGE_AMETHYST_BUD, "#CC6532B8");
            DEFAULT_COLORS.put(Items.AMETHYST_CLUSTER, "#CC6532B8");
        }
        return DEFAULT_COLORS.getOrDefault((Item) object, "#CC737373");
    }

    /**
     * Saves configurations to configuration file.
     */
    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Messages", Messages.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Outlines", Outlines.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Plugin", Plugin.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Highlights", Highlights.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Edits", Edits.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Analysis", Analysis.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Lists", Lists.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Hotkeys", Hotkeys.HOTKEY_LIST);

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load()
    {
        loadFromFile();
    }

    @Override
    public void save()
    {
        saveToFile();
    }
}