package eu.minemania.watson.gui;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.LedgerInfo;
import eu.minemania.watson.gui.GuiLedger.ButtonListenerCycleTypePacket.LedgerMode;
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
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GuiLedger extends GuiBase
{
    protected GuiTextFieldInteger textFieldRange;
    protected GuiTextFieldGeneric textFieldSource;
    protected GuiTextFieldGeneric textFieldTimeBefore;
    protected GuiTextFieldGeneric textFieldTimeAfter;
    protected GuiTextFieldInteger textFieldX;
    protected GuiTextFieldInteger textFieldY;
    protected GuiTextFieldInteger textFieldZ;
    protected GuiTextFieldInteger textFieldPages;
    protected LedgerInfo ledgerInfo = new LedgerInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "", "", 0, 0, 0, 0, LedgerMode.INSPECT, 10);

    protected GuiLedger()
    {
        this.title = StringUtils.translate("watson.gui.title.ledger", DataManager.getLedgerVersion());
        this.ledgerInfo = DataManager.getLedgerInfo() == null ? this.ledgerInfo : DataManager.getLedgerInfo();
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

        if (ledgerInfo.getLedgerMode() != LedgerMode.INSPECT)
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
            this.textFieldRange.setText(String.valueOf(ledgerInfo.getRange()));
            this.addTextField(this.textFieldRange, new RangeTextFieldListener(this));

            label = StringUtils.translate("watson.gui.label.ledger.title.source"); //Source
            this.addLabel(textFieldRange.getX() + textFieldRange.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + textFieldRange.getWidth();
            this.addWidget(new WidgetInfoIcon(textFieldRange.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.source"));

            this.textFieldSource = new GuiTextFieldGeneric(textFieldRange.getX() + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldSource.setText(ledgerInfo.getSources());
            this.addTextField(this.textFieldSource, new SourceTextFieldListener(this));

            y += 30;

            label = StringUtils.translate("watson.gui.label.ledger.title.time.before"); //Time before
            this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 4;
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.time"));

            this.textFieldTimeBefore = new GuiTextFieldGeneric(x + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldTimeBefore.setText(ledgerInfo.getTimeBefore());
            this.addTextField(this.textFieldTimeBefore, new TimeBeforeTextFieldListener(this));

            label = StringUtils.translate("watson.gui.label.ledger.title.time.after"); //Time after
            this.addLabel(textFieldTimeBefore.getX() + textFieldTimeBefore.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + textFieldTimeBefore.getWidth();
            this.addWidget(new WidgetInfoIcon(textFieldTimeBefore.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.time"));

            this.textFieldTimeAfter = new GuiTextFieldGeneric(textFieldTimeBefore.getX() + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldTimeAfter.setText(ledgerInfo.getTimeAfter());
            this.addTextField(this.textFieldTimeAfter, new TimeAfterTextFieldListener(this));
        }

        if (ledgerInfo.getLedgerMode() == LedgerMode.INSPECT)
        {
            label = "X"; //X
            this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 4;
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.coords"));

            this.textFieldX = new GuiTextFieldInteger(x + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldX.setText(String.valueOf(ledgerInfo.getX()));
            this.addTextField(this.textFieldX, new XTextFieldListener(this));

            label = "Y"; //Y
            this.addLabel(textFieldX.getX() + textFieldX.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + textFieldX.getWidth();
            this.addWidget(new WidgetInfoIcon(textFieldX.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.coords"));

            this.textFieldY = new GuiTextFieldInteger(textFieldX.getX() + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldY.setText(String.valueOf(ledgerInfo.getY()));
            this.addTextField(this.textFieldY, new YTextFieldListener(this));

            label = "Z"; //Z
            this.addLabel(textFieldY.getX() + textFieldY.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + textFieldY.getWidth();
            this.addWidget(new WidgetInfoIcon(textFieldY.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.coords"));

            this.textFieldZ = new GuiTextFieldInteger(textFieldY.getX() + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldZ.setText(String.valueOf(ledgerInfo.getZ()));
            this.addTextField(this.textFieldZ, new ZTextFieldListener(this));
        }

        if (ledgerInfo.getLedgerMode() == LedgerMode.INSPECT || ledgerInfo.getLedgerMode() == LedgerMode.SEARCH)
        {
            y += 30;

            label = StringUtils.translate("watson.gui.label.ledger.title.pages"); //Pages
            this.addLabel(x, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 4;
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.pages"));

            this.textFieldPages = new GuiTextFieldInteger(x + offset + 20, y + 2, width, 14, this.textRenderer);
            this.textFieldPages.setText(String.valueOf(ledgerInfo.getPages()));
            this.addTextField(this.textFieldPages, new PagesTextFieldListener(this));
        }

        y = this.height - 50;

        label = StringUtils.translate("watson.gui.button.ledger.ledgermode", ledgerInfo.getLedgerMode().getDisplayName());
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

    private ArrayList<String> getDimensions(String dimensionText)
    {
        ArrayList<String> currentDimensions = new ArrayList<>();
        ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();

        if (networkHandler == null)
        {
            return currentDimensions;
        }

        Set<RegistryKey<World>> dimensions = networkHandler.getWorldKeys();
        for (RegistryKey<World> dimension : dimensions)
        {
            String dimensionString = dimension.getValue().toString();
            String invertedDimensionString = "!"+dimensionString;
            if (!dimensionText.isEmpty() && (dimensionString.equals(dimensionText) || invertedDimensionString.equals(dimensionText)))
            {
                currentDimensions.add(dimensionText);
                return currentDimensions;
            }
            else if (dimensionText.isEmpty())
            {
                currentDimensions.add(dimension.getValue().toString());
            }
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
        ArrayList<String> ledgerActions = getTotalList(DataManager.getLedgerActions());
        for (String action : ledgerInfo.getActions())
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

        ArrayList<String> listDimensionError = new ArrayList<>();
        for (String dimensionText : ledgerInfo.getDimensions())
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

            if (getDimensions(dimensionText).isEmpty())
            {
                listDimensionError.add(dimensionText);
            }
        }
        if (!listDimensionError.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.dimension"), String.join(",", listDimensionError));
            error = true;
        }

        ArrayList<String> listBlockError = new ArrayList<>();
        ArrayList<String> ledgerBlocks = getTotalList(DataManager.getBlocks());
        for (String blockText : ledgerInfo.getBlocks())
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
        for (String entityTypeText : ledgerInfo.getEntityTypes())
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
        for (String itemText : ledgerInfo.getItems())
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
        for (String tagText : ledgerInfo.getTags())
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

        int textRange = ledgerInfo.getRange();
        if (textRange <= 1 && textRange != 0)
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.range"), textRange);
            error = true;
        }
        String textSource = ledgerInfo.getSources();
        String[] sourcesText = textSource.split(",");
        for (String sourceText : sourcesText)
        {
            if (sourceText.trim().isEmpty())
            {
                break;
            }
        }
        String textTimeBefore = ledgerInfo.getTimeBefore();
        if (!textTimeBefore.matches("^([0-9]+[smhdw])+$") && !textTimeBefore.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.time.before"), textTimeBefore);
            error = true;
        }
        String textTimeAfter = ledgerInfo.getTimeAfter();
        if (!textTimeAfter.matches("^([0-9]+[smhdw])+$") && !textTimeAfter.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.time.after"), textTimeAfter);
            error = true;
        }

        return error;
    }

    private void setLedgerInfo()
    {
        List<String> actions = this.ledgerInfo.getActions();
        List<String> blocks = this.ledgerInfo.getBlocks();
        List<String> dimension = this.ledgerInfo.getDimensions();
        List<String> entityTypes = this.ledgerInfo.getEntityTypes();
        List<String> items = this.ledgerInfo.getItems();
        List<String> tags = this.ledgerInfo.getTags();
        String source = this.ledgerInfo.getSources();
        String timeBefore = this.ledgerInfo.getTimeBefore();
        String timeAfter = this.ledgerInfo.getTimeAfter();
        int range = this.ledgerInfo.getRange();
        int x = this.ledgerInfo.getX();
        int y = this.ledgerInfo.getY();
        int z = this.ledgerInfo.getZ();
        LedgerMode ledgerMode = this.ledgerInfo.getLedgerMode();
        int pages = this.ledgerInfo.getPages();

        LedgerInfo ledgerInfo = new LedgerInfo(actions, blocks, dimension, entityTypes, items, tags, source, timeBefore, timeAfter, range, x, y, z, ledgerMode, pages);
        DataManager.setLedgerInfo(ledgerInfo);
    }

    private void clearLedgerInfo()
    {
        LedgerInfo ledgerInfo = new LedgerInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "", "", 0, 0, 0, 0, LedgerMode.INSPECT, 10);
        this.ledgerInfo = ledgerInfo;
        DataManager.setLedgerInfo(ledgerInfo);
    }

    @Override
    protected void closeGui(boolean showParent)
    {
        setLedgerInfo();
        super.closeGui(showParent);
    }

    public static class SourceTextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        private final GuiLedger parent;

        public SourceTextFieldListener(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            try
            {
                parent.ledgerInfo.setSources(textField.getText());
            }
            catch (Exception e)
            {
                parent.ledgerInfo.setSources("");
            }

            return false;
        }
    }

    public static class TimeBeforeTextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        private final GuiLedger parent;

        public TimeBeforeTextFieldListener(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            try
            {
                parent.ledgerInfo.setTimeBefore(textField.getText());
            }
            catch (Exception e)
            {
                parent.ledgerInfo.setTimeBefore("");
            }

            return false;
        }
    }

    public static class TimeAfterTextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        private final GuiLedger parent;

        public TimeAfterTextFieldListener(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            try
            {
                parent.ledgerInfo.setTimeAfter(textField.getText());
            }
            catch (Exception e)
            {
                parent.ledgerInfo.setTimeAfter("");
            }

            return false;
        }
    }

    public static class RangeTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiLedger parent;

        public RangeTextFieldListener(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.ledgerInfo.setRange(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    public static class XTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiLedger parent;

        public XTextFieldListener(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.ledgerInfo.setX(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    public static class YTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiLedger parent;

        public YTextFieldListener(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.ledgerInfo.setY(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    public static class ZTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiLedger parent;

        public ZTextFieldListener(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.ledgerInfo.setZ(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    public static class PagesTextFieldListener implements ITextFieldListener<GuiTextFieldInteger>
    {
        private final GuiLedger parent;

        public PagesTextFieldListener(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean onTextChange(GuiTextFieldInteger textField)
        {
            try
            {
                parent.ledgerInfo.setPages(Integer.parseInt(textField.getText()));
            }
            catch (Exception ignored)
            {}

            return false;
        }
    }

    private static class ActionListCreator implements IStringListConsumer
    {
        GuiLedger parent;

        public ActionListCreator(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.ledgerInfo.setActions(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class DimensionListCreator implements IStringListConsumer
    {
        GuiLedger parent;

        public DimensionListCreator(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.ledgerInfo.setDimensions(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class BlockListCreator implements IStringListConsumer
    {
        GuiLedger parent;

        public BlockListCreator(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.ledgerInfo.setBlocks(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class EntityTypeListCreator implements IStringListConsumer
    {
        GuiLedger parent;

        public EntityTypeListCreator(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.ledgerInfo.setEntityTypes(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class ItemListCreator implements IStringListConsumer
    {
        GuiLedger parent;

        public ItemListCreator(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.ledgerInfo.setItems(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    private static class TagListCreator implements IStringListConsumer
    {
        GuiLedger parent;

        public TagListCreator(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.ledgerInfo.setTags(ImmutableList.copyOf(strings));
            GuiBase.openGui(parent);
            return true;
        }
    }

    public static class ConsumerButtonListener implements IButtonActionListener
    {
        private final ButtonType type;
        private final GuiLedger parent;

        public ConsumerButtonListener(ButtonType type, GuiLedger parent)
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
                ArrayList<String> actions = parent.getTotalList(DataManager.getLedgerActions());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(actions, actionCreator, false, parent.ledgerInfo.getActions());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.DIMENSION)
            {
                DimensionListCreator dimensionCreator = new DimensionListCreator(parent);
                ArrayList<String> dimensions = parent.getTotalList(parent.getDimensions(""));
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(dimensions, dimensionCreator, false, parent.ledgerInfo.getDimensions());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.BLOCK)
            {
                BlockListCreator blockCreator = new BlockListCreator(parent);
                ArrayList<String> blocks = parent.getTotalList(DataManager.getBlocks());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(blocks, blockCreator, true, parent.ledgerInfo.getBlocks());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.ENTITYTYPE)
            {
                EntityTypeListCreator entityTypeCreator = new EntityTypeListCreator(parent);
                ArrayList<String> entityTypes = parent.getTotalList(DataManager.getEntityTypes());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(entityTypes, entityTypeCreator, true, parent.ledgerInfo.getEntityTypes());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.ITEM)
            {
                ItemListCreator itemCreator = new ItemListCreator(parent);
                ArrayList<String> items = parent.getTotalList(DataManager.getItems());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(items, itemCreator, true, parent.ledgerInfo.getItems());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
            if (this.type == ButtonType.TAG)
            {
                TagListCreator tagCreator = new TagListCreator(parent);
                ArrayList<String> tags = parent.getTotalList(DataManager.getTags());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(tags, tagCreator, true, parent.ledgerInfo.getTags());
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
        private final GuiLedger parent;

        public ButtonListener(ButtonType type, GuiLedger parent)
        {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.MAIN_MENU)
            {
                this.parent.setLedgerInfo();
                GuiMainMenu gui = new GuiMainMenu();
                gui.setParent(parent.getParent());
                GuiBase.openGui(gui);
                return;
            }

            if (this.type == ButtonType.CLEAR)
            {
                this.parent.clearLedgerInfo();
                this.parent.initGui();
                return;
            }

            if (this.type == ButtonType.SUBMIT)
            {
                LedgerInfo ledgerInfo = parent.ledgerInfo;
                if (!ledgerInfo.getLedgerMode().equals(LedgerMode.INSPECT) && parent.validate())
                {
                    return;
                }

                List<String> action = ledgerInfo.getActions();
                List<String> dimension = ledgerInfo.getDimensions();
                List<String> block = ledgerInfo.getBlocks();
                List<String> entityType = ledgerInfo.getEntityTypes();
                List<String> item = ledgerInfo.getItems();
                List<String> tag = ledgerInfo.getTags();
                int range = ledgerInfo.getRange();
                String source = ledgerInfo.getSources();
                String timeBefore = ledgerInfo.getTimeBefore();
                String timeAfter = ledgerInfo.getTimeAfter();
                int x = ledgerInfo.getX();
                int y = ledgerInfo.getY();
                int z = ledgerInfo.getZ();
                int pages = ledgerInfo.getPages();
                MinecraftClient mc = parent.mc;

                switch (ledgerInfo.getLedgerMode())
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
        private final GuiLedger parent;

        public ButtonListenerCycleTypePacket(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            LedgerMode mode = parent.ledgerInfo.getLedgerMode().cycle(mouseButton == 0);
            parent.ledgerInfo.setLedgerMode(mode);
            parent.initGui();
        }

        public enum LedgerMode
        {
            INSPECT("watson.gui.button.ledger.inspect"),
            PURGE("watson.gui.button.ledger.purge"),
            RESTORE("watson.gui.button.ledger.restore"),
            ROLLBACK("watson.gui.button.ledger.rollback"),
            SEARCH("watson.gui.button.ledger.search");

            private final String labelKey;

            LedgerMode(String labelKey)
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

            public LedgerMode cycle(boolean forward)
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
