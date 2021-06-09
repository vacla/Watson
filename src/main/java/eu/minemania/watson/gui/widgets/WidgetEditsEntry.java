package eu.minemania.watson.gui.widgets;

import java.util.List;
import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.minemania.watson.db.data.EditListBlockedit;
import eu.minemania.watson.gui.GuiBlockeditData;
import eu.minemania.watson.gui.Icons;
import eu.minemania.watson.selection.PlayereditBase;
import eu.minemania.watson.selection.PlayereditBase.SortCriteria;
import eu.minemania.watson.selection.PlayereditEntry;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntrySortable;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class WidgetEditsEntry extends WidgetListEntrySortable<PlayereditEntry>
{
    private static final String[] HEADERS = new String[]{
            "watson.gui.label.edits.title.item",
            "watson.gui.label.edits.title.broken",
            "watson.gui.label.edits.title.placed",
            "watson.gui.label.edits.title.contadded",
            "watson.gui.label.edits.title.contremoved",
            "watson.gui.label.edits.title.total"
    };
    private static int maxNameLength;
    private static int maxBrokenLength;
    private static int maxPlacedLength;
    private static int maxContAddedLength;
    private static int maxContRemovedLength;
    private static int maxTotalLength;

    private final PlayereditBase edits;
    private final WidgetListEdits listWidget;
    @Nullable
    private final PlayereditEntry entry;
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

    public WidgetEditsEntry(int x, int y, int width, int height, boolean isOdd, PlayereditBase edits, @Nullable PlayereditEntry entry, int listIndex, WidgetListEdits listWidget)
    {
        super(x, y, width, height, entry, listIndex);

        this.columnCount = 6;
        this.entry = entry;
        this.isOdd = isOdd;
        this.listWidget = listWidget;
        this.edits = edits;

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

        posX = this.createButtonGeneric(posX, posY, ButtonListener.ButtonType.BLOCKS);
    }

    private int createButtonGeneric(int xRight, int y, ButtonListener.ButtonType type)
    {
        String label = type.getDisplayName();
        ButtonListener listener = new ButtonListener(type, this.entry, this.listWidget);
        return this.addButton(new ButtonGeneric(xRight, y, -1, true, label), listener).getX();
    }

    public static void setMaxNameLength(List<PlayereditEntry> edits)
    {
        maxNameLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[0]) + GuiBase.TXT_RST);
        maxBrokenLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[1]) + GuiBase.TXT_RST);
        maxPlacedLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[2]) + GuiBase.TXT_RST);
        maxContAddedLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[3]) + GuiBase.TXT_RST);
        maxContRemovedLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[4]) + GuiBase.TXT_RST);
        maxTotalLength = StringUtils.getStringWidth(GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[5]) + GuiBase.TXT_RST);

        for (PlayereditEntry entry : edits)
        {
            maxNameLength = Math.max(maxNameLength, StringUtils.getStringWidth(entry.getStack().getName().getString()));
            maxBrokenLength = Math.max(maxBrokenLength, StringUtils.getStringWidth(String.valueOf(entry.getCountBroken())));
            maxPlacedLength = Math.max(maxPlacedLength, StringUtils.getStringWidth(String.valueOf(entry.getCountPlaced())));
            maxContAddedLength = Math.max(maxContAddedLength, StringUtils.getStringWidth(String.valueOf(entry.getCountContAdded())));
            maxContRemovedLength = Math.max(maxContRemovedLength, StringUtils.getStringWidth(String.valueOf(entry.getCountContRemoved())));
            maxTotalLength = Math.max(maxTotalLength, StringUtils.getStringWidth(String.valueOf(entry.getCountTotal())));
        }
    }

    @Override
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    @Override
    protected int getColumnPosX(int column)
    {
        int x1 = this.x + 4;
        int x2 = x1 + maxNameLength + 40;
        int x3 = x2 + maxBrokenLength + 20;
        int x4 = x3 + maxPlacedLength + 20;
        int x5 = x4 + maxContAddedLength + 20;
        int x6 = x5 + maxContRemovedLength + 20;
        int x7 = x6 + maxTotalLength + 20;

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
            case 6:
                return x7;
            default:
                return x1;
        }
    }

    @Override
    protected int getCurrentSortColumn()
    {
        return this.edits.getSortCriteria().ordinal();
    }

    @Override
    protected boolean getSortInReverse()
    {
        return this.edits.getSortInReverse();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClickedImpl(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (this.entry != null)
        {
            return false;
        }

        int column = this.getMouseOverColumn(mouseX, mouseY);

        switch (column)
        {
            case 0:
                this.edits.setSortCriteria(SortCriteria.NAME);
                break;
            case 5:
                this.edits.setSortCriteria(SortCriteria.COUNT_TOTAL);
                break;
            default:
                return false;
        }

        this.listWidget.refreshEntries();

        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack)
    {
        if (this.header1 == null && (selected || this.isMouseOver(mouseX, mouseY)))
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0xA0707070);
        }
        else if (this.isOdd)
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0xA0101010);
        }
        else
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0xA0303030);
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
            if (!this.listWidget.getSearchBarWidget().isSearchOpen())
            {
                this.drawString(x1, y, color, this.header1, matrixStack);
                this.drawString(x2, y, color, this.header2, matrixStack);
                this.drawString(x3, y, color, this.header3, matrixStack);
                this.drawString(x4, y, color, this.header4, matrixStack);
                this.drawString(x5, y, color, this.header5, matrixStack);
                this.drawString(x6, y, color, this.header6, matrixStack);

                this.renderColumnHeader(mouseX, mouseY, Icons.ARROW_DOWN, Icons.ARROW_UP);
            }
        }
        else if (this.entry != null)
        {
            this.drawString(x1 + 20, y, color, this.entry.getStack().getName().getString(), matrixStack);
            this.drawString(x2, y, color, String.valueOf(this.entry.getCountBroken()), matrixStack);
            this.drawString(x3, y, color, String.valueOf(this.entry.getCountPlaced()), matrixStack);
            this.drawString(x4, y, color, String.valueOf(this.entry.getCountContAdded()), matrixStack);
            this.drawString(x5, y, color, String.valueOf(this.entry.getCountContRemoved()), matrixStack);
            this.drawString(x6, y, color, String.valueOf(this.entry.getCountTotal()), matrixStack);

            matrixStack.push();
            //RenderSystem.disableLighting();
            RenderUtils.enableDiffuseLightingGui3D();

            y = this.y + 3;
            RenderUtils.drawRect(x1, y, 16, 16, 0x20FFFFFF);
            this.mc.getItemRenderer().renderInGuiWithOverrides(this.entry.getStack(), x1, y);

            RenderSystem.disableBlend();
            RenderUtils.disableDiffuseLighting();
            matrixStack.pop();

            super.render(mouseX, mouseY, selected, matrixStack);
        }
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack)
    {
        if (this.entry != null)
        {
            matrixStack.push();
            matrixStack.translate(0, 0, 200);
            RenderSystem.applyModelViewMatrix();

            String header1 = GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[0]);
            String header2 = GuiBase.TXT_BOLD + StringUtils.translate(HEADERS[5]);

            ItemStack stack = this.entry.getStack();
            String stackName = stack.getName().getString();
            int total = this.entry.getCountTotal();
            String strTotal = this.getFormattedCountString(total);

            int w1 = Math.max(this.getStringWidth(header1), this.getStringWidth(header2));
            int w2 = Math.max(this.getStringWidth(stackName), this.getStringWidth(strTotal));
            int totalWidth = w1 + w2 + 60;

            int x = mouseX + 10;
            int y = mouseY - 10;

            if (x + totalWidth - 20 >= this.width)
            {
                x -= totalWidth + 20;
            }

            int x1 = x + 10;
            int x2 = x1 + w1 + 20;

            RenderUtils.drawOutlinedBox(x, y, totalWidth, 60, 0xFF000000, GuiBase.COLOR_HORIZONTAL_BAR);
            y += 6;
            int y1 = y;
            y += 4;

            this.drawString(x1, y, 0xFFFFFFFF, header1, matrixStack);
            this.drawString(x2 + 20, y, 0xFFFFFFFF, stackName, matrixStack);
            y += 16;

            this.drawString(x1, y, 0xFFFFFFFF, header2, matrixStack);
            this.drawString(x2, y, 0xFFFFFFFF, strTotal, matrixStack);

            RenderUtils.drawRect(x2, y1, 16, 16, 0x20FFFFFF);

            //RenderSystem.disableLighting();
            RenderUtils.enableDiffuseLightingGui3D();

            this.mc.getItemRenderer().renderInGuiWithOverrides(stack, x2, y1);

            RenderUtils.disableDiffuseLighting();
            matrixStack.pop();
        }
    }

    private String getFormattedCountString(int total)
    {
        return String.format("%d", total);
    }

    static class ButtonListener implements IButtonActionListener
    {
        private final ButtonType type;
        private final WidgetListEdits listWidget;
        private final PlayereditEntry entry;

        public ButtonListener(ButtonType type, PlayereditEntry entry, WidgetListEdits listWidget)
        {
            this.type = type;
            this.listWidget = listWidget;
            this.entry = entry;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.BLOCKS)
            {
                EditListBlockedit editList = new EditListBlockedit(this.entry.getBlocks(), true);
                GuiBase.openGui(new GuiBlockeditData(editList, this.entry.getStack().getTranslationKey(), listWidget.getParent()));
            }
        }

        public enum ButtonType
        {
            BLOCKS("watson.gui.button.edits.blocks");

            private final String translationKey;

            ButtonType(String translationKey)
            {
                this.translationKey = translationKey;
            }

            public String getDisplayName()
            {
                return StringUtils.translate(translationKey);
            }
        }
    }
}
