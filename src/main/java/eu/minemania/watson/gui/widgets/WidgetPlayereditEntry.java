package eu.minemania.watson.gui.widgets;

import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.gui.GuiEdits;
import eu.minemania.watson.selection.EditListPlayeredit;
import malilib.gui.BaseScreen;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;

import java.util.List;
import java.util.Locale;

public class WidgetPlayereditEntry extends BaseDataListEntryWidget<PlayereditSet>
{
    protected final GenericButton openBlockListButton;
    protected final GenericButton toggleVisibilityButton;
    protected final GenericButton removeEntryButton;
    protected int buttonsStartX;

    public WidgetPlayereditEntry(PlayereditSet playeredit, DataListEntryWidgetData constructData)
    {
        super(playeredit, constructData);

        this.removeEntryButton = GenericButton.create("watson.gui.button.playeredit.remove", this::removeEntry);
        this.toggleVisibilityButton = GenericButton.create(this.getVibilityKey(), this::toggleVisibility);
        this.openBlockListButton = GenericButton.create("watson.gui.button.playeredit.edit_list", this::openBlockList);

        this.textOffset.setXOffset(20);

        this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, this.isOdd ? 0x20FFFFFF : 0x50FFFFFF);
        this.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, 0x70FFFFFF);
        this.setText(StyledTextLine.of(playeredit.getPlayer()));
        this.addHoverInfo();
    }

    protected void addHoverInfo()
    {
        String text = StringUtils.translate("watson.gui.button.playeredit.hover", this.getData().getBlockEditCount());

        this.getHoverInfoFactory().addStrings(text);
    }

    protected String getVibilityKey()
    {
        return this.getData().isVisible() ? "watson.message.setting.shown" : "watson.message.setting.hidden";
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.removeEntryButton);
        this.addWidget(this.toggleVisibilityButton);
        this.addWidget(this.openBlockListButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        this.removeEntryButton.centerVerticallyInside(this);
        this.toggleVisibilityButton.centerVerticallyInside(this);
        this.openBlockListButton.centerVerticallyInside(this);

        this.removeEntryButton.setRight(this.getRight() - 2);
        this.toggleVisibilityButton.setRight(this.removeEntryButton.getX() - 1);
        this.openBlockListButton.setRight(this.toggleVisibilityButton.getX() - 1);

        this.buttonsStartX = this.openBlockListButton.getX() - 1;
    }

    @Override
    public boolean canHoverAt(int mouseX, int mouseY, int mouseButton)
    {
        return mouseX <= this.buttonsStartX - 12 && super.canHoverAt(mouseX, mouseY, mouseButton);
    }

    public static boolean searchFilter(PlayereditSet entry, List<String> searchTerms)
    {
        String name = entry.getPlayer().toLowerCase(Locale.ROOT);

        for (String searchTerm : searchTerms)
        {
            if (name.contains(searchTerm))
            {
                return true;
            }
        }

        return false;
    }

    public void openBlockList()
    {
        EditListPlayeredit editList = new EditListPlayeredit(this.getData(), true);
        GuiEdits screen = new GuiEdits(editList);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    public void removeEntry()
    {
        PlayereditSet entry = this.getData();

        DataManager.getEditSelection().getBlockEditSet().removeEdits(entry.getPlayer());
        this.listWidget.refreshEntries();
    }

    public void toggleVisibility()
    {
        PlayereditSet entry = this.getData();

        entry.setVisible(!entry.isVisible());
        this.listWidget.refreshEntries();
    }

}
