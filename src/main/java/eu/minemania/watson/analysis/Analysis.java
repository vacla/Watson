package eu.minemania.watson.analysis;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.IChatHandler;
import eu.minemania.watson.chat.IMatchedChatHandler;
import eu.minemania.watson.config.Configs;
import fi.dy.masa.malilib.config.option.StringConfig;
import net.minecraft.text.MutableText;

public class Analysis implements IChatHandler
{
    protected static ListMultimap<String, IMatchedChatHandler> m = ArrayListMultimap.create();

    public boolean dispatchMatchedChat(MutableText chat)
    {
        String unformatted = chat.getString();
        unformatted = unformatted.replaceAll("\u00A7.", "");
        if (Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info("unformatted: " + unformatted);
        }
        for (Entry<String, IMatchedChatHandler> entry : m.entries())
        {
            if (Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info("key: " + entry.getKey());
            }
            Matcher m = Pattern.compile(entry.getKey()).matcher(unformatted);
            if (m.find())
            {
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("key matched: " + entry.getKey());
                }
                return entry.getValue().onMatchedChat(chat, m);
            }
        }
        return true;
    }

    public void addMatchedChatHandler(StringConfig pattern, IMatchedChatHandler handler)
    {
        m.put(pattern.getValue(), handler);
    }

    public static void removeMatchedChatHandler(String newValue, String oldValue)
    {
        for (IMatchedChatHandler handler : m.get(oldValue))
        {
            m.put(newValue, handler);
        }
        m.removeAll(oldValue);
    }

    @Override
    public boolean onChat(MutableText chat)
    {
        return dispatchMatchedChat(chat);
    }
}