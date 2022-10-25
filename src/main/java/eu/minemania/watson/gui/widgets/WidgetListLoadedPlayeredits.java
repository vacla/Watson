package eu.minemania.watson.gui.widgets;

import java.util.*;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.gui.GuiPlayereditLoadedList;
import eu.minemania.watson.gui.Icons;
//import eu.minemania.watson.selection.LoadedPlayereditSorter;
import malilib.config.value.HorizontalAlignment;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.search.SearchBarWidget;

public class WidgetListLoadedPlayeredits /*extends DataListWidget<WidgetPlayereditEntry>*/
{
    /*private final LoadedPlayereditSorter sorter;
    private final GuiPlayereditLoadedList parent;

    public WidgetListLoadedPlayeredits(int x, int y, int width, int height, GuiPlayereditLoadedList parent)
    {
        super(Collections::emptyList, false);

        this.entryWidgetFixedHeight = 22;
        this.searchBarWidget = new SearchBarWidget(x + 2, y + 4, this::onSearchBarTextChanged, this::refreshFilteredEntries, Icons.FILE_ICON_SEARCH);
        this.searchBarWidget.setToggleButtonAlignment(HorizontalAlignment.LEFT);
        this.shouldSortList = true;
        this.setEntryFilterStringFunction((p) -> Collections.singletonList(p.render();));
        this.sorter = ;
        this.parent = parent;
    }

    protected GuiPlayereditLoadedList getGuiParent()
    {
        return this.parent;
    }

    @Override
    protected Collection<PlayereditSet> getNonFilteredDataList()
    {
        return DataManager.getEditSelection().getBlockEditSet().getPlayereditSet().values();
    }

    @Override
    protected Comparator<PlayereditSet> getComparator()
    {
        return this.sorter;
    }

    @Override
    protected WidgetPlayereditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd,
                                                          PlayereditSet entry)
    {
        return new WidgetPlayereditEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex, this);
    }
*/
}
