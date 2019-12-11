package eu.minemania.watson.config;

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

import java.util.List;
import com.google.common.collect.ImmutableList;

/**
 * Default hotkeys configuration.
 */
public class Hotkeys
{
	public static final ConfigHotkey KEYBIND_SCREENSHOT = new ConfigHotkey("screenshot", "F12", "Take a screenshot");
	public static final ConfigHotkey KEYBIND_TP_NEXT = new ConfigHotkey("tpnext", "RIGHT", "TP to next ore");
	public static final ConfigHotkey KEYBIND_TP_PREV = new ConfigHotkey("tpprev", "LEFT", "TP to previous ore");
	public static final ConfigHotkey KEYBIND_QUERY_BEFORE = new ConfigHotkey("querybefore", "BUTTON_1", "Query edits before\n(Set Plugin in Generic to LogBlock and only works with LogBlock)");
	public static final ConfigHotkey KEYBIND_QUERY_AFTER = new ConfigHotkey("queryafter", "BUTTON_2", "Query edits after\n(Set Plugin in Generic to LogBlock and only works with LogBlock)");
	public static final ConfigHotkey KEYBIND_CURSOR_NEXT = new ConfigHotkey("cursornext", "UP", "Cursor to next edit");
	public static final ConfigHotkey KEYBIND_CURSOR_PREV = new ConfigHotkey("cursorprev", "DOWN", "Cursor to previous edit");
	public static final ConfigHotkey KEYBIND_TP_CURSOR = new ConfigHotkey("tpcursor", "BUTTON_1", "TP to cursor");
	public static final ConfigHotkey OPEN_GUI_SETTINGS = new ConfigHotkey("openGuiSettings", "L,C",  "Open the Config GUI");
	public static final ConfigHotkey OPEN_GUI_MAIN_MENU = new ConfigHotkey("openGuiMainMenu", "L", KeybindSettings.RELEASE_EXCLUSIVE, "Open the Watson main menu");

	public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
			KEYBIND_SCREENSHOT,
			KEYBIND_TP_NEXT,
			KEYBIND_TP_PREV,
			KEYBIND_QUERY_BEFORE,
			KEYBIND_QUERY_AFTER,
			KEYBIND_CURSOR_NEXT,
			KEYBIND_CURSOR_PREV,
			KEYBIND_TP_CURSOR,
			OPEN_GUI_SETTINGS,
			OPEN_GUI_MAIN_MENU
			);
}

