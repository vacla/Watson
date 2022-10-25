package eu.minemania.watson.network.ledger;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.network.IPluginChannelHandlerExtended;
import malilib.overlay.message.MessageDispatcher;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class PluginResponsePacketHandler implements IPluginChannelHandlerExtended
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("ledger:reponse"));

    public static final PluginResponsePacketHandler INSTANCE = new PluginResponsePacketHandler();

    private boolean registered;

    public void reset()
    {
        registered = false;
    }

    @Override
    public boolean registerToServer()
    {
        return true;
    }

    @Override
    public boolean usePacketSplitter()
    {
        return false;
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

            switch (responseCode)
            {
                case 0 -> {
                    MessageDispatcher.error("watson.message.ledger.no_permission", identifier.getPath());
                }
                case 1 -> {
                    MessageDispatcher.generic("watson.message.ledger.executing", identifier.getPath());
                }
                case 2 -> {
                    MessageDispatcher.success("watson.message.ledger.completed", identifier.getPath());
                }
                case 3 -> {
                    MessageDispatcher.error("watson.message.ledger.error_executing", identifier.getPath());
                }
                case 4 -> {
                    MessageDispatcher.warning("watson.message.ledger.cannot_execute", identifier.getPath());
                }
                default -> {
                    MessageDispatcher.error("watson.message.ledger.unknown", identifier.getPath());
                }
            }
        }
    }

    @Override
    public PacketByteBuf onPacketSend()
    {
        return null;
    }
}
