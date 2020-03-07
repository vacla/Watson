package eu.minemania.watson.chat;

import java.util.regex.Matcher;

import net.minecraft.util.text.ITextComponent;

public interface IMatchedChatHandler
{
    public boolean onMatchedChat(ITextComponent chat, Matcher m);
}