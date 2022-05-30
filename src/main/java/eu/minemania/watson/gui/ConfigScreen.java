package eu.minemania.watson.gui;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Reference;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Hotkeys;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.gui.tab.ScreenTab;
import fi.dy.masa.malilib.util.data.ModInfo;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;

public class ConfigScreen
{
    public static final ModInfo MOD_INFO = Reference.MOD_INFO;

    public static final BaseConfigTab GENERIC = new BaseConfigTab(MOD_INFO, "generic", 160, Configs.Generic.OPTIONS, ConfigScreen::create);
    public static final BaseConfigTab MESSAGES = new BaseConfigTab(MOD_INFO, "messages", 160, Configs.Messages.OPTIONS, ConfigScreen::create);
    public static final BaseConfigTab OUTLINES = new BaseConfigTab(MOD_INFO, "outlines", 160, Configs.Outlines.OPTIONS, ConfigScreen::create);
    public static final BaseConfigTab PLUGIN = new BaseConfigTab(MOD_INFO, "plugin", 160, Configs.Plugin.OPTIONS, ConfigScreen::create);
    public static final BaseConfigTab HIGHLIGHTS = new BaseConfigTab(MOD_INFO, "highlights", 160, Configs.Highlights.OPTIONS, ConfigScreen::create);
    public static final BaseConfigTab EDITS = new BaseConfigTab(MOD_INFO, "edits", 160, Configs.Edits.OPTIONS, ConfigScreen::create);
    public static final BaseConfigTab ANALYSIS = new BaseConfigTab(MOD_INFO, "analysis", 160, Configs.Analysis.OPTIONS, ConfigScreen::create);
    public static final BaseConfigTab LISTS = new BaseConfigTab(MOD_INFO, "lists", 160, Configs.Lists.OPTIONS, ConfigScreen::create);
    public static final BaseConfigTab HOTKEYS = new BaseConfigTab(MOD_INFO, "hotkeys", 160, getHotkeys(), ConfigScreen::create);

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return CONFIG_TABS;
    }

    public static final ImmutableList<ConfigTab> CONFIG_TABS = ImmutableList.of(
            GENERIC,
            MESSAGES,
            OUTLINES,
            PLUGIN,
            HIGHLIGHTS,
            EDITS,
            ANALYSIS,
            LISTS,
            HOTKEYS
    );

    public static final ImmutableList<ScreenTab> ALL_TABS = ImmutableList.of(
            GENERIC,
            MESSAGES,
            OUTLINES,
            PLUGIN,
            HIGHLIGHTS,
            EDITS,
            ANALYSIS,
            LISTS,
            HOTKEYS
    );

    public static BaseConfigScreen create()
    {
        return new BaseConfigScreen(MOD_INFO, null, ALL_TABS, GENERIC, "watson.title.screen.configs", Reference.MOD_VERSION);
    }

    public static BaseConfigScreen create(@Nullable Screen currentScreen)
    {
        return new BaseConfigScreen(MOD_INFO, null, ALL_TABS, GENERIC, "watson.title.screen.configs", Reference.MOD_VERSION);
    }

    public static BaseConfigScreen createOnTab(ConfigTab tab)
    {
        BaseConfigScreen screen = create();
        screen.setCurrentTab(tab);
        DataManager.setConfigGuiTab(tab);
        return screen;
    }

    public static void openConfigScreen()
    {
        BaseScreen.openScreen(create());
    }

    private static ImmutableList<ConfigInfo> getHotkeys()
    {
        ArrayList<ConfigInfo> list = new ArrayList<>(Hotkeys.HOTKEY_LIST);

        ConfigUtils.sortConfigsByDisplayName(list);

        return ImmutableList.copyOf(list);
    }
}