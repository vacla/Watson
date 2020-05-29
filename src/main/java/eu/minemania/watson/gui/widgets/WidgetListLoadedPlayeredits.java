package eu.minemania.watson.gui.widgets;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.gui.GuiPlayereditLoadedList;
import eu.minemania.watson.gui.Icons;
import eu.minemania.watson.selection.LoadedPlayereditSorter;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;

public class WidgetListLoadedPlayeredits extends WidgetListBase<PlayereditSet, WidgetPlayereditEntry>
{
    private final LoadedPlayereditSorter sorter;

    public WidgetListLoadedPlayeredits(int x, int y, int width, int height,
            ISelectionListener<PlayereditSet> selectionListener, GuiPlayereditLoadedList parent)
    {
        super(x, y, width, height, selectionListener);

        this.browserEntryHeight = 22;
        this.widgetSearchBar = new WidgetSearchBar(x + 2, y + 4, width - 14, 14, 0, Icons.FILE_ICON_SEARCH, LeftRight.LEFT);
        this.browserEntriesOffsetY = this.widgetSearchBar.getHeight() + 3;
        this.shouldSortList = true;
        this.sorter = new LoadedPlayereditSorter(parent);
    }

    @Override
    protected Collection<PlayereditSet> getAllEntries()
    {
        return DataManager.getEditSelection().getBlockEditSet().getPlayereditSet().values();
    }

    @Override
    protected Comparator<PlayereditSet> getComparator()
    {
        return this.sorter;
    }

    @Override
    protected List<String> getEntryStringsForFilter(PlayereditSet entry)
    {
        String name = entry.getPlayer().toLowerCase();

        if(name != null)
        {
            return ImmutableList.of(name);
        }
        else
        {
            return ImmutableList.of();
        }
    }

    @Override
    protected WidgetPlayereditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd,
            PlayereditSet entry)
    {
        return new WidgetPlayereditEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex, this);
    }

}
