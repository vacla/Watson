package eu.minemania.watson.analysis;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.minemania.watson.chat.IChatHandler;
import eu.minemania.watson.chat.IMatchedChatHandler;
import net.minecraft.util.text.ITextComponent;

public class Analysis implements IChatHandler {
	protected LinkedHashMap<Pattern, IMatchedChatHandler> _handlers = new LinkedHashMap<Pattern, IMatchedChatHandler>();
	
	public boolean dispatchMatchedChat(ITextComponent chat) {
		String unformatted = chat.getString();
		System.out.println("chat: " + chat);
		System.out.println("unformatted: " + unformatted);
		for(Entry<Pattern, IMatchedChatHandler> entry : _handlers.entrySet()) {
			Matcher m = entry.getKey().matcher(unformatted);
			System.out.println("entry: " + entry.getKey());
			if(m.matches()) {
				System.out.println("matches");
				return entry.getValue().onMatchedChat(chat, m);
			}
		}
		return true;
	}
	
	public void addMatchedChatHandler(Pattern pattern, IMatchedChatHandler handler) {
		_handlers.put(pattern, handler);
	}
	
	@Override
	public boolean onChat(ITextComponent chat) {
		return dispatchMatchedChat(chat);
	}
}
