package eu.minemania.watson.event;

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
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import net.minecraft.client.MinecraftClient;

public class KeyCallbacks {

	public static void init(MinecraftClient mc) {	
		IHotkeyCallback callbackHotkeys = new KeyCallbackHotkeys(mc);
		ValueChangeStringCallback valueChangeStringCallback = new ValueChangeStringCallback();
		ValueChangeStringListCallback valueChangeStringListCallback = new ValueChangeStringListCallback();

		Configs.Generic.WATSON_PREFIX.setValueChangeCallback(valueChangeStringCallback);
		Configs.Lists.HIGHLIGHT.setValueChangeCallback(valueChangeStringListCallback);

		Hotkeys.OPEN_GUI_MAIN_MENU.getKeybind().setCallback(callbackHotkeys);
		Hotkeys.OPEN_GUI_SETTINGS.getKeybind().setCallback(callbackHotkeys);
		Hotkeys.KEYBIND_SCREENSHOT.getKeybind().setCallback(callbackHotkeys);
		Hotkeys.KEYBIND_TP_NEXT.getKeybind().setCallback(callbackHotkeys);
		Hotkeys.KEYBIND_TP_PREV.getKeybind().setCallback(callbackHotkeys);
		Hotkeys.KEYBIND_QUERY_BEFORE.getKeybind().setCallback(callbackHotkeys);
		Hotkeys.KEYBIND_QUERY_AFTER.getKeybind().setCallback(callbackHotkeys);
		Hotkeys.KEYBIND_CURSOR_NEXT.getKeybind().setCallback(callbackHotkeys);
		Hotkeys.KEYBIND_CURSOR_PREV.getKeybind().setCallback(callbackHotkeys);
		Hotkeys.KEYBIND_TP_CURSOR.getKeybind().setCallback(callbackHotkeys);
	}

	private static class ValueChangeStringCallback implements IValueChangeCallback<ConfigString> {

		public ValueChangeStringCallback() {
		}

		@Override
		public void onValueChanged(ConfigString config) {
			if(config == Configs.Generic.WATSON_PREFIX) {
				Command.reregisterWatsonCommand(Command.commandDispatcher, Configs.Generic.WATSON_PREFIX);
			}
		}
	}

	private static class ValueChangeStringListCallback implements IValueChangeCallback<ConfigStringList> {

		public ValueChangeStringListCallback() {
		}

		@Override
		public void onValueChanged(ConfigStringList config) {
			if(config == Configs.Lists.HIGHLIGHT) {
				Highlight.setHighlightList(Configs.Lists.HIGHLIGHT.getStrings());
			}
		}
	}

	private static class KeyCallbackHotkeys implements IHotkeyCallback {
		private final MinecraftClient mc;

		public KeyCallbackHotkeys(MinecraftClient mc) {
			this.mc = mc;
		}

		@Override
		public boolean onKeyAction(KeyAction action, IKeybind key) {
			if(this.mc.player == null || this.mc.world == null) {
				return false;
			}
			EditSelection edits = DataManager.getEditSelection();
			if (key == Hotkeys.OPEN_GUI_MAIN_MENU.getKeybind()) {
				GuiBase.openGui(new GuiMainMenu());
				return true;
			} else if(key == Hotkeys.OPEN_GUI_SETTINGS.getKeybind()) {
				GuiBase.openGui(new GuiConfigs());
				return true;
			} else if(key == Hotkeys.KEYBIND_SCREENSHOT.getKeybind()) {
				if(Configs.Generic.SS_KEY_CUSTOM.getBooleanValue()) {
					Screenshot.makeScreenshot();
					return true;
				}
				return true;
			} else if(Configs.Generic.ENABLED.getBooleanValue()) {
				if(key == Hotkeys.KEYBIND_TP_NEXT.getKeybind()) {
					edits.getBlockEditSet().getOreDB().tpNext();
					return true;
				} else if(key == Hotkeys.KEYBIND_TP_PREV.getKeybind()) {
					edits.getBlockEditSet().getOreDB().tpPrev();
					return true;
				} else if(key == Hotkeys.KEYBIND_QUERY_BEFORE.getKeybind() && Configs.Generic.PLUGIN.getStringValue().equals("LogBlock")) {
					edits.queryPreEdits(Configs.Generic.PRE_COUNT.getIntegerValue());
					return true;
				} else if(key == Hotkeys.KEYBIND_QUERY_AFTER.getKeybind() && Configs.Generic.PLUGIN.getStringValue().equals("LogBlock")) {
					edits.queryPostEdits(Configs.Generic.POST_COUNT.getIntegerValue());
					return true;
				} else if(key == Hotkeys.KEYBIND_CURSOR_NEXT.getKeybind()) {
					if (edits.getSelection() != null && edits.getSelection().playereditSet != null) {
						BlockEdit edit = edits.getSelection().playereditSet.getEditAfter(edits.getSelection());
						if (edit != null) {
							edits.selectBlockEdit(edit);
							return true;
						}
					}
				} else if(key == Hotkeys.KEYBIND_CURSOR_PREV.getKeybind()) {
					if (edits.getSelection() != null && edits.getSelection().playereditSet != null) {
						BlockEdit edit = edits.getSelection().playereditSet.getEditBefore(edits.getSelection());
						if (edit != null) {
							edits.selectBlockEdit(edit);
							return true;
						}
					}
				} else if(key == Hotkeys.KEYBIND_TP_CURSOR.getKeybind()) {
					if (edits.getSelection() != null) {
						Teleport.teleport(edits.getSelection().x, edits.getSelection().y, edits.getSelection().z);
						return true;
					}
				}
			}
			return false;
		}
	}
}

