package eu.minemania.watson.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import eu.minemania.watson.Reference;
import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.db.Filters;
import eu.minemania.watson.gui.GuiConfigs.ConfigGuiTab;
import eu.minemania.watson.selection.EditSelection;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryCache;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

public class DataManager implements IDirectoryCache
{
    private static final DataManager INSTANCE = new DataManager();

    protected static final Pattern DATE_PATTERN = Pattern.compile("^(\\d{4})-(\\d{1,2})-(\\d{1,2})$");
    private static final Map<String, File> LAST_DIRECTORIES = new HashMap<>();

    private static ConfigGuiTab configGuiTab = ConfigGuiTab.GENERIC;
    private static boolean canSave;
    private static long clientTickStart;

    private final EditSelection editselection = new EditSelection();

    protected Filters filters = new Filters();

    private DataManager()
    {

    }

    private static DataManager getInstance()
    {
        return INSTANCE;
    }

    public static IDirectoryCache getDirectoryCache()
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

    public static ConfigGuiTab getConfigGuiTab()
    {
        return configGuiTab;
    }

    public static void setConfigGuiTab(ConfigGuiTab tab)
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

    @Override
    @Nullable
    public File getCurrentDirectoryForContext(String context)
    {
        return LAST_DIRECTORIES.get(context);
    }

    @Override
    public void setCurrentDirectoryForContext(String context, File dir)
    {
        LAST_DIRECTORIES.put(context, dir);
    }

    public static void load()
    {
        File file = getCurrentStorageFile(true);

        JsonElement element = JsonUtils.parseJsonFile(file);

        if (element != null && element.isJsonObject())
        {
            LAST_DIRECTORIES.clear();

            JsonObject root = element.getAsJsonObject();

            if (JsonUtils.hasObject(root, "last_directories"))
            {
                JsonObject obj = root.get("last_directories").getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : obj.entrySet())
                {
                    String name = entry.getKey();
                    JsonElement el = entry.getValue();

                    if (el.isJsonPrimitive())
                    {
                        File dir = new File(el.getAsString());

                        if (dir.exists() && dir.isDirectory())
                        {
                            LAST_DIRECTORIES.put(name, dir);
                        }
                    }
                }
            }

            if (JsonUtils.hasString(root, "config_gui_tab"))
            {
                try
                {
                    configGuiTab = ConfigGuiTab.valueOf(root.get("config_gui_tab").getAsString());
                }
                catch (Exception e)
                {
                }

                if (configGuiTab == null)
                {
                    configGuiTab = ConfigGuiTab.GENERIC;
                }
            }
        }

        canSave = true;
    }

    public static void save()
    {
        save(false);
    }

    public static void save(boolean forceSave)
    {
        if (!canSave && !forceSave)
        {
            return;
        }

        JsonObject root = new JsonObject();
        JsonObject objDirs = new JsonObject();

        for (Map.Entry<String, File> entry : LAST_DIRECTORIES.entrySet())
        {
            objDirs.add(entry.getKey(), new JsonPrimitive(entry.getValue().getAbsolutePath()));
        }

        root.add("last_directories", objDirs);

        root.add("config_gui_tab", new JsonPrimitive(configGuiTab.name()));

        File file = getCurrentStorageFile(true);
        JsonUtils.writeJsonToFile(root, file);

        canSave = false;
    }

    public static File getCurrentConfigDirectory()
    {
        return new File(FileUtils.getConfigDirectory(), Reference.MOD_ID);
    }

    public static File getPlayereditsBaseDirectory()
    {
        File dir = FileUtils.getCanonicalFileIfPossible(new File(FileUtils.getMinecraftDirectory(), "playeredits"));

        if (!dir.exists() && !dir.mkdirs())
        {
            Watson.logger.warn("Failed to create the playeredit directory '{}'", dir.getAbsolutePath());
        }

        return dir;
    }

    private static File getCurrentStorageFile(boolean globalData)
    {
        File dir = getCurrentConfigDirectory();

        if (!dir.exists() && !dir.mkdirs())
        {
            Watson.logger.warn("Failed to create the config directory '{}'", dir.getAbsolutePath());
        }

        return new File(dir, getStorageFileName(globalData));
    }

    private static String getStorageFileName(boolean globalData)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        String name = StringUtils.getWorldOrServerName();

        if (name != null)
        {
            if (globalData)
            {
                return Reference.MOD_ID + "_" + name + ".json";
            }
            else
            {
                return Reference.MOD_ID + "_" + name + "_dim" + WorldUtils.getDimensionId(mc.world) + ".json";
            }
        }

        return Reference.MOD_ID + "_default.json";
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

        File file = new File(getPlayereditsBaseDirectory(), fileName + ".txt");
        try
        {
            BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
            int editCount = edits.save(file);
            int annoCount = edits.getAnnotations().size();
            StringUtils.sendOpenFileChatMessage(MinecraftClient.getInstance().player, "%s", file);
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
        File file = new File(getPlayereditsBaseDirectory(), fileName + ".txt");
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

            int pages = (files.length + Configs.Generic.PAGE_LINES.getIntegerValue() - 1) / Configs.Generic.PAGE_LINES.getIntegerValue();
            if (page > pages)
            {
                ChatMessage.localErrorT("watson.message.blockedit.highest_page", page);
            }
            else
            {
                ChatMessage.localOutputT("watson.message.blockedit.pages", page, pages);

                int start = (page - 1) * Configs.Generic.PAGE_LINES.getIntegerValue();
                int end = Math.min(files.length, page * Configs.Generic.PAGE_LINES.getIntegerValue());

                for (int i = start; i < end; ++i)
                {
                    ChatMessage.localOutput("     " + files[i].getName(), true);
                }

                ChatMessage.localOutputT("watson.message.blockedit.pages", page, pages);
                if (page < pages)
                {
                    ChatMessage.localOutputT("watson.message.blockedit.next_page", Configs.Generic.WATSON_PREFIX.getStringValue(), prefix, (page + 1));
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
        File[] files = getPlayereditsBaseDirectory().listFiles(new CaseInsensitivePrefixFileFilter(prefix));
        Arrays.sort(files);
        return files;
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
}