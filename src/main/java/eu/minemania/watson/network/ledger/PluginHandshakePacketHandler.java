package eu.minemania.watson.network.ledger;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Reference;
import eu.minemania.watson.network.IPluginChannelHandlerExtended;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class PluginHandshakePacketHandler implements IPluginChannelHandlerExtended
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("ledger:handshake"));

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
            System.out.println("1: "+buf.readInt());
            System.out.println("2: "+buf.readBoolean());
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
