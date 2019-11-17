package eu.minemania.watson.chat.command;

import java.util.HashSet;
import java.util.Set;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * @author Earthcomputer
 */
public class ClientCommandManager {
	private static Set<String> clientSideCommands = new HashSet<>();

    public static void clearClientSideCommands() {
        clientSideCommands.clear();
    }
    
    public static Set<String> getClientSideCommands() {
    	return clientSideCommands;
    }

    public static void addClientSideCommand(String name) {
        clientSideCommands.add(name);
    }

    public static boolean isClientSideCommand(String name) {
        return clientSideCommands.contains(name);
    }

    public static void sendError(ITextComponent error) {
        sendFeedback(new TextComponentString("").appendText(error.getFormattedText()).applyTextStyles(TextFormatting.RED));
    }

    public static void sendFeedback(String message) {
        sendFeedback(new TextComponentTranslation(message));
    }

    public static void sendFeedback(ITextComponent message) {
    	System.out.println("before");
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(message);
        System.out.println("after");
    }

    public static int executeCommand(StringReader reader, String command) {
    	Minecraft mc = Minecraft.getInstance();
        EntityPlayerSP player = mc.player;
        try {
            return player.connection.func_195515_i().execute(reader, new FakeCommandSource(player));
        } catch (CommandException e) {
            ClientCommandManager.sendError(e.getComponent());
        } catch (CommandSyntaxException e) {
            ClientCommandManager.sendError(TextComponentUtils.toTextComponent(e.getRawMessage()));
            if (e.getInput() != null && e.getCursor() >= 0) {
                int cursor = Math.min(e.getCursor(), e.getInput().length());
                ITextComponent text = new TextComponentString("").applyTextStyles(TextFormatting.GRAY)
                        .applyTextStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (cursor > 10) {
                    text.appendText("...");
                }
                text.appendText(e.getInput().substring(Math.max(0, cursor - 10), cursor));
                if (cursor < e.getInput().length()) {
                    text.appendText((new TextComponentString(e.getInput().substring(cursor)).applyTextStyles(TextFormatting.RED, TextFormatting.UNDERLINE)).getFormattedText());
                }

                text.appendText((new TextComponentTranslation("command.context.here").applyTextStyles(TextFormatting.RED, TextFormatting.ITALIC)).getFormattedText());
                ClientCommandManager.sendError(text);
            }
        } catch (Exception e) {
            TextComponentString error = new TextComponentString(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
            ClientCommandManager.sendError(new TextComponentTranslation("command.failed").applyTextStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error))));
            e.printStackTrace();
        }
        return 1;
    }

    public static ITextComponent getCoordsTextComponent(BlockPos pos) {
        ITextComponent text = new TextComponentTranslation("commands.client.blockpos", pos.getX(), pos.getY(),
                pos.getZ());
        text.getStyle().setUnderlined(true);
        text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                String.format("/clook block %d %d %d", pos.getX(), pos.getY(), pos.getZ())));
        text.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new TextComponentString(String.format("/clook block %d %d %d", pos.getX(), pos.getY(), pos.getZ()))));
        return text;
    }

    public static ITextComponent getCommandTextComponent(String translationKey, String command) {
        ITextComponent text = new TextComponentTranslation(translationKey).applyTextStyle(style -> style.setUnderlined(true));
        text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        text.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(command)));
        return text;
    }
}
