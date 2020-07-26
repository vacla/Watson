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
import net.minecraft.client.util.math.MatrixStack;

public class WidgetBlockeditEntry extends WidgetListEntrySortable<BlockeditEntry>
{
    private static final String[] HEADERS = new String[]{
            "watson.gui.label.blockedit.title.action",
            "watson.gui.label.blockedit.title.time",
            "watson.gui.label.blockedit.title.coords",
            "watson.gui.label.blockedit.title.world",
            "watson.gui.label.blockedit.title.amount",
            "watson.gui.label.blockedit.title.description"
    };
    private static int maxActionLength;
    private static int maxTimeLength;
    private static int maxCoordsLength;
    private static int maxWorldLength;
    private static int maxAmountLength;
    private static int maxDescriptionLength;

    @Nullable
    private final BlockeditEntry entry;
    @Nullable
    private final String header1;
    @Nullable
    private final String header2;
    @Nullable
    private final String header3;
    @Nullable
    private final String header4;
    @Nullable
    private final String header5;
    @Nullable
    private final String header6;
    private final boolean isOdd;

    public WidgetBlockeditEntry(int x, int y, int width, int height, boolean isOdd,
                                @Nullable BlockeditEntry entry, int listIndex)
    {
        super(x, y, width, height, entry, listIndex);

        this.columnCount = 1;
        this.entry = entry;
        this.isOdd = isOdd;

        if (this.entry != null)
        {
            this.header1 = null;
            this.header2 = null;
            this.header3 = null;
            this.header4 = null;
            this.header5 = null;
            this.header6 = null;
        }
        else
        {
            this.header1 = GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[0]) + GuiBase.TXT_RST;
            this.header2 = GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[1]) + GuiBase.TXT_RST;
            this.header3 = GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[2]) + GuiBase.TXT_RST;
            this.header4 = GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[3]) + GuiBase.TXT_RST;
            this.header5 = GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[4]) + GuiBase.TXT_RST;
            this.header6 = GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[5]) + GuiBase.TXT_RST;
        }

        int posX = x + width;
        int posY = y + 1;

        posX = this.createButtonGeneric(posX, posY, WidgetBlockeditEntry.ButtonListenerTeleport.ButtonType.TELEPORT);
    }

    private int createButtonGeneric(int xRight, int y, WidgetBlockeditEntry.ButtonListenerTeleport.ButtonType type)
    {
        String label = type.getDisplayName();
        WidgetBlockeditEntry.ButtonListenerTeleport listener = new WidgetBlockeditEntry.ButtonListenerTeleport(type, this.entry);
        return this.addButton(new ButtonGeneric(xRight, y, -1, true, label), listener).getX();
    }

    public static void setMaxNameLength(List<BlockeditEntry> edits)
    {
        maxActionLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[0]) + GuiBase.TXT_RST);
        maxTimeLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[1]) + GuiBase.TXT_RST);
        maxCoordsLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[2]) + GuiBase.TXT_RST);
        maxWorldLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[3]) + GuiBase.TXT_RST);
        maxAmountLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[4]) + GuiBase.TXT_RST);
        maxDescriptionLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[5]) + GuiBase.TXT_RST);

        for (BlockeditEntry entry : edits)
        {
            maxActionLength = Math.max(maxActionLength, StringUtils.getStringWidth(PlayereditUtils.blockString(entry.getEdit(), PlayereditUtils.Edit.ACTION)));
            maxTimeLength = Math.max(maxTimeLength, StringUtils.getStringWidth(PlayereditUtils.blockString(entry.getEdit(), PlayereditUtils.Edit.TIME)));
            maxCoordsLength = Math.max(maxCoordsLength, StringUtils.getStringWidth(PlayereditUtils.blockString(entry.getEdit(), PlayereditUtils.Edit.COORDS)));
            maxWorldLength = Math.max(maxWorldLength, StringUtils.getStringWidth(PlayereditUtils.blockString(entry.getEdit(), PlayereditUtils.Edit.WORLD)));
            maxAmountLength = Math.max(maxAmountLength, StringUtils.getStringWidth(PlayereditUtils.blockString(entry.getEdit(), PlayereditUtils.Edit.AMOUNT)));
            maxDescriptionLength = Math.max(maxDescriptionLength, StringUtils.getStringWidth(PlayereditUtils.blockString(entry.getEdit(), PlayereditUtils.Edit.DESCRIPTION)));
        }
    }

    @Override
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.header1 == null && (selected || this.isMouseOver(mouseX, mouseY)))
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x70FFFFFF);
        }
        else if (this.isOdd)
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x20FFFFFF);
        }
        else
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x50FFFFFF);
        }

        int x1 = this.getColumnPosX(0);
        int x2 = this.getColumnPosX(1);
        int x3 = this.getColumnPosX(2);
        int x4 = this.getColumnPosX(3);
        int x5 = this.getColumnPosX(4);
        int x6 = this.getColumnPosX(5);
        int y = this.y + 7;
        int color = 0xFFFFFFFF;

        if (this.header1 != null)
        {
            this.drawString(x1, y, color, this.header1, matrixStack);
            this.drawString(x2, y, color, this.header2, matrixStack);
            this.drawString(x3, y, color, this.header3, matrixStack);
            this.drawString(x4, y, color, this.header4, matrixStack);
            this.drawString(x5, y, color, this.header5, matrixStack);
            this.drawString(x6, y, color, this.header6, matrixStack);
        }
        else if (this.entry != null)
        {
            String action = PlayereditUtils.blockString(this.entry.getEdit(), PlayereditUtils.Edit.ACTION);
            String time = PlayereditUtils.blockString(this.entry.getEdit(), PlayereditUtils.Edit.TIME);
            String coords = PlayereditUtils.blockString(this.entry.getEdit(), PlayereditUtils.Edit.COORDS);
            String world = PlayereditUtils.blockString(this.entry.getEdit(), PlayereditUtils.Edit.WORLD);
            String amount = PlayereditUtils.blockString(this.entry.getEdit(), PlayereditUtils.Edit.AMOUNT);
            String description = PlayereditUtils.blockString(this.entry.getEdit(), PlayereditUtils.Edit.DESCRIPTION);
            this.drawString(x1, y, 0xFFFFFFFF, action, matrixStack);
            this.drawString(x2, y, 0xFFFFFFFF, time, matrixStack);
            this.drawString(x3, y, 0xFFFFFFFF, coords, matrixStack);
            this.drawString(x4, y, 0xFFFFFFFF, world, matrixStack);
            this.drawString(x5, y, 0xFFFFFFFF, amount, matrixStack);
            this.drawString(x6, y, 0xFFFFFFFF, description, matrixStack);

            super.render(mouseX, mouseY, selected, matrixStack);
        }
    }

    static class ButtonListenerTeleport implements IButtonActionListener
    {
        private final WidgetBlockeditEntry.ButtonListenerTeleport.ButtonType type;
        private final BlockeditEntry entry;

        public ButtonListenerTeleport(WidgetBlockeditEntry.ButtonListenerTeleport.ButtonType type, BlockeditEntry entry)
        {
            this.type = type;
            this.entry = entry;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == WidgetBlockeditEntry.ButtonListenerTeleport.ButtonType.TELEPORT)
            {
                Teleport.teleport(entry.getEdit().x, entry.getEdit().y, entry.getEdit().z, entry.getEdit().world);
            }
        }

        public enum ButtonType
        {
            TELEPORT("watson.gui.label.blockedit.list.teleport");

            private final String translationKey;

            ButtonType(String translationKey)
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
        int x2 = x1 + maxActionLength + 20;
        int x3 = x2 + maxTimeLength + 20;
        int x4 = x3 + maxCoordsLength + 20;
        int x5 = x4 + maxWorldLength + 20;
        int x6 = x5 + maxAmountLength + 20;

        switch (column)
        {
            case 0:
                return x1;
            case 1:
                return x2;
            case 2:
                return x3;
            case 3:
                return x4;
            case 4:
                return x5;
            case 5:
                return x6;
            default:
                return x1;
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
