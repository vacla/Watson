package eu.minemania.watson.network;

import fi.dy.masa.malilib.network.IPluginChannelHandler;
import net.minecraft.network.PacketByteBuf;

public interface IPluginChannelHandlerExtended extends IPluginChannelHandler
{
    PacketByteBuf onPacketSend();
}
