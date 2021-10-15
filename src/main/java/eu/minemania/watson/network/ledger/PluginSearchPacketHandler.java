package eu.minemania.watson.network.ledger;

import com.google.common.base.Charsets;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.LedgerSearch;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.util.List;

public class PluginSearchPacketHandler
{
    public static final Identifier CHANNEL = new Identifier("ledger:search");

    public static final PluginSearchPacketHandler INSTANCE = new PluginSearchPacketHandler();

    public boolean sendPacket(List<String> action, List<String> dimension, List<String> object, int range, String source, String timeBefore, String timeAfter, MinecraftClient mc)
    {
        try
        {
            LedgerSearch ledgerSearch = new LedgerSearch(action, dimension, object, range, source, timeBefore, timeAfter);
            sendPacket(ledgerSearch.getSearchData(), mc);
            if(Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info("action: "+ledgerSearch.getActions());
                Watson.logger.info("dimension: "+ledgerSearch.getDimensions());
                Watson.logger.info("object: "+ledgerSearch.getObjects());
                Watson.logger.info("range: "+ledgerSearch.getRange());
                Watson.logger.info("source: "+ledgerSearch.getSources());
                Watson.logger.info("timeBefore: "+ledgerSearch.getTimeBefore());
                Watson.logger.info("timeAfter: "+ledgerSearch.getTimeAfter());
                Watson.logger.info("search: "+ledgerSearch.getSearchData());
            }
            return true;
        }
        catch (Exception ignored)
        {}
        return false;
    }

    public void sendPacket(String searchData, MinecraftClient mc)
    {
        ClientPlayNetworkHandler packetHandler = mc.getNetworkHandler();
        if (packetHandler == null)
        {
            return;
        }
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeString(searchData);

        packetHandler.sendPacket(new CustomPayloadC2SPacket(CHANNEL, packetByteBuf));
        if (Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info(packetByteBuf.toString(Charsets.UTF_8));
            Watson.logger.info(CHANNEL);
        }
    }
}
