package eu.minemania.watson.gui;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.TimeStamp;
import eu.minemania.watson.db.data.BlockeditBase;
import eu.minemania.watson.db.data.BlockeditEntry;
import eu.minemania.watson.db.data.WidgetBlockeditEntry;
import eu.minemania.watson.db.data.WidgetListBlockedit;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetInfoIcon;
import fi.dy.masa.malilib.interfaces.ICompletionListener;
import fi.dy.masa.malilib.interfaces.IStringRetriever;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public class GuiBlockeditData extends GuiListBase<BlockeditEntry, WidgetBlockeditEntry, WidgetListBlockedit>
        implements ICompletionListener, ISelectionListener<GuiBlockeditData.YHeightFilter>
{
    protected final BlockeditBase display;
    protected long time = 0;
    protected int yHeight = 0;
    protected YHeightDropdown<YHeightFilter> yHeightDropDown;
    protected GuiTextFieldGeneric textFieldTimeAgo;
    protected GuiTextFieldGeneric textFieldYHeight;
    private YHeightFilter yHeightDropDownSelection;

    public GuiBlockeditData(BlockeditBase display, String titleKey, @Nullable Screen parent)
    {
        super(12, 40);

        this.setParent(parent);
        this.display = display;
        this.title = StringUtils.translate(titleKey);
        this.useTitleHierarchy = false;
        this.setZOffset(1);

        WidgetBlockeditEntry.setMaxNameLength(display.getBlockeditAll());
    }

    @Override
    protected WidgetListBlockedit createListWidget(int listX, int listY)
    {
        return new WidgetListBlockedit(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this);
    }

    @Override
    protected int getBrowserWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 80;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = 12;
        int y = this.height - 26;
        int buttonWidth = getButtonWidth();

        this.createTimeInput(x, 22, 70);
        this.createButton(x, y, buttonWidth, ButtonType.CLOSE);
        this.reloadInput();
    }

    private void createTimeInput(int x, int y, int width)
    {
        String label = StringUtils.translate("watson.gui.label.blockedit.info.time");
        this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
        int offset = this.getStringWidth(label) + 4;
        this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.blockedit.info.format"));

        this.textFieldTimeAgo = new GuiTextFieldGeneric(x + offset + 20, y + 2, width, 14, this.textRenderer);
        this.textFieldTimeAgo.setText("0-0 0:0:0");
        this.addTextField(this.textFieldTimeAgo, new TimeAgoTextFieldListener(this));

        this.textFieldYHeight = new GuiTextFieldGeneric(this.textFieldTimeAgo.getX() + this.textFieldTimeAgo.getWidth() + 20, y + 2, width, 14, this.textRenderer);
        this.textFieldYHeight.setText("0");
        this.addTextField(this.textFieldYHeight, new YHeightTextFieldListener(this));

        this.yHeightDropDown = new YHeightDropdown<>(this.textFieldYHeight.getX() + this.textFieldYHeight.getWidth() + 20, y + 2, width, 14, 100, 3, ImmutableList.copyOf(YHeightFilter.values()), YHeightFilter::getDisplayName);
        this.yHeightDropDown.setZLevel(this.getZOffset() + 100);
        this.yHeightDropDown.setSelectionListener(this);

        this.addWidget(this.yHeightDropDown);
    }

    protected int getButtonWidth()
    {
        int width = 0;

        for (ButtonType type : ButtonType.values())
        {
            width = Math.max(width, this.getStringWidth(type.getDisplayName()) + 10);
        }

        return width;
    }

    protected void createButton(int x, int y, int buttonWidth, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, type.getDisplayName());
        this.addButton(button, this.createActionListener(type));
    }

    @Override
    public boolean isPauseScreen()
    {
        return this.getParent() != null && this.getParent().isPauseScreen();
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

    protected ButtonListener createActionListener(ButtonType type)
    {
        return new ButtonListener(type, this);
    }

    @Override
    public void onSelectionChange(@Nullable GuiBlockeditData.YHeightFilter entry)
    {
        this.yHeightDropDownSelection = entry;
        this.getListWidget().refreshEntries();
    }

    protected static class ButtonListener implements IButtonActionListener
    {
        private final GuiBlockeditData gui;
        private final ButtonType type;

        public ButtonListener(ButtonType type, GuiBlockeditData gui)
        {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.CLOSE)
            {
                GuiBase.openGui(this.gui.getParent());
            }

        }
    }

    public static class TimeAgoTextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        private final GuiBlockeditData parent;

        public TimeAgoTextFieldListener(GuiBlockeditData parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            try
            {
                String textValue = textField.getText();
                long time = DataManager.getTimeDiff(textValue);
                if (time != -1)
                {
                    parent.time = time;
                }
            }
            catch (Exception e)
            {
                parent.time = 0;
            }

            this.parent.getListWidget().refreshEntries();
            return false;
        }
    }

    public static class YHeightDropdown<T> extends WidgetDropDownList<T>
    {
        @Nullable protected ISelectionListener<T> selectionListener;

        public YHeightDropdown(int x, int y, int width, int height, int maxHeight, int maxVisibleEntries, List<T> entries, @Nullable IStringRetriever<T> stringRetriever)
        {
            super(x, y, width, height, maxHeight, maxVisibleEntries, entries, stringRetriever);
        }

        public void setSelectionListener(@Nullable ISelectionListener<T> selectionListener)
        {
            this.selectionListener = selectionListener;
        }

        @Override
        public YHeightDropdown<T> setSelectedEntry(T entry)
        {
            if (this.entries.contains(entry))
            {
                this.selectedEntry = entry;

                if (this.selectionListener != null)
                {
                    this.selectionListener.onSelectionChange(this.selectedEntry);
                }
            }

            return this;
        }

        @Override
        protected void setSelectedEntry(int index)
        {
            if (index >= 0 && index < this.filteredEntries.size())
            {
                this.setSelectedEntry(this.filteredEntries.get(index));
            }
        }
    }

    public static class YHeightTextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        private final GuiBlockeditData parent;

        public YHeightTextFieldListener(GuiBlockeditData parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            try
            {
                String yHeightString = textField.getText();
                parent.yHeight = Integer.parseInt(yHeightString);
            }
            catch (Exception e)
            {
                parent.yHeight = 0;
            }

            this.parent.getListWidget().refreshEntries();
            return false;
        }
    }

    protected enum ButtonType
    {
        CLOSE("watson.gui.button.change_menu.close");

        private final String labelKey;

        ButtonType(String labelKey)
        {
            this.labelKey = labelKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.labelKey);
        }
    }

    public enum YHeightFilter
    {
        ABOVE("watson.gui.dropdown.y_height.above"),
        BELOW("watson.gui.dropdown.y_height.below"),
        EQUAL("watson.gui.dropdown.y_height.equal");

        private final String labelKey;

        YHeightFilter(String labelKey)
        {
            this.labelKey = labelKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.labelKey);
        }
    }

    @Override
    public void onTaskCompleted()
    {
        if (GuiUtils.getCurrentScreen() == this)
        {
            WidgetBlockeditEntry.setMaxNameLength(this.display.getBlockeditAll());
            this.initGui();
        }
    }
}
