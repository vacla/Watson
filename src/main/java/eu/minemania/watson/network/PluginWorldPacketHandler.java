package eu.minemania.watson.network;

import com.google.common.base.Charsets;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PluginWorldPacketHandler implements IPluginChannelHandlerExtended
{
    public static final Identifier CHANNEL = new Identifier("watson:world");

    public static final PluginWorldPacketHandler INSTANCE = new PluginWorldPacketHandler();

    private boolean registered;

    public void reset()
    {
        registered = false;
        DataManager.setWorldPlugin("");
    }

    @Override
    public Identifier getChannel()
    {
        return CHANNEL;
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
            String world = buf.toString(Charsets.UTF_8);
            DataManager.setWorldPlugin(world);
            if (Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info("World: "+ world);
            }
        }
    }

    @Override
    public PacketByteBuf onPacketSend()
    {
        return null;
    }
}
