package eu.minemania.watson.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import eu.minemania.watson.chat.IMatchedChatHandler;
import eu.minemania.watson.config.Configs;
import net.minecraft.text.Text;

public class ModModeAnalysis extends Analysis
{
    public ModModeAnalysis()
    {
        IMatchedChatHandler modmodeHandler = new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                changeModMode(chat, m);
                return true;
            }
        };

        addMatchedChatHandler(Configs.Analysis.MODMODE_ENABLE, modmodeHandler);
        addMatchedChatHandler(Configs.Analysis.MODMODE_DISABLE, modmodeHandler);

        IMatchedChatHandler dutiesHandler = new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                changeDutyMode(chat, m);
                return true;
            }
        };

        addMatchedChatHandler(Configs.Analysis.DUTYMODE_ENABLE, dutiesHandler);
        addMatchedChatHandler(Configs.Analysis.DUTYMODE_DISABLE, dutiesHandler);
    }

    void changeModMode(Text chat, Matcher m)
    {
        Configs.Generic.DISPLAYED.setBooleanValue(m.pattern() == Pattern.compile(Configs.Analysis.MODMODE_ENABLE.getStringValue()));
    }

    void changeDutyMode(Text chat, Matcher m)
    {
        Configs.Generic.DISPLAYED.setBooleanValue(m.pattern() == Pattern.compile(Configs.Analysis.DUTYMODE_ENABLE.getStringValue()));
    }
}