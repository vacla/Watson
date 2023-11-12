package eu.minemania.watson.chat;

import java.util.ArrayList;

import eu.minemania.watson.analysis.*;
import eu.minemania.watson.config.Configs;
import net.minecraft.text.Text;

public class ChatProcessor
{
    private static final ChatProcessor INSTANCE = new ChatProcessor();
    private final ArrayList<IChatHandler> _handlers = new ArrayList<>();

    private ChatProcessor()
    {
        addChatHandler(new LogBlockAnalysis());

        addChatHandler(new ModModeAnalysis());
        addChatHandler(new RegionInfoAnalysis());

        addChatHandler(new CoreProtectAnalysis());

        addChatHandler(new PrismAnalysis());
    }

    public static ChatProcessor getInstance()
    {
        return INSTANCE;
    }

    public void addChatHandler(IChatHandler handler)
    {
        _handlers.add(handler);
    }

    public boolean onChat(Text chat)
    {
        if (Configs.Generic.ENABLED.getBooleanValue())
        {
            return new Analysis().onChat(chat);
        }
        else
        {
            return true;
        }
    }
}