package eu.minemania.watson.network;

import com.google.common.base.Charsets;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class ClientPacketChannelHandler implements IClientPacketChannelHandler
{
    public static final Identifier REGISTER = new Identifier("minecraft:register");
    public static final Identifier UNREGISTER = new Identifier("minecraft:unregister");

    private static final ClientPacketChannelHandler INSTANCE = new ClientPacketChannelHandler();

    private final HashMap<Identifier, IPluginChannelHandlerExtended> handlers;

    public static IClientPacketChannelHandler getInstance()
    {
        return INSTANCE;
    }

    private ClientPacketChannelHandler()
    {
        this.handlers = new HashMap<>();
    }

    @Override
    public void registerClientChannelHandler(IPluginChannelHandlerExtended handler)
    {
        List<Identifier> toRegister = new ArrayList<>();

        for (Identifier channel : handler.getChannels())
        {
            if (!this.handlers.containsKey(channel))
            {
                this.handlers.put(channel, handler);
                toRegister.add(channel);
            }
        }

        if (!toRegister.isEmpty())
        {
            this.sendRegisterPacket(REGISTER, toRegister);
        }
    }

    @Override
    public void unregisterClientChannelHandler(IPluginChannelHandlerExtended handler)
    {
        List<Identifier> toUnRegister = new ArrayList<>();

        for (Identifier channel : handler.getChannels())
        {
            if (this.handlers.remove(channel, handler))
            {
                toUnRegister.add(channel);
            }
        }

        if (!toUnRegister.isEmpty())
        {
            this.sendRegisterPacket(UNREGISTER, toUnRegister);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean processPacketFromServer(CustomPayloadS2CPacket packet, ClientPlayNetworkHandler netHandler)
    {
        Identifier channel = packet.getChannel();
        IPluginChannelHandlerExtended handler = this.handlers.get(channel);

        if (Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info("packet from server");
            Watson.logger.info(channel);
            Watson.logger.info(packet.getData().toString(Charsets.UTF_8));
        }

        if (handler != null)
        {
            PacketByteBuf buf = packet.getData();

            if (Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info("packet from server for watson");
                Watson.logger.info(channel);
                Watson.logger.info(buf.toString(Charsets.UTF_8));
            }

            // Finished the complete packet
            if (buf != null)
            {
                handler.onPacketReceived(buf);
            }

            return true;
        }

        return false;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void processPacketFromClient(ClientPlayNetworkHandler netHandler)
    {
        for(Map.Entry<Identifier, IPluginChannelHandlerExtended> entry : this.handlers.entrySet())
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

                netHandler.sendPacket(new CustomPayloadC2SPacket(entry.getKey(), packetByteBuf));
            }
        }
    }

    private void sendRegisterPacket(Identifier type, List<Identifier> channels)
    {
        String joinedChannels = channels.stream().map(Identifier::toString).collect(Collectors.joining("\0"));
        ByteBuf payload = Unpooled.wrappedBuffer(joinedChannels.getBytes(Charsets.UTF_8));
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(type, new PacketByteBuf(payload));

        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

        if (handler != null)
        {
            handler.sendPacket(packet);
        }
        else
        {
            Watson.logger.warn("Failed to send register channel packet - network handler was null");
        }
    }
}
