package eu.minemania.watson.chat.command;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static dev.xpple.clientarguments.arguments.CBlockPosArgumentType.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.Annotation;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.db.LocalAnnotation;
import malilib.overlay.message.MessageDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.math.BlockPos;

public class AnnoCommand extends WatsonCommandBase
{
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        LiteralCommandNode<FabricClientCommandSource> anno = dispatcher.register(literal("anno"));
        dispatcher.register(literal("anno")
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
                                                .then(argument("text", greedyString()).executes(AnnoCommand::addLocal)))))));
    }

    private static int list(CommandContext<FabricClientCommandSource> context)
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

    private static int listLocal(CommandContext<FabricClientCommandSource> context)
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

    private static int clear(CommandContext<FabricClientCommandSource> context)
    {
        BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
        ArrayList<Annotation> annotations = edits.getAnnotations();

        MessageDispatcher.generic("watson.message.anno.clear", annotations.size());
        annotations.clear();
        return 1;
    }

    private static int clearLocal(CommandContext<FabricClientCommandSource> context)
    {
        ArrayList<Annotation> annotations = LocalAnnotation.getInstance().getAnnotations();

        MessageDispatcher.generic("watson.message.anno.clear", annotations.size());
        annotations.clear();
        return 1;
    }

    private static int teleportNext(CommandContext<FabricClientCommandSource> context)
    {
        DataManager.getEditSelection().getBlockEditSet().tpNextAnno();
        return 1;
    }

    private static int teleportNextLocal(CommandContext<FabricClientCommandSource> context)
    {
        LocalAnnotation.getInstance().tpNextAnno();
        return 1;
    }

    private static int teleportPrev(CommandContext<FabricClientCommandSource> context)
    {
        DataManager.getEditSelection().getBlockEditSet().tpPrevAnno();
        return 1;
    }

    private static int teleportPrevLocal(CommandContext<FabricClientCommandSource> context)
    {
        LocalAnnotation.getInstance().tpPrevAnno();
        return 1;
    }

    private static int teleport(CommandContext<FabricClientCommandSource> context)
    {
        int index = getInteger(context, "index");
        DataManager.getEditSelection().getBlockEditSet().tpIndexAnno(index);
        return 1;
    }

    private static int teleportLocal(CommandContext<FabricClientCommandSource> context)
    {
        int index = getInteger(context, "index");
        LocalAnnotation.getInstance().tpIndexAnno(index);
        return 1;
    }

    private static int remove(CommandContext<FabricClientCommandSource> context)
    {
        BlockEditSet edits = DataManager.getEditSelection().getBlockEditSet();
        ArrayList<Annotation> annotations = edits.getAnnotations();

        int index = getInteger(context, "index") - 1;
        if (index < annotations.size())
        {
            annotations.remove(index);
            MessageDispatcher.generic("watson.message.anno.remove", index + 1);
        }
        else
        {
            localErrorT(context.getSource(), "watson.error.anno.out_range");
        }
        return 1;
    }

    private static int removeLocal(CommandContext<FabricClientCommandSource> context)
    {
        ArrayList<Annotation> annotations = LocalAnnotation.getInstance().getAnnotations();

        int index = getInteger(context, "index") - 1;
        if (index < annotations.size())
        {
            annotations.remove(index);
            MessageDispatcher.generic("watson.message.anno.remove", index + 1);
        }
        else
        {
            localErrorT(context.getSource(), "watson.error.anno.out_range");
        }
        return 1;
    }

    private static int add(CommandContext<FabricClientCommandSource> context)
    {
        HashMap<String, Object> vars = DataManager.getEditSelection().getVariables();
        Integer x = (Integer) vars.get("x");
        Integer y = (Integer) vars.get("y");
        Integer z = (Integer) vars.get("z");
        String world = (String) vars.get("world");
        if (x == null || y == null || z == null || world == null)
        {
            MessageDispatcher.error("watson.error.anno.position");
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

    private static int addLocal(CommandContext<FabricClientCommandSource> context)
    {
        BlockPos blockPos = BlockPos.ORIGIN;
        String world = null;

        try
        {
            blockPos = getCBlockPos(context, "pos");
            world = getString(context, "world");
        }
        catch (CommandSyntaxException ignored)
        {
        }

        if (blockPos == BlockPos.ORIGIN || world == null)
        {
            MessageDispatcher.error("watson.error.anno.position");
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
