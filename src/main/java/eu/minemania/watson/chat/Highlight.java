package eu.minemania.watson.chat;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import net.minecraft.sound.SoundEvent;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.tuple.MutablePair;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Formatting;

public class Highlight
{
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Highlight INSTANCE = new Highlight();
    public static boolean changeUsername;
    public static boolean returnBoolean;
    protected static String username;
    protected static Style style;
    private static final HashSet<MutablePair<String, MutablePair<Formatting, Formatting>>> highlights = new HashSet<>();
    private static final String tempkey = "chat.type.text";

    private static Highlight getInstance()
    {
        return INSTANCE;
    }

    /**
     * Highlights player chat message in vanilla.
     *
     * @param key     Key to translate this component
     * @param message Chat message to highlight
     * @return Highlighted TextComponent
     */
    public static MutableText setHighlightChatMessage(String key, MutableText message, boolean watsonMessage)
    {
        final String[] user = {""};
        StringBuilder textChat = new StringBuilder();
        final int[] i = {0};
        MutableText endMessage;
        if (!watsonMessage)
        {
            message.visit((style, string) -> {
                MutableText chatComponent = new LiteralText(string).setStyle(style);

                if (i[0] == 1)
                {
                    user[0] = chatComponent.asString();
                }
                if (i[0] > 2)
                {
                    textChat.append(chatComponent.asString());
                }
                i[0]++;
                return Optional.empty();
            }, Style.EMPTY);

            setUsername(user[0], null);
            endMessage = new TranslatableText(key, mc.player.getDisplayName(), Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue() ? highlight(textChat.toString()) : textChat.toString());
        }
        else
        {
            endMessage = message;
        }
        if (Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info("vanilla message: " + endMessage);
        }
        return endMessage;
    }

    /**
     * Highlights player chat message on modded server (Paper/Spigot).
     *
     * @param message Chat message to highlight
     * @return Highlighted TextComponent
     */
    public static MutableText setHighlightChatMessage(MutableText message)
    {
        String textChat;
        StringBuilder chat = new StringBuilder();
        MutableText endMessage = new LiteralText("");
        final MutableText[] playerChatComponent = {null};
        MutableText prefix = new LiteralText("");
        final Style[] prefixStyle = {null};
        final Style[] dividerStyle = {null};
        String divineDivider = "\u00BB";
        final int[] i = {0};
        ClientPlayerEntity player = mc.player;
        String serverBrand;
        if (player != null)
        {
            serverBrand = player.getServerBrand().toLowerCase();
        }
        else
        {
            return message;
        }
        if (serverBrand.contains("spigot") || serverBrand.contains("paper") || serverBrand.contains("tuinity"))
        {
            if (Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info("message: " + message);
            }
            message.visit((style, string) -> {
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("component text: " + string + " component style: " + style);
                }
                if (i[0] > 0)
                {
                    chat.append(string);
                    if (Configs.Generic.DEBUG.getBooleanValue())
                    {
                        Watson.logger.info(i[0] + ": " + chat);
                    }
                    if (i[0] == 1 && string.contains("[") && string.contains("]"))
                    {
                        prefixStyle[0] = style;
                    }
                    if (playerChatComponent[0] == null && !string.equals(" ") && (i[0] == 2 || i[0] == 3) && prefixStyle[0] != null)
                    {
                        playerChatComponent[0] = new LiteralText(string).setStyle(style);
                    }
                    if (string.contains(divineDivider))
                    {
                        dividerStyle[0] = style;
                    }
                }
                i[0]++;
                return Optional.empty();
            }, Style.EMPTY);

            if (chat.toString().contains("<") && chat.toString().contains(">") && !chat.toString().startsWith("/") && (!chat.toString().startsWith("§") && chat.charAt(2) != '/'))
            {
                int startUsername = chat.indexOf("<") + 1;
                int endUsername = chat.indexOf(">");
                if (chat.toString().contains("[") && chat.toString().contains("]") && chat.indexOf("]") < startUsername - 1 && ((startUsername - 2) - (chat.indexOf("]")) <= 5))
                {
                    prefix = new LiteralText(chat.substring(chat.indexOf("["), chat.indexOf("]") + 1));
                }
                if (!prefix.equals(new LiteralText("")) || chat.toString().startsWith("<"))
                {
                    username = chat.substring(startUsername, endUsername);
                }
                else
                {
                    return message;
                }
                textChat = chat.substring(endUsername + 2);
                changeUsername = true;
                setUsername(username, null);

                endMessage.append(new TranslatableText(tempkey, mc.player.getDisplayName(), highlight(textChat)));
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("textchat: " + textChat);
                    Watson.logger.info("prefix: " + prefix.getString());
                    Watson.logger.info("text endmessage: " + endMessage.getString());
                }
                if (!prefix.equals(new LiteralText("")))
                {
                    prefix.append(" ");
                    prefix.append(endMessage);
                    endMessage = prefix;
                }
            }
            else if (chat.toString().contains(divineDivider))
            {
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("chat: " + chat);
                }
                int startUsername = chat.indexOf("]") + 1;
                int endUsername = chat.indexOf(divineDivider) - 1;
                if (prefix.equals(new LiteralText("")) && (chat.toString().contains("[") && chat.toString().contains("]")) && chat.indexOf("]") < endUsername)
                {
                    String textPrefix = chat.substring(chat.indexOf("["), chat.indexOf("]") + 1);
                    if (prefixStyle[0] != null)
                    {
                        prefix = new LiteralText(textPrefix).setStyle(prefixStyle[0]);
                        if (Configs.Generic.DEBUG.getBooleanValue())
                        {
                            Watson.logger.info("prefixStyle: " + prefixStyle[0] + " formatting: " + Formatting.RESET);
                            Watson.logger.info("text prefix: " + prefix.getString());
                        }
                    }
                    if (Configs.Generic.DEBUG.getBooleanValue())
                    {
                        Watson.logger.info("prefix: " + prefix.getString());
                    }
                }
                if (!prefix.equals(new LiteralText("")) || chat.toString().startsWith("["))
                {
                    if (prefix.equals(new LiteralText("")))
                    {
                        prefix = new LiteralText(chat.substring(0, startUsername));
                        Watson.logger.info("prefix: " + prefix.getString());
                    }
                    username = chat.substring(startUsername, endUsername);
                    if (Configs.Generic.DEBUG.getBooleanValue())
                    {
                        Watson.logger.info("username: " + username);
                    }
                }
                else
                {
                    MutableText beforeDivider = new LiteralText("");
                    AtomicBoolean dividerShown = new AtomicBoolean(false);
                    List<MutableText> textMessage = new ArrayList<>();

                    message.visit((style, string) -> {
                        MutableText test = new LiteralText(string).setStyle(style);
                        if (dividerShown.get())
                        {
                            textMessage.add(test);
                        }
                        else if (string.contains(divineDivider))
                        {
                            dividerShown.set(true);
                            textMessage.add(string.length() > 3 ? new LiteralText(string.substring(string.indexOf(divineDivider))).setStyle(style) : test);
                        }
                        else
                        {
                            beforeDivider.append(new LiteralText(string).setStyle(style));
                        }
                        return Optional.empty();
                    }, Style.EMPTY);

                    if (textMessage.size() > 0)
                    {
                        if (Configs.Generic.DEBUG.getBooleanValue())
                        {
                            Watson.logger.info("text message: " + textMessage);
                            Watson.logger.info("divider: " + beforeDivider.getString());
                        }
                        message = beforeDivider.append(highlight(textMessage));
                        if (Configs.Generic.DEBUG.getBooleanValue())
                        {
                            Watson.logger.info("total message: " + message);
                            Watson.logger.info("total message string: " + message.getString());
                        }
                    }
                    return message;
                }

                textChat = chat.substring(endUsername + 2).trim();

                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("input textChat1: " + textChat);
                    Watson.logger.info("output player:" + playerChatComponent[0]);
                }
                changeUsername = true;
                if (playerChatComponent[0] != null)
                {
                    setUsername(playerChatComponent[0].asString(), playerChatComponent[0].getStyle());
                }
                else
                {
                    setUsername(username, prefixStyle[0]);
                }
                MutableText displayName = (MutableText) mc.player.getDisplayName();
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("watson.chat.message.hover", (new LiteralText(time)).formatted(Formatting.YELLOW)));
                displayName.styled(style -> style.withHoverEvent(hover));
                if (!prefix.equals(new LiteralText("")))
                {
                    if (Configs.Generic.DEBUG.getBooleanValue())
                    {
                        Watson.logger.info("endmessage2: " + prefix);
                    }
                    endMessage.append(prefix);
                }
                if (dividerStyle[0] != null)
                {
                    endMessage.append(" ");
                    endMessage.append(new TranslatableText("watson.chat.message", displayName, new LiteralText(divineDivider).setStyle(dividerStyle[0]), highlight(textChat)));
                }
                else
                {
                    endMessage.append(new TranslatableText(tempkey, displayName, new LiteralText(highlight(textChat))));
                }
            }
            else
            {
                endMessage = message;
            }
        }
        else
        {
            endMessage = message;
        }
        if (Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info("endmessage3: " + endMessage);
        }
        changeUsername = false;
        return endMessage;
    }

    /**
     * Highlights text of player chat message.
     *
     * @param chatText Text that player send
     * @return Highlighted text
     */
    private static String highlight(String chatText)
    {
        boolean madeSound = false;
        for (MutablePair<String, MutablePair<Formatting, Formatting>> item_highlight : highlights)
        {
            int case_sensitive = Configs.Generic.HIGHLIGHT_CASE_SENSITIVE.getBooleanValue() ? 0 : Pattern.CASE_INSENSITIVE;
            Pattern pattern = Pattern.compile(item_highlight.getLeft(), case_sensitive);
            Matcher matcher = pattern.matcher(chatText);
            if (matcher.find())
            {
                if (!madeSound && Configs.Generic.HIGHLIGHT_SOUND_ENABLE.getBooleanValue())
                {
                    madeSound = true;
                    String sound = Configs.Generic.HIGHLIGHT_SOUND.getStringValue();
                    try
                    {
                        SoundEvent soundEvent = Registry.SOUND_EVENT.get(new Identifier(sound));
                        float soundVolume = (float) Configs.Generic.HIGHLIGHT_SOUND_VOLUME.getDoubleValue();
                        mc.player.playSound(soundEvent, soundVolume, 1f);
                    }
                    catch (Exception e)
                    {
                        ChatMessage.localErrorT("watson.error.highlight_sound", sound);
                    }
                }
                matcher.reset();
                while (matcher.find())
                {
                    int start = matcher.start();
                    int stop = matcher.end();
                    if (item_highlight.getRight().getLeft() != null && item_highlight.getRight().getRight() == null)
                    {
                        chatText = matcher.replaceAll(item_highlight.getRight().getLeft() + chatText.substring(start, stop) + Formatting.RESET);
                    }
                    else if (item_highlight.getRight().getLeft() == null && item_highlight.getRight().getRight() != null)
                    {
                        chatText = matcher.replaceAll(item_highlight.getRight().getRight() + chatText.substring(start, stop) + Formatting.RESET);
                    }
                    else
                    {
                        chatText = matcher.replaceAll(item_highlight.getRight().getLeft() + "" + item_highlight.getRight().getRight() + chatText.substring(start, stop) + Formatting.RESET);
                    }
                }
            }
        }
        return chatText;
    }

    private static MutableText highlight(List<MutableText> messages)
    {
        MutableText endMessage = new LiteralText("");
        for (MutableText message : messages)
        {
            endMessage.append(new LiteralText(highlight(message.asString())).setStyle(message.getStyle()));
        }
        return endMessage;
    }

    private static void setUsername(String user, @Nullable Style styleLocal)
    {
        username = user;
        style = styleLocal;
    }

    public static String getUsername()
    {
        return username;
    }

    public static Style getStyle()
    {
        return style;
    }

    /**
     * Converts character style to Formatting style.
     *
     * @param charac Character of style
     * @return Style in Formatting
     */
    private Formatting getStyle(String charac)
    {
        Formatting result = Formatting.RESET;
        switch (charac)
        {
            case "+":
                result = Formatting.BOLD;
                break;
            case "/":
                result = Formatting.ITALIC;
                break;
            case "_":
                result = Formatting.UNDERLINE;
                break;
            case "-":
                result = Formatting.STRIKETHROUGH;
                break;
            case "?":
                result = Formatting.OBFUSCATED;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Sets lists from where to get highlight list config.
     *
     * @param list Config list of highlight items
     */
    public static void setHighlightList(List<String> list)
    {
        highlights.clear();

        getInstance().populateHighlightList(list);
    }

    /**
     * Checks if style character.
     *
     * @param style String of 1 character that might be a style
     * @return True if style character used
     */
    private boolean isStyle(String style)
    {
        switch (style)
        {
            case "+":
            case "/":
            case "_":
            case "-":
            case "?":
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if color string.
     *
     * @param color String that might be a color
     * @return True if color string
     */
    private boolean isColor(String color)
    {
        switch (color)
        {
            case "black":
            case "darkblue":
            case "darkgreen":
            case "darkaqua":
            case "darkred":
            case "darkpurple":
            case "gold":
            case "grey":
            case "gray":
            case "darkgrey":
            case "darkgray":
            case "blue":
            case "green":
            case "aqua":
            case "red":
            case "lightpurple":
            case "yellow":
            case "white":
                return true;
            default:
                return false;
        }
    }

    public static void listHighlights()
    {
        if (highlights.isEmpty())
        {
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.empty");
        }
        else
        {
            int index = 0;
            for (MutablePair<String, MutablePair<Formatting, Formatting>> item_highlight : highlights)
            {
                Formatting color = item_highlight.getRight().getLeft();
                Formatting style = item_highlight.getRight().getRight();

                ChatMessage.localOutputT("watson.message.highlight.list_string", index + 1, item_highlight.getLeft(), color != null ? color.getName() : null, style != null ? style.getName() : null);
                ++index;
            }
        }
    }

    public static void remove(String pattern)
    {
        if (highlights.isEmpty())
        {
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.empty");
            return;
        }
        List<String> orig = Configs.Lists.HIGHLIGHT.getStrings();
        List<String> copy = new ArrayList<>(orig);
        copy.removeIf((str) -> str.contains(pattern));

        if (copy.size() != orig.size())
        {
            Configs.Lists.HIGHLIGHT.setStrings(copy);
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.removed", pattern);
        }
    }

    public static void add(String pattern, String color, String style)
    {
        List<String> orig = Configs.Lists.HIGHLIGHT.getStrings();
        List<String> copy = new ArrayList<>(orig);
        if (pattern != null && (style != null || color != null))
        {
            copy.add(style + color + ";" + pattern);
            Configs.Lists.HIGHLIGHT.setStrings(copy);
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.added", copy.size(), pattern);
        }
    }

    /**
     * Populates highlight list with config highlight list.
     *
     * @param names Config highlight list items
     */
    private void populateHighlightList(List<String> names)
    {
        for (String str : names)
        {
            try
            {
                if (!str.isEmpty())
                {
                    int index = str.indexOf(";");
                    if (index != -1)
                    {
                        String format = str.substring(0, index);
                        String pattern = str.substring(index + 1);
                        MutablePair<String, MutablePair<Formatting, Formatting>> pr = new MutablePair<>();
                        MutablePair<Formatting, Formatting> pr2 = new MutablePair<>();
                        if (format.length() > 0)
                        {
                            if (format.length() == 1)
                            {
                                if (isStyle(format))
                                {
                                    pr2.setRight(getStyle(format));
                                }
                            }
                            else
                            {
                                String style = format.substring(0, 1);
                                String color = format.substring(1);
                                if (isStyle(style))
                                {
                                    pr2.setRight(getStyle(style));
                                }
                                if (isColor(format))
                                {
                                    pr2.setLeft(Color.getByColorOrName(format).getColor());
                                }
                                else if (isColor(color))
                                {
                                    pr2.setLeft(Color.getByColorOrName(color).getColor());
                                }
                            }
                            pr.setRight(pr2);
                        }
                        if (pattern.length() > 0)
                        {
                            pr.setLeft(pattern);
                        }
                        if (pr.getLeft() != null && pr.getRight() != null)
                        {
                            Highlight.highlights.add(pr);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                InfoUtils.showGuiMessage(MessageType.ERROR, "watson.error.highlight", str);
            }
        }
    }

    public static boolean getReturnBoolean()
    {
        return returnBoolean;
    }

    public static void toggleReturnBoolean()
    {
        returnBoolean = !returnBoolean;
    }
}