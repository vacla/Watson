package eu.minemania.watson.gui;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.TimeStamp;
import eu.minemania.watson.db.data.BlockeditBase;
import eu.minemania.watson.db.data.BlockeditEntry;
import eu.minemania.watson.db.data.WidgetBlockeditEntry;
import eu.minemania.watson.selection.PlayereditUtils;
import malilib.gui.BaseListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.*;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.header.ColumnizedDataListHeaderWidget;
import malilib.gui.widget.list.header.DataListHeaderWidget;
import malilib.util.StringUtils;

import java.util.List;
import java.util.function.Supplier;

public class GuiBlockeditData extends BaseListScreen<DataListWidget<BlockeditEntry>>
{
    protected final BlockeditBase display;
    protected long time = 0;
    protected int yHeight = 0;
    protected DropDownListWidget<YHeightFilter> yHeightDropDown;
    protected final GenericButton closeButton;
    protected final LabelWidget labelTimeAgo;
    protected final InfoIconWidget infoIconTimeAgo;
    protected final BaseTextFieldWidget textFieldTimeAgo;
    protected final BaseTextFieldWidget textFieldYHeight;
    private YHeightFilter yHeightDropDownSelection;

    public GuiBlockeditData(BlockeditBase display, String titleKey)
    {
        super(12, 40, 20, 80);

        this.display = display;
        this.setTitle(titleKey);
        this.useTitleHierarchy = false;
        this.shouldRestoreScrollbarPosition = true;
        this.setZOffset(1);

        this.labelTimeAgo = new LabelWidget("watson.gui.label.blockedit.info.time");
        this.infoIconTimeAgo = new InfoIconWidget(Icons.INFO_11, "watson.gui.label.blockedit.info.format");
        this.textFieldTimeAgo = new BaseTextFieldWidget(100, 14, "0-0 0:0:0");
        this.textFieldTimeAgo.setListener(this::timeAgoTextChange);
        this.textFieldYHeight = new IntegerTextFieldWidget(70, 14, 0);
        this.textFieldYHeight.setListener(this::yHeightTextChange);
        this.yHeightDropDown = new DropDownListWidget<>(14, 3, YHeightFilter.VALUES, YHeightFilter::getDisplayName);
        this.yHeightDropDown.setZ(this.getZOffset() + 100);
        this.yHeightDropDown.setSelectionListener(this::onSelectionChange);
        this.closeButton = GenericButton.create("watson.gui.button.change_menu.close");
        this.closeButton.setClickListener(() -> BaseScreen.openScreen(this.getParent()));
        this.reloadInput();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.labelTimeAgo);
        this.addWidget(this.infoIconTimeAgo);
        this.addWidget(this.textFieldTimeAgo);
        this.addWidget(this.textFieldYHeight);
        this.addWidget(this.yHeightDropDown);
        this.addWidget(this.closeButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.titleX;
        int y = this.titleY + 15;

        this.labelTimeAgo.setPosition(x, y);
        this.infoIconTimeAgo.setPosition(this.labelTimeAgo.getRight() + 5, y);
        this.textFieldTimeAgo.setPosition(this.infoIconTimeAgo.getRight() + 5, y);
        this.textFieldYHeight.setPosition(this.textFieldTimeAgo.getRight() + 5, y);
        this.yHeightDropDown.setPosition(this.textFieldYHeight.getRight() + 5, y);
        this.closeButton.setRight(this.getRight() - 10);
        this.closeButton.setBottom(this.getBottom() - 3);
    }

    private void yHeightTextChange(String yHeightString)
    {
        try
        {
            yHeight = Integer.parseInt(yHeightString);
        }
        catch (Exception e)
        {
            yHeight = 0;
        }

        this.getListWidget().refreshEntries();
    }

    private void timeAgoTextChange(String textValue)
    {
        try
        {
            long timeDiff = DataManager.getTimeDiff(textValue);
            if (timeDiff != -1)
            {
                time = timeDiff;
            }
        }
        catch (Exception e)
        {
            time = 0;
        }

        this.getListWidget().refreshEntries();
    }

    @Override
    protected DataListWidget<BlockeditEntry> createListWidget()
    {
        Supplier<List<BlockeditEntry>> supplier = this::getBlockeditEntry;
        DataListWidget<BlockeditEntry> listWidget = new DataListWidget<>(supplier, true);

        listWidget.setListEntryWidgetFixedHeight(26);
        listWidget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0x80101010);
        listWidget.setHeaderWidgetFactory(this::createListHeaderWidget);
        listWidget.setEntryFilterStringFunction(this::filterEntry);
        listWidget.setDataListEntryWidgetFactory(WidgetBlockeditEntry::new);
        listWidget.setWidgetInitializer(new WidgetBlockeditEntry.WidgetInitializer());

        listWidget.setColumnSupplier(() -> WidgetBlockeditEntry.COLUMNS);
        listWidget.setDefaultSortColumn(WidgetBlockeditEntry.ACTION_COLUMN);
        listWidget.setHasDataColumns(true);
        listWidget.setShouldSortList(true);
        listWidget.updateActiveColumns();
        listWidget.setParentScreen(this);
        return listWidget;
    }

    private List<String> filterEntry(BlockeditEntry blockeditEntry)
    {
        if (this.getYHeight() != 0 && this.getYHeightDropDownSelection() != null)
        {
            switch (this.getYHeightDropDownSelection()) {
                case ABOVE:
                    if (blockeditEntry.getEdit().y > this.getYHeight())
                    {
                        return ImmutableList.of(PlayereditUtils.blockString(blockeditEntry.getEdit(), PlayereditUtils.Edit.DESCRIPTION));
                    }
                    break;
                case BELOW:
                    if (blockeditEntry.getEdit().y < this.getYHeight())
                    {
                        return ImmutableList.of(PlayereditUtils.blockString(blockeditEntry.getEdit(), PlayereditUtils.Edit.DESCRIPTION));
                    }
                    break;
                case EQUAL:
                    if (blockeditEntry.getEdit().y == this.getYHeight())
                    {
                        return ImmutableList.of(PlayereditUtils.blockString(blockeditEntry.getEdit(), PlayereditUtils.Edit.DESCRIPTION));
                    }
                    break;
            }
        }
        if (this.getTime() != 0 && blockeditEntry.getEdit().time >= this.getTime())
        {
            return ImmutableList.of(PlayereditUtils.blockString(blockeditEntry.getEdit(), PlayereditUtils.Edit.DESCRIPTION));
        }
        if (Configs.Generic.ACTION_REVERSE.getBooleanValue())
        {
            return (List<String>) PlayereditUtils.getInstance().getRevertAction(blockeditEntry.getEdit(), ImmutableList.of(), ImmutableList.of(PlayereditUtils.blockString(blockeditEntry.getEdit(), PlayereditUtils.Edit.DESCRIPTION)));
        }
        return ImmutableList.of();
    }

    private DataListHeaderWidget<BlockeditEntry> createListHeaderWidget(DataListWidget<BlockeditEntry> blockeditEntryDataListWidget)
    {
        ColumnizedDataListHeaderWidget<BlockeditEntry> widget = new ColumnizedDataListHeaderWidget<>(this.getListWidget().getWidth() - 10, 16, this.getListWidget(), WidgetBlockeditEntry.COLUMNS);
        widget.getMargin().setAll(2, 0, 0, 1);
        return widget;
    }

    private List<BlockeditEntry> getBlockeditEntry()
    {
        return this.display.getBlockeditAll();
    }

    @Override
    public boolean shouldPause()
    {
        return this.getParent() != null && this.getParent().shouldPause();
    }

    public BlockeditBase getDisplay()
    {
        return this.display;
    }

    public long getTime()
    {
        return this.time;
    }

    public long getYHeight()
    {
        return this.yHeight;
    }

    public YHeightFilter getYHeightDropDownSelection()
    {
        return this.yHeightDropDownSelection;
    }

    private void reloadInput()
    {
        if (this.textFieldTimeAgo != null)
        {
            this.textFieldTimeAgo.setText(TimeStamp.formatMonthDayTime(this.getTime()));
        }
        if (this.textFieldYHeight != null)
        {
            this.textFieldYHeight.setText(String.valueOf(this.getYHeight()));
        }
        if (this.yHeightDropDown != null)
        {
            this.yHeightDropDown.setSelectedEntry(this.getYHeightDropDownSelection());
        }
    }

    public void onSelectionChange(@Nullable GuiBlockeditData.YHeightFilter entry)
    {
        this.yHeightDropDownSelection = entry;
        this.getListWidget().refreshEntries();
    }
    public enum YHeightFilter
    {
        ABOVE("watson.gui.dropdown.y_height.above"),
        BELOW("watson.gui.dropdown.y_height.below"),
        EQUAL("watson.gui.dropdown.y_height.equal");

        private final String labelKey;
        public static final ImmutableList<YHeightFilter> VALUES = ImmutableList.copyOf(values());

        YHeightFilter(String labelKey)
        {
            this.labelKey = labelKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.labelKey);
        }
    }

    protected void onRefreshFinished()
    {
        if (GuiUtils.getCurrentScreen() == this)
        {
            this.getListWidget().refreshEntries();
        }
    }
}
