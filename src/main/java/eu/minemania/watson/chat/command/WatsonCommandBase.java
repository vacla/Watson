package eu.minemania.watson.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class WatsonCommandBase {
	
	public static void localOutput(CommandSource sender, String message) {
		sendColoredText(sender, TextFormatting.AQUA, message);
	}
	
	public static void localOutputT(CommandSource sender, String translationKey, Object... args) {
		
		sendColoredText(sender, TextFormatting.AQUA, new TextComponentTranslation(translationKey, args));
	}
	
	public static void localError(CommandSource sender, String message) {
		sendColoredText(sender, TextFormatting.DARK_RED, message);
	}
	
	public static void localErrorT(CommandSource sender, String translationKey, Object... args) {
		sendColoredText(sender, TextFormatting.DARK_RED, new TextComponentTranslation(translationKey, args));
	}
	
	public static void sendColoredText(CommandSource sender, TextFormatting color, String message) {
		TextComponentString chat = new TextComponentString(message);
		Style style = new Style();
		style.setColor(color);
		chat.setStyle(style);
		sender.getEntity().sendMessage(chat);
	}

	public static void sendColoredText(CommandSource sender, TextFormatting color, ITextComponent component) {
		Style style = new Style();
		style.setColor(color);
		component.setStyle(style);
		sender.getEntity().sendMessage(component);
	}
}
