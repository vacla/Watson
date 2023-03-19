package eu.minemania.watson.network.coreprotect;

import com.google.common.base.Charsets;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.CoreProtectInput;
import eu.minemania.watson.data.DataManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

public class PluginCoreProtectInputPacketHandler
{
    public static final Identifier CHANNEL = new Identifier("coreprotect:input");

    public static final PluginCoreProtectInputPacketHandler INSTANCE = new PluginCoreProtectInputPacketHandler();

    public void sendPacket(String type, List<String> action, List<String> dimension, List<String> block, List<String> entityType, List<String> item, int range, String source, String time, int x, int y, int z, boolean optimize, boolean silentChat, MinecraftClient mc)
    {
        try
        {
            int amountRows = Configs.Plugin.AMOUNT_ROWS.getIntegerValue();
            int page = type.equals("lookup") ? 1 : 0;
            CoreProtectInput coreProtectInput = new CoreProtectInput(type, action, dimension, block, entityType, item, range, x, y, z, source, time, optimize, silentChat, page, amountRows);
            sendPacketData(coreProtectInput, mc);
        }
        catch (Exception ignored)
        {}
    }

    public void sendLookupPagePacket(int page)
    {
        try
        {
            CoreProtectInput coreProtectInput = new CoreProtectInput("lookup", List.of(), List.of(), List.of(), List.of(), List.of(), 0, 0, 0, 0, "", "", false, DataManager.getCoreProtectInfo().getSilentChat(), page, 0);
            sendPacketData(coreProtectInput, MinecraftClient.getInstance());
        }
        catch (Exception ignored)
        {}
    }

    private void sendPacketData(CoreProtectInput coreProtectInput, MinecraftClient mc)
    {
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
            msgOut.writeUTF(coreProtectInput.getSearchData());
            msgOut.writeBoolean(coreProtectInput.getSilentChat());
            packetByteBuf.writeBytes(msgBytes.toByteArray());
        } catch (Exception ignored) {
        }
        if(Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info(packetByteBuf.toString(Charsets.UTF_8));
            Watson.logger.info("type: "+coreProtectInput.getType());
            Watson.logger.info("action: "+coreProtectInput.getActions());
            Watson.logger.info("dimension: "+coreProtectInput.getDimensions());
            Watson.logger.info("included: "+coreProtectInput.getIncluded());
            Watson.logger.info("excluded: "+coreProtectInput.getExcluded());
            Watson.logger.info("range: "+coreProtectInput.getRange());
            Watson.logger.info("source: "+coreProtectInput.getSources());
            Watson.logger.info("time: "+coreProtectInput.getTime());
            Watson.logger.info("coords: "+coreProtectInput.getCoords());
            Watson.logger.info("search: "+coreProtectInput.getSearchData());
            Watson.logger.info("pages: "+coreProtectInput.getPage());
            Watson.logger.info("amount Rows: "+coreProtectInput.getAmountRows());
            Watson.logger.info("optimize: "+coreProtectInput.getOptimize());
            Watson.logger.info("silent chat: "+coreProtectInput.getSilentChat());
            Watson.logger.info("page: "+coreProtectInput.getPage());
            Watson.logger.info(CHANNEL);
        }
        packetHandler.sendPacket(new CustomPayloadC2SPacket(CHANNEL, packetByteBuf));
    }
}
