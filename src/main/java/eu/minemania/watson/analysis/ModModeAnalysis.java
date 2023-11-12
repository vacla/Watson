package eu.minemania.watson.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.minemania.watson.chat.IMatchedChatHandler;
import eu.minemania.watson.config.Configs;

public class ModModeAnalysis extends Analysis
{
    public ModModeAnalysis()
    {
        IMatchedChatHandler modmodeHandler = (chat, m) -> {
            changeModMode(m);
            return true;
        };

        addMatchedChatHandler(Configs.Analysis.MODMODE_ENABLE, modmodeHandler);
        addMatchedChatHandler(Configs.Analysis.MODMODE_DISABLE, modmodeHandler);

        IMatchedChatHandler dutiesHandler = (chat, m) -> {
            changeDutyMode(m);
            return true;
        };

        addMatchedChatHandler(Configs.Analysis.DUTYMODE_ENABLE, dutiesHandler);
        addMatchedChatHandler(Configs.Analysis.DUTYMODE_DISABLE, dutiesHandler);
    }

    void changeModMode(Matcher m)
    {
        Configs.Generic.DISPLAYED.setBooleanValue(m.pattern().pattern().equals(Pattern.compile(Configs.Analysis.MODMODE_ENABLE.getStringValue()).pattern()));
    }

    void changeDutyMode(Matcher m)
    {
        Configs.Generic.DISPLAYED.setBooleanValue(m.pattern().pattern().equals(Pattern.compile(Configs.Analysis.DUTYMODE_ENABLE.getStringValue()).pattern()));
    }
}