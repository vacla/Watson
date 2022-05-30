package eu.minemania.watson.network;

import fi.dy.masa.malilib.network.message.BasePacketHandler;
import net.minecraft.network.PacketByteBuf;

public interface IPluginChannelHandlerExtended extends BasePacketHandler
{
    PacketByteBuf onPacketSend();
}
