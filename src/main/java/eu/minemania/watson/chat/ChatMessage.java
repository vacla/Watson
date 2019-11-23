package eu.minemania.watson.chat;

import java.util.concurrent.ConcurrentLinkedQueue;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ChatMessage {
	private static ChatMessage INSTANCE = new ChatMessage();
	protected ConcurrentLinkedQueue<String> _serverChatQueue = new ConcurrentLinkedQueue<String>();
	protected long _lastServerChatTime;
	
	public static ChatMessage getInstance() {
		return INSTANCE;
	}
	
	public static void localOutput(String message, boolean watsonMessage) {
		sendToLocalChat(TextFormatting.AQUA, null, message, watsonMessage);
	}
	
	public static void localOutputT(String translationKey, Object... args) {
		sendToLocalChat(TextFormatting.AQUA, new TextComponentTranslation(translationKey, args),true);
	}
	
	public static void localError(String message, boolean watsonMessage) {
		sendToLocalChat(TextFormatting.DARK_RED, null, message, watsonMessage);
	}
	
	public void serverChat(String message) {
		_serverChatQueue.add(message);
	}
	
	public void immediateServerChat(String message) {
		if(message != null) {
			sendToServerChat(message);
		}
	}
	
	public static void sendToLocalChat(String message, boolean watsonMessage) {
		sendToLocalChat(new TextComponentString(message), watsonMessage);
	}
	
	public static void sendToLocalChat(ITextComponent inputmessage, boolean watsonMessage) {
		ITextComponent message = Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue() ? Highlight.setHighlightChatMessage("chat.type.text", inputmessage, watsonMessage) : inputmessage;
		Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(message);
	}
	
	public static void sendToLocalChat(TextFormatting color, TextFormatting style, String message, boolean watsonMessage) {
		TextComponentString chat = new TextComponentString(message);
		if(style == null) {
			chat.applyTextStyle(color);
		} else {
			chat.applyTextStyles(color, style);
		}
		sendToLocalChat(chat, watsonMessage);
	}
	
	public static void sendToLocalChat(TextFormatting color, ITextComponent message, boolean watsonMessage) {
		message.applyTextStyle(color);
		sendToLocalChat(message, watsonMessage);
	}
	
	public static void sendToServerChat(String message) {
		try {
			Minecraft mc = Minecraft.getInstance();
			mc.player.sendChatMessage(message);
		} catch (Exception e) {
			Watson.logger.error("Sending chat to the server.", e);
		}
	}
	
	public void processServerChatQueue() {
		if(!_serverChatQueue.isEmpty()) {
			long now = System.currentTimeMillis();
			if(now - _lastServerChatTime >= (long) (1000 * Configs.Generic.CHAT_TIMEOUT.getDoubleValue())) {
				_lastServerChatTime = now;
				String message = _serverChatQueue.poll();
				immediateServerChat(message);
			}
		}
	}
}
