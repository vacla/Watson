package eu.minemania.watson.gui;

import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.gui.GuiMainMenu.ButtonListenerChangeMenu;
import eu.minemania.watson.gui.widgets.WidgetListLoadedPlayeredits;
import eu.minemania.watson.gui.widgets.WidgetPlayereditEntry;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetCheckBox;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiPlayereditLoadedList extends GuiListBase<PlayereditSet, WidgetPlayereditEntry, WidgetListLoadedPlayeredits>
{
    protected boolean checked;

    protected GuiPlayereditLoadedList()
    {
        super(12, 30);

        this.title = StringUtils.translate("watson.gui.title.loaded_playeredits");
    }

    @Override
    protected WidgetListLoadedPlayeredits createListWidget(int listX, int listY)
    {
        return new WidgetListLoadedPlayeredits(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), null, this);
    }

    @Override
    protected int getBrowserWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 68;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = 12;
        int y = this.height - 26;
        int buttonWidth;
        String label;
        ButtonGeneric button;

        label = StringUtils.translate("watson.gui.label.playeredit.checkbox");
        WidgetCheckBox cb = new WidgetCheckBox(x, y, Icons.CHECKBOX_UNSELECTED, Icons.CHECKBOX_SELECTED, label);
        cb.setChecked(false, false);
        cb.setListener(new CheckBoxListener(this));
        this.addWidget(cb);
        ButtonListenerChangeMenu.ButtonType type = ButtonListenerChangeMenu.ButtonType.MAIN_MENU;
        label = StringUtils.translate(type.getLabelKey());
        buttonWidth = this.getStringWidth(label) + 20;
        x = this.width - buttonWidth - 10;
        button = new ButtonGeneric(x, y, buttonWidth, 20, label);
        this.addButton(button, new ButtonListenerChangeMenu(type, this.getParent()));
    }

    public boolean isChecked()
    {
        return this.checked;
    }

    public static class CheckBoxListener implements ISelectionListener<WidgetCheckBox>
    {
        private final GuiPlayereditLoadedList parent;

        public CheckBoxListener(GuiPlayereditLoadedList parent)
        {
            this.parent = parent;
        }

        @Override
        public void onSelectionChange(WidgetCheckBox entry)
        {
            parent.checked = entry.isChecked();
            parent.getListWidget().refreshEntries();
        }
    }
}
