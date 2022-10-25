package eu.minemania.watson.gui.widgets;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.gui.Icons;
/*import malilib.gui.LeftRight;
import malilib.gui.widgets.WidgetListStringSelection;
import malilib.gui.widgets.WidgetSearchBar;
import malilib.interfaces.IStringListProvider;*/

import java.util.ArrayList;
import java.util.List;

/*public class WidgetListStringSelectionWithSearch extends WidgetListStringSelection
{
    public WidgetListStringSelectionWithSearch(int x, int y, int width, int height, IStringListProvider stringProvider, boolean hasSearch, List<String> entries)
    {
        super(x, y, width, height, stringProvider);
        if (hasSearch)
        {
            this.widgetSearchBar = new WidgetSearchBar(x + 2, y + 4, width - 14, 14, 0, Icons.FILE_ICON_SEARCH, LeftRight.RIGHT);
            this.browserEntriesOffsetY = this.widgetSearchBar.getHeight() + 3;
        }
        this.selectedEntries.addAll(entries == null ? new ArrayList<>() : entries);
    }

    @Override
    protected List<String> getEntryStringsForFilter(String entry)
    {
        String name = entry.toLowerCase();

        return ImmutableList.of(name);
    }
}*/
