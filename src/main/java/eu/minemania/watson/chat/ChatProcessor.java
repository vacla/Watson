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
        addChatHandler(new LbCoordsAnalysis());
        addChatHandler(new LbToolBlockAnalysis());
        addChatHandler(new TeleportAnalysis());
        addChatHandler(new RatioAnalysis());
        addChatHandler(ServerTime.getInstance());

        addChatHandler(new ModModeAnalysis());
        addChatHandler(new RegionInfoAnalysis());

        addChatHandler(new CoreProtectAnalysis());
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