package eu.minemania.watson.event;

import eu.minemania.watson.analysis.Analysis;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.chat.command.Command;
import eu.minemania.watson.client.Screenshot;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Hotkeys;
import eu.minemania.watson.config.Plugins;
import eu.minemania.watson.data.Actions;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.gui.ConfigScreen;
import eu.minemania.watson.gui.GuiMainMenu;
import eu.minemania.watson.selection.EditSelection;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import net.minecraft.client.MinecraftClient;

public class KeyCallbacks
{
    public static void init(MinecraftClient mc)
    {
        KeyCallbackHotkeys callbackHotkeys = new KeyCallbackHotkeys(mc);

        Configs.Generic.WATSON_PREFIX.setValueChangeCallback(Command::reregisterWatsonCommand);
        Configs.Lists.HIGHLIGHT.setValueChangeCallback(((newValue, oldValue) -> Highlight.setHighlightList(newValue)));
        Configs.Lists.HIGHLIGHT.setValueLoadCallback(Highlight::setHighlightList);
        Configs.Lists.OVERRIDING_ACTIONS.setValueChangeCallback(((newValue, oldValue) -> Actions.setActionsList(newValue)));
        Configs.Lists.OVERRIDING_ACTIONS.setValueLoadCallback(Actions::setActionsList);
        Configs.Lists.WATSON_BLOCKS.setValueLoadCallback(WatsonBlockRegistery::setWatsonBlockList);

        Configs.Analysis.CP_BUSY.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.CP_DETAILS.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.CP_DETAILS_SESSION.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.CP_DETAILS_SIGN.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.CP_INSPECTOR_COORDS.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.CP_LOOKUP_COORDS.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.CP_LOOKUP_HEADER.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.CP_NO_RESULT.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.CP_PAGE.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.CP_SEARCH.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.DUTYMODE_DISABLE.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.DUTYMODE_ENABLE.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.MODMODE_DISABLE.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.MODMODE_ENABLE.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_DATA.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_BLOCK.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_BLOCKS.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_CHANGES.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_NO_RESULTS.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_RATIO.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_RATIO_CURRENT.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_SEARCHING.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_SUM_BLOCKS.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_SUM_PLAYERS.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_HEADER_TIME_CHECK.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_PAGE.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_POSITION.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_SUM.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.LB_TP.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.PRISM_DATA.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.PRISM_PAGE.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.PRISM_PAGINATION.setValueChangeCallback(Analysis::removeMatchedChatHandler);
        Configs.Analysis.WG_REGIONS.setValueChangeCallback(Analysis::removeMatchedChatHandler);

        Hotkeys.KEYBIND_COMMAND_CO_INSPECT.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_CURSOR_NEXT.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_CURSOR_PREV.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_QUERY_AFTER.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_QUERY_BEFORE.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_SCREENSHOT.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_TP_CURSOR.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_TP_NEXT.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_TP_NEXT_ANNO.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_TP_PREV.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_TP_PREV_ANNO.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_WATSON_CLEAR.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.OPEN_GUI_MAIN_MENU.getKeyBind().setCallback(callbackHotkeys);
        Hotkeys.OPEN_GUI_SETTINGS.getKeyBind().setCallback(callbackHotkeys);
    }

    private static class KeyCallbackHotkeys implements HotkeyCallback
    {
        private final MinecraftClient mc;

        public KeyCallbackHotkeys(MinecraftClient mc)
        {
            this.mc = mc;
        }

        @Override
        public ActionResult onKeyAction(KeyAction action, KeyBind key)
        {
            if (this.mc.player == null || this.mc.world == null)
            {
                return ActionResult.FAIL;
            }
            EditSelection edits = DataManager.getEditSelection();
            if (key == Hotkeys.OPEN_GUI_MAIN_MENU.getKeyBind())
            {
                BaseScreen.openScreen(new GuiMainMenu());
            }
            else if (key == Hotkeys.OPEN_GUI_SETTINGS.getKeyBind())
            {
                BaseScreen.openScreen(ConfigScreen.create());
                return ActionResult.SUCCESS;
            }
            else if (key == Hotkeys.KEYBIND_SCREENSHOT.getKeyBind())
            {
                if (Configs.Generic.SS_KEY_CUSTOM.getBooleanValue())
                {
                    Screenshot.makeScreenshot();
                    return ActionResult.SUCCESS;
                }
                return ActionResult.SUCCESS;
            }
            else if (Configs.Generic.ENABLED.getBooleanValue())
            {
                if (key == Hotkeys.KEYBIND_COMMAND_CO_INSPECT.getKeyBind())
                {
                    if (Configs.Plugin.PLUGIN.getValue().equals(Plugins.COREPROTECT))
                    {
                        ChatMessage.sendToServerChat(String.format("/%s i", Configs.Plugin.COREPROTECT_COMMAND.getValue().getName()));
                        return ActionResult.SUCCESS;
                    }
                }
                else if (key == Hotkeys.KEYBIND_CURSOR_NEXT.getKeyBind())
                {
                    if (edits.getSelection() != null && edits.getSelection().playereditSet != null)
                    {
                        BlockEdit edit = edits.getSelection().playereditSet.getEditAfter(edits.getSelection());
                        if (edit != null)
                        {
                            edits.selectBlockEdit(edit);
                            return ActionResult.SUCCESS;
                        }
                    }
                }
                else if (key == Hotkeys.KEYBIND_CURSOR_PREV.getKeyBind())
                {
                    if (edits.getSelection() != null && edits.getSelection().playereditSet != null)
                    {
                        BlockEdit edit = edits.getSelection().playereditSet.getEditBefore(edits.getSelection());
                        if (edit != null)
                        {
                            edits.selectBlockEdit(edit);
                            return ActionResult.SUCCESS;
                        }
                    }
                }
                else if (key == Hotkeys.KEYBIND_QUERY_AFTER.getKeyBind() && Configs.Plugin.PLUGIN.getValue().equals(Plugins.LOGBLOCK))
                {
                    edits.queryPostEdits(Configs.Edits.POST_COUNT.getIntegerValue());
                    return ActionResult.SUCCESS;
                }
                else if (key == Hotkeys.KEYBIND_QUERY_BEFORE.getKeyBind() && Configs.Plugin.PLUGIN.getValue().equals(Plugins.LOGBLOCK))
                {
                    edits.queryPreEdits(Configs.Edits.PRE_COUNT.getIntegerValue());
                    return ActionResult.SUCCESS;
                }
                else if (key == Hotkeys.KEYBIND_TP_CURSOR.getKeyBind())
                {
                    if (edits.getSelection() != null)
                    {
                        Teleport.teleport(edits.getSelection().x, edits.getSelection().y, edits.getSelection().z, edits.getSelection().world);
                        return ActionResult.SUCCESS;
                    }
                }
                else if (key == Hotkeys.KEYBIND_TP_NEXT.getKeyBind())
                {
                    edits.getBlockEditSet().getOreDB().tpNext();
                    return ActionResult.SUCCESS;
                }
                else if (key == Hotkeys.KEYBIND_TP_NEXT_ANNO.getKeyBind())
                {
                    edits.getBlockEditSet().tpNextAnno();
                    return ActionResult.SUCCESS;
                }
                else if (key == Hotkeys.KEYBIND_TP_PREV.getKeyBind())
                {
                    edits.getBlockEditSet().getOreDB().tpPrev();
                    return ActionResult.SUCCESS;
                }
                else if (key == Hotkeys.KEYBIND_TP_PREV_ANNO.getKeyBind())
                {
                    edits.getBlockEditSet().tpPrevAnno();
                    return ActionResult.SUCCESS;
                }
                else if (key == Hotkeys.KEYBIND_WATSON_CLEAR.getKeyBind())
                {
                    edits.clearBlockEditSet();
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.FAIL;
        }
    }
}