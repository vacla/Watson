package eu.minemania.watson.event;

import eu.minemania.watson.analysis.Analysis;
import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.chat.command.Command;
import eu.minemania.watson.client.Screenshot;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Hotkeys;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.gui.GuiConfigs;
import eu.minemania.watson.gui.GuiMainMenu;
import eu.minemania.watson.selection.EditSelection;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBoolean;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import net.minecraft.client.MinecraftClient;

public class KeyCallbacks
{
    public static void init(MinecraftClient mc)
    {	
        IHotkeyCallback callbackHotkeys = new KeyCallbackHotkeys(mc);
        ValueChangeStringCallback valueChangeStringCallback = new ValueChangeStringCallback();
        ValueChangeStringListCallback valueChangeStringListCallback = new ValueChangeStringListCallback();

        Configs.Generic.WATSON_PREFIX.setValueChangeCallback(valueChangeStringCallback);
        Configs.Lists.HIGHLIGHT.setValueChangeCallback(valueChangeStringListCallback);

        Configs.Analysis.CP_BUSY.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.CP_DETAILS.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.CP_DETAILS_SIGN.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.CP_INSPECTOR_COORDS.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.CP_LOOKUP_COORDS.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.CP_LOOKUP_HEADER.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.CP_NO_RESULT.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.CP_PAGE.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.CP_SEARCH.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.DUTYMODE_DISABLE.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.DUTYMODE_ENABLE.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.MODMODE_DISABLE.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.MODMODE_ENABLE.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_COORD.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_COORD_KILLS.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_COORD_POSITION.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_COORD_REPLACED.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_EDIT.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_EDIT_REPLACED.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_BLOCK.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_BLOCKS.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_CHANGES.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_NO_RESULTS.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_RATIO.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_RATIO_CURRENT.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_SEARCHING.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_SUM_BLOCKS.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_SUM_PLAYERS.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_HEADER_TIME_CHECK.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_KILLS.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_PAGE.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_POSITION.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_SUM.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.LB_TP.setValueChangeCallback(valueChangeStringCallback);
        Configs.Analysis.WG_REGIONS.setValueChangeCallback(valueChangeStringCallback);

        Hotkeys.KEYBIND_AUTO_PAGE.getKeybind().setCallback(new KeyCallbackToggleBoolean(Configs.Generic.AUTO_PAGE));
        Hotkeys.KEYBIND_CURSOR_NEXT.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_CURSOR_PREV.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_QUERY_AFTER.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_QUERY_BEFORE.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_SCREENSHOT.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_TP_CURSOR.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_TP_NEXT.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_TP_PREV.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.KEYBIND_WATSON_CLEAR.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.OPEN_GUI_MAIN_MENU.getKeybind().setCallback(callbackHotkeys);
        Hotkeys.OPEN_GUI_SETTINGS.getKeybind().setCallback(callbackHotkeys);
    }

    private static class ValueChangeStringCallback implements IValueChangeCallback<ConfigString>
    {
        public ValueChangeStringCallback()
        {
        }

        @Override
        public void onValueChanged(ConfigString config)
        {
            if(config == Configs.Analysis.CP_BUSY)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.CP_BUSY);
            }
            else if(config == Configs.Analysis.CP_DETAILS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.CP_DETAILS);
            }
            else if(config == Configs.Analysis.CP_DETAILS_SIGN)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.CP_DETAILS_SIGN);
            }
            else if(config == Configs.Analysis.CP_INSPECTOR_COORDS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.CP_INSPECTOR_COORDS);
            }
            else if(config == Configs.Analysis.CP_LOOKUP_COORDS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.CP_LOOKUP_COORDS);
            }
            else if(config == Configs.Analysis.CP_LOOKUP_HEADER)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.CP_LOOKUP_HEADER);
            }
            else if(config == Configs.Analysis.CP_NO_RESULT)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.CP_NO_RESULT);
            }
            else if(config == Configs.Analysis.CP_PAGE)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.CP_PAGE);
            }
            else if(config == Configs.Analysis.CP_SEARCH)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.CP_SEARCH);
            }
            else if(config == Configs.Analysis.DUTYMODE_DISABLE)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.DUTYMODE_DISABLE);
            }
            else if(config == Configs.Analysis.DUTYMODE_ENABLE)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.DUTYMODE_ENABLE);
            }
            else if(config == Configs.Analysis.MODMODE_DISABLE)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.MODMODE_DISABLE);
            }
            else if(config == Configs.Analysis.MODMODE_ENABLE)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.MODMODE_ENABLE);
            }
            else if(config == Configs.Analysis.LB_COORD)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_COORD);
            }
            else if(config == Configs.Analysis.LB_COORD_KILLS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_COORD_KILLS);
            }
            else if(config == Configs.Analysis.LB_COORD_POSITION)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_COORD_POSITION);
            }
            else if(config == Configs.Analysis.LB_COORD_REPLACED)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_COORD_REPLACED);
            }
            else if(config == Configs.Analysis.LB_EDIT)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_EDIT);
            }
            else if(config == Configs.Analysis.LB_EDIT_REPLACED)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_EDIT_REPLACED);
            }
            else if(config == Configs.Analysis.LB_HEADER_BLOCK)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_BLOCK);
            }
            else if(config == Configs.Analysis.LB_HEADER_BLOCKS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_BLOCKS);
            }
            else if(config == Configs.Analysis.LB_HEADER_CHANGES)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_CHANGES);
            }
            else if(config == Configs.Analysis.LB_HEADER_NO_RESULTS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_NO_RESULTS);
            }
            else if(config == Configs.Analysis.LB_HEADER_RATIO)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_RATIO);
            }
            else if(config == Configs.Analysis.LB_HEADER_RATIO_CURRENT)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_RATIO_CURRENT);
            }
            else if(config == Configs.Analysis.LB_HEADER_SEARCHING)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_SEARCHING);
            }
            else if(config == Configs.Analysis.LB_HEADER_SUM_BLOCKS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_SUM_BLOCKS);
            }
            else if(config == Configs.Analysis.LB_HEADER_SUM_PLAYERS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_SUM_PLAYERS);
            }
            else if(config == Configs.Analysis.LB_HEADER_TIME_CHECK)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_HEADER_TIME_CHECK);
            }
            else if(config == Configs.Analysis.LB_KILLS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_KILLS);
            }
            else if(config == Configs.Analysis.LB_PAGE)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_PAGE);
            }
            else if(config == Configs.Analysis.LB_POSITION)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_POSITION);
            }
            else if(config == Configs.Analysis.LB_SUM)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_SUM);
            }
            else if(config == Configs.Analysis.LB_TP)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.LB_TP);
            }
            else if(config == Configs.Analysis.WG_REGIONS)
            {
                Analysis.removeMatchedChatHandler(Configs.Analysis.WG_REGIONS);
            }
            else if(config == Configs.Generic.WATSON_PREFIX)
            {
                Command.reregisterWatsonCommand(Command.commandDispatcher, Configs.Generic.WATSON_PREFIX);
            }
        }
    }

    private static class ValueChangeStringListCallback implements IValueChangeCallback<ConfigStringList>
    {

        public ValueChangeStringListCallback()
        {
        }

        @Override
        public void onValueChanged(ConfigStringList config)
        {
            if(config == Configs.Lists.HIGHLIGHT)
            {
                Highlight.setHighlightList(Configs.Lists.HIGHLIGHT.getStrings());
            }
        }
    }

    private static class KeyCallbackHotkeys implements IHotkeyCallback
    {
        private final MinecraftClient mc;

        public KeyCallbackHotkeys(MinecraftClient mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if(this.mc.player == null || this.mc.world == null)
            {
                return false;
            }
            EditSelection edits = DataManager.getEditSelection();
            if (key == Hotkeys.OPEN_GUI_MAIN_MENU.getKeybind())
            {
                GuiBase.openGui(new GuiMainMenu());
                return true;
            }
            else if(key == Hotkeys.OPEN_GUI_SETTINGS.getKeybind())
            {
                GuiBase.openGui(new GuiConfigs());
                return true;
            }
            else if(key == Hotkeys.KEYBIND_SCREENSHOT.getKeybind())
            {
                if(Configs.Generic.SS_KEY_CUSTOM.getBooleanValue())
                {
                    Screenshot.makeScreenshot();
                    return true;
                }
                return true;
            }
            else if(Configs.Generic.ENABLED.getBooleanValue())
            {
                if(key == Hotkeys.KEYBIND_CURSOR_NEXT.getKeybind())
                {
                    if (edits.getSelection() != null && edits.getSelection().playereditSet != null)
                    {
                        BlockEdit edit = edits.getSelection().playereditSet.getEditAfter(edits.getSelection());
                        if (edit != null)
                        {
                            edits.selectBlockEdit(edit);
                            return true;
                        }
                    }
                }
                else if(key == Hotkeys.KEYBIND_CURSOR_PREV.getKeybind())
                {
                    if (edits.getSelection() != null && edits.getSelection().playereditSet != null)
                    {
                        BlockEdit edit = edits.getSelection().playereditSet.getEditBefore(edits.getSelection());
                        if (edit != null)
                        {
                            edits.selectBlockEdit(edit);
                            return true;
                        }
                    }
                }
                else if(key == Hotkeys.KEYBIND_QUERY_AFTER.getKeybind() && Configs.Generic.PLUGIN.getStringValue().equals("LogBlock"))
                {
                    edits.queryPostEdits(Configs.Generic.POST_COUNT.getIntegerValue());
                    return true;
                }
                else if(key == Hotkeys.KEYBIND_QUERY_BEFORE.getKeybind() && Configs.Generic.PLUGIN.getStringValue().equals("LogBlock"))
                {
                    edits.queryPreEdits(Configs.Generic.PRE_COUNT.getIntegerValue());
                    return true;
                }
                else if(key == Hotkeys.KEYBIND_TP_CURSOR.getKeybind())
                {
                    if (edits.getSelection() != null)
                    {
                        Teleport.teleport(edits.getSelection().x, edits.getSelection().y, edits.getSelection().z);
                        return true;
                    }
                }
                if(key == Hotkeys.KEYBIND_TP_NEXT.getKeybind())
                {
                    edits.getBlockEditSet().getOreDB().tpNext();
                    return true;
                }
                else if(key == Hotkeys.KEYBIND_TP_PREV.getKeybind())
                {
                    edits.getBlockEditSet().getOreDB().tpPrev();
                    return true;
                }
                else if (key == Hotkeys.KEYBIND_WATSON_CLEAR.getKeybind())
                {
                    edits.clearBlockEditSet();
                    return true;
                }
            }
            return false;
        }
    }
}