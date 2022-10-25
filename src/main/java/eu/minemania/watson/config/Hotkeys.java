package eu.minemania.watson.config;

import malilib.config.option.HotkeyConfig;

import java.util.List;

import com.google.common.collect.ImmutableList;
import malilib.input.KeyBindSettings;

/**
 * Default hotkeys configuration.
 */
public class Hotkeys
{
    public static final HotkeyConfig KEYBIND_COMMAND_CO_INSPECT = new HotkeyConfig("coreInspect", "");
    public static final HotkeyConfig KEYBIND_CURSOR_NEXT = new HotkeyConfig("cursornext", "");
    public static final HotkeyConfig KEYBIND_CURSOR_PREV = new HotkeyConfig("cursorprev", "");
    public static final HotkeyConfig KEYBIND_QUERY_AFTER = new HotkeyConfig("queryafter", "");
    public static final HotkeyConfig KEYBIND_QUERY_BEFORE = new HotkeyConfig("querybefore", "");
    public static final HotkeyConfig KEYBIND_SCREENSHOT = new HotkeyConfig("screenshot", "F12");
    public static final HotkeyConfig KEYBIND_TP_CURSOR = new HotkeyConfig("tpcursor", "");
    public static final HotkeyConfig KEYBIND_TP_NEXT = new HotkeyConfig("tpnext", "");
    public static final HotkeyConfig KEYBIND_TP_NEXT_ANNO = new HotkeyConfig("tpnextanno", "");
    public static final HotkeyConfig KEYBIND_TP_PREV = new HotkeyConfig("tpprev", "");
    public static final HotkeyConfig KEYBIND_TP_PREV_ANNO = new HotkeyConfig("tpprevanno", "");
    public static final HotkeyConfig KEYBIND_WATSON_CLEAR = new HotkeyConfig("watsonClear", "");
    public static final HotkeyConfig OPEN_GUI_MAIN_MENU = new HotkeyConfig("openGuiMainMenu", "L", KeyBindSettings.INGAME_RELEASE_EXCLUSIVE);
    public static final HotkeyConfig OPEN_GUI_SETTINGS = new HotkeyConfig("openGuiSettings", "L,C");

    public static final List<HotkeyConfig> HOTKEY_LIST = ImmutableList.of(
            KEYBIND_COMMAND_CO_INSPECT,
            KEYBIND_CURSOR_NEXT,
            KEYBIND_CURSOR_PREV,
            KEYBIND_QUERY_AFTER,
            KEYBIND_QUERY_BEFORE,
            KEYBIND_SCREENSHOT,
            KEYBIND_TP_CURSOR,
            KEYBIND_TP_NEXT,
            KEYBIND_TP_NEXT_ANNO,
            KEYBIND_TP_PREV,
            KEYBIND_TP_PREV_ANNO,
            KEYBIND_WATSON_CLEAR,
            OPEN_GUI_MAIN_MENU,
            OPEN_GUI_SETTINGS
    );
}