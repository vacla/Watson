package eu.minemania.watson.gui;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.LedgerInfo;
import eu.minemania.watson.network.ledger.PluginInspectPacketHandler;
import eu.minemania.watson.network.ledger.PluginPurgePacketHandler;
import eu.minemania.watson.network.ledger.PluginRollbackPacketHandler;
import eu.minemania.watson.network.ledger.PluginSearchPacketHandler;
import eu.minemania.watson.util.DataUtils;
import malilib.gui.*;
import malilib.gui.listener.IntegerTextFieldListener;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.*;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.StringListEntryWidget;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GuiLedger extends BaseScreen
{
    protected LabelWidget labelRange;
    protected LabelWidget labelSource;
    protected LabelWidget labelTimeBefore;
    protected LabelWidget labelTimeAfter;
    protected LabelWidget labelX;
    protected LabelWidget labelY;
    protected LabelWidget labelZ;
    protected LabelWidget labelPages;
    protected LabelWidget labelActionList;
    protected LabelWidget labelDimensionList;
    protected LabelWidget labelBlockList;
    protected LabelWidget labelEntityTypeList;
    protected LabelWidget labelItemList;
    protected LabelWidget labelTagList;
    protected InfoIconWidget infoRange;
    protected InfoIconWidget infoSource;
    protected InfoIconWidget infoTimeBefore;
    protected InfoIconWidget infoTimeAfter;
    protected InfoIconWidget infoX;
    protected InfoIconWidget infoY;
    protected InfoIconWidget infoZ;
    protected InfoIconWidget infoPages;
    protected InfoIconWidget infoActionList;
    protected InfoIconWidget infoDimensionList;
    protected InfoIconWidget infoBlockList;
    protected InfoIconWidget infoEntityTypeList;
    protected InfoIconWidget infoItemList;
    protected InfoIconWidget infoTagList;
    protected IntegerEditWidget textFieldRange;
    protected BaseTextFieldWidget textFieldSource;
    protected BaseTextFieldWidget textFieldTimeBefore;
    protected BaseTextFieldWidget textFieldTimeAfter;
    protected IntegerTextFieldWidget textFieldX;
    protected IntegerTextFieldWidget textFieldY;
    protected IntegerTextFieldWidget textFieldZ;
    protected IntegerTextFieldWidget textFieldPages;
    protected GenericButton mainMenuButton;
    protected GenericButton cyclePacketButton;
    protected GenericButton submitButton;
    protected GenericButton clearButton;
    protected GenericButton actionListButton;
    protected GenericButton dimensionListButton;
    protected GenericButton blockListButton;
    protected GenericButton entityTypeListButton;
    protected GenericButton itemListButton;
    protected GenericButton tagListButton;
    protected LedgerInfo ledgerInfo = new LedgerInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "", "", 0, 0, 0, 0, LedgerMode.INSPECT, 10);

    protected GuiLedger()
    {
        this.ledgerInfo = DataManager.getLedgerInfo() == null ? this.ledgerInfo : DataManager.getLedgerInfo();

        this.labelRange = new LabelWidget("watson.gui.label.ledger.title.range");
        this.infoRange = new InfoIconWidget(Icons.INFO_11, "watson.gui.label.ledger.info.range");
        this.textFieldRange = new IntegerEditWidget(70, 14, this::setRange);
        this.textFieldRange.setIntegerValue(ledgerInfo.getRange());
        this.labelSource = new LabelWidget("watson.gui.label.ledger.title.source");
        this.infoSource = new InfoIconWidget(Icons.INFO_11, "watson.gui.label.ledger.info.source");
        this.textFieldSource = new BaseTextFieldWidget(70, 14);
        this.textFieldSource.setListener(this::setSource);
        this.textFieldSource.setText(ledgerInfo.getSources());
        this.labelTimeBefore = new LabelWidget("watson.gui.label.ledger.title.time.before");
        this.infoTimeBefore = new InfoIconWidget(Icons.INFO_11, "watson.gui.label.ledger.info.time");
        this.textFieldTimeBefore = new BaseTextFieldWidget(70, 14);
        this.textFieldTimeBefore.setListener(this::setTextFieldTimeBefore);
        this.textFieldTimeBefore.setText(ledgerInfo.getTimeBefore());
        this.labelTimeAfter = new LabelWidget("watson.gui.label.ledger.title.time.after");
        this.infoTimeAfter = new InfoIconWidget(Icons.INFO_11, "watson.gui.label.ledger.info.time");
        this.textFieldTimeAfter = new BaseTextFieldWidget(70, 14);
        this.textFieldTimeAfter.setListener(this::setTextFieldTimeAfter);
        this.textFieldTimeAfter.setText(ledgerInfo.getTimeAfter());
        this.labelX = new LabelWidget("watson.gui.label.ledger.title.x");
        this.infoX = new InfoIconWidget(Icons.INFO_11, "watson.gui.label.ledger.info.coords");
        this.textFieldX = new IntegerTextFieldWidget(70, 14);
        this.textFieldX.setListener(new IntegerTextFieldListener(this::setTextFieldX));
        this.textFieldX.setText(String.valueOf(ledgerInfo.getX()));
        this.labelY = new LabelWidget("watson.gui.label.ledger.title.y");
        this.infoY = new InfoIconWidget(Icons.INFO_11, "watson.gui.label.ledger.info.coords");
        this.textFieldY = new IntegerTextFieldWidget(70, 14);
        this.textFieldY.setListener(new IntegerTextFieldListener(this::setTextFieldY));
        this.textFieldY.setText(String.valueOf(ledgerInfo.getY()));
        this.labelZ = new LabelWidget("watson.gui.label.ledger.title.z");
        this.infoZ = new InfoIconWidget(Icons.INFO_11, "watson.gui.label.ledger.info.coords");
        this.textFieldZ = new IntegerTextFieldWidget(70, 14);
        this.textFieldZ.setListener(new IntegerTextFieldListener(this::setTextFieldZ));
        this.textFieldZ.setText(String.valueOf(ledgerInfo.getZ()));
        this.labelPages = new LabelWidget("watson.gui.label.ledger.title.pages");
        this.infoPages = new InfoIconWidget(Icons.INFO_11, "watson.gui.label.ledger.info.pages");
        this.textFieldPages = new IntegerTextFieldWidget(70, 14);
        this.textFieldPages.setListener(new IntegerTextFieldListener(this::setTextFieldPages));
        this.textFieldPages.setText(String.valueOf(ledgerInfo.getPages()));

        String translationLabel = "watson.gui.label.ledger.title.action";
        this.labelActionList = new LabelWidget(translationLabel);
        this.infoActionList = setInfoIconList(translationLabel);
        this.actionListButton = GenericButton.create("watson.gui.button.ledger.action", this::openActionList);
        translationLabel = "watson.gui.label.ledger.title.dimension";
        this.labelDimensionList = new LabelWidget(translationLabel);
        this.infoDimensionList = setInfoIconList(translationLabel);
        this.dimensionListButton = GenericButton.create("watson.gui.button.ledger.dimension", this::openDimensionList);
        translationLabel = "watson.gui.label.ledger.title.block";
        this.labelBlockList = new LabelWidget(translationLabel);
        this.infoBlockList = setInfoIconList(translationLabel);
        this.blockListButton = GenericButton.create("watson.gui.button.ledger.block", this::openBlockList);
        translationLabel = "watson.gui.label.ledger.title.entitytype";
        this.labelEntityTypeList = new LabelWidget(translationLabel);
        this.infoEntityTypeList = setInfoIconList(translationLabel);
        this.entityTypeListButton = GenericButton.create("watson.gui.button.ledger.entitytype", this::openEntityTypeList);
        translationLabel = "watson.gui.label.ledger.title.item";
        this.labelItemList = new LabelWidget(translationLabel);
        this.infoItemList = setInfoIconList(translationLabel);
        this.itemListButton = GenericButton.create("watson.gui.button.ledger.item", this::openItemList);
        translationLabel = "watson.gui.label.ledger.title.tag";
        this.labelTagList = new LabelWidget(translationLabel);
        this.infoTagList = setInfoIconList(translationLabel);
        this.tagListButton = GenericButton.create("watson.gui.button.ledger.tag", this::openTagList);
        this.mainMenuButton = GenericButton.create("watson.gui.button.change_menu.to_main_menu", GuiMainMenu::openMainMenu);
        this.cyclePacketButton = GenericButton.create(this::getCyclePacketButtonLabel);
        this.cyclePacketButton.setActionListener((mouseButton) -> {
            ledgerInfo.setLedgerMode(ledgerInfo.getLedgerMode().cycle(mouseButton == 0));
            this.cyclePacketButton.updateButtonState();
            this.initScreen();
            return true;
        });
        this.submitButton = GenericButton.create("watson.gui.button.ledger.submit");
        this.submitButton.setActionListener(this::submit);
        this.clearButton = GenericButton.create("watson.gui.button.ledger.clear");
        this.clearButton.setActionListener(() -> {
            this.clearLedgerInfo();
            this.initScreen();
        });
        this.setTitle("watson.gui.title.ledger", DataManager.getLedgerVersion());
        this.screenCloseListener = this::setLedgerInfo;
    }

    @Override
    public void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();
        if (ledgerInfo.getLedgerMode() != LedgerMode.INSPECT)
        {
            this.addWidget(this.labelActionList);
            this.addWidget(this.infoActionList);
            this.addWidget(this.actionListButton);
            this.addWidget(this.labelDimensionList);
            this.addWidget(this.infoDimensionList);
            this.addWidget(this.dimensionListButton);
            this.addWidget(this.labelBlockList);
            this.addWidget(this.infoBlockList);
            this.addWidget(this.blockListButton);
            this.addWidget(this.labelEntityTypeList);
            this.addWidget(this.infoEntityTypeList);
            this.addWidget(this.entityTypeListButton);
            this.addWidget(this.labelItemList);
            this.addWidget(this.infoItemList);
            this.addWidget(this.itemListButton);
            this.addWidget(this.labelTagList);
            this.addWidget(this.infoTagList);
            this.addWidget(this.tagListButton);
            this.addWidget(this.labelRange);
            this.addWidget(this.infoRange);
            this.addWidget(this.textFieldRange);
            this.addWidget(this.labelSource);
            this.addWidget(this.infoSource);
            this.addWidget(this.textFieldSource);
            this.addWidget(this.labelTimeBefore);
            this.addWidget(this.infoTimeBefore);
            this.addWidget(this.textFieldTimeBefore);
            this.addWidget(this.labelTimeAfter);
            this.addWidget(this.infoTimeAfter);
            this.addWidget(this.textFieldTimeAfter);
        }
        if (ledgerInfo.getLedgerMode() == LedgerMode.INSPECT)
        {
            this.addWidget(this.labelX);
            this.addWidget(this.infoX);
            this.addWidget(this.textFieldX);
            this.addWidget(this.labelY);
            this.addWidget(this.infoY);
            this.addWidget(this.textFieldY);
            this.addWidget(this.labelZ);
            this.addWidget(this.infoZ);
            this.addWidget(this.textFieldZ);
        }
        if (ledgerInfo.getLedgerMode() == LedgerMode.INSPECT || ledgerInfo.getLedgerMode() == LedgerMode.SEARCH)
        {
            this.addWidget(this.labelPages);
            this.addWidget(this.infoPages);
            this.addWidget(this.textFieldPages);
        }
        this.addWidget(this.cyclePacketButton);
        this.addWidget(this.submitButton);
        this.addWidget(this.clearButton);
        this.addWidget(this.mainMenuButton);
        /*
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
*/
            /*this.textFieldRange.setText(String.valueOf(ledgerInfo.getRange()));
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
        this.addButton(button, new ButtonListener(type, this));*/
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int y = this.titleY + 20;
        int x = this.titleX;

        if (ledgerInfo.getLedgerMode() != LedgerMode.INSPECT)
        {
            this.labelActionList.setPosition(x, y);
            this.infoActionList.setPosition(this.labelActionList.getRight() + 10, y);
            this.actionListButton.setPosition(this.infoActionList.getRight() + 10, y);
            this.labelDimensionList.setPosition(this.actionListButton.getRight() + 10, y);
            this.infoDimensionList.setPosition(this.labelDimensionList.getRight() + 10, y);
            this.dimensionListButton.setPosition(this.infoDimensionList.getRight() + 10, y);
            this.labelBlockList.setPosition(this.dimensionListButton.getRight() + 10, y);
            this.infoBlockList.setPosition(this.labelBlockList.getRight() + 10, y);
            this.blockListButton.setPosition(this.infoBlockList.getRight() + 10, y);
            y += 40;
            this.labelEntityTypeList.setPosition(x, y);
            this.infoEntityTypeList.setPosition(this.labelEntityTypeList.getRight() + 10, y);
            this.entityTypeListButton.setPosition(this.infoEntityTypeList.getRight() + 10, y);
            this.labelItemList.setPosition(this.entityTypeListButton.getRight() + 10, y);
            this.infoItemList.setPosition(this.labelItemList.getRight() + 10, y);
            this.itemListButton.setPosition(this.infoItemList.getRight() + 10, y);
            this.labelTagList.setPosition(this.itemListButton.getRight() + 10, y);
            this.infoTagList.setPosition(this.labelTagList.getRight() + 10, y);
            this.tagListButton.setPosition(this.infoTagList.getRight() + 10, y);
            y += 40;
            this.labelRange.setPosition(x, y);
            this.infoRange.setPosition(this.labelRange.getRight() + 10, y);
            this.textFieldRange.setPosition(this.infoRange.getRight() + 10, y);
            this.labelSource.setPosition(this.textFieldRange.getRight() + 10, y);
            this.infoSource.setPosition(this.labelSource.getRight() + 10, y);
            this.textFieldSource.setPosition(this.infoSource.getRight() + 10, y);
            y += 40;
            this.labelTimeBefore.setPosition(x, y);
            this.infoTimeBefore.setPosition(this.labelTimeBefore.getRight() + 10, y);
            this.textFieldTimeBefore.setPosition(this.infoTimeBefore.getRight() + 10, y);
            this.labelTimeAfter.setPosition(this.textFieldTimeBefore.getRight() + 10, y);
            this.infoTimeAfter.setPosition(this.labelTimeAfter.getRight() + 10, y);
            this.textFieldTimeAfter.setPosition(this.infoTimeAfter.getRight() + 10, y);
        }
        if (ledgerInfo.getLedgerMode() == LedgerMode.INSPECT)
        {
            this.labelX.setPosition(x, y);
            this.infoX.setPosition(this.labelX.getRight() + 10, y);
            this.textFieldX.setPosition(this.infoX.getRight() + 10, y);
            this.labelY.setPosition(this.textFieldX.getRight() + 10, y);
            this.infoY.setPosition(this.labelY.getRight() + 10, y);
            this.textFieldY.setPosition(this.infoY.getRight() + 10, y);
            this.labelZ.setPosition(this.textFieldY.getRight() + 10, y);
            this.infoZ.setPosition(this.labelZ.getRight() + 10, y);
            this.textFieldZ.setPosition(this.infoZ.getRight() + 10, y);
        }
        if (ledgerInfo.getLedgerMode() == LedgerMode.INSPECT || ledgerInfo.getLedgerMode() == LedgerMode.SEARCH)
        {
            y += 40;
            this.labelPages.setPosition(x, y);
            this.infoPages.setPosition(this.labelPages.getRight() + 10, y);
            this.textFieldPages.setPosition(this.infoPages.getRight() + 10, y);
        }
        y = this.getBottom() - 30;
        this.cyclePacketButton.setPosition(x, y);
        this.submitButton.setPosition(this.cyclePacketButton.getRight() + 10, y);
        this.clearButton.setPosition(this.submitButton.getRight() + 10, y);
        this.mainMenuButton.setPosition(this.getRight() - this.mainMenuButton.getWidth() - 30, y);
    }

    private InfoIconWidget setInfoIconList(String translation)
    {
        String label = StringUtils.translate(translation);
        return new InfoIconWidget(Icons.INFO_11, "watson.gui.label.ledger.info.button", label);
    }

    private void submit()
    {
        if (!ledgerInfo.getLedgerMode().equals(LedgerMode.INSPECT) && validate())
        {
            return;
        }

        List<String> action = ledgerInfo.getActions().stream().toList();
        List<String> dimension = ledgerInfo.getDimensions().stream().toList();
        List<String> block = ledgerInfo.getBlocks().stream().toList();
        List<String> entityType = ledgerInfo.getEntityTypes().stream().toList();
        List<String> item = ledgerInfo.getItems().stream().toList();
        List<String> tag = ledgerInfo.getTags().stream().toList();
        int range = ledgerInfo.getRange();
        String source = ledgerInfo.getSources();
        String timeBefore = ledgerInfo.getTimeBefore();
        String timeAfter = ledgerInfo.getTimeAfter();
        int x = ledgerInfo.getX();
        int y = ledgerInfo.getY();
        int z = ledgerInfo.getZ();
        int pages = ledgerInfo.getPages();

        switch (ledgerInfo.getLedgerMode())
        {
            case INSPECT -> new PluginInspectPacketHandler().sendPacket(x, y, z, pages, mc);
            case PURGE -> new PluginPurgePacketHandler().sendPacket(action, dimension, block, entityType, item, tag, range, source, timeBefore, timeAfter, mc);
            case ROLLBACK -> new PluginRollbackPacketHandler().sendPacket(action, dimension, block, entityType, item, tag, range, source, timeBefore, timeAfter, false, mc);
            case RESTORE -> new PluginRollbackPacketHandler().sendPacket(action, dimension, block, entityType, item, tag, range, source, timeBefore, timeAfter, true, mc);
            case SEARCH -> new PluginSearchPacketHandler().sendPacket(action, dimension, block, entityType, item, tag, range, source, timeBefore, timeAfter, pages, mc);
        }
    }

    public String getCyclePacketButtonLabel()
    {
        return ledgerInfo.getLedgerMode().getDisplayName();
    }

    private void openTagList()
    {
        ArrayList<String> tags = getTotalList(DataUtils.getTags());
        StringListSelectionScreen gui = new StringListSelectionScreen(tags, (tagConsumer) -> { ledgerInfo.setTags(tagConsumer); BaseScreen.openScreen(this); });
        gui.setHasSearch(true);
        gui.getListWidget().getEntrySelectionHandler().setEntriesOnCreate(ledgerInfo.getTags());
        gui.setTitle("watson.gui.button.ledger.tag");
        gui.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(gui);
    }

    private void openItemList()
    {
        ArrayList<String> items = getTotalList(DataUtils.getItems());
        StringListSelectionScreen gui = new StringListSelectionScreen(items, (itemConsumer) -> {ledgerInfo.setItems(itemConsumer); BaseScreen.openScreen(this);});
        gui.setHasSearch(true);
        gui.getListWidget().getEntrySelectionHandler().setEntriesOnCreate(ledgerInfo.getItems());
        gui.setTitle("watson.gui.button.ledger.item");
        gui.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(gui);
    }

    private void openEntityTypeList()
    {
        ArrayList<String> entityTypes = getTotalList(DataUtils.getEntityTypes());
        StringListSelectionScreen gui = new StringListSelectionScreen(entityTypes, (entityTypeConsumer) -> {ledgerInfo.setEntityTypes(entityTypeConsumer); BaseScreen.openScreen(this);});
        gui.setHasSearch(true);
        gui.getListWidget().getEntrySelectionHandler().setEntriesOnCreate(ledgerInfo.getEntityTypes());
        gui.setTitle("watson.gui.button.ledger.entitytype");
        gui.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(gui);
    }

    private void openBlockList()
    {
        ArrayList<String> blocks = getTotalList(DataUtils.getBlocks());
        StringListSelectionScreen gui = new StringListSelectionScreen(blocks, (blockConsumer) -> {ledgerInfo.setBlocks(blockConsumer); BaseScreen.openScreen(this);});
        gui.setHasSearch(true);
        gui.getListWidget().getEntrySelectionHandler().setEntriesOnCreate(ledgerInfo.getBlocks());
        gui.setTitle("watson.gui.button.ledger.block");
        gui.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(gui);
    }

    private void openDimensionList()
    {
        ArrayList<String> dimensions = getTotalList(getDimensions(""));
        StringListSelectionScreen gui = new StringListSelectionScreen(dimensions, (dimensionConsumer) -> {ledgerInfo.setDimensions(dimensionConsumer); BaseScreen.openScreen(this);});
        gui.setHasSearch(true);
        gui.getListWidget().getEntrySelectionHandler().setEntriesOnCreate(ledgerInfo.getDimensions());
        gui.setTitle("watson.gui.button.ledger.dimension");
        gui.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(gui);
    }

    private void openActionList()
    {
        ArrayList<String> actions = getTotalList(DataManager.getLedgerActions());
        StringListSelectionScreen gui = new StringListSelectionScreen(actions, (actionConsumer) -> {ledgerInfo.setActions(actionConsumer); BaseScreen.openScreen(this);});
        gui.setHasSearch(true);
        gui.getListWidget().getEntrySelectionHandler().setEntriesOnCreate(ledgerInfo.getActions());
        gui.setTitle("watson.gui.button.ledger.action");
        gui.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(gui);
    }

    private void setTextFieldPages(int textFieldPages)
    {
        ledgerInfo.setPages(textFieldPages);
    }

    private void setTextFieldZ(int textFieldZ)
    {
        ledgerInfo.setZ(textFieldZ);
    }

    private void setTextFieldY(int textFieldY)
    {
        ledgerInfo.setY(textFieldY);
    }

    private void setTextFieldX(int textFieldX)
    {
        ledgerInfo.setX(textFieldX);
    }

    private void setTextFieldTimeAfter(String timeAfter)
    {
        ledgerInfo.setTimeAfter(timeAfter);
    }

    private void setTextFieldTimeBefore(String timeBefore)
    {
        ledgerInfo.setTimeBefore(timeBefore);
    }

    private void setSource(String source)
    {
        ledgerInfo.setSources(source);
    }

    protected void setRange(int range)
    {
        ledgerInfo.setRange(range);
    }

    /*private GenericButton createButton(int x, int y, int width, ConsumerButtonListener.ButtonType type)
    {
        ConsumerButtonListener listener = new ConsumerButtonListener(type, this);
        String label = type.getDisplayName();

        if (width == -1)
        {
            width = this.getStringWidth(label) + 10;
        }

        GenericButton button = new GenericButton(x, y, width, 20, label);

        this.addButton(button, listener);

        return button;
    }*/

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
                MessageDispatcher.warning("watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.action"), action);
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
            MessageDispatcher.warning( "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.action"), String.join(",", listActionError));
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
                MessageDispatcher.warning( "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.dimension"), dimensionText);
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
            MessageDispatcher.warning( "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.dimension"), String.join(",", listDimensionError));
            error = true;
        }

        ArrayList<String> listBlockError = new ArrayList<>();
        ArrayList<String> ledgerBlocks = getTotalList(DataUtils.getBlocks());
        for (String blockText : ledgerInfo.getBlocks())
        {
            if (blockText.isEmpty())
            {
                break;
            }
            if (!blockText.contains(":"))
            {
                MessageDispatcher.warning( "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.block"), blockText);
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
            MessageDispatcher.warning( "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.block"), String.join(",", listBlockError));
            error = true;
        }

        ArrayList<String> listEntityTypeError = new ArrayList<>();
        ArrayList<String> ledgerEntityTypes = getTotalList(DataUtils.getEntityTypes());
        for (String entityTypeText : ledgerInfo.getEntityTypes())
        {
            if (entityTypeText.isEmpty())
            {
                break;
            }
            if (!entityTypeText.contains(":"))
            {
                MessageDispatcher.warning( "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.entitytype"), entityTypeText);
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
            MessageDispatcher.warning( "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.entitytype"), String.join(",", listEntityTypeError));
            error = true;
        }

        ArrayList<String> listItemError = new ArrayList<>();
        ArrayList<String> ledgerItems = getTotalList(DataUtils.getItems());
        for (String itemText : ledgerInfo.getItems())
        {
            if (itemText.isEmpty())
            {
                break;
            }
            if (!itemText.contains(":"))
            {
                MessageDispatcher.warning( "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.item"), itemText);
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
            MessageDispatcher.warning( "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.item"), String.join(",", listItemError));
            error = true;
        }

        ArrayList<String> listTagError = new ArrayList<>();
        ArrayList<String> ledgerTags = getTotalList(DataUtils.getTags());
        for (String tagText : ledgerInfo.getTags())
        {
            if (tagText.isEmpty())
            {
                break;
            }
            if (!tagText.contains(":") || !tagText.contains("#"))
            {
                MessageDispatcher.warning( "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.tag"), tagText);
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
            MessageDispatcher.warning( "watson.error.ledger.not_exist", StringUtils.translate("watson.gui.label.ledger.title.tag"), String.join(",", listTagError));
            error = true;
        }

        int textRange = ledgerInfo.getRange();
        if (textRange <= 1 && textRange != 0)
        {
            MessageDispatcher.warning( "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.range"), textRange);
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
            MessageDispatcher.warning( "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.time.before"), textTimeBefore);
            error = true;
        }
        String textTimeAfter = ledgerInfo.getTimeAfter();
        if (!textTimeAfter.matches("^([0-9]+[smhdw])+$") && !textTimeAfter.isEmpty())
        {
            MessageDispatcher.warning( "watson.error.ledger.invalid_format", StringUtils.translate("watson.gui.label.ledger.title.time.after"), textTimeAfter);
            error = true;
        }

        return error;
    }

    private void setLedgerInfo()
    {
        Collection<String> actions = this.ledgerInfo.getActions();
        Collection<String> blocks = this.ledgerInfo.getBlocks();
        Collection<String> dimension = this.ledgerInfo.getDimensions();
        Collection<String> entityTypes = this.ledgerInfo.getEntityTypes();
        Collection<String> items = this.ledgerInfo.getItems();
        Collection<String> tags = this.ledgerInfo.getTags();
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

    /*public static class ButtonListener implements IButtonActionListener
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

                List<String> action = ledgerInfo.getActions().stream().toList();
                List<String> dimension = ledgerInfo.getDimensions().stream().toList();
                List<String> block = ledgerInfo.getBlocks().stream().toList();
                List<String> entityType = ledgerInfo.getEntityTypes().stream().toList();
                List<String> item = ledgerInfo.getItems().stream().toList();
                List<String> tag = ledgerInfo.getTags().stream().toList();
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
    }*/
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
