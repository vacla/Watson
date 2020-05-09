package eu.minemania.watson.gui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nullable;
import com.mojang.blaze3d.systems.RenderSystem;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.db.BlockEdit;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiDialogBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.interfaces.ICompletionListener;
import fi.dy.masa.malilib.interfaces.IConfirmationListener;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class GuiDisplayBoxBase extends GuiDialogBase implements ICompletionListener
{
    protected final List<BlockEdit> blockedit;
    protected final List<String> originalText;
    protected final IConfirmationListener listener;

    public GuiDisplayBoxBase(String titleKey, List<BlockEdit> blockedit, IConfirmationListener listener, @Nullable Screen parent)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(titleKey);
        System.out.println("title: "+this.title);
        this.listener = listener;
        this.useTitleHierarchy = false;
        this.setBlitOffset(1);
        this.blockedit = blockedit;
        this.originalText = blockString(blockedit);
System.out.println(this.originalText);
        this.setWidthAndHeight(300, getMessageHeight() + 50);
        this.centerOnScreen();
    }

    @Override
    public void initGui()
    {
        this.clearElements();
        //this.centerOnScreen();
        int x = this.dialogLeft + 10;
        System.out.println("x: "+x);
        int y = this.dialogTop + this.dialogHeight - 24;
        System.out.println("y: "+y);
        int buttonWidth = this.getButtonWidth();

        ButtonGeneric button;
        for(int i = 0; i < this.originalText.size();i++)
        {
            String text = this.originalText.get(i);
            this.addLabel(x, y, StringUtils.getStringWidth(text), 8, 0xFFC0C0C0, text);
            button = new ButtonGeneric(x + 10 + StringUtils.getStringWidth(text), y - 7, StringUtils.getStringWidth("TELEPORT") + 6, 20, "TELEPORT");
            this.addButton(button, new ButtonListenerTeleport(blockedit.get(i)));
            y += this.fontHeight + 10;
        }

        this.createButton(x, y, buttonWidth, ButtonType.CLOSE);

        this.mc.keyboard.enableRepeatEvents(true);
    }

    public int getMessageHeight()
    {
        return this.originalText.size() * (this.fontHeight + 1) - 1 + 5;
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

    @Override
    public void resize(MinecraftClient client, int width, int height)
    {
        if(this.getParent() != null)
        {
            System.out.println(this.getParent().getClass());
            System.out.println("something");
            System.out.println(width);
            System.out.println(height);
            this.getParent().resize(client, width, height);
        }
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        if(this.getParent() != null)
        {
            this.getParent().render(mouseX, mouseY, partialTicks);
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef(0, 0, this.getBlitOffset());

        this.drawStringWithShadow(this.getTitleString(), this.dialogLeft + 10, this.dialogTop + 4, COLOR_WHITE);

        this.drawButtons(mouseX, mouseY, partialTicks);
        RenderSystem.popMatrix();
    }

    protected ButtonListener createActionListener(ButtonType type)
    {
        return new ButtonListener(type, this);
    }

    @Override
    public void onTaskCompleted()
    {
        if (this.getParent() instanceof ICompletionListener)
        {
            ((ICompletionListener) this.getParent()).onTaskCompleted();
        }
    }

    @Override
    public void onTaskAborted()
    {
        if (this.getParent() instanceof ICompletionListener)
        {
            ((ICompletionListener) this.getParent()).onTaskAborted();
        }
    }

    public List<String> blockString(List<BlockEdit> listBlockEdit)
    {
        List<String> strings = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for(BlockEdit edit : listBlockEdit)
        {
            calendar.setTimeInMillis(edit.time);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            strings.add(StringUtils.translate("watson.gui.label.blockedit.list.blocks", edit.x, edit.y, edit.z, day, month, year, hour, minute, second, edit.world, edit.block.getName()));
        }
        return strings;
    }

    protected static class ButtonListener implements IButtonActionListener
    {
        private final GuiDisplayBoxBase gui;
        private final ButtonType type;

        public ButtonListener(ButtonType type, GuiDisplayBoxBase gui)
        {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.CLOSE)
            {
                this.gui.listener.onActionConfirmed();
            }

            GuiBase.openGui(this.gui.getParent());
        }
    }

    public static class ButtonListenerTeleport implements IButtonActionListener
    {
        private BlockEdit edit;

        public ButtonListenerTeleport(BlockEdit edit)
        {
            this.edit = edit;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            Teleport.teleport(edit.x, edit.y, edit.z);
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
}
