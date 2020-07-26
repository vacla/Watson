package eu.minemania.watson.gui;

import javax.annotation.Nullable;

import eu.minemania.watson.Reference;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.screen.Screen;

public class GuiMainMenu extends GuiBase
{
    public GuiMainMenu()
    {
        String version = String.format("v%s", Reference.MOD_VERSION);
        this.title = StringUtils.translate("watson.gui.title.watson_main_menu", version);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = 12;
        int y = 30;
        int width = this.getButtonWidth();

        this.createChangeMenuButton(x, y, width, ButtonListenerChangeMenu.ButtonType.CONFIGURATION);
        if (mc.player != null)
        {
            y += 22;
            this.createChangeMenuButton(x, y, width, ButtonListenerChangeMenu.ButtonType.PLAYEREDIT_LOADED);
        }
    }

    private void createChangeMenuButton(int x, int y, int width, ButtonListenerChangeMenu.ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, type.getDisplayName(), type.getIcon());

        this.addButton(button, new ButtonListenerChangeMenu(type, this));
    }

    private int getButtonWidth()
    {
        int width = 0;

        for (ButtonListenerChangeMenu.ButtonType type : ButtonListenerChangeMenu.ButtonType.values())
        {
            width = Math.max(width, this.getStringWidth(type.getDisplayName()) + 30);
        }

        return width;
    }

    public static class ButtonListenerChangeMenu implements IButtonActionListener
    {
        private final ButtonType type;
        @Nullable
        private final Screen parent;

        public ButtonListenerChangeMenu(ButtonType type, @Nullable Screen parent)
        {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            GuiBase gui = null;

            switch (this.type)
            {
                case CONFIGURATION:
                    GuiBase.openGui(new GuiConfigs());
                    return;
                case MAIN_MENU:
                    gui = new GuiMainMenu();
                    break;
                case PLAYEREDIT_LOADED:
                    gui = new GuiPlayereditLoadedList();
                    break;
            }

            if (gui != null)
            {
                gui.setParent(this.parent);
                GuiBase.openGui(gui);
            }
        }

        public enum ButtonType
        {
            CONFIGURATION("watson.gui.button.change_menu.configuration_menu", ButtonIcons.CONFIGURATION),
            MAIN_MENU("watson.gui.button.change_menu.to_main_menu", null),
            PLAYEREDIT_LOADED("watson.gui.button.change_menu.playeredit_loaded_menu", null);

            private final String labelKey;
            private final ButtonIcons icon;

            ButtonType(String labelKey, ButtonIcons icon)
            {
                this.labelKey = labelKey;
                this.icon = icon;
            }

            public String getLabelKey()
            {
                return this.labelKey;
            }

            public String getDisplayName()
            {
                return StringUtils.translate(this.getLabelKey());
            }

            public ButtonIcons getIcon()
            {
                return this.icon;
            }
        }
    }
}