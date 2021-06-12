package eu.minemania.watson.db;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeSet;

import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.render.RenderUtils;
import eu.minemania.watson.selection.PlayereditUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import eu.minemania.watson.config.Configs;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.util.math.Vec3d;

public class PlayereditSet
{
    protected String _player;
    protected TreeSet<BlockEdit> _edits = new TreeSet<>(new BlockEditComparator());
    protected boolean _visible = true;
    protected static final double UNIT_VECTOR_ARROW_SIZE = 0.025;
    protected static final double MAX_ARROW_SIZE = 0.5;

    public PlayereditSet(String player)
    {
        _player = player;
    }

    public TreeSet<BlockEdit> getBlockEdits()
    {
        return _edits;
    }

    public String getPlayer()
    {
        return _player;
    }

    public synchronized BlockEdit findEdit(int x, int y, int z)
    {
        for (BlockEdit edit : _edits)
        {
            if (edit.x == x && edit.y == y && edit.z == z)
            {
                return edit;
            }
        }
        return null;
    }

    public synchronized BlockEdit getEditBefore(BlockEdit edit)
    {
        return _edits.lower(edit);
    }

    public synchronized BlockEdit getEditAfter(BlockEdit edit)
    {
        return _edits.higher(edit);
    }

    public synchronized void addBlockEdit(BlockEdit edit)
    {
        _edits.add(edit);
        edit.playereditSet = this;
    }

    public synchronized int getBlockEditCount()
    {
        int totalEdits = 0;
        for (BlockEdit edit : _edits) {
            totalEdits += (int)PlayereditUtils.getInstance().getRevertAction(edit, 0, 1);
        }
        return totalEdits;
    }

    public void setVisible(boolean visible)
    {
        _visible = visible;
    }

    public boolean isVisible()
    {
        return _visible;
    }

    public synchronized void drawOutlines(MatrixStack matrices)
    {
        if (isVisible())
        {
            for (BlockEdit edit : _edits)
            {
                if (DataManager.getWorldPlugin().isEmpty() || DataManager.getWorldPlugin().equals(edit.world))
                {
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();
                    RenderUtils.startDrawingLines(buffer);

                    PlayereditUtils.getInstance().getRevertAction(edit, null, edit.drawOutline(buffer, matrices));

                    tessellator.draw();
                }
            }
        }
    }

    public synchronized void drawVectors(int intcolor, BufferBuilder buffer)
    {
        if (Configs.Edits.VECTOR_SHOWN.getBooleanValue() && isVisible() && !_edits.isEmpty())
        {
            Color4f color = Color4f.fromColor(intcolor, 1f);

            Vec3d unitX = new Vec3d(1, 0, 0);
            Vec3d unitY = new Vec3d(0, 1, 0);

            Iterator<BlockEdit> it = _edits.iterator();
            if (it.hasNext())
            {
                BlockEdit prev = it.next();
                if (!DataManager.getWorldPlugin().isEmpty() && !DataManager.getWorldPlugin().equals(prev.world))
                {
                    while (it.hasNext() && !DataManager.getWorldPlugin().equals(prev.world))
                    {
                        prev = it.next();
                    }
                }
                while (it.hasNext())
                {
                    BlockEdit next = it.next();
                    if (!DataManager.getWorldPlugin().isEmpty() && !DataManager.getWorldPlugin().equals(next.world))
                    {
                        while (it.hasNext() && !DataManager.getWorldPlugin().equals(next.world))
                        {
                            next = it.next();
                        }
                        if (!DataManager.getWorldPlugin().equals(next.world))
                        {
                            return;
                        }
                    }
                    boolean show = (next.isCreated() && Configs.Edits.LINKED_CREATION.getBooleanValue()) || (!next.isCreated() && Configs.Edits.LINKED_DESTRUCTION.getBooleanValue());
                    if (show)
                    {
                        Vec3d pPos = new Vec3d(prev.x + 0.5, prev.y + 0.5, prev.z + 0.5);
                        Vec3d nPos = new Vec3d(next.x + 0.5, next.y + 0.5, next.z + 0.5);
                        //vector difference, from prev to next
                        Vec3d diff = nPos.subtract(pPos);
                        // Compute length. We want to scale the arrow heads by the length, so can't avoid the sqrt() here
                        double length = diff.length();
                        if (length >= (float) Configs.Edits.VECTOR_LENGTH.getDoubleValue())
                        {
                            buffer.vertex(pPos.x, pPos.y, pPos.z).color(color.r, color.g, color.b, color.a).next();
                            buffer.vertex(nPos.x, nPos.y, nPos.z).color(color.r, color.g, color.b, color.a).next();

                            // Length from arrow tip to midpoint of vector as a fraction of
                            // the total vector length. Scale the arrow in proportion to the
                            // square root of the length up to a maximum size.
                            double arrowSize = UNIT_VECTOR_ARROW_SIZE * Math.sqrt(length);
                            if (arrowSize > MAX_ARROW_SIZE)
                            {
                                arrowSize = MAX_ARROW_SIZE;
                            }
                            double arrowScale = arrowSize / length;
                            // Position of the tip and tail of the arrow, sitting in the
                            // middle of the vector.
                            Vec3d tip = new Vec3d(pPos.x * (0.5 - arrowScale) + nPos.x * (0.5 + arrowScale), pPos.y * (0.5 - arrowScale) + nPos.y * (0.5 + arrowScale), pPos.z * (0.5 - arrowScale) + nPos.z * (0.5 + arrowScale));
                            Vec3d tail = new Vec3d(pPos.x * (0.5 + arrowScale) + nPos.x * (0.5 - arrowScale), pPos.y * (0.5 + arrowScale) + nPos.y * (0.5 - arrowScale), pPos.z * (0.5 + arrowScale) + nPos.z * (0.5 - arrowScale));
                            // Fin axes, perpendicular to vector. Scale by vector length.
                            // If the vector is colinear with the Y axis, use the X axis for
                            // the cross products to derive the fin directions.
                            Vec3d fin1;
                            if (Math.abs(unitY.dotProduct(diff)) > 0.9 * length)
                            {
                                fin1 = unitX.crossProduct(diff).normalize();
                            }
                            else
                            {
                                fin1 = unitY.crossProduct(diff).normalize();
                            }

                            Vec3d fin2 = fin1.crossProduct(diff).normalize();
                            Vec3d draw1 = new Vec3d(fin1.x * arrowScale * length, fin1.y * arrowScale * length, fin1.z * arrowScale * length);
                            Vec3d draw2 = new Vec3d(fin2.x * arrowScale * length, fin2.y * arrowScale * length, fin2.z * arrowScale * length);
                            // Draw four fins
                            buffer.vertex(tip.x, tip.y, tip.z).color(color.r, color.g, color.b, color.a).next();
                            buffer.vertex(tail.x + draw1.x, tail.y + draw1.y, tail.z + draw1.z).color(color.r, color.g, color.b, color.a).next();
                            buffer.vertex(tip.x, tip.y, tip.z).color(color.r, color.g, color.b, color.a).next();
                            buffer.vertex(tail.x - draw1.x, tail.y - draw1.y, tail.z - draw1.z).color(color.r, color.g, color.b, color.a).next();
                            buffer.vertex(tip.x, tip.y, tip.z).color(color.r, color.g, color.b, color.a).next();
                            buffer.vertex(tail.x + draw2.x, tail.y + draw2.y, tail.z + draw2.z).color(color.r, color.g, color.b, color.a).next();
                            buffer.vertex(tip.x, tip.y, tip.z).color(color.r, color.g, color.b, color.a).next();
                            buffer.vertex(tail.x - draw2.x, tail.y - draw2.y, tail.z - draw2.z).color(color.r, color.g, color.b, color.a).next();
                        }
                        prev = next;
                    }
                }
            }
        }
    }

    public synchronized int save(PrintWriter writer)
    {
        Calendar calendar = Calendar.getInstance();
        int editCount = 0;
        for (BlockEdit edit : _edits)
        {
            calendar.setTimeInMillis(edit.time);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String action = edit.action;
            writer.format("%4d-%02d-%02d|%02d:%02d:%02d|%s|%s|%s|%d|%d|%d|%s|%d\n", year, month, day, hour, minute, second, edit.player, action, edit.block.getName(), edit.x, edit.y, edit.z, edit.world, edit.amount);
            ++editCount;
        }
        return editCount;
    }
}