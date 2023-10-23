package eu.minemania.watson.network;

import com.google.common.base.Charsets;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import net.minecraft.network.PacketByteBuf;
import com.google.common.collect.ArrayListMultimap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.*;

public class ClientPacketChannelHandler implements IClientPacketChannelHandler
{
    private static final ClientPacketChannelHandler INSTANCE = new ClientPacketChannelHandler();

    private final ArrayListMultimap<Identifier, IPluginChannelHandlerExtended> handlers = ArrayListMultimap.create();

    public static IClientPacketChannelHandler getInstance()
    {
        return INSTANCE;
    }

    private ClientPacketChannelHandler()
    {
    }

    @Override
    public void registerClientChannelHandler(IPluginChannelHandlerExtended handler)
    {
        Identifier channel = handler.getChannel();

        if (!this.handlers.containsEntry(channel, handler))
        {
            this.handlers.put(channel, handler);

            if (handler.registerToServer())
            {
                ClientPlayNetworking.registerGlobalReceiver(channel, handler.getClientPacketHandler());
            }
        }
    }

    @Override
    public void unregisterClientChannelHandler(IPluginChannelHandlerExtended handler)
    {
        Identifier channel = handler.getChannel();

        if (this.handlers.remove(channel, handler) && handler.registerToServer())
        {
            ClientPlayNetworking.unregisterGlobalReceiver(channel);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void processPacketFromClient()
    {
        for(Map.Entry<Identifier, IPluginChannelHandlerExtended> entry : this.handlers.entries())
        {
            IPluginChannelHandlerExtended handler = entry.getValue();

            PacketByteBuf packetByteBuf = handler.onPacketSend();
            if (packetByteBuf != null)
            {

                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("packet from client");
                    Watson.logger.info(entry.getKey());
                    Watson.logger.info(packetByteBuf.toString(Charsets.UTF_8));
                }

                ClientPlayNetworking.send(entry.getKey(), packetByteBuf);
            }
        }
    }
}
