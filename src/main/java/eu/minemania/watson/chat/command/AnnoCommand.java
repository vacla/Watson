package eu.minemania.watson.chat.command;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;

import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.Annotation;
import eu.minemania.watson.db.BlockEditSet;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class AnnoCommand extends WatsonCommandBase
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ClientCommandManager.addClientSideCommand("anno");
        LiteralArgumentBuilder<ServerCommandSource> anno = literal("anno").executes(AnnoCommand::help)
                .then(literal("help").executes(AnnoCommand::help))
                .then(literal("list").executes(AnnoCommand::list))
                .then(literal("clear").executes(AnnoCommand::clear))
                .then(literal("tp")
                        .then(argument("index", integer(1)).executes(AnnoCommand::teleport)))
                .then(literal("remove")
                        .then(argument("index", integer(1)).executes(AnnoCommand::remove)))
                .then(literal("add")
                        .then(argument("text", greedyString()).executes(AnnoCommand::add)));
        dispatcher.register(anno);
    }

    private static int help(CommandContext<ServerCommandSource> context)
    {
        int cmdCount = 0;
        CommandDispatcher<ServerCommandSource> dispatcher = Command.commandDispatcher;
        for (CommandNode<ServerCommandSource> command : dispatcher.getRoot().getChildren())
        {
            String cmdName = command.getName();
            if (ClientCommandManager.isClientSideCommand(cmdName))
            {
                Map<CommandNode<ServerCommandSource>, String> usage = dispatcher.getSmartUsage(command, context.getSource());
                for (String u : usage.values())
                {
                    ClientCommandManager.sendFeedback(new LiteralText("/" + cmdName + " " + u));
                }
                cmdCount += usage.size();
                if (usage.size() == 0)
                {
                    ClientCommandManager.sendFeedback(new LiteralText("/" + cmdName));
                    cmdCount++;
                }
            }
        }
        return cmdCount;
    }

    private static int list(CommandContext<ServerCommandSource> context)
    {
        BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
        ArrayList<Annotation> annotations = edits.getAnnotations();

        localOutputT(context.getSource(), "watson.message.anno.list.size", annotations.size());
        int index = 1;
        for (Annotation annotation : annotations)
        {
            localOutputT(context.getSource(), "watson.message.anno.annot", index, annotation.getX(), annotation.getY(), annotation.getZ(), annotation.getWorld(), annotation.getText());
            ++index;
        }
        return 1;
    }

    private static int clear(CommandContext<ServerCommandSource> context)
    {
        BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
        ArrayList<Annotation> annotations = edits.getAnnotations();

        InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.anno.clear", annotations.size());
        annotations.clear();
        return 1;
    }

    private static int teleport(CommandContext<ServerCommandSource> context)
    {
        BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
        ArrayList<Annotation> annotations = edits.getAnnotations();

        int index = getInteger(context, "index");
        if (index < annotations.size())
        {
            Annotation annotation = annotations.get(index);
            Teleport.teleport(annotation.getX(), annotation.getY(), annotation.getZ());
        }
        else
        {
            localErrorT(context.getSource(), "watson.error.anno.out_range");
        }
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> context)
    {
        BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
        ArrayList<Annotation> annotations = edits.getAnnotations();

        int index = getInteger(context, "index") - 1;
        if (index < annotations.size())
        {
            annotations.remove(index);
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.anno.remove", index + 1);
        }
        else
        {
            localErrorT(context.getSource(), "watson.error.anno.out_range");
        }
        return 1;
    }

    private static int add(CommandContext<ServerCommandSource> context)
    {
        HashMap<String, Object> vars = DataManager.getEditSelection().getVariables();
        Integer x = (Integer) vars.get("x");
        Integer y = (Integer) vars.get("y");
        Integer z = (Integer) vars.get("z");
        String world = (String) vars.get("world");
        if (x == null || y == null || z == null || world == null)
        {
            InfoUtils.showInGameMessage(MessageType.ERROR, "watson.error.anno.position");
        }
        else
        {
            BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
            ArrayList<Annotation> annotations = edits.getAnnotations();

            String text = getString(context, "text");
            Annotation annotation = new Annotation(x, y, z, world, text);
            annotations.add(annotation);
            localOutputT(context.getSource(), "watson.message.anno.annot", annotations.size(), annotation.getX(), annotation.getY(), annotation.getZ(), annotation.getWorld(), annotation.getText());
        }
        return 1;
    }
}
