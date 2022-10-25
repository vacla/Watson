package eu.minemania.watson.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import eu.minemania.watson.Reference;
import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.db.Filters;
import eu.minemania.watson.db.LedgerInfo;
import eu.minemania.watson.db.TimeStamp;
import eu.minemania.watson.gui.ConfigScreen;
import eu.minemania.watson.selection.EditSelection;
import malilib.config.util.ConfigUtils;
import malilib.gui.config.ConfigTab;
import malilib.gui.tab.ScreenTab;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.FileUtils;
import malilib.util.StringUtils;
import malilib.util.data.json.JsonUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DataManager
{
    private static final DataManager INSTANCE = new DataManager();

    protected static final Pattern DATE_PATTERN = Pattern.compile("^(\\d{4})-(\\d{1,2})-(\\d{1,2})$");
    protected static final Pattern ABSOLUTE_TIME = Pattern.compile("(\\d{1,2})-(\\d{1,2}) (\\d{1,2}):(\\d{1,2}):(\\d{1,2})");

    private static ScreenTab configGuiTab = ConfigScreen.GENERIC;
    private static boolean canSave;
    private static long clientTickStart;
    private static String worldName = "";
    private static String ledgerVersion = "";
    private static LedgerInfo ledgerInfo;
    private static final ArrayList<String> ledgerActions = new ArrayList<>();

    private final EditSelection editselection = new EditSelection();

    protected Filters filters = new Filters();

    private static DataManager getInstance()
    {
        return INSTANCE;
    }

    public static void setClientTick(long time)
    {
        clientTickStart = time;
    }

    public static void onClientTickStart()
    {
        clientTickStart = System.currentTimeMillis();
    }

    public static long getClientTickStartTime()
    {
        return clientTickStart;
    }

    public static ScreenTab getConfigGuiTab()
    {
        return configGuiTab;
    }

    public static void setConfigGuiTab(ConfigTab tab)
    {
        configGuiTab = tab;
    }

    public static EditSelection getEditSelection()
    {
        return getInstance().editselection;
    }

    public static Filters getFilters()
    {
        return getInstance().filters;
    }

    public static void setWorldPlugin(String world)
    {
        worldName = world;
    }

    public static String getWorldPlugin()
    {
        return worldName;
    }

    public static void setLedgerVersion(String version)
    {
        ledgerVersion = version;
    }

    public static String getLedgerVersion()
    {
        return ledgerVersion;
    }

    public static void setLedgerActions(List<String> allowedLedgerActions)
    {
        ledgerActions.addAll(allowedLedgerActions);
    }

    public static ArrayList<String> getLedgerActions()
    {
        return ledgerActions;
    }

    public static void load()
    {
        Path file = getCurrentStorageFile();

        JsonElement element = JsonUtils.parseJsonFile(file);

        if (element != null && element.isJsonObject())
        {

            JsonObject root = element.getAsJsonObject();

            if (JsonUtils.hasString(root, "config_gui_tab"))
            {
                configGuiTab = ScreenTab.getTabByNameOrDefault(root.get("config_gui_tab").getAsString(), ConfigScreen.ALL_TABS, ConfigScreen.GENERIC);
            }
        }

        canSave = true;
    }

    public static void save()
    {
        if (!canSave)
        {
            return;
        }

        JsonObject root = new JsonObject();

        root.add("config_gui_tab", new JsonPrimitive(configGuiTab.getName()));

        Path file = getCurrentStorageFile();
        JsonUtils.writeJsonToFile(root, file);

        canSave = false;
    }

    public static Path getCurrentConfigDirectory()
    {
        return ConfigUtils.getConfigDirectory().resolve(Reference.MOD_ID);
    }

    public static Path getPlayereditsBaseDirectory()
    {
        Path dir = FileUtils.getMinecraftDirectory().resolve("playeredits");

        if (!FileUtils.createDirectoriesIfMissing(dir))
        {
            Watson.logger.warn("Failed to create the playeredit directory '{}'", dir.toAbsolutePath().toString());
        }

        return dir;
    }

    private static Path getCurrentStorageFile()
    {
        Path dir = getCurrentConfigDirectory();

        if (!FileUtils.createDirectoriesIfMissing(dir))
        {
            Watson.logger.warn("Failed to create the config directory '{}'", dir.toAbsolutePath().toString());
        }

        return dir.resolve(getStorageFileName());
    }

    private static String getStorageFileName()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        String name = StringUtils.getWorldOrServerName();
        World world = mc.world;
        if (name == null || world == null)
        {
            return Reference.MOD_ID + "_default.json";
        }

        return Reference.MOD_ID + "_" + name + ".json";
    }

    public static String getServerIP()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        ServerInfo serverData = mc.getCurrentServerEntry();
        if (!mc.isInSingleplayer() && serverData != null)
        {
            return serverData.address;
        }
        else
        {
            return null;
        }
    }

    public static void saveBlockEditFile(String fileName)
    {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity == null)
        {
            return;
        }
        if (fileName == null)
        {
            String player = (String) getEditSelection().getVariables().get("player");
            if (player == null)
            {
                ChatMessage.localErrorT("watson.message.blockedit.no_player");
                return;
            }
            else
            {
                Calendar calendar = Calendar.getInstance();
                fileName = String.format("%s-%4d-%02d-%02d-%02d.%02d.%02d", player, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            }
        }

        Path file = getPlayereditsBaseDirectory().resolve(fileName + ".txt");

        try
        {
            Files.createFile(file);
            BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
            int editCount = edits.save(file);
            int annoCount = edits.getAnnotations().size();
            StringUtils.sendOpenFileChatMessage(playerEntity, "%s", file);
            ChatMessage.localOutputT("watson.message.blockedit.edits_annotations.saved", editCount, annoCount, fileName);
        }
        catch (IOException e)
        {
            Watson.logger.error("error saving BlockEditSet to " + file, e);
            ChatMessage.localErrorT("watson.message.blockedit.not_saved", fileName);
        }
    }

    public static void loadBlockEditFile(String fileName)
    {
        File file = new File(getPlayereditsBaseDirectory().toFile(), fileName + ".txt");
        if (!file.canRead())
        {
            File[] files = getInstance().getBlockEditFileList(fileName);
            if (files.length > 0)
            {
                file = files[files.length - 1];
            }
        }

        if (file.canRead())
        {
            try
            {
                BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
                int editCount = edits.load(file);
                int annoCount = edits.getAnnotations().size();
                ChatMessage.localOutputT("watson.message.blockedit.edits_annotations.loaded", editCount, annoCount, file.getName());
            }
            catch (Exception e)
            {
                Watson.logger.error("error loading BlockEditSet from " + file, e);
                ChatMessage.localErrorT("watson.message.blockedit.not_loaded", fileName);
            }
        }
        else
        {
            ChatMessage.localErrorT("watson.message.blockedit.not_read", fileName);
        }
    }

    public static void listBlockEditFiles(String prefix, int page)
    {
        File[] files = getInstance().getBlockEditFileList(prefix);
        if (files.length == 0)
        {
            ChatMessage.localOutputT("watson.message.blockedit.not_match");
        }
        else
        {
            if (files.length == 1)
            {
                ChatMessage.localOutputT("watson.message.blockedit.match_file.1");
            }
            else
            {
                ChatMessage.localOutputT("watson.message.blockedit.match_file.more", files.length);
            }

            int pages = (files.length + Configs.Plugin.PAGE_LINES.getIntegerValue() - 1) / Configs.Plugin.PAGE_LINES.getIntegerValue();
            if (page > pages)
            {
                ChatMessage.localErrorT("watson.message.blockedit.highest_page", page);
            }
            else
            {
                ChatMessage.localOutputT("watson.message.blockedit.pages", page, pages);

                int start = (page - 1) * Configs.Plugin.PAGE_LINES.getIntegerValue();
                int end = Math.min(files.length, page * Configs.Plugin.PAGE_LINES.getIntegerValue());

                for (int i = start; i < end; ++i)
                {
                    ChatMessage.localOutput("     " + files[i].getName(), true);
                }

                ChatMessage.localOutputT("watson.message.blockedit.pages", page, pages);
                if (page < pages)
                {
                    ChatMessage.localOutputT("watson.message.blockedit.next_page", Configs.Generic.WATSON_PREFIX.getValue(), prefix, (page + 1));
                }
            }
        }
    }

    public static void deleteBlockEditFiles(String prefix)
    {
        File[] files = getInstance().getBlockEditFileList(prefix);
        if (files.length > 0)
        {
            int failed = 0;
            for (File file : files)
            {
                if (file.delete())
                {
                    ChatMessage.localOutputT("watson.message.blockedit.deleted", file.getName());
                }
                else
                {
                    ++failed;
                }
            }
            String message = "watson.message.blockedit.deleted_matching";
            if (failed == 0)
            {
                ChatMessage.localOutputT(message, (files.length - failed), files.length, prefix);
            }
            else
            {
                ChatMessage.localErrorT(message, (files.length - failed), files.length, prefix);
            }
        }
        else
        {
            ChatMessage.localOutputT("watson.message.blockedit.no_matching", prefix);
        }
    }

    @SuppressWarnings("MagicConstant")
    public static void expireBlockEditFiles(String date)
    {
        Matcher m = DATE_PATTERN.matcher(date);
        if (m.matches())
        {
            Calendar expiry = Calendar.getInstance();
            long expiryTime;
            try
            {
                int year = Integer.parseInt(m.group(1));
                int month = Integer.parseInt(m.group(2));
                int day = Integer.parseInt(m.group(3));

                expiry.setLenient(false);
                expiry.set(year, month - 1, day, 0, 0);

                expiryTime = expiry.getTimeInMillis();
            }
            catch (Exception e)
            {
                ChatMessage.localErrorT("watson.message.blockedit.date_not_valid", date);
                return;
            }

            int deleted = 0;
            int failed = 0;
            File[] files = getInstance().getBlockEditFileList("*");
            for (File file : files)
            {
                if (file.lastModified() < expiryTime)
                {
                    if (file.delete())
                    {
                        ++deleted;
                        ChatMessage.localOutputT("watson.message.blockedit.deleted", file.getName());
                    }
                    else
                    {
                        ++failed;
                        ChatMessage.localErrorT("watson.message.blockedit.not_delete", file.getName());
                    }
                }
            }
            if (deleted + failed == 0)
            {
                ChatMessage.localOutputT("watson.message.blockedit.nothing_between", date);
            }
            else
            {
                String message = "watson.message.blockedit.deleted_older";
                if (failed == 0)
                {
                    ChatMessage.localOutputT(message, deleted, deleted + failed, date);
                }
                else
                {
                    ChatMessage.localErrorT(message, deleted, deleted + failed, date);
                }
            }
        }
        else
        {
            ChatMessage.localErrorT("watson.message.blockedit.date_form");
        }
    }

    public File[] getBlockEditFileList(String prefix)
    {
        File[] files = getPlayereditsBaseDirectory().toFile().listFiles(new CaseInsensitivePrefixFileFilter(prefix));
        if (files != null)
        {
            Arrays.sort(files);
        }
        return files;
    }

    public static long getTimeDiff(String time)
    {
        Matcher absolute = ABSOLUTE_TIME.matcher(time);
        if (absolute.matches())
        {
            int month = Integer.parseInt(absolute.group(1));
            int day = Integer.parseInt(absolute.group(2));
            int hour = Integer.parseInt(absolute.group(3));
            int minute = Integer.parseInt(absolute.group(4));
            int second = Integer.parseInt(absolute.group(5));
            if (month != 0 || day != 0 || hour != 0 || minute != 0 || second != 0)
            {
                return TimeStamp.timeDiff(month, day, hour, minute, second);
            }
            else
            {
                return 0;
            }
        }
        else
        {
            MessageDispatcher.error("watson.gui.label.blockedit.info.format");
            return -1;
        }
    }

    public static class CaseInsensitivePrefixFileFilter implements FileFilter
    {
        protected String _lowerPrefix;

        public CaseInsensitivePrefixFileFilter(String prefix)
        {
            _lowerPrefix = (prefix == null || prefix.equals("*")) ? "" : prefix.toLowerCase();
        }

        @Override
        public boolean accept(File file)
        {
            return file.isFile() && file.canRead() && (_lowerPrefix.length() == 0 || file.getName().toLowerCase().startsWith(_lowerPrefix));
        }
    }

    public static String onSendChatMessage(String message)
    {
        if((message.contains("/pr l") || message.contains("/pr i")) && !message.contains("-extended"))
        {
            return message + " -extended";
        }
        return message;
    }

    public static void setLedgerInfo(LedgerInfo ledgerInfo)
    {
        DataManager.ledgerInfo = ledgerInfo;
    }

    public static LedgerInfo getLedgerInfo()
    {
        return ledgerInfo;
    }
}