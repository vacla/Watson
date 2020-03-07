package eu.minemania.watson.gui;

import java.util.Collections;
import java.util.List;

import eu.minemania.watson.Reference;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Hotkeys;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiConfigs extends GuiConfigsBase
{
    public GuiConfigs()
    {
        super(10, 50, Reference.MOD_ID, null, "watson.gui.title.configs");
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        x += this.createButton(x, y, -1, ConfigGuiTab.GENERIC);
        x += this.createButton(x, y, -1, ConfigGuiTab.LISTS);
        x += this.createButton(x, y, -1, ConfigGuiTab.HOTKEYS);
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(DataManager.getConfigGuiTab() != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    protected int getConfigWidth()
    {
        ConfigGuiTab tab = DataManager.getConfigGuiTab();

        if(tab == ConfigGuiTab.GENERIC || tab == ConfigGuiTab.LISTS)
        {
            return 140;
        }

        return super.getConfigWidth();
    }

    @Override
    protected boolean useKeybindSearch()
    {
        return DataManager.getConfigGuiTab() == ConfigGuiTab.HOTKEYS;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = DataManager.getConfigGuiTab();

        if(tab == ConfigGuiTab.GENERIC)
        {
            configs = Configs.Generic.OPTIONS;
        }
        else if(tab == ConfigGuiTab.LISTS)
        {
            configs = Configs.Lists.OPTIONS;
        }
        else if(tab == ConfigGuiTab.HOTKEYS)
        {
            configs = Hotkeys.HOTKEY_LIST;
        }
        else
        {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

    @Override
    protected void onSettingsChanged()
    {
        super.onSettingsChanged();
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final GuiConfigs parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, GuiConfigs parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            DataManager.setConfigGuiTab(this.tab);

            this.parent.reCreateListWidget();
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum ConfigGuiTab
    {
        GENERIC ("watson.gui.button.config_gui.generic"),
        LISTS ("watson.gui.button.config_gui.lists"),
        HOTKEYS ("watson.gui.button.config_gui.hotkeys");

        private final String translationKey;

        private ConfigGuiTab(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}