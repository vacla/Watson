package eu.minemania.watson.network;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.network.IPluginChannelHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class PluginDeltaLoggerPacketHandler implements IPluginChannelHandler
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("deltalogger:placement"), new Identifier("deltalogger:transaction"));

    public static final PluginDeltaLoggerPacketHandler INSTANCE = new PluginDeltaLoggerPacketHandler();

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
    {System.out.println("test");
        if (!buf.toString(Charsets.UTF_8).isEmpty())
        {
            this.registered = true;
        }

        if (this.registered)
        {
            System.out.println(this.registered);
            //DataManager.setWorldPlugin(buf.read);
        }
    }
}
