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
import fi.dy.masa.malilib.config.options.ConfigString;
import net.minecraft.text.Text;

public class Analysis implements IChatHandler
{
    protected static ListMultimap<String, IMatchedChatHandler> m = ArrayListMultimap.create();

    public boolean dispatchMatchedChat(Text chat)
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

    public void addMatchedChatHandler(ConfigString pattern, IMatchedChatHandler handler)
    {
        m.put(pattern.getStringValue(), handler);
    }

    public static void removeMatchedChatHandler(ConfigString pattern)
    {
        for (IMatchedChatHandler handler : m.get(pattern.getOldStringValue()))
        {
            m.put(pattern.getStringValue(), handler);
        }
        m.removeAll(pattern.getOldStringValue());
    }

    @Override
    public boolean onChat(Text chat)
    {
        return dispatchMatchedChat(chat);
    }
}