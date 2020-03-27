package eu.minemania.watson.config;

import fi.dy.masa.malilib.config.IConfigHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class Configs implements IConfigHandler
{
    /**
     * Config file for mod.
     */
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    private static final Map<Block, String> DEFAULT_COLORS = new HashMap<>();

    /**
     * Default Generic configuration.
     */
    public static class Generic
    {
        public static final ConfigBoolean ANNOTATION_SHOWN = new ConfigBoolean("annotationShown", true, "Shows annotation if enabled");
        public static final ConfigBoolean AUTO_PAGE = new ConfigBoolean("autopage", true, "Does automatic 3 pages at a time if enabled");
        public static final ConfigColor BILLBOARD_BACKGROUND = new ConfigColor("billboardBackground", "0xA8000000", "Background color of the annotations");
        public static final ConfigColor BILLBOARD_FOREGROUND = new ConfigColor("billboardForeground", "0x7FFFFFFF", "Foreground color of the annotations");
        public static final ConfigDouble CHAT_TIMEOUT = new ConfigDouble("chatTimeoutSeconds", 0.1, "The minimum amount of seconds between sent chat messages by the mod");
        public static final ConfigBoolean DEBUG = new ConfigBoolean("debugWatson", false, "enables debugging of Watson");
        public static final ConfigBoolean DISPLAYED = new ConfigBoolean("displayed", true, "If true, watson will draw stuff");
        public static final ConfigBoolean ENABLED = new ConfigBoolean("enabled", true, "Enables watson fully");
        public static final ConfigBoolean GROUPING_ORES_IN_CREATIVE = new ConfigBoolean("groupingOresInCreative", true, "Ores are grouped even in creative");
        public static final ConfigBoolean LABEL_SHOWN = new ConfigBoolean("labelshown", true, "Show ore deposit number label");
        public static final ConfigBoolean LINKED_CREATION = new ConfigBoolean("linkedcreation", false, "If true, block creations will be linked by vectors");
        public static final ConfigBoolean LINKED_DESTRUCTION = new ConfigBoolean("linkeddestruction", false, "If true, block destruction will be linked by vectors");
        public static final ConfigInteger MAX_AUTO_PAGES = new ConfigInteger("maxAutoPages", 10, "Amount of automatic stepped thru pages");
        public static final ConfigBoolean ONLY_ORE_BLOCK = new ConfigBoolean("onlyOreBlock", false, "Only shows block in block space");
        public static final ConfigInteger ORE_LINEWIDTH = new ConfigInteger("oreLinewidth", 3 , 1, 10, "Uses this linewidth for all ores if oreOutlineThicker is enabled");
        public static final ConfigBoolean ORE_OUTLINE_THICKER = new ConfigBoolean("oreOutlineThicker", false, "Ore outline thicker when enabled\nIf false, uses the integer for outline in watson blocks list config\nDefault line width: 1");
        public static final ConfigBoolean OUTLINE_SHOWN = new ConfigBoolean("outlineshown", true, "If true, wireframe outline will be displayed");
        public static final ConfigInteger PAGE_LINES = new ConfigInteger("pagelines", 50, "Number of chat lines in a page");
        public static final ConfigOptionList PLUGIN = new ConfigOptionList("plugin", Plugins.NULL, "which plugin does the server use");
        public static final ConfigInteger POST_COUNT = new ConfigInteger("postCount", 45, "Number of edits to be run post");
        public static final ConfigInteger PRE_COUNT = new ConfigInteger("precount", 45, "Number of edits to be fetched");
        public static final ConfigBoolean RECOLOR_QUERY_RESULTS = new ConfigBoolean("recolourQueryResults", true, "Recolour query results in chat");
        public static final ConfigBoolean REFORMAT_QUERY_RESULTS = new ConfigBoolean("reformatQueryResults", true, "Format query in chat more compact if enabled");
        public static final ConfigDouble REGION_INFO_TIMEOUT = new ConfigDouble("regionInfoTimeoutSeconds", 5.0, 1.0, 9.0, "Sets the timeout in seconds when right clicking with a wooden sword");
        public static final ConfigBoolean SELECTION_SHOWN = new ConfigBoolean("selectionShown", true, "If enabled selection will be shown");
        public static final ConfigString SS_DATE_DIRECTORY = new ConfigString("ssDateDirectory", "yyyy-MM-dd HH:mm:ss", "Format for the screenshot subdirectory");
        public static final ConfigBoolean SS_KEY_CUSTOM = new ConfigBoolean("ssKeyCustom", false, "Custom screenshot key");
        public static final ConfigBoolean SS_PLAYER_DIRECTORY = new ConfigBoolean("ssPlayerDirectory", true, "Subdirectory named after the currently selected player is created to hold screenshots of his edits if enabled");
        public static final ConfigBoolean SS_PLAYER_SUFFIX = new ConfigBoolean("ssPlayerSuffix", true, "Name of current selected player is appended to screenshot files");
        public static final ConfigString TELEPORT_COMMAND = new ConfigString("teleportCommand", "/tppos %d %d %d", "Sets teleport command\n%d for integers\n%g for decimal numbers");
        public static final ConfigBoolean TIME_ORDERED_DEPOSITS = new ConfigBoolean("timeOrderedDeposits", false, "If true, ore deposits should be numeric labeled when mined, when false it gets ordered in descending order of rareness of the ore");
        public static final ConfigBoolean USE_CHAT_HIGHLIGHTS = new ConfigBoolean("useChatHighlights", false, "If true, chat highlighter will be enabled");
        public static final ConfigDouble VECTOR_LENGTH = new ConfigDouble("vectorFloat", 4.0f, 4.0f, 10.0f, "The current displayed vector length");
        public static final ConfigBoolean VECTOR_SHOWN = new ConfigBoolean("vectorShown", true, "Shows vector if enabled");
        public static final ConfigString WATSON_PREFIX = new ConfigString("watsonPrefix", "watson", "The start of all Watson commands, without a slash");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ANNOTATION_SHOWN,
                AUTO_PAGE,
                BILLBOARD_BACKGROUND,
                BILLBOARD_FOREGROUND,
                CHAT_TIMEOUT,
                DEBUG,
                DISPLAYED,
                ENABLED,
                GROUPING_ORES_IN_CREATIVE,
                LABEL_SHOWN,
                LINKED_CREATION,
                LINKED_DESTRUCTION,
                MAX_AUTO_PAGES,
                ONLY_ORE_BLOCK,
                ORE_LINEWIDTH,
                ORE_OUTLINE_THICKER,
                OUTLINE_SHOWN,
                PAGE_LINES,
                PLUGIN,
                POST_COUNT,
                PRE_COUNT,
                RECOLOR_QUERY_RESULTS,
                REFORMAT_QUERY_RESULTS,
                REGION_INFO_TIMEOUT,
                SELECTION_SHOWN,
                SS_DATE_DIRECTORY,
                SS_KEY_CUSTOM,
                SS_PLAYER_DIRECTORY,
                SS_PLAYER_SUFFIX,
                TELEPORT_COMMAND,
                TIME_ORDERED_DEPOSITS,
                USE_CHAT_HIGHLIGHTS,
                VECTOR_LENGTH,
                VECTOR_SHOWN,
                WATSON_PREFIX
                );
    }

    /**
     * Default Lists configuration.
     */
    public static class Lists
    {
        public static final ConfigStringList HIGHLIGHT = new ConfigStringList("highlight", ImmutableList.of(), "What gets highlighted in chat");
        public static final ConfigStringList SMALLER_RENDER_BOX = new ConfigStringList("Smaller render box", ImmutableList.of("minecraft:stone", "minecraft:gravel", "minecraft:dirt", "minecraft:diorite", "minecraft:sand", "minecraft:andesite", "minecraft:granite"), "blocks will have a smaller rendering box");
        public static final ConfigStringList WATSON_BLOCKS = new ConfigStringList("watson blocks", setWatsonBlockData(), "Watson blocks");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                HIGHLIGHT,
                SMALLER_RENDER_BOX,
                WATSON_BLOCKS
                );
    }

    public static class Analysis
    {
        public static final ConfigString CP_DETAILS = new ConfigString("cp details", "^(\\d+[.,]\\d+\\/h ago|\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{2}:\\d{2}) - (#?\\w+) (\\w+) (\\w+).+", "Changes regex for cp details");
        public static final ConfigString CP_INSPECTOR_COORDS = new ConfigString("cp inspector coords", "^----- CoreProtect ----- \\(x(-?\\d+)\\/y(\\d+)\\/z(-?\\d+)\\)$", "Changes regex for cp inspector coords");
        public static final ConfigString CP_LOOKUP_COORDS = new ConfigString("cp lookup coords", "^ +\\^ \\(x(-?\\d+)\\/y(\\d+)\\/z(-?\\d+)\\/(.+)\\)$", "Changes regex for cp lookup coords");
        public static final ConfigString CP_LOOKUP_HEADER = new ConfigString("cp lookup header", "^----- CoreProtect Lookup Results -----$", "Changes regex for cp lookup header");
        public static final ConfigString DUTYMODE_DISABLE = new ConfigString("duty mode disable", "^\\[Duties\\] Duty mode disabled.*", "Changes regex for disable duty mode");
        public static final ConfigString DUTYMODE_ENABLE = new ConfigString("duty mode enable", "^\\[Duties\\] Duty mode enabled.*", "Changes regex for enable duty mode");
        public static final ConfigString LB_POSITION = new ConfigString("lb position", "^Block changes in the last \\d+ \\w+ at (-?\\d+):(-?\\d+):(-?\\d+) in (.+):$", "Changes regex for position text");
        public static final ConfigString LB_KILLS = new ConfigString("lb kills", "^Kills in the last \\d+ \\w+ in (.+):$", "Changes regex for kills text");
        public static final ConfigString LB_COORD_POSITION = new ConfigString("lb coord position", "^Block changes in the last \\d+ \\w+ in (.+):$", "Changes regex for coords position text");
        public static final ConfigString LB_EDIT = new ConfigString("lb edit", "^\\[((?:\\d{2,4}-)?\\d{2}-\\d{2}) (\\d{2}):(\\d{2})(?::?)(\\d{2})?\\] (\\w+) (created|destroyed) ((?: |\\w)+)( \\[.*\\] \\[.*\\\\] \\[.*\\] \\[.*\\])?$", "Changes regex for edits text");
        public static final ConfigString LB_EDIT_REPLACED = new ConfigString("lb edit replaced", "^\\[((?:\\d{2,4}-)?\\d{2}-\\d{2}) (\\d{2}):(\\d{2})(?::?)(\\d{2})?\\] (\\w+) replaced ((?: |\\w)+) with ((?: |\\w)+)$", "Changes regex for edits replaced text");
        public static final ConfigString LB_COORD = new ConfigString("lb coord", "^\\((\\d+)\\) \\[((?:\\d{2,4}-)?\\d{2}-\\d{2}) (\\d{2}):(\\d{2})(?::?)(\\d{2})?\\] (\\w+) (created|destroyed) ([a-zA-Z_]+)(?: \\[(?<sign1>.*)\\] \\[(?<sign2>.*)\\] \\[(?<sign3>.*)\\] \\[(?<sign4>.*)\\])? at (-?\\d+), (\\d+), (-?\\d+)$", "Changes regex for coords text");
        public static final ConfigString LB_COORD_KILLS = new ConfigString("lb coord kills", "^\\((\\d+)\\) \\[((?:\\\\d{2,4}-)?\\d{2}-\\d{2}) (\\d{2}):(\\d{2})(?::?)(\\d{2})?\\] (\\w+) killed (\\w+) at (-?\\d+):(\\d+):(-?\\d+) with (.*)$", "Changes regex for coords kills text");
        public static final ConfigString LB_COORD_REPLACED = new ConfigString("lb coord replaced", "^\\((\\d+)\\) ((?:\\d{2,4}-)?\\d{2}-\\d{2}) (\\d{2}):(\\d{2})(?::?)(\\d{2})?\\] (\\w+) replaced ([a-zA-Z_]+) with ([a-zA-Z_]+) at (-?\\d+):(\\d+):(-?\\d+)$", "Changes regex for coords replaced text");
        public static final ConfigString LB_TP = new ConfigString("lb tp", "^Teleported to (-?\\d+):(\\d+):(-?\\d+)$", "Changes regex for teleport text");
        public static final ConfigString LB_PAGE = new ConfigString("lb page", "^Page (\\d+)/(\\d+)$", "Changes regex for page text");
        public static final ConfigString LB_HEADER_NO_RESULTS = new ConfigString("lb header no results", "^No results found\\.$", "Changes regex for header no results");
        public static final ConfigString LB_HEADER_CHANGES = new ConfigString("lb header changes", "^\\d+ changes? found\\.$", "Changes regex for header changes");
        public static final ConfigString LB_HEADER_BLOCKS = new ConfigString("lb header blocks", "^\\d+ blocks? found\\.$", "Changes regex for header blocks");
        public static final ConfigString LB_HEADER_SUM_BLOCKS = new ConfigString("lb header sum blocks", "^Created - Destroyed - Block$", "Changes regex for header sum blocks");
        public static final ConfigString LB_HEADER_SUM_PLAYERS = new ConfigString("lb header sum players", "^Created - Destroyed - Player$", "Changes regex for header sum players");
        public static final ConfigString LB_HEADER_SEARCHING = new ConfigString("lb header searching", "^Searching Block changes from player \\w+ in the last \\d+ minutes (?:within \\d+ blocks of you )?in .+:$", "Changes regex for header searching");
        public static final ConfigString LB_HEADER_RATIO = new ConfigString("lb header ratio", "^Stone and diamond ore changes from player \\w+ between (\\d+) and (\\d+) minutes ago in .+ summed up by blocks:$", "Changes regex for header ratio");
        public static final ConfigString LB_HEADER_RATIO_CURRENT = new ConfigString("lb header ratio current", "^Stone and diamond ore changes from player \\w+ in the last (\\d+) minutes in .+ summed up by blocks:$", "Changes regex for header ratio current");
        public static final ConfigString LB_HEADER_TIME_CHECK = new ConfigString("lb header time check", "Block changes from player \\w+ between (\\d+) and \\d+ minutes ago in .+:", "Changes regex for header time check");
        public static final ConfigString LB_HEADER_BLOCK = new ConfigString("lb header block", "^(?: |,|\\w)+ (?:destructions|changes) from player \\w+ (?:in the last \\d+ minutes |between \\d+ and \\d+ minutes ago |more than -?\\d+ minutes ago )?(?:within \\d+ blocks of you )?in .+(?: summed up by (players|blocks))?:$", "Changes regex for header block");
        public static final ConfigString LB_SUM = new ConfigString("lb sum", "^(\\d+)[ ]{6,}(\\d+)[ ]{6,}((?:\\w| )+)$", "Changes regex for header sum");
        public static final ConfigString MODMODE_DISABLE = new ConfigString("modmode disable", "^You are no longer in ModMode!$", "Changes regex modmode disable");
        public static final ConfigString MODMODE_ENABLE = new ConfigString("modmode enable", "^You are now in ModMode!$", "Changes regex modmode enable");
        public static final ConfigString WG_REGIONS = new ConfigString("wg regions", "^Applicable regions: ([a-zA-Z0-9_-]+(?:, [a-zA-Z0-9_-]+)*)$", "Changes regex wg regions");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                LB_COORD,
                LB_COORD_KILLS,
                LB_COORD_POSITION,
                LB_COORD_REPLACED,
                LB_EDIT,
                LB_EDIT_REPLACED,
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
                LB_KILLS,
                LB_PAGE,
                LB_POSITION,
                LB_SUM,
                LB_TP
                );
    }

    /**
     * Loads configurations from configuration file.
     */
    public static void loadFromFile()
    {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if(configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if(element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Analysis", Analysis.OPTIONS);
                ConfigUtils.readConfigBase(root, "Lists", Lists.OPTIONS);
                ConfigUtils.readConfigBase(root, "Hotkeys", Hotkeys.HOTKEY_LIST);
            }
        }

        Highlight.setHighlightList(Lists.HIGHLIGHT.getStrings());
        WatsonBlockRegistery.setWatsonBlockList(Lists.WATSON_BLOCKS.getStrings());
    }

    private static ImmutableList<String> setWatsonBlockData()
    {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        Registry.BLOCK.forEach((block) -> builder.add(Registry.BLOCK.getId(block).getNamespace() + ":" + Registry.BLOCK.getId(block).getPath() + ";1;"+ setCustomColorOres(block)));
        Registry.ENTITY_TYPE.forEach((type) -> builder.add(Registry.ENTITY_TYPE.getId(type).getNamespace() + ":" + Registry.ENTITY_TYPE.getId(type).getPath() + ";1;#CC780E22"));
        return builder.build();
    }

    private static String setCustomColorOres(Block block)
    {
        if(DEFAULT_COLORS.isEmpty())
        {
            DEFAULT_COLORS.put(Blocks.DIAMOND_ORE, "#CC5DECF5");
            DEFAULT_COLORS.put(Blocks.IRON_ORE, "#CCE68C3F");
            DEFAULT_COLORS.put(Blocks.LAPIS_ORE, "#CC1846B2");
            DEFAULT_COLORS.put(Blocks.GOLD_ORE, "#CCFCEE4B");
            DEFAULT_COLORS.put(Blocks.REDSTONE_ORE, "#CCA00000");
            DEFAULT_COLORS.put(Blocks.COAL_ORE, "#CC191611");
            DEFAULT_COLORS.put(Blocks.EMERALD_ORE, "#CC17DD62");
            DEFAULT_COLORS.put(Blocks.NETHER_QUARTZ_ORE, "#CCEBE9E3");
        }
        return DEFAULT_COLORS.getOrDefault(block, "#CC737373");
    }

    /**
     * Saves configurations to configuration file.
     */
    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();

        if((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
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