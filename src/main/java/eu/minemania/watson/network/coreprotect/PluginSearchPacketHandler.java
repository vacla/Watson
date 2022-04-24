package eu.minemania.watson.network.coreprotect;

import com.google.common.base.Charsets;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.CoreProtectSearch;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

public class PluginSearchPacketHandler
{
    public static final Identifier CHANNEL = new Identifier("coreprotect:input");

    public static final PluginSearchPacketHandler INSTANCE = new PluginSearchPacketHandler();

    public void sendPacket(String type, List<String> action, List<String> dimension, List<String> block, List<String> entityType, List<String> item, int range, String source, String time, int pages, int x, int y, int z, boolean optimize, boolean silentChat, MinecraftClient mc)
    {
        try
        {
            int amountRows = Configs.Plugin.AMOUNT_ROWS.getIntegerValue();
            CoreProtectSearch coreProtectSearch = new CoreProtectSearch(type, action, dimension, block, entityType, item, range, x, y, z, source, time, optimize, silentChat);
            ClientPlayNetworkHandler packetHandler = mc.getNetworkHandler();
            if (packetHandler == null)
            {
                return;
            }
            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
            DataOutputStream msgOut = new DataOutputStream(msgBytes);
            try
            {
                msgOut.writeUTF(coreProtectSearch.getSearchData());
                msgOut.writeInt(pages);
                msgOut.writeInt(amountRows);
                packetByteBuf.writeBytes(msgBytes.toByteArray());
            } catch (Exception ignored) {
            }
            if(Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info(packetByteBuf.toString(Charsets.UTF_8));
                Watson.logger.info("type: "+coreProtectSearch.getType());
                Watson.logger.info("action: "+coreProtectSearch.getActions());
                Watson.logger.info("dimension: "+coreProtectSearch.getDimensions());
                Watson.logger.info("included: "+coreProtectSearch.getIncluded());
                Watson.logger.info("excluded: "+coreProtectSearch.getExcluded());
                Watson.logger.info("range: "+coreProtectSearch.getRange());
                Watson.logger.info("source: "+coreProtectSearch.getSources());
                Watson.logger.info("time: "+coreProtectSearch.getTime());
                Watson.logger.info("coords: "+coreProtectSearch.getCoords());
                Watson.logger.info("search: "+coreProtectSearch.getSearchData());
                Watson.logger.info("pages: "+pages);
                Watson.logger.info("amount Rows: "+amountRows);
                Watson.logger.info("optimize: "+coreProtectSearch.getOptimize());
                Watson.logger.info("silent chat: "+coreProtectSearch.getSilentChat());
                Watson.logger.info(CHANNEL);
            }
            packetHandler.sendPacket(new CustomPayloadC2SPacket(CHANNEL, packetByteBuf));
        }
        catch (Exception ignored)
        {}
    }
}
