package eu.minemania.watson.config;

import eu.minemania.watson.data.DataManager;
import java.util.*;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Reference;
import fi.dy.masa.malilib.config.category.BaseConfigOptionCategory;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.*;
import fi.dy.masa.malilib.config.option.list.StringListConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Configs
{
    public static final int CURRENT_VERSION = 1;

    private static final Map<Item, String> DEFAULT_COLORS = new HashMap<>();

    /**
     * Default Generic configuration.
     */
    public static class Generic
    {
        public static final BooleanConfig ACTION_REVERSE = new BooleanConfig("actionreverse", false);
        public static final BooleanConfig ANNOTATION_SHOWN = new BooleanConfig("annotationshown", true);
        public static final ColorConfig BILLBOARD_BACKGROUND = new ColorConfig("billboardbackground", "0xA8000000");
        public static final ColorConfig BILLBOARD_FOREGROUND = new ColorConfig("billboardforeground", "0x7FFFFFFF");
        public static final DoubleConfig CHAT_TIMEOUT = new DoubleConfig("chattimeoutseconds", 1, 0.1, 5);
        public static final BooleanConfig DEBUG = new BooleanConfig("debugwatson", false);
        public static final BooleanConfig DISPLAYED = new BooleanConfig("displayed", true);
        public static final BooleanConfig ENABLED = new BooleanConfig("enabled", true);
        public static final StringConfig SS_DATE_DIRECTORY = new StringConfig("ssdatedirectory", "yyyy-MM-dd HH:mm:ss");
        public static final BooleanConfig SS_KEY_CUSTOM = new BooleanConfig("sskeycustom", false);
        public static final BooleanConfig SS_PLAYER_DIRECTORY = new BooleanConfig("ssplayerdirectory", true);
        public static final BooleanConfig SS_PLAYER_SUFFIX = new BooleanConfig("ssplayersuffix", true);
        public static final StringConfig TELEPORT_COMMAND = new StringConfig("teleportcommand", "/tppos {x:d} {y:d} {z:d} {world}");
        public static final StringConfig WATSON_PREFIX = new StringConfig("watsonprefix", "watson");

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
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
        public static final BooleanConfig DISABLE_CP_MESSAGES = new BooleanConfig("disablecpmessages", false);
        public static final BooleanConfig DISABLE_JOIN_MESSAGES = new BooleanConfig("disablejoinmessages", false);
        public static final BooleanConfig DISABLE_LB_MESSAGES = new BooleanConfig("disablelbmessages", false);
        public static final BooleanConfig DISABLE_PR_MESSAGES = new BooleanConfig("disableprmessages", false);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                DISABLE_CP_MESSAGES,
                DISABLE_JOIN_MESSAGES,
                DISABLE_LB_MESSAGES,
                DISABLE_PR_MESSAGES
        );
    }

    public static class Outlines
    {
        public static final BooleanConfig ONLY_ORE_BLOCK = new BooleanConfig("onlyoreblock", false);
        public static final IntegerConfig ORE_LINEWIDTH = new IntegerConfig("orelinewidth", 3, 1, 10);
        public static final BooleanConfig ORE_OUTLINE_THICKER = new BooleanConfig("oreoutlinethicker", false);
        public static final BooleanConfig OUTLINE_SHOWN = new BooleanConfig("outlineshown", true);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                ONLY_ORE_BLOCK,
                ORE_LINEWIDTH,
                ORE_OUTLINE_THICKER,
                OUTLINE_SHOWN
        );
    }

    public static class Plugin
    {
        public static final IntegerConfig AMOUNT_ROWS = new IntegerConfig("amountrowes", 5);
        public static final HotkeyedBooleanConfig AUTO_PAGE = new HotkeyedBooleanConfig("autopage", true, "");
        public static final OptionListConfig<CoreprotectCommand> COREPROTECT_COMMAND = new OptionListConfig<>("coreprotectcommand", CoreprotectCommand.CO, CoreprotectCommand.VALUES);
        public static final IntegerConfig MAX_AUTO_PAGES = new IntegerConfig("maxautopages", 100);
        public static final IntegerConfig MAX_AUTO_PAGES_LOOP = new IntegerConfig("maxautopagesloop", 100);
        public static final IntegerConfig PAGE_LINES = new IntegerConfig("pagelines", 50);
        public static final OptionListConfig<Plugins> PLUGIN = new OptionListConfig<>("plugin", Plugins.NULL, Plugins.VALUES);
        public static final BooleanConfig RECOLOR_QUERY_RESULTS = new BooleanConfig("recolourqueryresults", true);
        public static final BooleanConfig REFORMAT_QUERY_RESULTS = new BooleanConfig("reformatqueryresults", true);
        public static final DoubleConfig REGION_INFO_TIMEOUT = new DoubleConfig("regioninfotimeoutseconds", 5.0, 1.0, 9.0);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
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
        public static final BooleanConfig HIGHLIGHT_CASE_SENSITIVE = new BooleanConfig("highlightcasesensitive", true);
        public static final StringConfig HIGHLIGHT_SOUND = new StringConfig("highlightsound", "");
        public static final BooleanConfig HIGHLIGHT_SOUND_ENABLE = new BooleanConfig("highlightsoundenable", false);
        public static final DoubleConfig HIGHLIGHT_SOUND_VOLUME = new DoubleConfig("highlightsoundvolume", 1, 0, 1);
        public static final BooleanConfig USE_CHAT_HIGHLIGHTS = new BooleanConfig("usechathighlights", false);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                HIGHLIGHT_CASE_SENSITIVE,
                HIGHLIGHT_SOUND,
                HIGHLIGHT_SOUND_ENABLE,
                HIGHLIGHT_SOUND_VOLUME,
                USE_CHAT_HIGHLIGHTS
        );
    }

    public static class Edits
    {
        public static final BooleanConfig GROUPING_ORES_IN_CREATIVE = new BooleanConfig("groupingoresincreative", true);
        public static final BooleanConfig LABEL_SHOWN = new BooleanConfig("labelshown", true);
        public static final BooleanConfig LINKED_CREATION = new BooleanConfig("linkedcreation", false);
        public static final BooleanConfig LINKED_DESTRUCTION = new BooleanConfig("linkeddestruction", false);
        public static final IntegerConfig POST_COUNT = new IntegerConfig("postcount", 45);
        public static final IntegerConfig PRE_COUNT = new IntegerConfig("precount", 45);
        public static final BooleanConfig SELECTION_SHOWN = new BooleanConfig("selectionshown", true);
        public static final BooleanConfig TIME_ORDERED_DEPOSITS = new BooleanConfig("timeordereddeposits", false);
        public static final DoubleConfig VECTOR_LENGTH = new DoubleConfig("vectorfloat", 4.0f, 0.0f, 10.0f);
        public static final BooleanConfig VECTOR_SHOWN = new BooleanConfig("vectorshown", true);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
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
        public static final StringListConfig HIGHLIGHT = new StringListConfig("highlight", ImmutableList.of());
        public static final StringListConfig OVERRIDING_ACTIONS = new StringListConfig("overridingactions", ImmutableList.of());
        public static final StringListConfig SMALLER_RENDER_BOX = new StringListConfig("smallerrenderbox", ImmutableList.of("minecraft:stone", "minecraft:gravel", "minecraft:dirt", "minecraft:diorite", "minecraft:sand", "minecraft:andesite", "minecraft:granite"));
        public static final StringListConfig WATSON_BLOCKS = new StringListConfig("watsonblocks", setWatsonBlockData());

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                HIGHLIGHT,
                OVERRIDING_ACTIONS,
                SMALLER_RENDER_BOX,
                WATSON_BLOCKS
        );
    }

    public static class Analysis
    {
        public static final ConfigStringExt CP_BUSY = new ConfigStringExt("cpbusy", "^CoreProtect - Database busy. Please try again later.$", "watson.config.comment.analysis").setCommentArguments("cp busy");
        public static final ConfigStringExt CP_DETAILS = new ConfigStringExt("cpdetails", "^(?:\\s+)?(\\d+[.,]\\d+\\/[mhd] ago|\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{2}:\\d{2}) - #?(\\w+) ((?!.*logged).*?) ((?:x(\\d+) )?\\w+(?::\\w+)?)\\.$", "watson.config.comment.analysis").setCommentArguments("cp details");
        public static final ConfigStringExt CP_DETAILS_SESSION = new ConfigStringExt("cpdetailssession", "^(\\d+[.,]\\d+\\/[mhd] ago|\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{2}:\\d{2}) - (\\w+) (logged \\w+)\\.$", "watson.config.comment.analysis").setCommentArguments("cp details session");
        public static final ConfigStringExt CP_DETAILS_SIGN = new ConfigStringExt("cpdetailssign", "^(\\d+[.,]\\d+\\/[mhd] ago|\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{2}:\\d{2}) - (\\w+): ([\\s\\w+\\W]+)", "watson.config.comment.analysis").setCommentArguments("cp details sign");
        public static final ConfigStringExt CP_INSPECTOR_COORDS = new ConfigStringExt("cpinspectorcoords", "^-{5} \\w+(?:\\s\\w+)* -{5} \\(x(-?\\d+)\\/y(-?\\d+)\\/z(-?\\d+)\\)$", "watson.config.comment.analysis").setCommentArguments("cp inspector coords");
        public static final ConfigStringExt CP_LOOKUP_COORDS = new ConfigStringExt("cplookupcoords", "^ +\\^ \\(x(-?\\d+)\\/y(-?\\d+)\\/z(-?\\d+)\\/([^\\)]+)\\)(?: \\(.+\\))?$", "watson.config.comment.analysis").setCommentArguments("cp lookup coords");
        public static final ConfigStringExt CP_LOOKUP_HEADER = new ConfigStringExt("cplookupheader", "^----- CoreProtect Lookup Results -----$", "watson.config.comment.analysis").setCommentArguments("cp lookup header");
        public static final ConfigStringExt CP_NO_RESULT = new ConfigStringExt("cpnoresult", "^CoreProtect - No results found.$", "watson.config.comment.analysis").setCommentArguments("cp no result");
        public static final ConfigStringExt CP_PAGE = new ConfigStringExt("cppage", "^(?:.\\s)*Page (\\d+)\\/(\\d+) (?:.\\s)*\\| To view a page, type \"\\/co l <page>\"\\.$", "watson.config.comment.analysis").setCommentArguments("cp page");
        public static final ConfigStringExt CP_SEARCH = new ConfigStringExt("cpsearch", "^CoreProtect - Lookup searching. Please wait...$", "watson.config.comment.analysis").setCommentArguments("cp search");
        public static final ConfigStringExt DUTYMODE_DISABLE = new ConfigStringExt("dutymodedisable", "^\\[Duties\\] Duty mode disabled.*", "watson.config.comment.analysis").setCommentArguments("duty mode disable");
        public static final ConfigStringExt DUTYMODE_ENABLE = new ConfigStringExt("dutymodeenable", "^\\[Duties\\] Duty mode enabled.*", "watson.config.comment.analysis").setCommentArguments("duty mode enable");
        public static final ConfigStringExt LB_POSITION = new ConfigStringExt("lbposition", "^(?:[\\w ]+) in the last \\d+ \\w+ (?:at (-?\\d+):(-?\\d+):(-?\\d+) |within .+ blocks of location )?in (.+):$", "watson.config.comment.analysis").setCommentArguments("lb position");
        public static final ConfigStringExt LB_DATA = new ConfigStringExt("lbdata", "(?:\\((\\d+)\\) )?\\[((?:\\d{2,4}-)?\\d{2}-\\d{2} \\d{2}:\\d{2})] (\\w+) (\\w+\\s?\\w) ((?:(\\d+)x )?[A-Z_]+)(?:(?: with)? (\\w+[A-Z]))?(?:(?: to )? \\[(.*[^\\[\\]])] \\[(.*[^\\[\\]])] \\[(.*[^\\[\\]])] \\[(.*[^\\[\\]])])?(?: at (-?\\d+), (\\d+), (-?\\d+)| (?:from|into) \\w+)?(?: with (\\w+))?", "watson.config.comment.analysis").setCommentArguments("lb data");
        public static final ConfigStringExt LB_TP = new ConfigStringExt("lbtp", "^Teleported to (-?\\d+):(\\d+):(-?\\d+)$", "watson.config.comment.analysis").setCommentArguments("lb tp");
        public static final ConfigStringExt LB_PAGE = new ConfigStringExt("lbpage", "^Page (\\d+)/(\\d+)$", "watson.config.comment.analysis").setCommentArguments("lb page");
        public static final ConfigStringExt LB_HEADER_NO_RESULTS = new ConfigStringExt("lbheadernoresults", "^No results found\\.$", "watson.config.comment.analysis").setCommentArguments("lb header no results");
        public static final ConfigStringExt LB_HEADER_CHANGES = new ConfigStringExt("lbheaderchanges", "^\\d+ changes? found\\.$", "watson.config.comment.analysis").setCommentArguments("lb header changes");
        public static final ConfigStringExt LB_HEADER_BLOCKS = new ConfigStringExt("lbheaderblocks", "^\\d+ blocks? found\\.$", "watson.config.comment.analysis").setCommentArguments("lb header blocks");
        public static final ConfigStringExt LB_HEADER_SUM_BLOCKS = new ConfigStringExt("lbheadersumblocks", "^Created - Destroyed - Block$", "watson.config.comment.analysis").setCommentArguments("lb header sum blocks");
        public static final ConfigStringExt LB_HEADER_SUM_PLAYERS = new ConfigStringExt("lbheadersumplayers", "^Created - Destroyed - Player$", "watson.config.comment.analysis").setCommentArguments("lb header sum players");
        public static final ConfigStringExt LB_HEADER_SEARCHING = new ConfigStringExt("lbheadersearching", "^Searching Block changes from player \\w+ in the last \\d+ minutes (?:within \\d+ blocks of you )?in .+:$", "watson.config.comment.analysis").setCommentArguments("lb header searching");
        public static final ConfigStringExt LB_HEADER_RATIO = new ConfigStringExt("lbheaderratio", "^STONE and DIAMOND_ORE changes from player \\w+ between (\\d+) and (\\d+) minutes ago in .+ summed up by blocks:$", "watson.config.comment.analysis").setCommentArguments("lb header ratio");
        public static final ConfigStringExt LB_HEADER_RATIO_CURRENT = new ConfigStringExt("lbheaderratiocurrent", "^Stone and diamond ore changes from player \\w+ in the last (\\d+) minutes in .+ summed up by blocks:$", "watson.config.comment.analysis").setCommentArguments("lb header ratio current");
        public static final ConfigStringExt LB_HEADER_TIME_CHECK = new ConfigStringExt("lbheadertimecheck", "Block changes from player \\w+ between (\\d+) and \\d+ minutes ago in .+:", "watson.config.comment.analysis").setCommentArguments("lb header time check");
        public static final ConfigStringExt LB_HEADER_BLOCK = new ConfigStringExt("lbheaderblock", "^(?!STONE and DIAMOND_ORE)(?: |,|\\w)+ from player \\w+ (?:in the last \\d+ minutes |between \\d+ and \\d+ minutes ago |more than -?\\d+ minutes ago )?(?:within \\d+ blocks of you )?in .+ summed up by (?:players|blocks):$", "watson.config.comment.analysis").setCommentArguments("lb header block");
        public static final ConfigStringExt LB_SUM = new ConfigStringExt("lbsum", "^(\\d+)[ ]{6,}(\\d+)[ ]{6,}((?:\\w| )+)$", "watson.config.comment.analysis").setCommentArguments("lb sum");
        public static final ConfigStringExt MODMODE_DISABLE = new ConfigStringExt("modmodedisable", "^You are no longer in ModMode!$", "watson.config.comment.analysis").setCommentArguments("modmode disable");
        public static final ConfigStringExt MODMODE_ENABLE = new ConfigStringExt("modmodeenable", "^You are now in ModMode!$", "watson.config.comment.analysis").setCommentArguments("modmode enable");
        public static final ConfigStringExt PRISM_DATA = new ConfigStringExt("prismdata", ".*?[-+] \\[([0-9]+)\\]\\s+(?<instigator>.*) (?<cause>grew|killed|picked up|placed|grew|ignited|set a fire|used|threw potion|sheared|dispensed|blew up|formed|poured|broke|filled a|accessed|ate|(?:un)?leashed|launched|hung|wrote|entered|exited|removed|dropped|inserted|ran command|said|spawned|quit|joined)\\s+(?<target>.*)\\s+(?<when>just now|(?:\\d+d)?(?:\\d+h)?(?:\\d+m)?\\sago) \\(a:(?<action>.*)\\)\\s-\\d+- (?<date>\\d+\\/\\d+\\/\\d+) (?<time>\\d+:\\d+:\\d+\\w+) - (?<world>\\w+) @ (?<x>-?\\d+) (?<y>-?\\d+) (?<z>-?\\d+).*?", "watson.config.comment.analysis").setCommentArguments("prism data");
        public static final ConfigStringExt PRISM_PAGE = new ConfigStringExt("prismpage", "Showing\\s+\\d+ results\\. Page\\s+(?<current>\\d+) of\\s+(?<max>\\d+)", "watson.config.comment.analysis").setCommentArguments("prism page");
        public static final ConfigStringExt PRISM_PAGINATION = new ConfigStringExt("prismpagination", "(?:\\s+\\[<< Prev] \\|)?\\s+\\[Next >>\\]", "watson.config.comment.analysis").setCommentArguments("prism pagination");
        public static final ConfigStringExt WG_REGIONS = new ConfigStringExt("wgregions", "^Applicable regions: ([a-zA-Z0-9_-]+(?:, [a-zA-Z0-9_-]+)*)$", "watson.config.comment.analysis").setCommentArguments("wg regions");

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
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

    private static ImmutableList<String> setWatsonBlockData()
    {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        ArrayList<String> list = new ArrayList<>();
        String color = "";

        for (String name : DataManager.getAllItemEntitiesStringIdentifiers())
        {
            Optional<Block> optionalBlock = Registry.BLOCK.getOrEmpty(new Identifier(name));
            Optional<Item> optionalItem = Registry.ITEM.getOrEmpty(optionalBlock.map(block -> Registry.ITEM.getId(block.asItem())).orElseGet(() -> new Identifier(name)));

            if (optionalItem.isEmpty())
            {
                color = setCustomColorOres(Registry.ENTITY_TYPE.get(new Identifier(name)));
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

    public static final List<ConfigOptionCategory> CATEGORIES = ImmutableList.of(
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Generic", Generic.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Messages", Messages.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Outlines", Outlines.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Plugin", Plugin.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Highlights", Highlights.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Edits", Edits.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Analysis", Analysis.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Lists", Lists.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Hotkeys", Hotkeys.HOTKEY_LIST)
    );
}