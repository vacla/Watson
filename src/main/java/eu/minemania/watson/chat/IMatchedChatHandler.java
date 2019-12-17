package eu.minemania.watson.chat;

import java.util.regex.Matcher;

import net.minecraft.text.Text;

public interface IMatchedChatHandler {
	public boolean onMatchedChat(Text chat, Matcher m);
}
