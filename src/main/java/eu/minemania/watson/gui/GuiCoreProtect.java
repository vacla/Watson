package eu.minemania.watson.gui;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.CoreProtectInfo;
import eu.minemania.watson.gui.GuiCoreProtect.ButtonListenerCycleTypePacket.CoreProtectMode;
import eu.minemania.watson.network.ledger.PluginInspectPacketHandler;
import eu.minemania.watson.network.ledger.PluginPurgePacketHandler;
import eu.minemania.watson.network.ledger.PluginRollbackPacketHandler;
import eu.minemania.watson.network.ledger.PluginSearchPacketHandler;
import fi.dy.masa.malilib.gui.*;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IStringListConsumer;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetInfoIcon;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuiCoreProtect extends GuiBase
{
    protected GuiTextFieldInteger textFieldRange;
    protected GuiTextFieldGeneric textFieldSource;
    protected GuiTextFieldGeneric textFieldTimeBefore;
    protected GuiTextFieldGeneric textFieldTimeAfter;
    protected GuiTextFieldInteger textFieldX;
    protected GuiTextFieldInteger textFieldY;
    protected GuiTextFieldInteger textFieldZ;
    protected GuiTextFieldInteger textFieldPages;
    protected CoreProtectInfo coreProtectInfo = new CoreProtectInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "", "", 0, 0, 0, 0, CoreProtectMode.INSPECT, 10);

    protected GuiCoreProtect()
    {
        this.title = DataManager.getPluginVersion();
        this.coreProtectInfo = DataManager.getCoreProtectInfo() == null ? this.coreProtectInfo : DataManager.getCoreProtectInfo();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int y = this.height - 26;
        int buttonWidth;
        String label;
        ButtonGeneric button;

        ButtonListener.ButtonType type = ButtonListener.ButtonType.MAIN_MENU;
        label = type.getDisplayName();
        buttonWidth = this.getStringWidth(label) + 20;
        int x = this.width - buttonWidth - 10;
        button = new ButtonGeneric(x, y, buttonWidth, 20, label);

        this.addButton(button, new ButtonListener(type, this));

        x = 12;
        y = button.getHeight() / 2 + 30;
        int width = 70;
        int offset;

        if (coreProtectInfo.getCoreProtectMode() != CoreProtectMode.INSPECT)
        {
            label = StringUtils.translate("watson.gui.label.ledger.title.action"); //Action
            this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 4;
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.button", label));
            button = createButton(x + offset + 20, y, width, ConsumerButtonListener.ButtonType.ACTION);

            label = StringUtils.translate("watson.gui.label.ledger.title.dimension"); //Dimension
            this.addLabel(button.getX() + button.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + button.getWidth();
            this.addWidget(new WidgetInfoIcon(button.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.button", label));
            button = createButton(button.getX() + offset + 20, y, width, ConsumerButtonListener.ButtonType.DIMENSION);

            label = StringUtils.translate("watson.gui.label.ledger.title.block"); //Block
            this.addLabel(button.getX() + button.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + button.getWidth();
            this.addWidget(new WidgetInfoIcon(button.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.button", label));
            createButton(button.getX() + offset + 20, y, width, ConsumerButtonListener.ButtonType.BLOCK);

            y += 30;

            label = StringUtils.translate("watson.gui.label.ledger.title.entitytype"); //EntityType
            this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 4;
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.button", label));
            button = createButton(x + offset + 20, y, width, ConsumerButtonListener.ButtonType.ENTITYTYPE);

            label = StringUtils.translate("watson.gui.label.ledger.title.item"); //Item
            this.addLabel(button.getX() + button.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + button.getWidth();
            this.addWidget(new WidgetInfoIcon(button.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.button", label));
            button = createButton(button.getX() + offset + 20, y, width, ConsumerButtonListener.ButtonType.ITEM);

            label = StringUtils.translate("watson.gui.label.ledger.title.tag"); //Tag
            this.addLabel(button.getX() + button.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + button.getWidth();
            this.addWidget(new WidgetInfoIcon(button.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.button", label));
            createButton(button.getX() + offset + 20, y, width, ConsumerButtonListener.ButtonType.TAG);

            y += 30;

            label = StringUtils.translate("watson.gui.label.ledger.title.range"); //Range
            this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 4;
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.range"));

            this.textFieldRange = new GuiTextFieldInteger(x + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldRange.setText(String.valueOf(coreProtectInfo.getRange()));
            this.addTextField(this.textFieldRange, new RangeTextFieldListener(this));

            label = StringUtils.translate("watson.gui.label.ledger.title.source"); //Source
            this.addLabel(textFieldRange.getX() + textFieldRange.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + textFieldRange.getWidth();
            this.addWidget(new WidgetInfoIcon(textFieldRange.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.source"));

            this.textFieldSource = new GuiTextFieldGeneric(textFieldRange.getX() + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldSource.setText(coreProtectInfo.getSources());
            this.addTextField(this.textFieldSource, new SourceTextFieldListener(this));

            y += 30;

            label = StringUtils.translate("watson.gui.label.ledger.title.time.before"); //Time before
            this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 4;
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.time"));

            this.textFieldTimeBefore = new GuiTextFieldGeneric(x + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldTimeBefore.setText(coreProtectInfo.getTimeBefore());
            this.addTextField(this.textFieldTimeBefore, new TimeBeforeTextFieldListener(this));

            label = StringUtils.translate("watson.gui.label.ledger.title.time.after"); //Time after
            this.addLabel(textFieldTimeBefore.getX() + textFieldTimeBefore.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + textFieldTimeBefore.getWidth();
            this.addWidget(new WidgetInfoIcon(textFieldTimeBefore.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.time"));

            this.textFieldTimeAfter = new GuiTextFieldGeneric(textFieldTimeBefore.getX() + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldTimeAfter.setText(coreProtectInfo.getTimeAfter());
            this.addTextField(this.textFieldTimeAfter, new TimeAfterTextFieldListener(this));
        }

        if (coreProtectInfo.getCoreProtectMode() == CoreProtectMode.INSPECT)
        {
            label = "X"; //X
            this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 4;
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.coords"));

            this.textFieldX = new GuiTextFieldInteger(x + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldX.setText(String.valueOf(coreProtectInfo.getX()));
            this.addTextField(this.textFieldX, new XTextFieldListener(this));

            label = "Y"; //Y
            this.addLabel(textFieldX.getX() + textFieldX.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + textFieldX.getWidth();
            this.addWidget(new WidgetInfoIcon(textFieldX.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.coords"));

            this.textFieldY = new GuiTextFieldInteger(textFieldX.getX() + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldY.setText(String.valueOf(coreProtectInfo.getY()));
            this.addTextField(this.textFieldY, new YTextFieldListener(this));

            label = "Z"; //Z
            this.addLabel(textFieldY.getX() + textFieldY.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + textFieldY.getWidth();
            this.addWidget(new WidgetInfoIcon(textFieldY.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.coords"));

            this.textFieldZ = new GuiTextFieldInteger(textFieldY.getX() + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldZ.setText(String.valueOf(coreProtectInfo.getZ()));
            this.addTextField(this.textFieldZ, new ZTextFieldListener(this));
        }

        if (coreProtectInfo.getCoreProtectMode() == CoreProtectMode.INSPECT || coreProtectInfo.getCoreProtectMode() == CoreProtectMode.SEARCH)
        {
            y += 30;

            label = StringUtils.translate("watson.gui.label.ledger.title.pages"); //Pages
            this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 4;
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.pages"));

            this.textFieldPages = new GuiTextFieldInteger(x + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldPages.setText(String.valueOf(coreProtectInfo.getPages()));
            this.addTextField(this.textFieldPages, new PagesTextFieldListener(this));
        }

        y = this.height - 50;

        label = StringUtils.translate("watson.gui.button.ledger.ledgermode", coreProtectInfo.getCoreProtectMode().getDisplayName());
        buttonWidth = this.getStringWidth(label) + 20;
        button = new ButtonGeneric(x, y, buttonWidth, 20, label);
        button.setHoverStrings("watson.gui.button.ledger.ledgermode.hover");
        button.setHoverInfoRequiresShift(true);
        this.addButton(button, new ButtonListenerCycleTypePacket(this));

        type = ButtonListener.ButtonType.SUBMIT;
        label = type.getDisplayName();
        buttonWidth = this.getStringWidth(label) + 25;
        button = new ButtonGeneric(button.getX() + button.getWidth() + 10, y, buttonWidth, 20, label);
        this.addButton(button, new ButtonListener(type, this));

        type = ButtonListener.ButtonType.CLEAR;
        label = type.getDisplayName();
        buttonWidth = this.getStringWidth(label) + 25;
        button = new ButtonGeneric(button.getX() + button.getWidth() + 10, y, buttonWidth, 20, label);
        this.addButton(button, new ButtonListener(type, this));
    }

    private ButtonGeneric createButton(int x, int y, int width, ConsumerButtonListener.ButtonType type)
    {
        ConsumerButtonListener listener = new ConsumerButtonListener(type, this);
        String label = type.getDisplayName();

        if (width == -1)
        {
            width = this.getStringWidth(label) + 10;
        }

        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, label);

        this.addButton(button, listener);

        return button;
    }

    private ArrayList<String> getDimensions()
    {
        ArrayList<String> currentDimensions = new ArrayList<>();

        if (!DataManager.getPluginWorlds().isEmpty())
        {
            currentDimensions.addAll(DataManager.getPluginWorlds());
        }

        return currentDimensions;
    }

    private ArrayList<String> getTotalList(ArrayList<String> list)
    {
        ArrayList<String> newList = new ArrayList<>(list);
        ArrayList<String> invertedList = new ArrayList<>();
        for (String item : list)
        {
            invertedList.add("!"+item);
        }

        newList.addAll(invertedList);

        return newList;
    }

    public boolean validate()
    {
        boolean error = false;
        ArrayList<String> listActionError = new ArrayList<>();
        ArrayList<String> ledgerActions = getTotalList(DataManager.getPluginActions());
        for (String action : coreProtectInfo.getActions())
        {
            if (!action.contains("-"))
            {
                addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.action"), action);
                error = true;
                break;
            }
            if (!ledgerActions.contains(action))
            {
                listActionError.add(action);
            }
        }
        if (!listActionError.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.action"), String.join(",", listActionError));
            error = true;
        }

        for (String dimensionText : coreProtectInfo.getDimensions())
        {
            if (dimensionText.isEmpty())
            {
                break;
            }
            if (!dimensionText.contains(":"))
            {
                addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.dimension"), dimensionText);
                error = true;
                break;
            }
        }

        ArrayList<String> listBlockError = new ArrayList<>();
        ArrayList<String> ledgerBlocks = getTotalList(DataManager.getBlocks());
        for (String blockText : coreProtectInfo.getBlocks())
        {
            if (blockText.isEmpty())
            {
                break;
            }
            if (!blockText.contains(":"))
            {
                addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.block"), blockText);
                error = true;
                break;
            }
            if (!ledgerBlocks.contains(blockText))
            {
                listBlockError.add(blockText);
            }
        }
        if (!listBlockError.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.block"), String.join(",", listBlockError));
            error = true;
        }

        ArrayList<String> listEntityTypeError = new ArrayList<>();
        ArrayList<String> ledgerEntityTypes = getTotalList(DataManager.getEntityTypes());
        for (String entityTypeText : coreProtectInfo.getEntityTypes())
        {
            if (entityTypeText.isEmpty())
            {
                break;
            }
            if (!entityTypeText.contains(":"))
            {
                addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.entitytype"), entityTypeText);
                error = true;
                break;
            }
            if (!ledgerEntityTypes.contains(entityTypeText))
            {
                listEntityTypeError.add(entityTypeText);
            }
        }
        if (!listEntityTypeError.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.entitytype"), String.join(",", listEntityTypeError));
            error = true;
        }

        ArrayList<String> listItemError = new ArrayList<>();
        ArrayList<String> ledgerItems = getTotalList(DataManager.getItems());
        for (String itemText : coreProtectInfo.getItems())
        {
            if (itemText.isEmpty())
            {
                break;
            }
            if (!itemText.contains(":"))
            {
                addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.item"), itemText);
                error = true;
                break;
            }
            if (!ledgerItems.contains(itemText))
            {
                listItemError.add(itemText);
            }
        }
        if (!listItemError.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.item"), String.join(",", listItemError));
            error = true;
        }

        ArrayList<String> listTagError = new ArrayList<>();
        ArrayList<String> ledgerTags = getTotalList(DataManager.getTags());
        for (String tagText : coreProtectInfo.getTags())
        {
            if (tagText.isEmpty())
            {
                break;
            }
            if (!tagText.contains(":") || !tagText.contains("#"))
            {
                addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.tag"), tagText);
                error = true;
                break;
            }
            if (!ledgerTags.contains(tagText))
            {
                listTagError.add(tagText);
            }
        }
        if (!listTagError.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.tag"), String.join(",", listTagError));
            error = true;
        }

        int textRange = coreProtectInfo.getRange();
        if (textRange <= 1 && textRange != 0)
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.range"), textRange);
            error = true;
        }
        String textSource = coreProtectInfo.getSources();
        String[] sourcesText = textSource.split(",");
        for (String sourceText : sourcesText)
        {
            if (sourceText.trim().isEmpty())
            {
                break;
            }
        }
        String textTimeBefore = coreProtectInfo.getTimeBefore();
        if (!textTimeBefore.matches("^([0-9]+[smhdw])+$") && !textTimeBefore.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.time.before"), textTimeBefore);
            error = true;
        }
        String textTimeAfter = coreProtectInfo.getTimeAfter();
        if (!textTimeAfter.matches("^([0-9]+[smhdw])+$") && !textTimeAfter.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.time.after"), textTimeAfter);
            error = true;
        }

        return error;
    }

    private void setCoreProtectInfo()
    {
        List<String> actions = this.coreProtectInfo.getActions();
        List<String> blocks = this.coreProtectInfo.getBlocks();
        List<String> dimension = this.coreProtectInfo.getDimensions();
        List<String> entityTypes = this.coreProtectInfo.getEntityTypes();
        List<String> items = this.coreProtectInfo.getItems();
        List<String> tags = this.coreProtectInfo.getTags();
        String source = this.coreProtectInfo.getSources();
        String timeBefore = this.coreProtectInfo.getTimeBefore();
        String timeAfter = this.coreProtectInfo.getTimeAfter();
        int range = this.coreProtectInfo.getRange();
        int x = this.coreProtectInfo.getX();
        int y = this.coreProtectInfo.getY();
        int z = this.coreProtectInfo.getZ();
        CoreProtectMode coreProtectMode = this.coreProtectInfo.getCoreProtectMode();
        int pages = this.coreProtectInfo.getPages();

        CoreProtectInfo coreProtectInfo = new CoreProtectInfo(actions, blocks, dimension, entityTypes, items, tags, source, timeBefore, timeAfter, range, x, y, z, coreProtectMode, pages);
        DataManager.setCoreProtectInfo(coreProtectInfo);
    }

    private void clearCoreProtectInfo()
    {
        CoreProtectInfo coreProtectInfo = new CoreProtectInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "", "", 0, 0, 0, 0, CoreProtectMode.INSPECT, 10);
        this.coreProtectInfo = coreProtectInfo;
        DataManager.setCoreProtectInfo(coreProtectInfo);
    }

    @Override
    protected void closeGui(boolean showParent)
    {
        setCoreProtectInfo();
        super.closeGui(showParent);
    }

    public static class SourceTextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        private final GuiCoreProtect parent;

        public SourceTextFieldListener(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            try
            {
                parent.coreProtectInfo.setSources(textField.getText());
            }
            catch (Exception e)
            {
                parent.coreProtectInfo.setSources("");
            }

            return false;
        }
    }

    public static class TimeBeforeTextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        private final GuiCoreProtect parent;

        public TimeBeforeTextFieldListener(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            try
            {
                parent.coreProtectInfo.setTimeBefore(textField.getText());
            }
            catch (Exception e)
            {
                parent.coreProtectInfo.setTimeBefore("");
            }

            return false;
        }
    }

    public static class TimeAfterTextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        private final GuiCoreProtect parent;

        public TimeAfterTextFieldListener(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            try
            {
                parent.coreProtectInfo.setTimeAfter(textField.getText());
            }
            catch (Exception e)
            {
                parent.coreProtectInfo.setTimeAfter("");
            }

            return false;
        }
    }

    public static class RangeTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiCoreProtect parent;

        public RangeTextFieldListener(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.coreProtectInfo.setRange(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    public static class XTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiCoreProtect parent;

        public XTextFieldListener(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.coreProtectInfo.setX(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    public static class YTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiCoreProtect parent;

        public YTextFieldListener(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.coreProtectInfo.setY(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    public static class ZTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiCoreProtect parent;

        public ZTextFieldListener(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.coreProtectInfo.setZ(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    public static class PagesTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiCoreProtect parent;

        public PagesTextFieldListener(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.coreProtectInfo.setPages(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    private static class ActionListCreator implements IStringListConsumer
    {
        GuiCoreProtect parent;

        public ActionListCreator(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.coreProtectInfo.setActions(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class DimensionListCreator implements IStringListConsumer
    {
        GuiCoreProtect parent;

        public DimensionListCreator(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.coreProtectInfo.setDimensions(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class BlockListCreator implements IStringListConsumer
    {
        GuiCoreProtect parent;

        public BlockListCreator(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.coreProtectInfo.setBlocks(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class EntityTypeListCreator implements IStringListConsumer
    {
        GuiCoreProtect parent;

        public EntityTypeListCreator(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.coreProtectInfo.setEntityTypes(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class ItemListCreator implements IStringListConsumer
    {
        GuiCoreProtect parent;

        public ItemListCreator(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.coreProtectInfo.setItems(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class TagListCreator implements IStringListConsumer
    {
        GuiCoreProtect parent;

        public TagListCreator(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.coreProtectInfo.setTags(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    public static class ConsumerButtonListener implements IButtonActionListener
    {
        private final ButtonType type;
        private final GuiCoreProtect parent;

        public ConsumerButtonListener(ButtonType type, GuiCoreProtect parent)
        {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.ACTION)
            {
                ActionListCreator actionCreator = new ActionListCreator(parent);
                ArrayList<String> actions = parent.getTotalList(DataManager.getPluginActions());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(actions, actionCreator, false, parent.coreProtectInfo.getActions());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.DIMENSION)
            {
                DimensionListCreator dimensionCreator = new DimensionListCreator(parent);
                ArrayList<String> dimensions = parent.getTotalList(parent.getDimensions());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(dimensions, dimensionCreator, false, parent.coreProtectInfo.getDimensions());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.BLOCK)
            {
                BlockListCreator blockCreator = new BlockListCreator(parent);
                ArrayList<String> blocks = parent.getTotalList(DataManager.getBlocks());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(blocks, blockCreator, true, parent.coreProtectInfo.getBlocks());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.ENTITYTYPE)
            {
                EntityTypeListCreator entityTypeCreator = new EntityTypeListCreator(parent);
                ArrayList<String> entityTypes = parent.getTotalList(DataManager.getEntityTypes());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(entityTypes, entityTypeCreator, true, parent.coreProtectInfo.getEntityTypes());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.ITEM)
            {
                ItemListCreator itemCreator = new ItemListCreator(parent);
                ArrayList<String> items = parent.getTotalList(DataManager.getItems());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(items, itemCreator, true, parent.coreProtectInfo.getItems());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.TAG)
            {
                TagListCreator tagCreator = new TagListCreator(parent);
                ArrayList<String> tags = parent.getTotalList(DataManager.getTags());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(tags, tagCreator, true, parent.coreProtectInfo.getTags());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
        }

        public enum ButtonType
        {
            ACTION("watson.gui.button.ledger.action"),
            BLOCK("watson.gui.button.ledger.block"),
            DIMENSION("watson.gui.button.ledger.dimension"),
            ENTITYTYPE("watson.gui.button.ledger.entitytype"),
            ITEM("watson.gui.button.ledger.item"),
            TAG("watson.gui.button.ledger.tag");

            private final String labelKey;

            ButtonType(String labelKey)
            {
                this.labelKey = labelKey;
            }

            public String getLabelKey()
            {
                return this.labelKey;
            }

            public String getDisplayName()
            {
                return StringUtils.translate(this.getLabelKey());
            }
        }
    }

    public static class ButtonListener implements IButtonActionListener
    {
        private final ButtonType type;
        private final GuiCoreProtect parent;

        public ButtonListener(ButtonType type, GuiCoreProtect parent)
        {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.MAIN_MENU)
            {
                this.parent.setCoreProtectInfo();
                GuiMainMenu gui = new GuiMainMenu();
                gui.setParent(parent.getParent());
                GuiBase.openGui(gui);
                return;
            }

            if (this.type == ButtonType.CLEAR)
            {
                this.parent.clearCoreProtectInfo();
                this.parent.initGui();
                return;
            }

            if (this.type == ButtonType.SUBMIT)
            {
                CoreProtectInfo coreProtectInfo = parent.coreProtectInfo;
                if (!coreProtectInfo.getCoreProtectMode().equals(CoreProtectMode.INSPECT) && parent.validate())
                {
                    return;
                }

                List<String> action = coreProtectInfo.getActions();
                List<String> dimension = coreProtectInfo.getDimensions();
                List<String> block = coreProtectInfo.getBlocks();
                List<String> entityType = coreProtectInfo.getEntityTypes();
                List<String> item = coreProtectInfo.getItems();
                List<String> tag = coreProtectInfo.getTags();
                int range = coreProtectInfo.getRange();
                String source = coreProtectInfo.getSources();
                String timeBefore = coreProtectInfo.getTimeBefore();
                String timeAfter = coreProtectInfo.getTimeAfter();
                int x = coreProtectInfo.getX();
                int y = coreProtectInfo.getY();
                int z = coreProtectInfo.getZ();
                int pages = coreProtectInfo.getPages();
                MinecraftClient mc = parent.mc;

                switch (coreProtectInfo.getCoreProtectMode())
                {
                    case INSPECT -> new PluginInspectPacketHandler().sendPacket(x, y, z, pages, mc);
                    case PURGE -> new PluginPurgePacketHandler().sendPacket(action, dimension, block, entityType, item, tag, range, source, timeBefore, timeAfter, mc);
                    case ROLLBACK -> new PluginRollbackPacketHandler().sendPacket(action, dimension, block, entityType, item, tag, range, source, timeBefore, timeAfter, false, mc);
                    case RESTORE -> new PluginRollbackPacketHandler().sendPacket(action, dimension, block, entityType, item, tag, range, source, timeBefore, timeAfter, true, mc);
                    case SEARCH -> new PluginSearchPacketHandler().sendPacket(action, dimension, block, entityType, item, tag, range, source, timeBefore, timeAfter, pages, mc);
                }
            }
        }

        public enum ButtonType
        {
            CLEAR("watson.gui.button.ledger.clear"),
            MAIN_MENU("watson.gui.button.change_menu.to_main_menu"),
            SUBMIT("watson.gui.button.ledger.submit");

            private final String labelKey;

            ButtonType(String labelKey)
            {
                this.labelKey = labelKey;
            }

            public String getLabelKey()
            {
                return this.labelKey;
            }

            public String getDisplayName()
            {
                return StringUtils.translate(this.getLabelKey());
            }
        }
    }

    public static class ButtonListenerCycleTypePacket implements IButtonActionListener
    {
        private final GuiCoreProtect parent;

        public ButtonListenerCycleTypePacket(GuiCoreProtect parent)
        {
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            CoreProtectMode mode = parent.coreProtectInfo.getCoreProtectMode().cycle(mouseButton == 0);
            parent.coreProtectInfo.setCoreProtectMode(mode);
            parent.initGui();
        }

        public enum CoreProtectMode
        {
            INSPECT("watson.gui.button.ledger.inspect"),
            PURGE("watson.gui.button.ledger.purge"),
            RESTORE("watson.gui.button.ledger.restore"),
            ROLLBACK("watson.gui.button.ledger.rollback"),
            SEARCH("watson.gui.button.ledger.search");

            private final String labelKey;

            CoreProtectMode(String labelKey)
            {
                this.labelKey = labelKey;
            }

            public String getLabelKey()
            {
                return this.labelKey;
            }

            public String getDisplayName()
            {
                return StringUtils.translate(this.getLabelKey());
            }

            public CoreProtectMode cycle(boolean forward)
            {
                int id = this.ordinal();

                if (forward)
                {
                    if (++id >= values().length)
                    {
                        id = 0;
                    }
                }
                else
                {
                    if (--id < 0)
                    {
                        id = values().length - 1;
                    }
                }

                return values()[id % values().length];
            }
        }
    }
}
