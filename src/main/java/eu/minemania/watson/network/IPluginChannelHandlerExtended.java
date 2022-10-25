package eu.minemania.watson.network;

import malilib.network.PluginChannelHandler;
import malilib.network.message.BasePacketHandler;
import net.minecraft.network.PacketByteBuf;

public interface IPluginChannelHandlerExtended extends PluginChannelHandler
{
    PacketByteBuf onPacketSend();
}
