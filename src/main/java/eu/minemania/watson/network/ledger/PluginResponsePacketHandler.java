package eu.minemania.watson.network.ledger;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.network.IPluginChannelHandlerExtended;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class PluginResponsePacketHandler implements IPluginChannelHandlerExtended
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("ledger:response"));

    public static final PluginResponsePacketHandler INSTANCE = new PluginResponsePacketHandler();

    private boolean registered;

    public void reset()
    {
        registered = false;
    }

    @Override
    public List<Identifier> getChannels()
    {
        return CHANNELS;
    }

    @Override
    public void onPacketReceived(PacketByteBuf buf)
    {
        if (!buf.toString(Charsets.UTF_8).isEmpty())
        {
            this.registered = true;
        }

        if (this.registered)
        {
            Identifier identifier = buf.readIdentifier();
            int responseCode = buf.readInt();
            String response;
            Message.MessageType messageType;

            switch (responseCode)
            {
                case 0 -> {
                    response = "watson.message.ledger.no_permission";
                    messageType = Message.MessageType.ERROR;
                }
                case 1 -> {
                    response = "watson.message.ledger.executing";
                    messageType = Message.MessageType.INFO;
                }
                case 2 -> {
                    response = "watson.message.ledger.completed";
                    messageType = Message.MessageType.SUCCESS;
                }
                case 3 -> {
                    response = "watson.message.ledger.error_executing";
                    messageType = Message.MessageType.ERROR;
                }
                case 4 -> {
                    response = "watson.message.ledger.cannot_execute";
                    messageType = Message.MessageType.WARNING;
                }
                default -> {
                    response = "watson.message.ledger.unknown";
                    messageType = Message.MessageType.ERROR;
                }
            }

            InfoUtils.showGuiOrInGameMessage(messageType, response, identifier.getPath());
        }
    }

    @Override
    public PacketByteBuf onPacketSend()
    {
        return null;
    }
}
