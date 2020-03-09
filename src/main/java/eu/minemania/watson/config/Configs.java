package eu.minemania.watson.config;

import fi.dy.masa.malilib.config.IConfigHandler;

import java.io.File;

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
import net.minecraft.util.registry.Registry;

public class Configs implements IConfigHandler
{
    /**
     * Config file for mod.
     */
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    /**
     * Default Generic configuration.
     */
    public static class Generic
    {
        public static final ConfigBoolean ENABLED = new ConfigBoolean("enabled", true, "Enables watson fully");
        public static final ConfigBoolean DEBUG = new ConfigBoolean("debugWatson", false, "enables debugging of Watson");
        public static final ConfigDouble REGION_INFO_TIMEOUT = new ConfigDouble("regionInfoTimeoutSeconds", 5.0, 1.0, 9.0, "Sets the timeout in seconds when right clicking with a wooden sword");
        public static final ConfigBoolean AUTO_PAGE = new ConfigBoolean("autopage", true, "Does automatic 3 pages at a time if enabled");
        public static final ConfigBoolean VECTOR_SHOWN = new ConfigBoolean("vectorShown", true, "Shows vector if enabled");
        public static final ConfigBoolean ANNOTATION_SHOWN = new ConfigBoolean("annotationShown", true, "Shows annotation if enabled");
        public static final ConfigColor BILLBOARD_BACKGROUND = new ConfigColor("billboardBackground", "0xA8000000", "Background color of the annotations");
        public static final ConfigColor BILLBOARD_FOREGROUND = new ConfigColor("billboardForeground", "0x7FFFFFFF", "Foreground color of the annotations");
        public static final ConfigBoolean GROUPING_ORES_IN_CREATIVE = new ConfigBoolean("groupingOresInCreative", true, "Ores are grouped even in creative");
        public static final ConfigString TELEPORT_COMMAND = new ConfigString("teleportCommand", "/tppos %g %d %g", "Sets teleport command");
        public static final ConfigDouble CHAT_TIMEOUT = new ConfigDouble("chatTimeoutSeconds", 0.1, "The minimum amount of seconds between sent chat messages by the mod");
        public static final ConfigInteger PAGE_LINES = new ConfigInteger("pagelines", 50, "Number of chat lines in a page");
        public static final ConfigInteger MAX_AUTO_PAGES = new ConfigInteger("maxAutoPages", 10, "Amount of automatic stepped thru pages");
        public static final ConfigInteger PRE_COUNT = new ConfigInteger("precount", 45, "Number of edits to be fetched");
        public static final ConfigInteger POST_COUNT = new ConfigInteger("postCount", 45, "Number of edits to be run post");
        public static final ConfigString WATSON_PREFIX = new ConfigString("watsonPrefix", "w", "The start of all Watson commands, without a slash");
        public static final ConfigBoolean SS_KEY_CUSTOM = new ConfigBoolean("ssKeyCustom", false, "Custom screenshot key");
        public static final ConfigBoolean SS_PLAYER_DIRECTORY = new ConfigBoolean("ssPlayerDirectory", true, "Subdirectory named after the currently selected player is created to hold screenshots of his edits if enabled");
        public static final ConfigString SS_DATE_DIRECTORY = new ConfigString("ssDateDirectory", "yyyy-MM-dd HH:mm:ss", "Format for the screenshot subdirectory");
        public static final ConfigBoolean SS_PLAYER_SUFFIX = new ConfigBoolean("ssPlayerSuffix", true, "Name of current selected player is appended to screenshot files");
        public static final ConfigBoolean REFORMAT_QUERY_RESULTS = new ConfigBoolean("reformatQueryResults", true, "Format query in chat more compact if enabled");
        public static final ConfigBoolean RECOLOR_QUERY_RESULTS = new ConfigBoolean("recolourQueryResults", true, "Recolour query results in chat");
        public static final ConfigBoolean TIME_ORDERED_DEPOSITS = new ConfigBoolean("timeOrderedDeposits", false, "If true, ore deposits should be numeric labeled when mined, when false it gets ordered in descending order of rareness of the ore");
        public static final ConfigDouble VECTOR_LENGTH = new ConfigDouble("vectorFloat", 4.0f, 4.0f, 10.0f, "The current displayed vector length");
        public static final ConfigBoolean LABEL_SHOWN = new ConfigBoolean("labelshown", true, "Show ore deposit number label");
        public static final ConfigBoolean USE_CHAT_HIGHLIGHTS = new ConfigBoolean("useChatHighlights", false, "If true, chat highlighter will be enabled");
        public static final ConfigBoolean OUTLINE_SHOWN = new ConfigBoolean("outlineshown", true, "If true, wireframe outline will be displayed");
        public static final ConfigBoolean LINKED_CREATION = new ConfigBoolean("linkedcreation", false, "If true, block creations will be linked by vectors");
        public static final ConfigBoolean LINKED_DESTRUCTION = new ConfigBoolean("linkeddestruction", false, "If true, block destruction will be linked by vectors");
        public static final ConfigBoolean DISPLAYED = new ConfigBoolean("displayed", true, "If true, watson will draw stuff");
        public static final ConfigBoolean SELECTION_SHOWN = new ConfigBoolean("selectionShown", true, "If enabled selection will be shown");
        public static final ConfigOptionList PLUGIN = new ConfigOptionList("plugin", Plugins.NULL, "which plugin does the server use");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ENABLED,
                DEBUG,
                REGION_INFO_TIMEOUT,
                AUTO_PAGE,
                VECTOR_SHOWN,
                ANNOTATION_SHOWN,
                BILLBOARD_BACKGROUND,
                BILLBOARD_FOREGROUND,
                GROUPING_ORES_IN_CREATIVE,
                TELEPORT_COMMAND,
                CHAT_TIMEOUT,
                PAGE_LINES,
                MAX_AUTO_PAGES,
                PRE_COUNT,
                POST_COUNT,
                WATSON_PREFIX,
                SS_KEY_CUSTOM,
                SS_PLAYER_DIRECTORY,
                SS_DATE_DIRECTORY,
                SS_PLAYER_SUFFIX,
                REFORMAT_QUERY_RESULTS,
                RECOLOR_QUERY_RESULTS,
                TIME_ORDERED_DEPOSITS,
                VECTOR_LENGTH,
                LABEL_SHOWN,
                USE_CHAT_HIGHLIGHTS,
                OUTLINE_SHOWN,
                LINKED_CREATION,
                LINKED_DESTRUCTION,
                DISPLAYED,
                SELECTION_SHOWN,
                PLUGIN
                );
    }

    /**
     * Default Lists configuration.
     */
    public static class Lists
    {
        public static final ConfigStringList HIGHLIGHT = new ConfigStringList("highlight", ImmutableList.of(), "What gets highlighted in chat");
        public static final ConfigStringList WATSON_BLOCKS = new ConfigStringList("watson blocks", setWatsonBlockData(), "Watson blocks");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                HIGHLIGHT,
                WATSON_BLOCKS
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
        Registry.BLOCK.forEach((block) -> builder.add(Registry.BLOCK.getId(block).getNamespace() + ":" + Registry.BLOCK.getId(block).getPath() + ";1;14033081"));
        Registry.ENTITY_TYPE.forEach((type) -> builder.add(Registry.ENTITY_TYPE.getId(type).getNamespace() + ":" + Registry.ENTITY_TYPE.getId(type).getPath() + ";1;14033081"));
        return builder.build();
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
