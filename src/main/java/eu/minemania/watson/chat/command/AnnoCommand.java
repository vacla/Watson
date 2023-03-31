package eu.minemania.watson.chat.command;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;
import static net.minecraft.command.argument.BlockPosArgumentType.getBlockPos;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.Annotation;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.db.LocalAnnotation;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class AnnoCommand extends WatsonCommandBase
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ClientCommandManager.addClientSideCommand("anno");
        LiteralArgumentBuilder<ServerCommandSource> anno = literal("anno").executes(AnnoCommand::help)
                .then(literal("help").executes(AnnoCommand::help))
                .then(literal("list").executes(AnnoCommand::list)
                        .then(literal("local").executes(AnnoCommand::listLocal)))
                .then(literal("clear").executes(AnnoCommand::clear)
                        .then(literal("local").executes(AnnoCommand::clearLocal)))
                .then(literal("tp")
                        .then(literal("next").executes(AnnoCommand::teleportNext)
                                .then(literal("local").executes(AnnoCommand::teleportNextLocal)))
                        .then(literal("previous").executes(AnnoCommand::teleportPrev)
                                .then(literal("local").executes(AnnoCommand::teleportPrevLocal)))
                        .then(argument("index", integer()).executes(AnnoCommand::teleport)
                                .then(literal("local").executes(AnnoCommand::teleportLocal))))
                .then(literal("remove")
                        .then(argument("index", integer(1)).executes(AnnoCommand::remove)
                                .then(literal("local").executes(AnnoCommand::removeLocal))))
                .then(literal("add")
                        .then(argument("text", greedyString()).executes(AnnoCommand::add))
                        .then(literal("local").executes(AnnoCommand::addLocal)
                                .then(argument("pos", blockPos())
                                        .then(argument("world", word())
                                                .then(argument("text", greedyString()).executes(AnnoCommand::addLocal))))));
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
                    ClientCommandManager.sendFeedback(Text.literal("/" + cmdName + " " + u));
                }
                cmdCount += usage.size();
                if (usage.size() == 0)
                {
                    ClientCommandManager.sendFeedback(Text.literal("/" + cmdName));
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

    private static int listLocal(CommandContext<ServerCommandSource> context)
    {
        ArrayList<Annotation> annotations = LocalAnnotation.getInstance().getAnnotations();

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

    private static int clearLocal(CommandContext<ServerCommandSource> context)
    {
        ArrayList<Annotation> annotations = LocalAnnotation.getInstance().getAnnotations();

        InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.anno.clear", annotations.size());
        annotations.clear();
        return 1;
    }

    private static int teleportNext(CommandContext<ServerCommandSource> context)
    {
        DataManager.getEditSelection().getBlockEditSet().tpNextAnno();
        return 1;
    }

    private static int teleportNextLocal(CommandContext<ServerCommandSource> context)
    {
        LocalAnnotation.getInstance().tpNextAnno();
        return 1;
    }

    private static int teleportPrev(CommandContext<ServerCommandSource> context)
    {
        DataManager.getEditSelection().getBlockEditSet().tpPrevAnno();
        return 1;
    }

    private static int teleportPrevLocal(CommandContext<ServerCommandSource> context)
    {
        LocalAnnotation.getInstance().tpPrevAnno();
        return 1;
    }

    private static int teleport(CommandContext<ServerCommandSource> context)
    {
        int index = getInteger(context, "index");
        DataManager.getEditSelection().getBlockEditSet().tpIndexAnno(index);
        return 1;
    }

    private static int teleportLocal(CommandContext<ServerCommandSource> context)
    {
        int index = getInteger(context, "index");
        LocalAnnotation.getInstance().tpIndexAnno(index);
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

    private static int removeLocal(CommandContext<ServerCommandSource> context)
    {
        ArrayList<Annotation> annotations = LocalAnnotation.getInstance().getAnnotations();

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

    private static int addLocal(CommandContext<ServerCommandSource> context)
    {
        BlockPos blockPos = BlockPos.ORIGIN;
        String world = null;

        try
        {
            blockPos = getBlockPos(context, "pos");
            world = getString(context, "world");
        }
        catch (CommandSyntaxException ignored)
        {
        }

        if (blockPos == BlockPos.ORIGIN || world == null)
        {
            InfoUtils.showInGameMessage(MessageType.ERROR, "watson.error.anno.position");
        }
        else
        {
            ArrayList<Annotation> annotations = LocalAnnotation.getInstance().getAnnotations();

            String text = getString(context, "text");
            Annotation annotation = new Annotation(blockPos.getX(), blockPos.getY(), blockPos.getZ(), world, text);
            annotations.add(annotation);
            localOutputT(context.getSource(), "watson.message.anno.annot", annotations.size(), annotation.getX(), annotation.getY(), annotation.getZ(), annotation.getWorld(), annotation.getText());
        }
        return 1;
    }
}
