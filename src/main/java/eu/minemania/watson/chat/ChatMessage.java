package eu.minemania.watson.chat;

import java.util.concurrent.ConcurrentLinkedQueue;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ChatMessage
{
    private static ChatMessage INSTANCE = new ChatMessage();
    protected ConcurrentLinkedQueue<String> _serverChatQueue = new ConcurrentLinkedQueue<String>();
    protected long _lastServerChatTime;

    public static ChatMessage getInstance()
    {
        return INSTANCE;
    }

    public static void localOutput(String message, boolean watsonMessage)
    {
        sendToLocalChat(Formatting.AQUA, null, message, watsonMessage);
    }

    public static void localOutputT(String translationKey, Object... args)
    {
        sendToLocalChat(Formatting.AQUA, new TranslatableText(translationKey, args),true);
    }

    public static void localError(String message, boolean watsonMessage)
    {
        sendToLocalChat(Formatting.DARK_RED, null, message, watsonMessage);
    }

    public void serverChat(String message)
    {
        _serverChatQueue.add(message);
    }

    public void immediateServerChat(String message)
    {
        if(message != null)
        {
            sendToServerChat(message);
        }
    }

    public static void sendToLocalChat(String message, boolean watsonMessage)
    {
        sendToLocalChat(new LiteralText(message), watsonMessage);
    }

    public static void sendToLocalChat(Text inputmessage, boolean watsonMessage)
    {
        Text message = Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue() ? Highlight.setHighlightChatMessage("chat.type.text", inputmessage, watsonMessage) : inputmessage;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
    }

    public static void sendToLocalChat(Formatting color, Formatting style, String message, boolean watsonMessage)
    {
        LiteralText chat = new LiteralText(message);
        if(color != null && style == null)
        {
            chat.formatted(color);
        }
        else if(color != null && style != null)
        {
            chat.formatted(color, style);
        }
        sendToLocalChat(chat, watsonMessage);
    }

    public static void sendToLocalChat(Formatting color, Text message, boolean watsonMessage)
    {
        message.formatted(color);
        sendToLocalChat(message, watsonMessage);
    }

    public static void sendToServerChat(String message)
    {
        try
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.player.sendChatMessage(message);
        }
        catch (Exception e)
        {
            Watson.logger.error("Sending chat to the server.", e);
        }
    }

    public void processServerChatQueue()
    {
        if(!_serverChatQueue.isEmpty())
        {
            long now = System.currentTimeMillis();
            if(now - _lastServerChatTime >= (long) (1000 * Configs.Generic.CHAT_TIMEOUT.getDoubleValue()))
            {
                _lastServerChatTime = now;
                String message = _serverChatQueue.poll();
                immediateServerChat(message);
            }
        }
    }
}
