package eu.minemania.watson.event;

import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.chat.command.Command;
import eu.minemania.watson.client.Screenshot;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Hotkeys;
import eu.minemania.watson.gui.GuiConfigs;
import eu.minemania.watson.gui.GuiMainMenu;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import net.minecraft.client.Minecraft;

public class KeyCallbacks {
	
	public static void init(Minecraft mc) {	
		IHotkeyCallback callbackHotkeys = new KeyCallbackHotkeys(mc);
		//IHotkeyCallback callbackMessage = new KeyCallBackToggleMessage(mc);
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
	
	/*private static class RenderToggle extends KeyCallbackToggleBooleanConfigWithMessage {
		public RenderToggle(IConfigBoolean config) {
			super(config);
		}
		
		@Override
		public boolean onKeyAction(KeyAction action, IKeybind key) {
			super.onKeyAction(action, key);
			
			return true;
		}
	}*/
	
	private static class KeyCallbackHotkeys implements IHotkeyCallback {
		private final Minecraft mc;
		
		public KeyCallbackHotkeys(Minecraft mc) {
			this.mc = mc;
		}
		
		@Override
		public boolean onKeyAction(KeyAction action, IKeybind key) {
			if(this.mc.player == null || this.mc.world == null) {
				return false;
			}
			
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
			} else if(key == Hotkeys.KEYBIND_TP_NEXT.getKeybind()) {
				
			} else if(key == Hotkeys.KEYBIND_TP_PREV.getKeybind()) {
				
			} else if(key == Hotkeys.KEYBIND_QUERY_BEFORE.getKeybind()) {
				
			} else if(key == Hotkeys.KEYBIND_QUERY_AFTER.getKeybind()) {
				
			} else if(key == Hotkeys.KEYBIND_CURSOR_NEXT.getKeybind()) {
				
			} else if(key == Hotkeys.KEYBIND_CURSOR_PREV.getKeybind()) {
				
			} else if(key == Hotkeys.KEYBIND_TP_CURSOR.getKeybind()) {
				
			}
			
			return false;
		}
	}
	
	/*private static class KeyCallBackToggleMessage implements IHotkeyCallback {
		//private final Minecraft mc;
		
		public KeyCallBackToggleMessage(Minecraft mc) {
			//this.mc = mc;
		}
		
		@Override
		public boolean onKeyAction(KeyAction action, IKeybind key) {
			return false;
		}
	}*/
}

