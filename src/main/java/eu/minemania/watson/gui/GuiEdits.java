package eu.minemania.watson.gui;

import eu.minemania.watson.gui.widgets.WidgetEditsEntry;
import eu.minemania.watson.selection.PlayereditBase;
import eu.minemania.watson.selection.PlayereditEntry;
import malilib.gui.BaseListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.header.ColumnizedDataListHeaderWidget;
import malilib.gui.widget.list.header.DataListHeaderWidget;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GuiEdits extends BaseListScreen<DataListWidget<PlayereditEntry>>
{
    protected final PlayereditBase edits;
    protected final GenericButton refreshButton;
    protected final GenericButton closeButton;

    public GuiEdits(PlayereditBase edits)
    {
        super(10, 44, 20, 80);

        this.edits = edits;
        this.edits.setCompletionListener(this::onRefreshFinished);
        this.shouldRestoreScrollbarPosition = true;
        this.setTitle(this.edits.getTitle());
        this.useTitleHierarchy = false;

        this.refreshButton = GenericButton.create("watson.gui.button.edits.refresh_list");
        this.refreshButton.setClickListener(edits::reCreatePlayeredits);
        this.closeButton = GenericButton.create("watson.gui.button.change_menu.close");
        this.closeButton.setClickListener(() -> BaseScreen.openScreen(this.getParent()));
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.refreshButton);
        this.addWidget(this.closeButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.titleX;
        int y = this.titleY;

        this.refreshButton.setPosition(x, y + 15);

        this.closeButton.setRight(this.getRight() - 10);
        this.closeButton.setBottom(this.getBottom() - 3);
    }

    @Override
    protected DataListWidget<PlayereditEntry> createListWidget()
    {
        Supplier<List<PlayereditEntry>> supplier = this::getPlayereditEntry;
        DataListWidget<PlayereditEntry> listWidget = new DataListWidget<>(supplier, true);

        listWidget.setListEntryWidgetFixedHeight(20);
        listWidget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0x80101010);
        listWidget.addDefaultSearchBar();
        listWidget.setHeaderWidgetFactory(this::createListHeaderWidget);
        listWidget.setEntryFilterStringFunction((e) -> Collections.singletonList(e.getStack().getName().getString()));
        listWidget.setDataListEntryWidgetFactory(WidgetEditsEntry::new);
        listWidget.setWidgetInitializer(new WidgetEditsEntry.WidgetInitializer());

        listWidget.setColumnSupplier(() -> WidgetEditsEntry.COLUMNS);
        listWidget.setDefaultSortColumn(WidgetEditsEntry.TOTAL_COLUMN);
        listWidget.setHasDataColumns(true);
        listWidget.setShouldSortList(true);
        listWidget.updateActiveColumns();
        listWidget.setParentScreen(this);
        return listWidget;
    }

    protected DataListHeaderWidget<PlayereditEntry> createListHeaderWidget(DataListWidget<PlayereditEntry> listWidget)
    {
        ColumnizedDataListHeaderWidget<PlayereditEntry> widget = new ColumnizedDataListHeaderWidget<>(this.getListWidget().getWidth() - 10, 16, this.getListWidget(), WidgetEditsEntry.COLUMNS);
        widget.getMargin().setAll(2, 0, 0, 1);
        return widget;
    }

    private List<PlayereditEntry> getPlayereditEntry()
    {
        return this.edits.getPlayereditsAll();
    }

    public PlayereditBase getEdits()
    {
        return this.edits;
    }

    protected void onRefreshFinished()
    {
        if (GuiUtils.getCurrentScreen() == this)
        {
            this.getListWidget().refreshEntries();
        }
    }
}
