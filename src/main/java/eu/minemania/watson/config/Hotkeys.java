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
    public static final ConfigHotkey KEYBIND_AUTO_PAGE = new ConfigHotkey("autopage", "", "watson.description.hotkey.keybind_auto_page");
    public static final ConfigHotkey KEYBIND_CURSOR_NEXT = new ConfigHotkey("cursornext", "", "watson.description.hotkey.keybind_cursor_next");
    public static final ConfigHotkey KEYBIND_CURSOR_PREV = new ConfigHotkey("cursorprev", "", "watson.description.hotkey.keybind_cursor_prev");
    public static final ConfigHotkey KEYBIND_QUERY_AFTER = new ConfigHotkey("queryafter", "", "watson.description.hotkey.keybind_query_after");
    public static final ConfigHotkey KEYBIND_QUERY_BEFORE = new ConfigHotkey("querybefore", "", "watson.description.hotkey.keybind_query_before");
    public static final ConfigHotkey KEYBIND_SCREENSHOT = new ConfigHotkey("screenshot", "F12", "watson.description.hotkey.keybind_screenshot");
    public static final ConfigHotkey KEYBIND_TP_CURSOR = new ConfigHotkey("tpcursor", "", "watson.description.hotkey.keybind_tp_cursor");
    public static final ConfigHotkey KEYBIND_TP_NEXT = new ConfigHotkey("tpnext", "", "watson.description.hotkey.keybind_tp_next");
    public static final ConfigHotkey KEYBIND_TP_PREV = new ConfigHotkey("tpprev", "", "watson.description.hotkey.keybind_tp_prev");
    public static final ConfigHotkey KEYBIND_WATSON_CLEAR = new ConfigHotkey("watsonClear", "", "watson.description.hotkey.keybind_watson_clear");
    public static final ConfigHotkey OPEN_GUI_MAIN_MENU = new ConfigHotkey("openGuiMainMenu", "L", KeybindSettings.RELEASE_EXCLUSIVE, "watson.description.hotkey.open_gui_main_menu");
    public static final ConfigHotkey OPEN_GUI_SETTINGS = new ConfigHotkey("openGuiSettings", "L,C", "watson.description.hotkey.open_gui_settings");

    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            KEYBIND_AUTO_PAGE,
            KEYBIND_CURSOR_NEXT,
            KEYBIND_CURSOR_PREV,
            KEYBIND_QUERY_AFTER,
            KEYBIND_QUERY_BEFORE,
            KEYBIND_SCREENSHOT,
            KEYBIND_TP_CURSOR,
            KEYBIND_TP_NEXT,
            KEYBIND_TP_PREV,
            KEYBIND_WATSON_CLEAR,
            OPEN_GUI_MAIN_MENU,
            OPEN_GUI_SETTINGS
    );
}