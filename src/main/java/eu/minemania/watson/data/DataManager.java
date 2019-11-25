package eu.minemania.watson.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.GameType;

public class DataManager implements IDirectoryCache {
	private static final DataManager INSTANCE = new DataManager();
	
	//private static final Pattern PATTERN_ITEM_BASE = Pattern.compile("^(?<name>(?:[a-z0-9\\._-]+:)[a-z0-9\\._-]+)$");
	protected static final Pattern DATE_PATTERN = Pattern.compile("^(\\d{4})-(\\d{1,2})-(\\d{1,2})$");
	private static final Map<String, File> LAST_DIRECTORIES = new HashMap<>();
	
	//private static ItemStack toolItem = new ItemStack(Items.STICK);
	private static ConfigGuiTab configGuiTab = ConfigGuiTab.GENERIC;
	//private static boolean createPlacementOnLoad = true;
	private static boolean canSave;
	private static long clientTickStart;
	
	private final EditSelection editselection = new EditSelection();
	/*private final PlayereditPlacementManager playereditPlacementManager = new PlayereditPlacementManager();
	private final PlayereditProjectsManager playereditProjectsManager = new PlayereditProjectsManager();*/
	//private LayerRange renderRange = new LayerRange(PlayereditWorldRefresher.INSTANCE);
	/*private ToolMode operationMode = ToolMode.PLAYEREDIT_PLACEMENT;
	private EditSelectionSimple editSimple = new EditSelectionSimple(true);
	@Nullable
	private MaterialListBase materialList;*/
	
	protected Filters filters = new Filters();
	
	private DataManager() {
		
	}
	
	private static DataManager getInstance() {
		return INSTANCE;
	}
	
	public static IDirectoryCache getDirectoryCache() {
		return INSTANCE;
	}
	
	public static void setClientTick(long time) {
		clientTickStart = time;
	}
	
	public static void onClientTickStart() {
		clientTickStart = System.currentTimeMillis();
	}
	
	public static long getClientTickStartTime() {
		return clientTickStart;
	}
	
	public static ConfigGuiTab getConfigGuiTab() {
		return configGuiTab;
	}
	
	public static void setConfigGuiTab(ConfigGuiTab tab) {
		configGuiTab = tab;
	}
	
	public static EditSelection getEditSelection() {
		return getInstance().editselection;
	}
	
	public static Filters getFilters() {
		return getInstance().filters;
	}
	
	@Override
	@Nullable
	public File getCurrentDirectoryForContext(String context) {
		return LAST_DIRECTORIES.get(context);
	}
	
	@Override
	public void setCurrentDirectoryForContext(String context, File dir) {
		LAST_DIRECTORIES.put(context, dir);
	}
	
	public static void load() {
		File file = getCurrentStorageFile(true);
		//File blockFile = getCurrentBlockStorageFile();
		
		JsonElement element = JsonUtils.parseJsonFile(file);
		/*JsonElement elementBlock = JsonUtils.parseJsonFile(blockFile);
		
		if(elementBlock != null && elementBlock.isJsonObject()) {
			JsonObject root = elementBlock.getAsJsonObject();
			//WatsonBlockRegistery.loadWatsonBlocks(root);
		}*/
		
		if(element != null && element.isJsonObject()) {
			LAST_DIRECTORIES.clear();

            JsonObject root = element.getAsJsonObject();

            if (JsonUtils.hasObject(root, "last_directories")) {
                JsonObject obj = root.get("last_directories").getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    String name = entry.getKey();
                    JsonElement el = entry.getValue();

                    if (el.isJsonPrimitive()) {
                        File dir = new File(el.getAsString());

                        if (dir.exists() && dir.isDirectory()) {
                            LAST_DIRECTORIES.put(name, dir);
                        }
                    }
                }
            }

            if (JsonUtils.hasString(root, "config_gui_tab")) {
                try {
                    configGuiTab = ConfigGuiTab.valueOf(root.get("config_gui_tab").getAsString());
                } catch (Exception e) {}

                if (configGuiTab == null) {
                    configGuiTab = ConfigGuiTab.GENERIC;
                }
            }

            //createPlacementOnLoad = JsonUtils.getBooleanOrDefault(root, "create_placement_on_load", true);
		}
		
		canSave = true;
	}
	
	public static void save() {
		save(false);
		//MaterialCache.getInstance().writeToFile();
	}
	
	public static void save(boolean forceSave) {
		if(canSave == false && forceSave == false) {
			return;
		}
		
		JsonObject root = new JsonObject();
		JsonObject objDirs = new JsonObject();
		
		for(Map.Entry<String, File> entry : LAST_DIRECTORIES.entrySet()) {
			objDirs.add(entry.getKey(), new JsonPrimitive(entry.getValue().getAbsolutePath()));
		}
		
		root.add("last_directories", objDirs);
		
		//root.add("create_placement_on_load", new JsonPrimitive(createPlacementOnLoad));
		root.add("config_gui_tab", new JsonPrimitive(configGuiTab.name()));
		
		File file = getCurrentStorageFile(true);
		JsonUtils.writeJsonToFile(root, file);
		
		canSave = false;
	}
	
	public static void clear() {
		
		//PlayereditVerifier.clearActiveVerifiers();
		
		//getPlayereditPlacementManager().clear();
		//getPlayereditProjectsManager().clear();
		//getSelectionManager().clear();
		//setMaterialList(null);
		
		//InfoHud.getInstance().reset();
	}
	
	public static File getCurrentConfigDirectory() {
		return new File(FileUtils.getConfigDirectory(), Reference.MOD_ID);
	}
	
	public static File getPlayereditsBaseDirectory() {
		File dir = FileUtils.getCanonicalFileIfPossible(new File(FileUtils.getMinecraftDirectory(), "playeredits"));
		
		if(dir.exists() == false && dir.mkdirs() == false) {
			Watson.logger.warn("Failed to create the playeredit directory '{}'", dir.getAbsolutePath());
		}
		
		return dir;
	}

	
	/*private static File getCurrentBlockStorageFile() {
		File dir = getCurrentConfigDirectory();
		
		if(dir.exists() == false && dir.mkdirs() == false) {
			Watson.logger.warn("Failed to create the config directory '{}'", dir.getAbsolutePath());
		}
		
		return new File(dir, getBlockStorageFileName());
	}
	
	private static String getBlockStorageFileName() {
		return Reference.MOD_ID + "_blocks.json";
	}*/
	
	private static File getCurrentStorageFile(boolean globalData) {
		File dir = getCurrentConfigDirectory();
		
		if(dir.exists() == false && dir.mkdirs() == false) {
			Watson.logger.warn("Failed to create the config directory '{}'", dir.getAbsolutePath());
		}
		
		return new File(dir, getStorageFileName(globalData));
	}
	
	private static String getStorageFileName(boolean globalData) {
		Minecraft mc = Minecraft.getInstance();
		String name = StringUtils.getWorldOrServerName();
		
		if(name != null) {
			if(globalData) {
				return Reference.MOD_ID + "_" + name + ".json";
			} else {
				return Reference.MOD_ID + "_" + name + "_dim" + WorldUtils.getDimensionId(mc.world) + ".json";
			}
		}
		
		return Reference.MOD_ID + "_default.json";
	}
	
	public static String getServerIP() {
		Minecraft mc = Minecraft.getInstance();
		ServerData serverData = mc.getCurrentServerData();
		if(!mc.isSingleplayer() && serverData != null) {
			return serverData.serverIP;
		} else {
			return null;
		}
	}
	
	public static void saveBlockEditFile(String fileName) {
		if(fileName == null) {
			String player = (String) getEditSelection().getVariables().get("player");
			if(player == null) {
				ChatMessage.localError("No current player set, so you must specify a file name.", true);
				return;
			} else {
				Calendar calendar = Calendar.getInstance();
				fileName = String.format(Locale.US, "%s-%4d-%02d-%02d-%02d.%02d.%02d", player, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
			}
		}
		
		File file = new File(getPlayereditsBaseDirectory(), fileName);
		try {
			BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
			int editCount = edits.save(file);
			int annoCount = edits.getAnnotations().size();
			ChatMessage.localOutput(String.format(Locale.US, "Saved %d edits and %d annotations to %s", editCount, annoCount, fileName), true);
		} catch (IOException e) {
			Watson.logger.error("error saving BlockEditSet to " + file, e);
			ChatMessage.localError("The file " + fileName + " could not be saved.", true);
		}
	}
	
	public static void loadBlockEditFile(String fileName) {
		File file = new File(getPlayereditsBaseDirectory(), fileName);
		if(!file.canRead()) {
			File[] files = getInstance().getBlockEditFileList(fileName);
			if(files.length > 0) {
				file = files[files.length - 1];
			}
		}
		
		if(file.canRead()) {
			try {
				BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
				int editCount = edits.load(file);
				int annoCount = edits.getAnnotations().size();
				ChatMessage.localOutput(String.format(Locale.US, "Loaded %d edits and %d annotations from %s", editCount, annoCount, file.getName()), true);
			} catch (Exception e) {
				Watson.logger.error("error loading BlockEditSet from " + file, e);
				ChatMessage.localError("The file " + fileName + " could not be loaded.", true);
			}
		} else {
			ChatMessage.localError("Can't open " + fileName + " to read.", true);
		}
	}
	
	public static void listBlockEditFiles(String prefix, int page) {
		File[] files = getInstance().getBlockEditFileList(prefix);
		if(files.length == 0) {
			ChatMessage.localOutput("No matching files.", true);
		} else {
			if(files.length == 1) {
				ChatMessage.localOutput("1 matching file:", true);
			} else {
				ChatMessage.localOutput(files.length + " matching files:", true);
			}
			
			int pages = (files.length + Configs.Generic.PAGE_LINES.getIntegerValue() - 1) / Configs.Generic.PAGE_LINES.getIntegerValue();
			if(page > pages) {
				ChatMessage.localError(String.format(Locale.US, "The highest page is %d.", page), true);
			} else {
				ChatMessage.localOutput(String.format(Locale.US, "Page %d of %d.", page, pages), true);
				
				int start = (page - 1) * Configs.Generic.PAGE_LINES.getIntegerValue();
				int end = Math.min(files.length, page * Configs.Generic.PAGE_LINES.getIntegerValue());
				
				for(int i = start; i < end; ++i) {
					ChatMessage.localOutput("     " + files[i].getName(), true);
				}
				
				ChatMessage.localOutput(String.format(Locale.US, "Page %d of %d.", page, pages), true);
				if(page < pages) {
					ChatMessage.localOutput(String.format(Locale.US, "Use \"/%s file list %s %d\" to see the next page.", Configs.Generic.WATSON_PREFIX.getStringValue(), prefix, (page + 1)), true);
				}
			}
		}
	}
	
	public static void deleteBlockEditFiles(String prefix) {
		File[] files = getInstance().getBlockEditFileList(prefix);
		if(files.length > 0) {
			int failed = 0;
			for(File file : files) {
				if(file.delete()) {
					ChatMessage.localOutput("Deleted " + file.getName(), true);
				} else {
					++failed;
				}
			}
			String message = String.format(Locale.US, "Deleted %d out of %d save files matching \"%s\".", (files.length - failed), files.length, prefix);
			if(failed == 0) {
				ChatMessage.localOutput(message, true);
			} else {
				ChatMessage.localError(message, true);
			}
		} else {
			ChatMessage.localOutput(String.format(Locale.US, "There are no save files matching \"%s\".", prefix), true);
		}
	}
	
	public static void expireBlockEditFiles(String date) {
		Matcher m = DATE_PATTERN.matcher(date);
		if(m.matches()) {
			Calendar expiry = Calendar.getInstance();
			long expiryTime;
			try {
				int year = Integer.parseInt(m.group(1));
				int month = Integer.parseInt(m.group(2));
				int day = Integer.parseInt(m.group(3));
				
				expiry.setLenient(false);
				expiry.set(year, month - 1, day, 0, 0);
				
				expiryTime = expiry.getTimeInMillis();
			} catch (Exception e) {
				ChatMessage.localError(date + " is not a valid date of the form YYYY-MM-DD.", true);
				return;
			}
			
			int deleted = 0;
			int failed = 0;
			File[] files = getInstance().getBlockEditFileList("*");
			for(File file : files) {
				if(file.lastModified() < expiryTime) {
					if(file.delete()) {
						++deleted;
						ChatMessage.localOutput("Deleted " + file.getName(), true);
					} else {
						++failed;
						ChatMessage.localError("Could not delete " + file.getName(), true);
					}
				}
			}
			if(deleted + failed == 0) {
				ChatMessage.localOutput("There are no save files older than " + date + " 00:00:00 to delete.", true);
			} else {
				String message = String.format(Locale.US, "Deleted %d out of %d save files older than %s 00:00:00", deleted, deleted + failed, date);
				if(failed == 0) {
					ChatMessage.localOutput(message, true);
				} else {
					ChatMessage.localError(message, true);
				}
			}
		} else {
			ChatMessage.localError("The date must take the form YYYY-MM-DD.", true);
		}
	}
	
	public File[] getBlockEditFileList(String prefix) {
		File[] files = getPlayereditsBaseDirectory().listFiles(new CaseInsensitivePrefixFileFilter(prefix));
		Arrays.sort(files);
		return files;
	}
	
	public static void configure(GameType gameMode) {
		Configs.Generic.DISPLAYED.setBooleanValue(gameMode.isCreative());
	}
	
	public class CaseInsensitivePrefixFileFilter implements FileFilter {
		protected String _lowerPrefix;
		
		public CaseInsensitivePrefixFileFilter(String prefix) {
			_lowerPrefix = (prefix == null || prefix.equals("*")) ? "" : prefix.toLowerCase();
		}

		@Override
		public boolean accept(File file) {
			return file.isFile() && file.canRead() && (_lowerPrefix.length() == 0 || file.getName().toLowerCase().startsWith(_lowerPrefix));
		}
	}
}
