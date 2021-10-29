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
    protected LedgerInfo ledgerInfo = new LedgerInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "", "", 0, 0, 0, 0, LedgerMode.INSPECT);

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
            this.addWidget(new WidgetInfoIcon(x + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.action"));
            button = createButton(x + offset + 20, y, width, ConsumerButtonListener.ButtonType.ACTION);

            label = StringUtils.translate("watson.gui.label.ledger.title.dimension"); //Dimension
            this.addLabel(button.getX() + button.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + button.getWidth();
            this.addWidget(new WidgetInfoIcon(button.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.dimension"));
            button = createButton(button.getX() + offset + 20, y, width, ConsumerButtonListener.ButtonType.DIMENSION);

            label = StringUtils.translate("watson.gui.label.ledger.title.object"); //Object
            this.addLabel(button.getX() + button.getWidth() + 5, y, width, 20, 0xFFFFFFFF, label);
            offset = this.getStringWidth(label) + 10 + button.getWidth();
            this.addWidget(new WidgetInfoIcon(button.getX() + offset, y + 4, Icons.INFO_11, "watson.gui.label.ledger.info.object"));

            createButton(button.getX() + offset + 20, y, width, ConsumerButtonListener.ButtonType.OBJECT);

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
                addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", "Action" , action);
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
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.actions_not_exist", String.join(",", listActionError));
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
                addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", "Dimension", dimensionText);
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
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.dimensions_not_exist", String.join(",", listDimensionError));
            error = true;
        }

        ArrayList<String> listObjectError = new ArrayList<>();
        ArrayList<String> ledgerObjects = getTotalList(DataManager.getAllItemEntitiesStringIdentifiers());
        for (String objectText : ledgerInfo.getObjects())
        {
            if (objectText.isEmpty()) {
                break;
            }
            if (!objectText.contains(":"))
            {
                addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", "Object", objectText);
                error = true;
                break;
            }
            if (!ledgerObjects.contains(objectText))
            {
                listObjectError.add(objectText);
            }
        }
        if (!listObjectError.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.objects_not_exist", String.join(",", listObjectError));
            error = true;
        }
        int textRange = ledgerInfo.getRange();
        if (textRange <= 1 && textRange != 0)
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", "Range", textRange);
            error = true;
        }
        String textSource = ledgerInfo.getSources();
        String[] sourcesText = textSource.split(",");
        ArrayList<String> listSourceError = new ArrayList<>();
        for (String sourceText : sourcesText)
        {
            if (sourceText.isEmpty()) {
                break;
            }
            if (!sourceText.matches("^[0-9a-zA-Z@_!]+$"))
            {
                listSourceError.add(sourceText);
                break;
            }
        }
        if (!listSourceError.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", "Source", String.join(",", listSourceError));
            error = true;
        }
        String textTimeBefore = ledgerInfo.getTimeBefore();
        if (!textTimeBefore.matches("^([0-9]+[smhdw])+$") && !textTimeBefore.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", "Time before", textTimeBefore);
            error = true;
        }
        String textTimeAfter = ledgerInfo.getTimeAfter();
        if (!textTimeAfter.matches("^([0-9]+[smhdw])+$") && !textTimeAfter.isEmpty())
        {
            addMessage(Message.MessageType.WARNING, "watson.error.ledger.invalid_format", "Time after", textTimeAfter);
            error = true;
        }

        return error;
    }

    private void setLedgerInfo()
    {
        List<String> actions = this.ledgerInfo.getActions();
        List<String> objects = this.ledgerInfo.getObjects();
        List<String> dimension = this.ledgerInfo.getDimensions();
        String source = this.ledgerInfo.getSources();
        String timeBefore = this.ledgerInfo.getTimeBefore();
        String timeAfter = this.ledgerInfo.getTimeAfter();
        Integer range = this.ledgerInfo.getRange();
        Integer x = this.ledgerInfo.getX();
        Integer y = this.ledgerInfo.getY();
        Integer z = this.ledgerInfo.getZ();
        LedgerMode ledgerMode = this.ledgerInfo.getLedgerMode();

        LedgerInfo ledgerInfo = new LedgerInfo(actions, objects, dimension, source, timeBefore, timeAfter, range, x, y, z, ledgerMode);
        DataManager.setLedgerInfo(ledgerInfo);
    }

    private void clearLedgerInfo()
    {
        LedgerInfo ledgerInfo = new LedgerInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "", "", 0, 0, 0, 0, LedgerMode.INSPECT);
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

    private static class ObjectListCreator implements IStringListConsumer
    {
        GuiLedger parent;

        public ObjectListCreator(GuiLedger parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            parent.ledgerInfo.setObjects(ImmutableList.copyOf(strings));
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
            if (this.type == ButtonType.OBJECT)
            {
                ObjectListCreator objectCreator = new ObjectListCreator(parent);
                ArrayList<String> itemEntities = parent.getTotalList(DataManager.getAllItemEntitiesStringIdentifiers());
                GuiStringListSelection gui = new GuiStringListSelectionWithSearch(itemEntities, objectCreator, true, parent.ledgerInfo.getObjects());
                gui.setTitle(type.getDisplayName());
                gui.setParent(parent);
                GuiBase.openGui(gui);
            }
        }

        public enum ButtonType
        {
            ACTION("watson.gui.button.ledger.action"),
            DIMENSION("watson.gui.button.ledger.dimension"),
            OBJECT("watson.gui.button.ledger.object");

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
                List<String> object = ledgerInfo.getObjects();
                int range = ledgerInfo.getRange();
                String source = ledgerInfo.getSources();
                String timeBefore = ledgerInfo.getTimeBefore();
                String timeAfter = ledgerInfo.getTimeAfter();
                int x = ledgerInfo.getX();
                int y = ledgerInfo.getY();
                int z = ledgerInfo.getZ();
                MinecraftClient mc = parent.mc;

                switch (ledgerInfo.getLedgerMode())
                {
                    case INSPECT -> new PluginInspectPacketHandler().sendPacket(x, y, z, mc);
                    case PURGE -> new PluginPurgePacketHandler().sendPacket(action, dimension, object, range, source, timeBefore, timeAfter, mc);
                    case ROLLBACK -> new PluginRollbackPacketHandler().sendPacket(action, dimension, object, range, source, timeBefore, timeAfter, mc);
                    case SEARCH -> new PluginSearchPacketHandler().sendPacket(action, dimension, object, range, source, timeBefore, timeAfter, mc);
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
