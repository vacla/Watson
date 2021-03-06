package eu.minemania.watson.selection;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.BlockEditSet;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class EditSelection
{
    protected boolean _selectionChanged;
    protected BlockEdit _selection;
    protected HashMap<String, Object> _variables = new HashMap<>();
    protected static HashMap<String, BlockEditSet> _edits = new HashMap<String, BlockEditSet>();
    protected Calendar _calendar = Calendar.getInstance();

    public HashMap<String, Object> getVariables()
    {
        return _variables;
    }

    public BlockEdit getSelection()
    {
        return _selection;
    }

    public void selectBlockEdit(BlockEdit edit)
    {
        if (edit != null)
        {
            _selection = edit;

            _variables.put("time", edit.time);
            _variables.put("player", edit.player);
            _variables.put("block", edit.block.getName());
            _variables.put("creation", edit.creation);
            _variables.put("world", edit.world);

            // Will also dispatch the onWatsonSelection Macro/Keybind event:
            selectPosition(edit.x, edit.y, edit.z, edit.world);
        }
    }

    public void clearBlockEditSet()
    {
        getBlockEditSet().clear();
        _variables.clear();
        _selectionChanged = true;
        _selection = null;
        ChatMessage.localOutput("Watson edits cleared", true);
        DataManager.getFilters().clear();
    }

    public void selectPosition(int x, int y, int z, String world)
    {
        if(_selection == null || _selection.x != x || _selection.y != y || _selection.z != z || _selection.world != world)
        {
            _selection = new BlockEdit(0, "", false, x, y, z, null, world);
        }

        _variables.put("x", x);
        _variables.put("y", y);
        _variables.put("z", z);
        _variables.put("world", world);
        _selectionChanged = true;
    }

    public BlockEditSet getBlockEditSet()
    {
        Minecraft mc = Minecraft.getInstance();
        StringBuilder idBuilder = new StringBuilder();
        String serverIP = DataManager.getServerIP();
        if(serverIP != null)
        {
            idBuilder.append(serverIP);
        }
        idBuilder.append('/');
        idBuilder.append(mc.player.dimension);
        String id = idBuilder.toString();

        BlockEditSet edits = _edits.get(id);
        if(edits == null)
        {
            edits = new BlockEditSet();
            _edits.put(id, edits);
        }
        return edits;
    }

    public void drawSelection()
    {
        if(_selection != null && Configs.Generic.SELECTION_SHOWN.getBooleanValue())
        {
            Tessellator tesselator = Tessellator.getInstance();
            BufferBuilder buffer = tesselator.getBuffer();
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            GL11.glLineWidth(4.0f);

            final float halfSize = 0.3f;
            float x = _selection.x + 0.5f;
            float y = _selection.y + 0.5f;
            float z = _selection.z + 0.5f;
            buffer.pos(x - halfSize, y, z).color(255/255f, 0/255f, 255/255f, 128).endVertex();
            buffer.pos(x + halfSize, y, z).color(255/255f, 0/255f, 255/255f, 128).endVertex();
            buffer.pos(x, y - halfSize, z).color(255/255f, 0/255f, 255/255f, 128).endVertex();
            buffer.pos(x, y + halfSize, z).color(255/255f, 0/255f, 255/255f, 128).endVertex();
            buffer.pos(x, y, z - halfSize).color(255/255f, 0/255f, 255/255f, 128).endVertex();
            buffer.pos(x, y, z + halfSize).color(255/255f, 0/255f, 255/255f, 128).endVertex();
            tesselator.draw();

            if(_selection.playereditSet != null)
            {
                BlockEdit previous = _selection.playereditSet.getEditBefore(_selection);
                if(previous != null)
                {
                    buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    GL11.glEnable(GL11.GL_LINE_STIPPLE);
                    GL11.glLineStipple(8, (short) 0xAAAA);
                    GL11.glLineWidth(3.0f);
                    buffer.pos(previous.x + 0.5f, previous.y + 0.5f, previous.z + 0.5f).color(255/255f, 0/255f, 255/255f, 128).endVertex();
                    buffer.pos(x, y, z).color(255/255f, 0/255f, 255/255f, 128).endVertex();
                    tesselator.draw();
                    GL11.glDisable(GL11.GL_LINE_STIPPLE);
                }
            }
        }
    }

    public boolean isSelectionChanged()
    {
        boolean result = _selectionChanged;
        _selectionChanged = false;
        return result;
    }

    public void queryPreEdits(int count)
    {
        if(_variables.containsKey("player") && _variables.containsKey("time"))
        {
            if(Configs.Generic.PLUGIN.getStringValue().equals("LogBlock"))
            {
                _calendar.setTimeInMillis((Long) _variables.get("time"));
                int day = _calendar.get(Calendar.DAY_OF_MONTH);
                int month = _calendar.get(Calendar.MONTH) + 1;
                int year = _calendar.get(Calendar.YEAR);
                int hour = _calendar.get(Calendar.HOUR_OF_DAY);
                int minute = _calendar.get(Calendar.MINUTE);
                int second = _calendar.get(Calendar.SECOND);
                String player = (String) _variables.get("player");

                String query = String.format(Locale.US, "/lb before %d.%d.%d %02d:%02d:%02d player %s coords limit %d", day, month, year, hour, minute, second, player, count);
                Watson.logger.debug(query);
                ChatMessage.sendToServerChat(query);
            }
            else
            {
                InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.info.no_logblock");
            }
        }
        else
        {
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.info.no_player_time");
        }
    }

    public void queryPostEdits(int count)
    {
        if(_variables.containsKey("player") && _variables.containsKey("time"))
        {
            if(Configs.Generic.PLUGIN.getStringValue().equals("LogBlock"))
            {
                _calendar.setTimeInMillis((Long) _variables.get("time"));
                int day = _calendar.get(Calendar.DAY_OF_MONTH);
                int month = _calendar.get(Calendar.MONTH) + 1;
                int year = _calendar.get(Calendar.YEAR);
                int hour = _calendar.get(Calendar.HOUR_OF_DAY);
                int minute = _calendar.get(Calendar.MINUTE);
                int second = _calendar.get(Calendar.SECOND);
                String player = (String) _variables.get("player");

                String query = String.format(Locale.US, "/lb since %d.%d.%d %02d:%02d:%02d player %s coords limit %d asc", day, month, year, hour, minute, second, player, count);
                Watson.logger.debug(query);
                ChatMessage.sendToServerChat(query);
            }
            else
            {
                InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.info.no_logblock");
            }
        }
        else
        {
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.info.no_player_time");
        }
    }
}