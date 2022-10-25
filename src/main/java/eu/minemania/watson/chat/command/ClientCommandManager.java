package eu.minemania.watson.chat.command;

import java.util.HashSet;
import java.util.Set;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

/**
 * @author Earthcomputer
 */
public class ClientCommandManager
{
    public static void sendError(Text error)
    {
        sendFeedback(Text.literal("").append(error).formatted(Formatting.RED));
    }

    public static void sendFeedback(String message)
    {
        sendFeedback(Text.translatable(message));
    }

    public static void sendFeedback(Text message)
    {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
    }
}