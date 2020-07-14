package eu.minemania.watson.chat;

import java.util.regex.Matcher;

import net.minecraft.text.MutableText;

public interface IMatchedChatHandler
{
    boolean onMatchedChat(MutableText chat, Matcher m);
}
