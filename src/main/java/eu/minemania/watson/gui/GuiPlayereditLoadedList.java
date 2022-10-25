package eu.minemania.watson.gui;

import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.gui.widgets.WidgetPlayereditEntry;
import malilib.gui.BaseListScreen;
import malilib.gui.widget.CheckBoxWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GuiPlayereditLoadedList extends BaseListScreen<DataListWidget<PlayereditSet>>
{
    protected final CheckBoxWidget checkbox;
    protected final GenericButton mainMenuButton;

    protected GuiPlayereditLoadedList()
    {
        super(12, 30, 20, 68);

        this.checkbox = new CheckBoxWidget("watson.gui.label.playeredit.checkbox", (String) null);
        this.checkbox.setListener((v) -> this.getListWidget().refreshEntries());

        this.mainMenuButton = GenericButton.create("watson.gui.button.change_menu.to_main_menu", GuiMainMenu::openMainMenu);

        this.setTitle("watson.gui.title.loaded_playeredits");
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.checkbox);
        this.addWidget(this.mainMenuButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 17;

        this.checkbox.setPosition(x, y);

        this.mainMenuButton.setRight(this.getRight() - 10);
        this.mainMenuButton.setBottom(this.getBottom() - 3);
    }

    @Override
    protected DataListWidget<PlayereditSet> createListWidget()
    {
        Supplier<List<PlayereditSet>> supplier = this::getPlayerEdits;
        DataListWidget<PlayereditSet> listWidget = new DataListWidget<>(supplier, true);
        listWidget.addDefaultSearchBar();
        listWidget.setEntryFilter(WidgetPlayereditEntry::searchFilter);
        listWidget.setDataListEntryWidgetFactory(WidgetPlayereditEntry::new);
        listWidget.setParentScreen(this);
        listWidget.setListSortComparator(this::compare);
        return listWidget;
    }

    private int compare(PlayereditSet playereditSet, PlayereditSet playereditSet1)
    {
        int nameCompare = playereditSet.getPlayer().compareTo(playereditSet1.getPlayer());

        if (this.checkbox.isSelected())
        {
            return playereditSet.getBlockEditCount() == playereditSet1.getBlockEditCount() ? nameCompare : ((playereditSet.getBlockEditCount() > playereditSet1.getBlockEditCount()) ? -1 : 1);
        }

        return nameCompare * -1;
    }

    private List<PlayereditSet> getPlayerEdits()
    {
        return new ArrayList<>(DataManager.getEditSelection().getBlockEditSet().getPlayereditSet().values());
    }
}
