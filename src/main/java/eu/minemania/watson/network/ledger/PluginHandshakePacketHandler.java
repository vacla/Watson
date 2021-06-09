package eu.minemania.watson.network.ledger;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Reference;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.scheduler.SyncTaskQueue;
import eu.minemania.watson.scheduler.tasks.AddBlockEditTask;
import fi.dy.masa.malilib.network.IPluginChannelHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class PluginHandshakePacketHandler implements IPluginChannelHandler
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("ledger:handshake"), new Identifier("ledger:handshakeinfo"));

    public static final PluginHandshakePacketHandler INSTANCE = new PluginHandshakePacketHandler();

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
            System.out.println(buf.toString(Charsets.UTF_8));
            System.out.println("1: "+buf.readBoolean());
            System.out.println("2: "+buf.readBoolean());
            System.out.println("3: "+buf.readBoolean());
            System.out.println("4: "+buf.readBoolean());
            //System.out.println("allowed: "+buf.readString(eadBoolean());
            /*int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            boolean placed = buf.readBoolean();
            String player = buf.readString();
            long time = buf.readLong();
            String blockType = buf.readString();
            String blockState = buf.readString();
            String world = buf.readString();
            String action = placed ? "placed" : "broke";
            WatsonBlock block = WatsonBlockRegistery.getInstance().getWatsonBlockByName(blockType);
            long correctTime = time * 1000;

            if (Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.debug("x:" + x);
                Watson.logger.debug("y:" + y);
                Watson.logger.debug("z:" + z);
                Watson.logger.debug("placed:" + placed);
                Watson.logger.debug("playername:" + player);
                Watson.logger.debug("time:" + time);
                Watson.logger.debug("actual time:" + correctTime);
                Watson.logger.debug("blocktype:" + blockType);
                Watson.logger.debug("blockstate:" + blockState);
                Watson.logger.debug("world:" + world);
                Watson.logger.debug("action:" + action);
                Watson.logger.debug("block:" + block.getName());
                Watson.logger.debug("");
            }

            BlockEdit edit = new BlockEdit(correctTime, player, action, x, y, z, block, world, 1);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, false));

            //DataManager.setWorldPlugin(buf.read);*/
        }
    }

    @Override
    public PacketByteBuf onPacketSend()
    {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("version", Reference.MOD_VERSION);
        nbtCompound.putString("modid", Reference.MOD_ID);
        nbtCompound.putInt("protocol_version", 0);
        packetByteBuf.writeNbt(nbtCompound);
        return packetByteBuf;
    }
}
