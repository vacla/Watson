package eu.minemania.watson.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.minemania.watson.client.Teleport;
import org.lwjgl.opengl.GL11;

import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.render.OverlayRenderer;
import eu.minemania.watson.selection.EditSelection;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

public class BlockEditSet
{
    protected LinkedHashMap<String, PlayereditSet> playerEdits = new LinkedHashMap<>();
    protected ArrayList<Annotation> annotations = new ArrayList<>();
    protected OreDB oreDB = new OreDB();
    protected int tpIndexAnno = 0;

    public synchronized int load(File file) throws Exception
    {

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            Pattern editPattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})\\|(\\d{2}):(\\d{2}):(\\d{2})\\|(\\w+)\\|(\\w+)\\|(minecraft:\\w+)\\|(-?\\d+)\\|(\\d+)\\|(-?\\d+)\\|(\\w+)\\|(\\d+)");
            Pattern annoPattern = Pattern.compile("#(-?\\d+)\\|(\\d+)\\|(-?\\d+)\\|(\\w+)\\|(.*)");
            Calendar time = Calendar.getInstance();
            String line;
            int edits = 0;
            BlockEdit blockEdit = null;
            while ((line = reader.readLine()) != null)
            {
                Matcher edit = editPattern.matcher(line);
                if (edit.matches())
                {
                    int year = Integer.parseInt(edit.group(1));
                    int month = Integer.parseInt(edit.group(2)) - 1;
                    int day = Integer.parseInt(edit.group(3));
                    int hour = Integer.parseInt(edit.group(4));
                    int minute = Integer.parseInt(edit.group(5));
                    int second = Integer.parseInt(edit.group(6));
                    time.set(year, month, day, hour, minute, second);

                    String player = edit.group(7);
                    String action = edit.group(8);
                    String blockName = edit.group(9);
                    int x = Integer.parseInt(edit.group(10));
                    int y = Integer.parseInt(edit.group(11));
                    int z = Integer.parseInt(edit.group(12));
                    String world = edit.group(13);
                    int amount = Integer.parseInt(edit.group(14));

                    WatsonBlock watsonBlock = WatsonBlockRegistery.getInstance().getWatsonBlockByName(blockName);
                    blockEdit = new BlockEdit(time.getTimeInMillis(), player, action, x, y, z, watsonBlock, world, amount);
                    addBlockEdit(blockEdit);
                    ++edits;
                }
                else
                {
                    Matcher anno = annoPattern.matcher(line);
                    if (anno.matches())
                    {
                        int x = Integer.parseInt(anno.group(1));
                        int y = Integer.parseInt(anno.group(2));
                        int z = Integer.parseInt(anno.group(3));
                        String world = anno.group(4);
                        String text = anno.group(5);
                        annotations.add(new Annotation(x, y, z, world, text));
                    }
                }
            }

            if (blockEdit != null)
            {
                EditSelection selection = DataManager.getEditSelection();
                if (selection != null)
                {
                    selection.selectBlockEdit(blockEdit);
                }
            }

            return edits;
        }
    }

    public synchronized int save(File file) throws IOException
    {

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file))))
        {
            int editCount = 0;
            for (PlayereditSet editsForPlayer : playerEdits.values())
            {
                editCount += editsForPlayer.save(writer);
            }

            for (Annotation annotation : annotations)
            {
                writer.format("#%d|%d|%d|%s|%s\n", annotation.getX(), annotation.getY(), annotation.getZ(), annotation.getWorld(), annotation.getText());
            }
            return editCount;
        }
    }

    public synchronized void clear()
    {
        playerEdits.clear();
        annotations.clear();
        oreDB.clear();
    }

    public synchronized BlockEdit findEdit(int x, int y, int z, String player)
    {
        if (player != null)
        {
            PlayereditSet editsForPlayer = playerEdits.get(player.toLowerCase());
            return editsForPlayer != null ? editsForPlayer.findEdit(x, y, z) : null;
        }
        else
        {
            for (PlayereditSet editsForPlayer : playerEdits.values())
            {
                BlockEdit edit = editsForPlayer.findEdit(x, y, z);
                if (edit != null)
                {
                    return edit;
                }
            }

            return null;
        }
    }

    public synchronized boolean addBlockEdit(BlockEdit edit)
    {
        return addBlockEdit(edit, true);
    }

    public synchronized boolean addBlockEdit(BlockEdit edit, boolean updateVariables)
    {
        if (DataManager.getFilters().isAcceptedPlayer(edit.player))
        {
            if (updateVariables)
            {
                EditSelection selection = DataManager.getEditSelection();
                selection.selectBlockEdit(edit);
            }
            String lowerName = edit.player.toLowerCase();
            PlayereditSet editsForPlayer = playerEdits.get(lowerName);
            if (editsForPlayer == null)
            {
                editsForPlayer = new PlayereditSet(edit.player);
                playerEdits.put(lowerName, editsForPlayer);
            }

            editsForPlayer.addBlockEdit(edit);
            if (Configs.Generic.GROUPING_ORES_IN_CREATIVE.getBooleanValue())
            {
                oreDB.addBlockEdit(edit);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public synchronized void listEdits()
    {
        if (playerEdits.size() == 0)
        {
            ChatMessage.localOutputT("watson.message.edits.none_world");
        }
        else
        {
            ChatMessage.localOutputT("watson.message.edits.edits_list");
            for (PlayereditSet editsByPlayer : playerEdits.values())
            {
                ChatMessage.localOutputT("watson.message.edits.edit", editsByPlayer.getPlayer(), editsByPlayer.getBlockEditCount(), StringUtils.translate(editsByPlayer.isVisible() ? "watson.message.setting.shown" : "watson.message.setting.hidden"));
            }
        }
    }

    public synchronized void setEditVisibility(String player, boolean visible)
    {
        player = player.toLowerCase();
        PlayereditSet editsByPlayer = playerEdits.get(player);
        if (editsByPlayer != null)
        {
            editsByPlayer.setVisible(visible);
            ChatMessage.localOutputT("watson.message.edits.visibility", editsByPlayer.getBlockEditCount(), editsByPlayer.getPlayer(), StringUtils.translate(editsByPlayer.isVisible() ? "watson.message.setting.shown" : "watson.message.setting.hidden"));
        }
        else
        {
            ChatMessage.localErrorT("watson.message.edits.none_edits", player);
        }
    }

    public synchronized void removeEdits(String player)
    {
        player = player.toLowerCase();
        PlayereditSet editsByPlayer = playerEdits.get(player);
        if (editsByPlayer != null)
        {
            playerEdits.remove(player.toLowerCase());
            getOreDB().removeDeposits(player);
            EditSelection edit = DataManager.getEditSelection();
            if (edit.getSelection() != null)
            {
                boolean selection = edit.getSelection().playereditSet == editsByPlayer;

                ChatMessage.localOutputT("watson.message.edits.edit_removed", editsByPlayer.getBlockEditCount(), editsByPlayer.getPlayer());
                if (playerEdits.isEmpty() || selection)
                {
                    DataManager.getEditSelection().clearSelection();
                }
            }
        }
        else
        {
            ChatMessage.localErrorT("watson.message.edits.none_edits", player);
        }
    }

    public synchronized void drawOutlines()
    {
        if (Configs.Generic.OUTLINE_SHOWN.getBooleanValue())
        {
            for (PlayereditSet editsForPlayer : playerEdits.values())
            {
                editsForPlayer.drawOutlines();
            }
        }
    }

    public synchronized void drawVectors()
    {
        if (Configs.Generic.VECTOR_SHOWN.getBooleanValue())
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
            int nextColorIndex1 = 0;
            for (PlayereditSet editsForPlayer : playerEdits.values())
            {
                editsForPlayer.drawVectors(OverlayRenderer.KELLY_COLORS[nextColorIndex1], buffer);
                nextColorIndex1 = (nextColorIndex1 + 1) % OverlayRenderer.KELLY_COLORS.length;
            }
            tessellator.draw();
        }
    }

    public synchronized void drawAnnotations(double dx, double dy, double dz)
    {
        if (Configs.Generic.ANNOTATION_SHOWN.getBooleanValue() && !annotations.isEmpty())
        {
            for (Annotation annotation : annotations)
            {
                annotation.draw(dx, dy, dz);
            }
        }
    }

    public ArrayList<Annotation> getAnnotations()
    {
        return annotations;
    }

    public OreDB getOreDB()
    {
        return oreDB;
    }

    public LinkedHashMap<String, PlayereditSet> getPlayereditSet()
    {
        return playerEdits;
    }

    public void tpNextAnno()
    {
        tpIndexAnno(tpIndexAnno + 1);
    }

    public void tpPrevAnno()
    {
        tpIndexAnno(tpIndexAnno - 1);
    }

    public void tpIndexAnno(int index)
    {
        if (annotations.isEmpty())
        {
            ChatMessage.localErrorT("watson.error.anno.out_range");
        }
        else
        {
            if (index < 1)
            {
                index = annotations.size();
            }
            else if (index > annotations.size())
            {
                index = 1;
            }
            tpIndexAnno = index;
            Annotation annotation = getAnnotations().get(index - 1);
            Teleport.teleport(annotation.getX(), annotation.getY(), annotation.getZ(), annotation.getWorld());
            ChatMessage.localOutputT("watson.message.anno.teleport", index);
        }
    }
}