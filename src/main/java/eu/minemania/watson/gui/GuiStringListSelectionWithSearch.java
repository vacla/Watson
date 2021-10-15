package eu.minemania.watson.gui;

import eu.minemania.watson.gui.widgets.WidgetListStringSelectionWithSearch;
import fi.dy.masa.malilib.gui.GuiStringListSelection;
import fi.dy.masa.malilib.gui.interfaces.IStringListConsumer;
import fi.dy.masa.malilib.gui.widgets.WidgetListStringSelection;

import java.util.Collection;
import java.util.List;

public class GuiStringListSelectionWithSearch extends GuiStringListSelection
{
    private List<String> entries;
    private boolean hasSearch;

    public GuiStringListSelectionWithSearch(Collection<String> strings, IStringListConsumer consumer, boolean hasSearch, List<String> entries)
    {
        super(strings, consumer);
        this.hasSearch = hasSearch;
        this.entries = entries;
    }

    @Override
    protected WidgetListStringSelection createListWidget(int listX, int listY)
    {
        return new WidgetListStringSelectionWithSearch(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this, this.hasSearch, this.entries);
    }
}
