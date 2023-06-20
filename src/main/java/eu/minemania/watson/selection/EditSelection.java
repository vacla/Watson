package eu.minemania.watson.selection;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.config.Plugins;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.BlockEditComparator;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.render.RenderUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

public class EditSelection
{
    protected boolean _selectionChanged;
    protected BlockEdit _selection;
    protected HashMap<String, Object> _variables = new HashMap<>();
    protected static HashMap<String, BlockEditSet> _edits = new HashMap<>();
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
            _variables.put("action", edit.action);
            _variables.put("world", edit.world);

            // Will also dispatch the onWatsonSelection Macro/Keybind event:
            selectPosition(edit.x, edit.y, edit.z, edit.world, edit.amount);
        }
    }

    public void clearBlockEditSet()
    {
        getBlockEditSet().clear();
        _variables.clear();
        _selectionChanged = true;
        _selection = null;
        ChatMessage.localOutputT("watson.message.filters.edits_clear");
        DataManager.getFilters().clear();
    }

    public void clearSelection()
    {
        _variables.clear();
        _selectionChanged = true;
        _selection = null;
    }

    public void selectPosition(int x, int y, int z, String world, int amount)
    {
        if (_selection == null || _selection.x != x || _selection.y != y || _selection.z != z || !_selection.world.equals(world) || _selection.amount != amount)
        {
            _selection = new BlockEdit(0, "", "selection", x, y, z, null, world, amount);
        }

        _variables.put("x", x);
        _variables.put("y", y);
        _variables.put("z", z);
        _variables.put("world", world);
        _selectionChanged = true;
    }

    public BlockEditSet getBlockEditSet()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null)
        {
            return null;
        }
        StringBuilder idBuilder = new StringBuilder();
        String serverIP = DataManager.getServerIP();
        if (serverIP != null)
        {
            idBuilder.append(serverIP);
        }
        idBuilder.append('/');
        idBuilder.append(WorldUtils.getDimensionId(player.getWorld()));
        String id = idBuilder.toString();

        BlockEditSet edits = _edits.get(id);
        if (edits == null)
        {
            edits = new BlockEditSet();
            _edits.put(id, edits);
        }
        return edits;
    }

    public void drawSelection(MatrixStack matrixStack)
    {
        if (_selection != null && Configs.Edits.SELECTION_SHOWN.getBooleanValue() && (DataManager.getWorldPlugin().isEmpty() || DataManager.getWorldPlugin().equals(_selection.world)))
        {
            Tessellator tesselator = Tessellator.getInstance();
            BufferBuilder buffer = tesselator.getBuffer();
            RenderUtils.startDrawingLines(buffer);
            RenderSystem.lineWidth(4.0F);

            final float halfSize = 0.3f;
            float x = _selection.x + 0.5f;
            float y = _selection.y + 0.5f;
            float z = _selection.z + 0.5f;
            buffer.vertex(x - halfSize, y, z).color(255 / 255f, 0 / 255f, 255 / 255f, 128).normal(0, 0, 0).next();
            buffer.vertex(x + halfSize, y, z).color(255 / 255f, 0 / 255f, 255 / 255f, 128).normal(0, 0, 0).next();
            buffer.vertex(x, y - halfSize, z).color(255 / 255f, 0 / 255f, 255 / 255f, 128).normal(0, 0, 0).next();
            buffer.vertex(x, y + halfSize, z).color(255 / 255f, 0 / 255f, 255 / 255f, 128).normal(0, 0, 0).next();
            buffer.vertex(x, y, z - halfSize).color(255 / 255f, 0 / 255f, 255 / 255f, 128).normal(0, 0, 0).next();
            buffer.vertex(x, y, z + halfSize).color(255 / 255f, 0 / 255f, 255 / 255f, 128).normal(0, 0, 0).next();
            tesselator.draw();

            if (_selection.playereditSet != null)
            {
                BlockEdit previous = _selection.playereditSet.getEditBefore(_selection);
                if (previous != null)
                {
                    RenderUtils.startDrawingLines(buffer);
                    RenderSystem.lineWidth(3.0F);
                    buffer.vertex(previous.x + 0.5f, previous.y + 0.5f, previous.z + 0.5f).color(255 / 255f, 0 / 255f, 255 / 255f, 128).normal(0, 0, 0).next();
                    buffer.vertex(x, y, z).color(255 / 255f, 0 / 255f, 255 / 255f, 128).normal(0, 0, 0).next();
                    tesselator.draw();
                }
            }
        }
    }

    public void queryPreEdits(int count)
    {
        if (_variables.containsKey("player") && _variables.containsKey("time"))
        {
            if (Configs.Plugin.PLUGIN.getOptionListValue() == Plugins.LOGBLOCK)
            {
                _calendar.setTimeInMillis((Long) _variables.get("time"));
                int day = _calendar.get(Calendar.DAY_OF_MONTH);
                int month = _calendar.get(Calendar.MONTH) + 1;
                int year = _calendar.get(Calendar.YEAR);
                int hour = _calendar.get(Calendar.HOUR_OF_DAY);
                int minute = _calendar.get(Calendar.MINUTE);
                int second = _calendar.get(Calendar.SECOND);
                String player = (String) _variables.get("player");

                String query = String.format("lb before %d.%d.%d %02d:%02d:%02d player %s coords limit %d", day, month, year, hour, minute, second, player, count);
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info(query);
                }
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
        if (_variables.containsKey("player") && _variables.containsKey("time"))
        {
            if (Configs.Plugin.PLUGIN.getOptionListValue() == Plugins.LOGBLOCK)
            {
                _calendar.setTimeInMillis((Long) _variables.get("time"));
                int day = _calendar.get(Calendar.DAY_OF_MONTH);
                int month = _calendar.get(Calendar.MONTH) + 1;
                int year = _calendar.get(Calendar.YEAR);
                int hour = _calendar.get(Calendar.HOUR_OF_DAY);
                int minute = _calendar.get(Calendar.MINUTE);
                int second = _calendar.get(Calendar.SECOND);
                String player = (String) _variables.get("player");

                String query = String.format("lb since %d.%d.%d %02d:%02d:%02d player %s coords limit %d asc", day, month, year, hour, minute, second, player, count);
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info(query);
                }
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

    public void replay(String since, double speed, int radius, ServerCommandSource source)
    {
        TreeSet<BlockEdit> edits = new TreeSet<>(new BlockEditComparator());
        long timing = DataManager.getTimeDiff(since);
        Entity entity = source.getEntity();

        if (entity == null)
        {
            return;
        }
        if (timing == -1)
        {
            InfoUtils.showInGameMessage(MessageType.ERROR, "watson.message.edits.none_edits", "here");
            return;
        }

        for (PlayereditSet playereditSet : DataManager.getEditSelection().getBlockEditSet().getPlayereditSet().values())
        {
            for (BlockEdit edit : playereditSet.getBlockEdits())
            {
                if (timing <= edit.time)
                {
                    Vec3d editPos = new Vec3d(edit.x, edit.y, edit.z);
                    if (entity.getPos().isInRange(editPos, radius))
                    {
                        edits.add(edit);
                    }
                }
            }
        }

        if (!edits.isEmpty())
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            PlayerEntity player = mc.player;
            ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
            if (player == null || networkHandler == null)
            {
                return;
            }
            Thread t = new Thread(() -> {
                for (BlockEdit edit : edits)
                {
                    try
                    {
                        double randX = MathHelper.clamp(edit.x + player.getRandom().nextDouble() * 16.0D, edit.x - 3, edit.x + 3);
                        double randY = MathHelper.clamp(edit.y + (double) (player.getRandom().nextInt(16)), edit.y - 3, edit.y + 3);
                        double randZ = MathHelper.clamp(edit.z + player.getRandom().nextDouble() * 16.0D, edit.z - 3, edit.z + 3);
                        player.startFallFlying();
                        networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                        Teleport.teleport(randX, randY, randZ, edit.world);
                        Thread.sleep(50L);
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(edit.x, edit.y, edit.z));
                        networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(player.getYaw(), player.getPitch(), false));
                        selectPosition(edit.x, edit.y, edit.z, edit.world, edit.amount);
                        player.stopFallFlying();
                        networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                        Thread.sleep((long) (10000L / speed) - 50L);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        else
        {
            InfoUtils.showInGameMessage(MessageType.ERROR, "watson.message.edits.none_world");
        }
    }
}