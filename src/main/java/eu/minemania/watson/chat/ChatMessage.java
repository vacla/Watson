package eu.minemania.watson.chat;

import java.util.concurrent.ConcurrentLinkedQueue;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatMessage
{
    private static final ChatMessage INSTANCE = new ChatMessage();
    protected ConcurrentLinkedQueue<String> _serverChatQueue = new ConcurrentLinkedQueue<>();
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
        sendToLocalChat(Formatting.AQUA, Text.translatable(translationKey, args), true);
    }

    public static void localError(String message, boolean watsonMessage)
    {
        sendToLocalChat(Formatting.DARK_RED, null, message, watsonMessage);
    }

    public static void localErrorT(String translationKey, Object... args)
    {
        sendToLocalChat(Formatting.DARK_RED, Text.translatable(translationKey, args), true);
    }

    public void serverChat(String message, boolean firstMessage)
    {
        _serverChatQueue.add(message);
        if (firstMessage)
        {
            _lastServerChatTime = System.currentTimeMillis();
        }
    }

    public void immediateServerChat(String message)
    {
        if (message != null)
        {
            sendToServerChat(message);
        }
    }

    public static void sendToLocalChat(String message, boolean watsonMessage)
    {
        sendToLocalChat(Text.translatable(message), watsonMessage);
    }

    public static void sendToLocalChat(Text inputmessage, boolean watsonMessage)
    {
        Text message = Configs.Highlights.USE_CHAT_HIGHLIGHTS.getBooleanValue() ? Highlight.setHighlightChatMessage("chat.type.text", inputmessage, watsonMessage) : inputmessage;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
    }

    public static void sendToLocalChat(Formatting color, Formatting style, String message, boolean watsonMessage)
    {
        MutableText chat = Text.literal(message);
        if (color != null && style == null)
        {
            chat.formatted(color);
        }
        else if (color != null)
        {
            chat.formatted(color, style);
        }
        sendToLocalChat(chat, watsonMessage);
    }

    public static void sendToLocalChat(Formatting color, Text message, boolean watsonMessage)
    {
        MutableText newMessage = Text.empty();
        newMessage.append(message);
        newMessage.formatted(color);
        sendToLocalChat(newMessage, watsonMessage);
    }

    public static void sendToServerChat(String message)
    {
        try
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.player.networkHandler.sendChatCommand(message);
        }
        catch (Exception e)
        {
            Watson.logger.error("Sending chat to the server.", e);
        }
    }

    public void processServerChatQueue()
    {
        if (!_serverChatQueue.isEmpty())
        {
            long now = System.currentTimeMillis();
            if (now - _lastServerChatTime >= (long) (1000 * Configs.Generic.CHAT_TIMEOUT.getDoubleValue()))
            {
                _lastServerChatTime = now;
                String message = _serverChatQueue.poll();
                immediateServerChat(message);
            }
        }
    }
}