package eu.minemania.watson.network.ledger;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.network.IPluginChannelHandlerExtended;
import eu.minemania.watson.scheduler.SyncTaskQueue;
import eu.minemania.watson.scheduler.tasks.AddBlockEditTask;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;

public class PluginActionPacketHandler implements IPluginChannelHandlerExtended
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("ledger:action"));

    public static final PluginActionPacketHandler INSTANCE = new PluginActionPacketHandler();

    private boolean registered;

    public void reset()
    {
        registered = false;
    }

    @Override
    public List<Identifier> getChannels()
    {
        return CHANNELS;
    }

    @Override
    public void onPacketReceived(PacketByteBuf buf)
    {
        if (!buf.toString(Charsets.UTF_8).isEmpty())
        {
            this.registered = true;
        }

        if (this.registered)
        {
            try
            {
                BlockPos pos = buf.readBlockPos();
                String type = buf.readString();
                Identifier dim = buf.readIdentifier();
                Identifier oldObj = buf.readIdentifier();
                Identifier newObj = buf.readIdentifier();
                String source = buf.readString();
                long time = buf.readLong() * 1000;
                boolean rolledBack = buf.readBoolean();
                String additional = buf.readString();
                int count = 1;
                String id = "";

                WatsonBlock watsonBlock = WatsonBlockRegistery.getInstance().getWatsonBlockByName(!type.contains("break") ? newObj.toString() : oldObj.toString());

                if (!additional.equals(""))
                {
                    NbtCompound nbtCompound = StringNbtReader.parse(additional);
                    if (nbtCompound.contains("Count", NbtElement.BYTE_TYPE))
                    {
                        count = nbtCompound.getByte("Count");
                    }
                    if (nbtCompound.contains("id", NbtElement.STRING_TYPE))
                    {
                        id = nbtCompound.getString("id");
                    }
                }

                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("watsonblock: " + watsonBlock.getName());
                    Watson.logger.info("pos: " + pos.toString());
                    Watson.logger.info("type: " + type);
                    Watson.logger.info("dim: " + dim.toString());
                    Watson.logger.info("oldobj: " + oldObj);
                    Watson.logger.info("newobj: " + newObj);
                    Watson.logger.info("source: " + source);
                    Watson.logger.info("time: " + time);
                    Watson.logger.info("rolled back: " + rolledBack);
                    Watson.logger.info("additional: " + additional);
                    Watson.logger.info("count: " + count);
                    Watson.logger.info("id: " + id);
                }
                HashMap<String, Object> addition = new HashMap<>();
                addition.put("rolledBack", rolledBack);
                addition.put("id", id);

                BlockEdit edit = new BlockEdit(time, source, type, pos.getX(), pos.getY(), pos.getZ(), watsonBlock, dim.toString(), count);
                edit.setAdditional(addition);
                SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, false));
            }
            catch (CommandSyntaxException exception)
            {
                Watson.logger.error(exception.getMessage());
            }

        }
    }

    @Override
    public PacketByteBuf onPacketSend()
    {
        return null;
    }
}
