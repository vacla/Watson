package eu.minemania.watson.db.data;

import java.util.Collection;
import java.util.List;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.gui.GuiBlockeditData;
import eu.minemania.watson.selection.PlayereditUtils;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;

public class WidgetListBlockedit extends WidgetListBase<BlockeditEntry, WidgetBlockeditEntry>
{
    private static int lastScrollbarPosition;

    private final GuiBlockeditData gui;
    private boolean scrollbarRestored;

    public WidgetListBlockedit(int x, int y, int width, int height, GuiBlockeditData parent)
    {
        super(x, y, width, height, null);

        this.browserEntryHeight = 26;
        this.gui = parent;
        this.setParent(parent);
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        super.drawContents(mouseX, mouseY, partialTicks);
        lastScrollbarPosition = this.scrollBar.getValue();
    }

    @Override
    protected void offsetSelectionOrScrollbar(int amount, boolean changeSelection)
    {
        super.offsetSelectionOrScrollbar(amount, changeSelection);
        lastScrollbarPosition = this.scrollBar.getValue();
    }

    @Override
    protected WidgetBlockeditEntry createHeaderWidget(int x, int y, int listIndexStart, int usableHeight, int usedHeight)
    {
        int height = this.browserEntryHeight;

        if((usedHeight + height) > usableHeight)
        {
            return null;
        }

        return this.createListEntryWidget(x, y, listIndexStart, true, null);
    }

    @Override
    protected Collection<BlockeditEntry> getAllEntries()
    {
        return this.gui.getDisplay().getBlockeditAll();
    }

    @Override
    protected void addFilteredContents(Collection<BlockeditEntry> entries)
    {
        for (BlockeditEntry entry : entries)
        {
            if(this.gui.getTime() == 0 || this.entryMatchesFilter(entry, ""))
            {
                this.listContents.add(entry);
            }
        }
    }

    @Override
    protected boolean entryMatchesFilter(BlockeditEntry entry, String filterText)
    {
        List<String> entryStrings = this.getEntryStringsForFilter(entry);

        if (entryStrings.isEmpty())
        {
            return false;
        }

        return this.matchesFilter(entryStrings, filterText);
    }

    @Override
    protected List<String> getEntryStringsForFilter(BlockeditEntry entry)
    {
        if (this.gui.getTime() != 0 && entry.getEdit().time >= this.gui.getTime())
        {
            return ImmutableList.of(PlayereditUtils.blockString(entry.getEdit()));
        }
        else
        {
            return ImmutableList.of();
        }
    }

    @Override
    protected void refreshBrowserEntries()
    {
        super.refreshBrowserEntries();

        if(this.scrollbarRestored == false && lastScrollbarPosition <= this.scrollBar.getMaxValue())
        {
            this.scrollBar.setValue(lastScrollbarPosition);
            this.scrollbarRestored = true;
            this.reCreateListEntryWidgets();
        }
    }

    @Override
    protected boolean hasFilter()
    {
        if(this.gui.getTime() != 0)
        {
            return true;
        }
        return false;
    }

    @Override
    protected WidgetBlockeditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, BlockeditEntry entry)
    {
        return new WidgetBlockeditEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex);
    }
}
