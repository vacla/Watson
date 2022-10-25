package eu.minemania.watson.gui.widgets;

import java.util.Comparator;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.db.data.EditListBlockedit;
import eu.minemania.watson.gui.GuiBlockeditData;
import eu.minemania.watson.selection.PlayereditEntry;
import malilib.config.value.SortDirection;
import malilib.gui.BaseScreen;
import malilib.gui.util.ElementOffset;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.InteractableWidget;
import malilib.gui.widget.ItemStackWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.ListEntryWidgetInitializer;
import malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.header.DataColumn;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;

public class WidgetEditsEntry extends BaseDataListEntryWidget<PlayereditEntry>
{
    public static final DataColumn<PlayereditEntry> ITEM_COLUMN = new DataColumn<>("watson.gui.label.edits.title.item", Comparator.comparing((e) -> e.getStack().getName().getString()));
    public static final DataColumn<PlayereditEntry> BROKEN_COLUMN = new DataColumn<>("watson.gui.label.edits.title.broken", Comparator.comparingLong(PlayereditEntry::getCountBroken), SortDirection.DESCENDING);
    public static final DataColumn<PlayereditEntry> PLACED_COLUMN = new DataColumn<>("watson.gui.label.edits.title.placed", Comparator.comparingLong(PlayereditEntry::getCountPlaced), SortDirection.DESCENDING);
    public static final DataColumn<PlayereditEntry> CONTADDED_COLUMN = new DataColumn<>("watson.gui.label.edits.title.contadded", Comparator.comparingLong(PlayereditEntry::getCountContAdded), SortDirection.DESCENDING);
    public static final DataColumn<PlayereditEntry> CONTREMOVED_COLUMN = new DataColumn<>("watson.gui.label.edits.title.contremoved", Comparator.comparingLong(PlayereditEntry::getCountContRemoved), SortDirection.DESCENDING);
    public static final DataColumn<PlayereditEntry> TOTAL_COLUMN = new DataColumn<>("watson.gui.label.edits.title.total", Comparator.comparingLong(PlayereditEntry::getCountTotal), SortDirection.DESCENDING);
    public static final ImmutableList<DataColumn<PlayereditEntry>> COLUMNS = ImmutableList.of(ITEM_COLUMN, BROKEN_COLUMN, PLACED_COLUMN, CONTADDED_COLUMN, CONTREMOVED_COLUMN, TOTAL_COLUMN);
    protected int brokenColumnRight;
    protected int placedColumnRight;
    protected int contAddedColumnRight;
    protected int contRemovedColumnRight;
    protected int totalColumnRight;
    protected final ItemStackWidget itemStackWidget;
    protected final GenericButton blocksButton;
    protected final StyledTextLine brokenText;
    protected final StyledTextLine placedText;
    protected final StyledTextLine containerAddedText;
    protected final StyledTextLine containerRemovedText;
    protected final StyledTextLine totalText;

    public WidgetEditsEntry(PlayereditEntry entry, DataListEntryWidgetData constructData)
    {
        super(entry, constructData);

        this.itemStackWidget = new ItemStackWidget(entry.getStack());
        this.blocksButton = GenericButton.create("watson.gui.button.edits.blocks", this::blocksEntry);
        this.setText(StyledTextLine.of(entry.getStack().getName().getString()));
        this.getTextOffset().setXOffset(22);
        this.hoverInfoWidget = new EditsEntryHoverInfoWidget();

        this.brokenText = StyledTextLine.of(String.valueOf(entry.getCountBroken()));
        this.placedText = StyledTextLine.of(String.valueOf(entry.getCountPlaced()));
        this.containerAddedText = StyledTextLine.of(String.valueOf(entry.getCountContAdded()));
        this.containerRemovedText = StyledTextLine.of(String.valueOf(entry.getCountContRemoved()));
        this.totalText = StyledTextLine.of(String.valueOf(entry.getCountTotal()));

        this.getBackgroundRenderer().getHoverSettings().setColor(0xA0707070);
        this.getBackgroundRenderer().getNormalSettings().setColor(this.isOdd ? 0xA0101010 : 0xA0303030);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.itemStackWidget);
        this.addWidget(this.blocksButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        this.itemStackWidget.setX(this.getX() + 2);
        this.itemStackWidget.centerVerticallyInside(this);

        this.blocksButton.setRight(this.getRight() - 2);
        this.blocksButton.centerVerticallyInside(this);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int textY = y + ElementOffset.getCenteredElementOffset(this.getHeight(), 8);
        int color = 0xFFFFFFFF;
        z += 0.0125f;
        this.renderTextLineRightAligned(x + this.brokenColumnRight, textY, z, color, true, this.brokenText, ctx);
        this.renderTextLineRightAligned(x + this.placedColumnRight, textY, z, color, true, this.placedText, ctx);
        this.renderTextLineRightAligned(x + this.contAddedColumnRight, textY, z, color, true, this.containerAddedText, ctx);
        this.renderTextLineRightAligned(x + this.contRemovedColumnRight, textY, z, color, true, this.containerRemovedText, ctx);
        this.renderTextLineRightAligned(x + this.totalColumnRight, textY, z, color, true, this.totalText, ctx);
    }

    protected void blocksEntry()
    {
        EditListBlockedit editList = new EditListBlockedit(this.getData().getBlocks(), true);
        BaseScreen.openScreen(new GuiBlockeditData(editList, this.getData().getStack().getTranslationKey()));
    }

    public static class WidgetInitializer implements ListEntryWidgetInitializer<PlayereditEntry>
    {
        @Override
        public void onListContentsRefreshed(DataListWidget<PlayereditEntry> dataListWidget, int entryWidgetWidth)
        {
            int nameColumnLength = 0;
            int maxBrokenLength = getRenderWidth(BROKEN_COLUMN.getName(), 40);
            int maxPlacedLength = getRenderWidth(PLACED_COLUMN.getName(), 40);
            int maxContAddedLength = getRenderWidth(CONTADDED_COLUMN.getName(), 40);
            int maxContRemovedLength = getRenderWidth(CONTREMOVED_COLUMN.getName(), 40);
            int maxTotalLength = getRenderWidth(TOTAL_COLUMN.getName(), 40);

            for (PlayereditEntry entry : dataListWidget.getNonFilteredDataList())
            {
                nameColumnLength = Math.max(nameColumnLength, StringUtils.getStringWidth(entry.getStack().getName().getString()));
            }

            int extra = 24;
            nameColumnLength += 32;
            maxBrokenLength += extra;
            maxPlacedLength += extra;
            maxContAddedLength += extra;
            maxContRemovedLength += extra;
            maxTotalLength += extra;
            int relativeStartX = 2;

            ITEM_COLUMN.setRelativeStartX(relativeStartX);
            ITEM_COLUMN.setWidth(nameColumnLength);
            relativeStartX += nameColumnLength + 2;

            BROKEN_COLUMN.setRelativeStartX(relativeStartX);
            BROKEN_COLUMN.setWidth(maxBrokenLength);
            relativeStartX += maxBrokenLength + 2;

            PLACED_COLUMN.setRelativeStartX(relativeStartX);
            PLACED_COLUMN.setWidth(maxPlacedLength);
            relativeStartX += maxPlacedLength + 2;

            CONTADDED_COLUMN.setRelativeStartX(relativeStartX);
            CONTADDED_COLUMN.setWidth(maxContAddedLength);
            relativeStartX += maxContAddedLength + 2;

            CONTREMOVED_COLUMN.setRelativeStartX(relativeStartX);
            CONTREMOVED_COLUMN.setWidth(maxContRemovedLength);
            relativeStartX += maxContRemovedLength + 2;

            TOTAL_COLUMN.setRelativeStartX(relativeStartX);
            TOTAL_COLUMN.setWidth(maxTotalLength);
        }

        @Override
        public void applyToEntryWidgets(DataListWidget<PlayereditEntry> dataListWidget)
        {
            int brokenColumnRight = BROKEN_COLUMN.getRelativeRight() - 3;
            int placedColumnRight = PLACED_COLUMN.getRelativeRight() - 3;
            int contAddedColumnRight = CONTADDED_COLUMN.getRelativeRight() - 3;
            int contRemovedColumnRight = CONTREMOVED_COLUMN.getRelativeRight() - 3;
            int totalColumnRight = TOTAL_COLUMN.getRelativeRight() - 3;

            for (InteractableWidget w : dataListWidget.getEntryWidgetList())
            {
                if (w instanceof WidgetEditsEntry widget)
                {
                    widget.brokenColumnRight = brokenColumnRight;
                    widget.placedColumnRight = placedColumnRight;
                    widget.contAddedColumnRight = contAddedColumnRight;
                    widget.contRemovedColumnRight = contRemovedColumnRight;
                    widget.totalColumnRight = totalColumnRight;
                }
            }
        }

        protected static int getRenderWidth(Optional<StyledTextLine> optional, int minWidth)
        {
            int width = optional.map(styledTextLine -> styledTextLine.renderWidth).orElse(minWidth);
            return Math.max(width, minWidth);
        }
    }
}
