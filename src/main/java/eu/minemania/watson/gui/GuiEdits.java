package eu.minemania.watson.gui;

import eu.minemania.watson.gui.widgets.WidgetEditsEntry;
import eu.minemania.watson.gui.widgets.WidgetListEdits;
import eu.minemania.watson.selection.PlayereditBase;
import eu.minemania.watson.selection.PlayereditEntry;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.interfaces.ICompletionListener;
import fi.dy.masa.malilib.listener.TaskCompletionListener;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.screen.Screen;

public class GuiEdits extends GuiListBase<PlayereditEntry, WidgetEditsEntry, WidgetListEdits>
        implements TaskCompletionListener
{
    private final PlayereditBase edits;

    public GuiEdits(PlayereditBase edits, Screen parent)
    {
        super(10, 44);

        this.edits = edits;
        this.edits.setCompletionListener(this);
        this.title = this.edits.getTitle();
        this.useTitleHierarchy = false;
        this.setParent(parent);

        WidgetEditsEntry.setMaxNameLength(edits.getPlayereditsAll());
    }

    @Override
    protected WidgetListEdits createListWidget(int listX, int listY)
    {
        return new WidgetListEdits(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this);
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

        boolean isNarrow = this.width < this.getElementTotalWidth();

        int x = 12;
        int y = 24;
        int buttonWidth;
        String label;
        ButtonGeneric button;

        int gap = 1;
        x += this.createButton(x, y, -1, ButtonListener.Type.REFRESH_LIST) + gap;

        if (isNarrow)
        {
            x = 12;
            y = this.height - 22;
        }

        y = this.height - 36;
        ButtonListener.Type type = ButtonListener.Type.CLOSE;
        buttonWidth = this.getStringWidth(type.getDisplayName()) + 10;
        this.createButton(12, y, buttonWidth, type);
    }

    private int createButton(int x, int y, int width, ButtonListener.Type type)
    {
        ButtonListener listener = new ButtonListener(type, this);
        String label;

        label = type.getDisplayName();

        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, label);

        this.addButton(button, listener);

        return button.getWidth();
    }

    private int getElementTotalWidth()
    {
        int width = 0;

        width += this.getStringWidth(ButtonListener.Type.REFRESH_LIST.getDisplayName());
        width += 130;

        return width;
    }

    public PlayereditBase getEdits()
    {
        return this.edits;
    }

    @Override
    public void onTaskCompleted()
    {
        if (GuiUtils.getCurrentScreen() == this)
        {
            WidgetEditsEntry.setMaxNameLength(this.edits.getPlayereditsAll());
            this.initGui();
        }
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final GuiEdits parent;
        private final Type type;

        public ButtonListener(Type type, GuiEdits parent)
        {
            this.parent = parent;
            this.type = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            PlayereditBase edits = this.parent.edits;

            switch (this.type)
            {
                case CLOSE:
                    GuiBase.openGui(this.parent.getParent());
                    break;
                case REFRESH_LIST:
                    edits.reCreatePlayeredits();
                    break;
            }

            this.parent.initGui();
        }

        public enum Type
        {
            CLOSE("watson.gui.button.change_menu.close"),
            REFRESH_LIST("watson.gui.button.edits.refresh_list");

            private final String translationKey;

            Type(String translationKey)
            {
                this.translationKey = translationKey;
            }

            public String getDisplayName(Object... args)
            {
                return StringUtils.translate(this.translationKey, args);
            }
        }
    }
}
