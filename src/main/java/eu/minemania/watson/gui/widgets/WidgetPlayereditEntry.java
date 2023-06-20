package eu.minemania.watson.gui.widgets;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.gui.GuiEdits;
import eu.minemania.watson.selection.EditListPlayeredit;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.DrawContext;

public class WidgetPlayereditEntry extends WidgetListEntryBase<PlayereditSet>
{
    private final WidgetListLoadedPlayeredits parent;
    private final PlayereditSet playeredit;
    private final boolean isOdd;
    private final int buttonsStartX;

    public WidgetPlayereditEntry(int x, int y, int width, int height, boolean isOdd,
                                 PlayereditSet playeredit, int listIndex, WidgetListLoadedPlayeredits parent)
    {
        super(x, y, width, height, playeredit, listIndex);

        this.parent = parent;
        this.playeredit = playeredit;
        this.isOdd = isOdd;
        y += 1;

        int posX = x + width;
        int len;
        ButtonListener listener;
        String text;

        text = StringUtils.translate("watson.gui.button.playeredit.remove");
        len = this.getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.REMOVE, this);
        this.addButton(new ButtonGeneric(posX, y, len, 20, text), listener);

        boolean enabled = this.playeredit.isVisible();
        String pre = enabled ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
        text = pre + StringUtils.translate("watson.message.setting." + (enabled ? "shown" : "hidden")) + GuiBase.TXT_RST;
        len = this.getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.VISIBLE, this);
        this.addButton(new ButtonGeneric(posX, y, len, 20, text), listener);

        text = StringUtils.translate("watson.gui.button.playeredit.edit_list");
        len = this.getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.OPEN_BLOCKLIST, this);
        this.addButton(new ButtonGeneric(posX, y, len, 20, text), listener);

        this.buttonsStartX = posX;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, DrawContext drawContext)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        if (selected || this.isMouseOver(mouseX, mouseY))
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

        String playerName = this.entry.getPlayer();
        this.drawString(this.x + 20, this.y + 7, 0xFFFFFFFF, playerName, drawContext);

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        String text = StringUtils.translate("watson.gui.button.playeredit.hover", this.entry.getBlockEditCount());

        this.drawSubWidgets(mouseX, mouseY, drawContext);

        if (GuiBase.isMouseOver(mouseX, mouseY, this.x, this.y, this.buttonsStartX - 12, this.height))
        {
            RenderUtils.drawHoverText(mouseX, mouseY, ImmutableList.of(text), drawContext);
        }

        RenderUtils.disableDiffuseLighting();
        //RenderSystem.disableLighting();
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final Type type;
        private final WidgetPlayereditEntry widget;

        public ButtonListener(Type type, WidgetPlayereditEntry widget)
        {
            this.type = type;
            this.widget = widget;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == Type.OPEN_BLOCKLIST)
            {
                EditListPlayeredit editList = new EditListPlayeredit(this.widget.playeredit, true);
                GuiEdits gui = new GuiEdits(editList, this.widget.parent.getGuiParent());
                GuiBase.openGui(gui);
            }
            else if (this.type == Type.REMOVE)
            {
                PlayereditSet entry = this.widget.entry;

                DataManager.getEditSelection().getBlockEditSet().removeEdits(entry.getPlayer());
                this.widget.parent.refreshEntries();
            }
            else if (this.type == Type.VISIBLE)
            {
                PlayereditSet entry = this.widget.playeredit;

                entry.setVisible(!entry.isVisible());
                this.widget.parent.refreshEntries();
            }
        }

        public enum Type
        {
            OPEN_BLOCKLIST,
            REMOVE,
            VISIBLE
        }
    }

}
