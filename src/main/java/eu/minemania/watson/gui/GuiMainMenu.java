package eu.minemania.watson.gui;

import eu.minemania.watson.Reference;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Plugins;
import malilib.gui.BaseScreen;
import malilib.gui.widget.InteractableWidget;
import malilib.gui.widget.button.GenericButton;

import java.util.ArrayList;
import java.util.List;

public class GuiMainMenu extends BaseScreen
{
    protected final GenericButton configScreenButton;
    protected final GenericButton playereditLoadedScreenButton;
    protected final GenericButton ledgerScreenButton;
    protected int equalWidthWidgetMaxWidth = -1;

    public GuiMainMenu()
    {
        this.configScreenButton = GenericButton.create("watson.gui.button.change_menu.configuration_menu", Icons.CONFIGURATION);
        this.playereditLoadedScreenButton = GenericButton.create("watson.gui.button.change_menu.playeredit_loaded_menu");
        this.ledgerScreenButton = GenericButton.create("watson.gui.button.change_menu.ledger_menu");

        this.configScreenButton.setActionListener(ConfigScreen::openConfigScreen);
        this.playereditLoadedScreenButton.setActionListener(() -> openScreenWithParent(new GuiPlayereditLoadedList()));
        this.ledgerScreenButton.setActionListener(() -> openScreenWithParent(new GuiLedger()));

        String version = String.format("v%s", Reference.MOD_VERSION);
        this.setTitle("watson.gui.title.watson_main_menu", version);
    }

    @Override
    public void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        List<InteractableWidget> list = new ArrayList<>();

        boolean isInitial = this.equalWidthWidgetMaxWidth < 0;
        this.equalWidthWidgetMaxWidth = 0;

        this.addEqualWidthWidget(this.configScreenButton, list);
        if (mc.player != null)
        {
            this.addEqualWidthWidget(this.playereditLoadedScreenButton, list);
            if (Configs.Plugin.PLUGIN.getValue().equals(Plugins.LEDGER))
            {
                isInitial = true;
                this.addEqualWidthWidget(this.ledgerScreenButton, list);
            }
        }

        if (isInitial)
        {
            int width = this.equalWidthWidgetMaxWidth + 10;
            for (InteractableWidget widget : list)
            {
                widget.setWidth(width);
            }
        }
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 12;
        int y = this.y + 30;
        this.configScreenButton.setPosition(x, y);
        this.playereditLoadedScreenButton.setPosition(x, y + 22);
        this.ledgerScreenButton.setPosition(x, y + 44);
    }

    protected void addEqualWidthWidget(InteractableWidget widget, List<InteractableWidget> widgets)
    {
        this.equalWidthWidgetMaxWidth = Math.max(this.equalWidthWidgetMaxWidth, widget.getWidth());
        widgets.add(widget);
        widget.setAutomaticWidth(false);
        this.addWidget(widget);
    }

    public static void openMainMenu()
    {
        GuiMainMenu screen = new GuiMainMenu();
        BaseScreen.openScreen(screen);
    }
}