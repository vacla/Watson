package eu.minemania.watson.gui;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.TimeStamp;
import eu.minemania.watson.db.data.BlockeditBase;
import eu.minemania.watson.db.data.BlockeditEntry;
import eu.minemania.watson.db.data.WidgetBlockeditEntry;
import eu.minemania.watson.db.data.WidgetListBlockedit;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetInfoIcon;
import fi.dy.masa.malilib.interfaces.ICompletionListener;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.screen.Screen;

public class GuiBlockeditData extends GuiListBase<BlockeditEntry, WidgetBlockeditEntry, WidgetListBlockedit>
implements ICompletionListener
{
    protected final BlockeditBase display;
    protected static final Pattern ABSOLUTE_TIME = Pattern.compile("(\\d{1,2})-(\\d{1,2}) (\\d{1,2}):(\\d{1,2}):(\\d{1,2})");
    protected long time = 0;

    public GuiBlockeditData(BlockeditBase display, String titleKey, List<BlockEdit> blockedit, @Nullable Screen parent)
    {
        super(12, 40);

        this.setParent(parent);
        this.display = display;
        this.title = StringUtils.translate(titleKey);
        this.useTitleHierarchy = false;
        this.setBlitOffset(1);

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
    }

    private void createTimeInput(int x, int y, int width)
    {
        String label = StringUtils.translate("watson.gui.label.blockedit.info.time");
        this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
        int offset = this.getStringWidth(label) + 4;
        this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.blockedit.info.format"));

        GuiTextFieldGeneric textField = new GuiTextFieldGeneric(x + offset + 20, y + 2, width, 14, this.textRenderer);
        textField.setText("0-0 0:0:0");
        this.addTextField(textField, new TextFieldListener(this));
    }

    protected int getButtonWidth()
    {
        int width = 0;

        for(ButtonType type : ButtonType.values())
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

    protected ButtonListener createActionListener(ButtonType type)
    {
        return new ButtonListener(type, this);
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

    public static class TextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        private final GuiBlockeditData parent;

        public TextFieldListener(GuiBlockeditData parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            try
            {
                String textValue = textField.getText();
                Matcher absolute = ABSOLUTE_TIME.matcher(textValue);
                if(absolute.matches())
                {
                    int month = Integer.parseInt(absolute.group(1));
                    int day = Integer.parseInt(absolute.group(2));
                    int hour = Integer.parseInt(absolute.group(3));
                    int minute = Integer.parseInt(absolute.group(4));
                    int second = Integer.parseInt(absolute.group(5));
                    if(month != 0 || day != 0 || hour != 0 || minute != 0 || second != 0)
                    {
                        parent.time = TimeStamp.timeDiff(month, day, hour, minute, second);
                    }
                    else
                    {
                        parent.time = 0;
                    }
                }
                else
                {
                    InfoUtils.showGuiMessage(MessageType.ERROR, "watson.gui.label.blockedit.info.format");
                }
            }
            catch (Exception e) {
                parent.time = 0;
            }

            this.parent.getListWidget().refreshEntries();
            return false;
        }
    }

    protected enum ButtonType
    {
        CLOSE  ("watson.gui.button.blockedit.close");

        private final String labelKey;

        private ButtonType(String labelKey)
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
        if(GuiUtils.getCurrentScreen() == this)
        {
            WidgetBlockeditEntry.setMaxNameLength(this.display.getBlockeditAll());
            this.initGui();
        }
    }
}
