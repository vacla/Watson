package eu.minemania.watson.db.data;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.gui.widgets.BlockeditEntryHoverInfoWidget;
import eu.minemania.watson.selection.PlayereditUtils;
import malilib.gui.util.ElementOffset;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.InteractableWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.ListEntryWidgetInitializer;
import malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.header.DataColumn;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;

import java.util.Optional;

public class WidgetBlockeditEntry extends BaseDataListEntryWidget<BlockeditEntry>
{
    public static final DataColumn<BlockeditEntry> ACTION_COLUMN = new DataColumn<>("watson.gui.label.blockedit.title.action", null);
    public static final DataColumn<BlockeditEntry> TIME_COLUMN = new DataColumn<>("watson.gui.label.blockedit.title.time", null);
    public static final DataColumn<BlockeditEntry> COORDS_COLUMN = new DataColumn<>("watson.gui.label.blockedit.title.coords", null);
    public static final DataColumn<BlockeditEntry> WORLD_COLUMN = new DataColumn<>("watson.gui.label.blockedit.title.world", null);
    public static final DataColumn<BlockeditEntry> AMOUNT_COLUMN = new DataColumn<>("watson.gui.label.blockedit.title.amount", null);
    public static final DataColumn<BlockeditEntry> DESCRIPTION_COLUMN = new DataColumn<>("watson.gui.label.blockedit.title.description", null);
    public static final ImmutableList<DataColumn<BlockeditEntry>> COLUMNS = ImmutableList.of(ACTION_COLUMN, TIME_COLUMN, COORDS_COLUMN, WORLD_COLUMN, AMOUNT_COLUMN, DESCRIPTION_COLUMN);
    protected final StyledTextLine actionText;
    protected final StyledTextLine timeText;
    protected final StyledTextLine coordsText;
    protected final StyledTextLine worldText;
    protected final StyledTextLine amountText;
    protected final StyledTextLine descriptionText;
    protected int actionColumnRight;
    protected int timeColumnRight;
    protected int coordsColumnRight;
    protected int worldColumnRight;
    protected int amountColumnRight;
    protected int descriptionColumnRight;
    private final GenericButton teleportButton;

    public WidgetBlockeditEntry(BlockeditEntry entry, DataListEntryWidgetData constructData)
    {
        super(entry, constructData);

        this.teleportButton = GenericButton.create("watson.gui.label.blockedit.list.teleport", this::teleport);
        this.hoverInfoWidget = new BlockeditEntryHoverInfoWidget();

        BlockEdit edit = entry.getEdit();
        this.actionText = StyledTextLine.of(PlayereditUtils.blockString(edit, PlayereditUtils.Edit.ACTION));
        this.timeText = StyledTextLine.of(PlayereditUtils.blockString(edit, PlayereditUtils.Edit.TIME));
        this.coordsText = StyledTextLine.of(PlayereditUtils.blockString(edit, PlayereditUtils.Edit.COORDS));
        this.worldText = StyledTextLine.of(PlayereditUtils.blockString(edit, PlayereditUtils.Edit.WORLD));
        this.amountText = StyledTextLine.of(PlayereditUtils.blockString(edit, PlayereditUtils.Edit.AMOUNT));
        this.descriptionText = StyledTextLine.of(PlayereditUtils.blockString(edit, PlayereditUtils.Edit.DESCRIPTION));

        this.getBackgroundRenderer().getHoverSettings().setColor(0x70FFFFFF);
        this.getBackgroundRenderer().getNormalSettings().setColor(this.isOdd ? 0x20FFFFFF : 0x50FFFFFF);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int textY = y + ElementOffset.getCenteredElementOffset(this.getHeight(), 8);
        int color = 0xFFFFFFFF;
        z += 0.0125f;
        this.renderTextLineRightAligned(x + this.actionColumnRight, textY, z, color, true, this.actionText, ctx);
        this.renderTextLineRightAligned(x + this.timeColumnRight, textY, z, color, true, this.timeText, ctx);
        this.renderTextLineRightAligned(x + this.coordsColumnRight, textY, z, color, true, this.coordsText, ctx);
        this.renderTextLineRightAligned(x + this.worldColumnRight, textY, z, color, true, this.worldText, ctx);
        this.renderTextLineRightAligned(x + this.amountColumnRight, textY, z, color, true, this.amountText, ctx);
        this.renderTextLineRightAligned(x + this.descriptionColumnRight, textY, z, color, true, this.descriptionText, ctx);
    }

    private void teleport()
    {
        Teleport.teleport(getData().getEdit().x, getData().getEdit().y, getData().getEdit().z, getData().getEdit().world);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.teleportButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        this.teleportButton.setRight(this.getRight() - 2);
        this.teleportButton.centerVerticallyInside(this);
    }

    public static class WidgetInitializer implements ListEntryWidgetInitializer<BlockeditEntry>
    {
        @Override
        public void onListContentsRefreshed(DataListWidget<BlockeditEntry> dataListWidget, int entryWidgetWidth)
        {
            int actionColumnLength = getRenderWidth(ACTION_COLUMN.getName(), 40);
            int timeColumnLength = getRenderWidth(TIME_COLUMN.getName(), 40);
            int coordsColumnLength = getRenderWidth(COORDS_COLUMN.getName(), 40);
            int worldColumnLength = getRenderWidth(WORLD_COLUMN.getName(), 40);
            int amountColumnLength = getRenderWidth(AMOUNT_COLUMN.getName(), 40);
            int descriptionColumnLength = getRenderWidth(DESCRIPTION_COLUMN.getName(), 40);

            for (BlockeditEntry entry : dataListWidget.getNonFilteredDataList())
            {
                BlockEdit edit = entry.getEdit();
                actionColumnLength = Math.max(actionColumnLength, getEditRenderWidth(edit, PlayereditUtils.Edit.ACTION));
                timeColumnLength = Math.max(timeColumnLength, getEditRenderWidth(edit, PlayereditUtils.Edit.TIME));
                coordsColumnLength = Math.max(coordsColumnLength, getEditRenderWidth(edit, PlayereditUtils.Edit.COORDS));
                worldColumnLength = Math.max(worldColumnLength, getEditRenderWidth(edit, PlayereditUtils.Edit.WORLD));
                amountColumnLength = Math.max(amountColumnLength, getEditRenderWidth(edit, PlayereditUtils.Edit.AMOUNT));
                descriptionColumnLength = Math.max(descriptionColumnLength, getEditRenderWidth(edit, PlayereditUtils.Edit.DESCRIPTION));
            }

            int extra = 24;
            actionColumnLength += extra;
            timeColumnLength += extra;
            coordsColumnLength += extra;
            worldColumnLength += extra;
            amountColumnLength += extra;
            descriptionColumnLength += extra;
            int relativeStartX = 2;

            ACTION_COLUMN.setRelativeStartX(relativeStartX);
            ACTION_COLUMN.setWidth(actionColumnLength);
            relativeStartX += actionColumnLength + 2;

            TIME_COLUMN.setRelativeStartX(relativeStartX);
            TIME_COLUMN.setWidth(timeColumnLength);
            relativeStartX += timeColumnLength + 2;

            COORDS_COLUMN.setRelativeStartX(relativeStartX);
            COORDS_COLUMN.setWidth(coordsColumnLength);
            relativeStartX += coordsColumnLength + 2;

            WORLD_COLUMN.setRelativeStartX(relativeStartX);
            WORLD_COLUMN.setWidth(worldColumnLength);
            relativeStartX += worldColumnLength + 2;

            AMOUNT_COLUMN.setRelativeStartX(relativeStartX);
            AMOUNT_COLUMN.setWidth(amountColumnLength);
            relativeStartX += amountColumnLength + 2;

            DESCRIPTION_COLUMN.setRelativeStartX(relativeStartX);
            DESCRIPTION_COLUMN.setWidth(descriptionColumnLength);
        }

        @Override
        public void applyToEntryWidgets(DataListWidget<BlockeditEntry> dataListWidget)
        {
            int actionColumnRight = ACTION_COLUMN.getRelativeRight() - 3;
            int timeColumnRight = TIME_COLUMN.getRelativeRight() - 3;
            int coordsColumnRight = COORDS_COLUMN.getRelativeRight() - 3;
            int worldColumnRight = WORLD_COLUMN.getRelativeRight() - 3;
            int amountColumnRight = AMOUNT_COLUMN.getRelativeRight() - 3;
            int descriptionColumnRight = DESCRIPTION_COLUMN.getRelativeRight() - 3;

            for (InteractableWidget w : dataListWidget.getEntryWidgetList())
            {
                if (w instanceof WidgetBlockeditEntry widget)
                {
                    widget.actionColumnRight = actionColumnRight;
                    widget.timeColumnRight = timeColumnRight;
                    widget.coordsColumnRight = coordsColumnRight;
                    widget.worldColumnRight = worldColumnRight;
                    widget.amountColumnRight = amountColumnRight;
                    widget.descriptionColumnRight = descriptionColumnRight;
                }
            }
        }

        protected int getRenderWidth(Optional<StyledTextLine> optional, int minWidth)
        {
            int width = optional.map(styledTextLine -> styledTextLine.renderWidth).orElse(minWidth);
            return Math.max(width, minWidth);
        }

        protected static int getEditRenderWidth(BlockEdit edit, PlayereditUtils.Edit type)
        {
            return StringUtils.getStringWidth(PlayereditUtils.blockString(edit, type));
        }
    }
}
