package eu.minemania.watson.network;

import com.google.common.base.Charsets;
import eu.minemania.watson.Watson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            if (this.handlers.containsKey(channel) == false)
            {
                this.handlers.put(channel, handler);
                toRegister.add(channel);
            }
        }

        if (toRegister.isEmpty() == false)
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

        if (toUnRegister.isEmpty() == false)
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

        if (handler != null)
        {
            PacketByteBuf buf = packet.getData();

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

                System.out.println("test");
                System.out.println(entry.getKey().toString());
                System.out.println(packetByteBuf.toString(Charsets.UTF_8));

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
