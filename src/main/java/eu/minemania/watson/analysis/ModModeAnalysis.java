package eu.minemania.watson.analysis;

import static eu.minemania.watson.analysis.MiscPatterns.DUTYMODE_DISABLE;
import static eu.minemania.watson.analysis.MiscPatterns.DUTYMODE_ENABLE;
import static eu.minemania.watson.analysis.MiscPatterns.MODMODE_DISABLE;
import static eu.minemania.watson.analysis.MiscPatterns.MODMODE_ENABLE;

import java.util.regex.Matcher;

import eu.minemania.watson.chat.IMatchedChatHandler;
import eu.minemania.watson.config.Configs;
import net.minecraft.text.Text;

public class ModModeAnalysis extends Analysis {
	public ModModeAnalysis() {
		IMatchedChatHandler modmodeHandler = new IMatchedChatHandler() {

			@Override
			public boolean onMatchedChat(Text chat, Matcher m) {
				changeModMode(chat, m);
				return true;
			}
		};

		addMatchedChatHandler(MODMODE_ENABLE, modmodeHandler);
		addMatchedChatHandler(MODMODE_DISABLE, modmodeHandler);

		IMatchedChatHandler dutiesHandler = new IMatchedChatHandler() {

			@Override
			public boolean onMatchedChat(Text chat, Matcher m) {
				changeDutyMode(chat, m);
				return true;
			}
		};

		addMatchedChatHandler(DUTYMODE_ENABLE, dutiesHandler);
		addMatchedChatHandler(DUTYMODE_DISABLE, dutiesHandler);
	}

	void changeModMode(Text chat, Matcher m) {
		Configs.Generic.DISPLAYED.setBooleanValue(m.pattern() == MODMODE_ENABLE);
	}

	void changeDutyMode(Text chat, Matcher m) {
		Configs.Generic.DISPLAYED.setBooleanValue(m.pattern() == DUTYMODE_ENABLE);
	}
}
