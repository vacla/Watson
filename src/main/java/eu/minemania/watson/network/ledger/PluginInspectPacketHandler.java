package eu.minemania.watson.network.ledger;

import com.google.common.base.Charsets;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PluginInspectPacketHandler
{
    public static final Identifier CHANNEL = new Identifier("ledger:inspect");

    public static final PluginInspectPacketHandler INSTANCE = new PluginInspectPacketHandler();

    public void sendPacket(double x, double y, double z, int pages, MinecraftClient mc)
    {
        try
        {
            ClientPlayNetworkHandler packetHandler = mc.getNetworkHandler();
            if (packetHandler == null)
            {
                return;
            }
            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            BlockPos blockPos = BlockPos.ofFloored(x, y, z);
            packetByteBuf.writeBlockPos(blockPos);
            packetByteBuf.writeInt(pages);
            if(Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info(packetByteBuf.toString(Charsets.UTF_8));
                Watson.logger.info("blockpos: "+blockPos.toShortString());
                Watson.logger.info("pages: "+pages);
                Watson.logger.info(CHANNEL);
            }
            packetHandler.sendPacket(new CustomPayloadC2SPacket(CHANNEL, packetByteBuf));
        }
        catch (Exception exception)
        {
            Watson.logger.error(exception.getMessage());
        }
    }
}
