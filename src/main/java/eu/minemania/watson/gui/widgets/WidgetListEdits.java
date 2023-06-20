package eu.minemania.watson.gui.widgets;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.gui.GuiEdits;
import eu.minemania.watson.gui.Icons;
import eu.minemania.watson.selection.PlayereditEntry;
import eu.minemania.watson.selection.PlayereditSorter;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class WidgetListEdits extends WidgetListBase<PlayereditEntry, WidgetEditsEntry>
{
    private static int lastScrollbarPosition;
    private final GuiEdits gui;
    private final PlayereditSorter sorter;
    private boolean scrollbarRestored;

    public WidgetListEdits(int x, int y, int width, int height, GuiEdits parent)
    {
        super(x, y, width, height, null);

        this.browserEntryHeight = 22;
        this.gui = parent;
        this.widgetSearchBar = new WidgetSearchBar(x + 2, y + 8, width - 16, 14, 0, Icons.FILE_ICON_SEARCH, LeftRight.RIGHT);
        this.widgetSearchBar.setZLevel(1);
        this.sorter = new PlayereditSorter(parent.getEdits());
        this.shouldSortList = true;
    }

    protected GuiEdits getGuiParent()
    {
        return this.gui;
    }

    @Override
    public void drawContents(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        super.drawContents(drawContext, mouseX, mouseY, partialTicks);
        lastScrollbarPosition = this.scrollBar.getValue();
    }

    @Override
    public void offsetSelectionOrScrollbar(int amount, boolean changeSelection)
    {
        super.offsetSelectionOrScrollbar(amount, changeSelection);
        lastScrollbarPosition = this.scrollBar.getValue();
    }

    @Override
    protected WidgetEditsEntry createHeaderWidget(int x, int y, int listIndexStart, int usableHeight, int usedHeight)
    {
        int height = this.browserEntryHeight;

        if ((usedHeight + height) > usableHeight)
        {
            return null;
        }

        return this.createListEntryWidget(x, y, listIndexStart, true, null);
    }

    @Override
    protected Collection<PlayereditEntry> getAllEntries()
    {
        return this.gui.getEdits().getPlayereditsAll();
    }

    @Override
    protected Comparator<PlayereditEntry> getComparator()
    {
        return this.sorter;
    }

    @Override
    protected List<String> getEntryStringsForFilter(PlayereditEntry entry)
    {
        ItemStack stack = entry.getStack();
        Identifier rl = Registries.ITEM.getId(stack.getItem());

        if (rl != null)
        {
            return ImmutableList.of(stack.getName().getString().toLowerCase(), rl.toString().toLowerCase());
        }
        else
        {
            return ImmutableList.of(stack.getName().getString().toLowerCase());
        }
    }

    @Override
    protected void refreshBrowserEntries()
    {
        super.refreshBrowserEntries();

        if (!this.scrollbarRestored && lastScrollbarPosition <= this.scrollBar.getMaxValue())
        {
            this.scrollBar.setValue(lastScrollbarPosition);
            this.scrollbarRestored = true;
            this.reCreateListEntryWidgets();
        }
    }

    @Override
    protected WidgetEditsEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, PlayereditEntry entry)
    {
        return new WidgetEditsEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, this.gui.getEdits(), entry, listIndex, this);
    }

}
