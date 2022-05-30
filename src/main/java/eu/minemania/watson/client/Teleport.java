package eu.minemania.watson.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;

public class Teleport
{
    public static void teleport(double x, double y, double z, String world)
    {
        String format = Configs.Generic.TELEPORT_COMMAND.getValue();
        Pattern pattern = Pattern.compile("\\{(\\w*):?(\\w*)}");
        Matcher matcher = pattern.matcher(format);
        while (matcher.find())
        {
            String text = Pattern.quote(matcher.group());
            switch (matcher.group(1))
            {
                case "world" -> {
                    world = world != null ? world : "";
                    format = format.replaceFirst(text, world);
                }
                case "x" -> {
                    Number nx = (matcher.group(2).equals("d") ? (Number) (x + 0.5) : (int) x);
                    format = format.replaceFirst(text, String.valueOf(nx));
                }
                case "y" -> {
                    Number ny = (matcher.group(2).equals("d") ? (Number) (y + 0.5) : (int) y);
                    format = format.replaceFirst(text, String.valueOf(ny));
                }
                case "z" -> {
                    Number nz = (matcher.group(2).equals("d") ? (Number) (z + 0.5) : (int) z);
                    format = format.replaceFirst(text, String.valueOf(nz));
                }
            }
        }
        if (Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info(format);
        }
        ChatMessage.sendToServerChat(format);
    }
}