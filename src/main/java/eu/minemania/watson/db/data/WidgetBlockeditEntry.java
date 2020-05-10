package eu.minemania.watson.db.data;

import java.util.List;
import javax.annotation.Nullable;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.selection.PlayereditUtils;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntrySortable;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetBlockeditEntry extends WidgetListEntrySortable<BlockeditEntry>
{
    private static int maxNameLength;
    @Nullable private final BlockeditEntry entry;
    @Nullable private final String header;
    private final boolean isOdd;

    public WidgetBlockeditEntry(int x, int y, int width, int height, boolean isOdd,
            @Nullable BlockeditEntry entry, int listIndex)
    {
        super(x, y, width, height, entry, listIndex);

        this.columnCount = 1;
        this.entry = entry;
        this.isOdd = isOdd;

        if(this.entry != null)
        {
            this.header = null;
        }
        else
        {
            this.header = GuiBase.TXT_BOLD + StringUtils.translate("watson.gui.label.blockedit.list.data") + GuiBase.TXT_RST;
        }

        int posX = x + width;
        int posY = y + 1;

        posX = this.createButtonGeneric(posX, posY, ButtonListenerTeleport.ButtonType.TELEPORT);
    }

    private int createButtonGeneric(int xRight, int y, ButtonListenerTeleport.ButtonType type)
    {
        String label = type.getDisplayName();
        ButtonListenerTeleport listener = new ButtonListenerTeleport(type, this.entry);
        return this.addButton(new ButtonGeneric(xRight, y, -1, true, label), listener).getX();
    }

    public static void setMaxNameLength(List<BlockeditEntry> edits)
    {
        maxNameLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate("watson.gui.label.blockedit.list.data") + GuiBase.TXT_RST);

        for(BlockeditEntry entry : edits)
        {
            maxNameLength = Math.max(maxNameLength, StringUtils.getStringWidth(PlayereditUtils.blockString(entry.getEdit())));
        }
    }

    @Override
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        if(this.header == null && (selected || this.isMouseOver(mouseX, mouseY)))
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x70FFFFFF);
        }
        else if(this.isOdd)
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x20FFFFFF);
        }
        else
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x50FFFFFF);
        }

        int x1 = this.getColumnPosX(0);
        int y = this.y + 7;
        int color = 0xFFFFFFFF;

        if(this.header != null)
        {
            this.drawString(x1, y, color, this.header);
        }
        else if(this.entry != null)
        {
            String blockName = PlayereditUtils.blockString(this.entry.getEdit());
            this.drawString(x1, y, 0xFFFFFFFF, blockName);

            super.render(mouseX, mouseY, selected);
        }
    }

    static class ButtonListenerTeleport implements IButtonActionListener
    {
        private final ButtonType type;
        private final BlockeditEntry entry;

        public ButtonListenerTeleport(ButtonType type, BlockeditEntry entry)
        {
            this.type = type;
            this.entry = entry;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if(this.type == ButtonType.TELEPORT)
            {
                Teleport.teleport(entry.getEdit().x, entry.getEdit().y, entry.getEdit().z);
            }
        }

        public enum ButtonType
        {
            TELEPORT  ("watson.gui.label.blockedit.list.teleport");

            private final String translationKey;

            private ButtonType(String translationKey)
            {
                this.translationKey = translationKey;
            }

            public String getDisplayName()
            {
                return StringUtils.translate(this.translationKey);
            }
        }
    }

    @Override
    protected int getColumnPosX(int column)
    {
        int x1 = this.x + 4;
        int x2 = x1 + maxNameLength + 40;

        switch (column)
        {
            case 0: return x1;
            case 1: return x2;
            default: return x1;
        }
    }

    @Override
    protected int getCurrentSortColumn()
    {
        return 0;
    }

    @Override
    protected boolean getSortInReverse()
    {
        return false;
    }
}
