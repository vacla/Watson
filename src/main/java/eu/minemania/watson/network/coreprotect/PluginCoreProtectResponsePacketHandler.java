package eu.minemania.watson.network.coreprotect;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.network.IPluginChannelHandlerExtended;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.List;

public class PluginCoreProtectResponsePacketHandler implements IPluginChannelHandlerExtended
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("coreprotect:response"));

    public static final PluginCoreProtectResponsePacketHandler INSTANCE = new PluginCoreProtectResponsePacketHandler();

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
            ByteArrayInputStream in = new ByteArrayInputStream(buf.getWrittenBytes());
            DataInputStream dis = new DataInputStream(in);
            try
            {
                String type = dis.readUTF();
                String message = dis.readUTF();

                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("type: " + type);
                    Watson.logger.info("message: " + message);
                }

                if (type.equals("coreprotect:lookupPage")) {
                    String[] pages = message.split("/");
                    int nextPage = Integer.parseInt(pages[0]);
                    int lastPage = Integer.parseInt(pages[1]);
                    if (nextPage <= DataManager.getCoreProtectInfo().getPages() &&
                            nextPage < Configs.Plugin.MAX_AUTO_PAGES_LOOP.getIntegerValue() &&
                            Configs.Plugin.AUTO_PAGE.getBooleanValue() &&
                            lastPage <= Configs.Plugin.MAX_AUTO_PAGES.getIntegerValue()
                    ) {
                        PluginCoreProtectInputPacketHandler.INSTANCE.sendLookupPagePacket(Integer.parseInt(message));
                    }
                }

                InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, message);
            }
            catch (Exception e)
            {
                Watson.logger.error("CoreProtect Response exception", e);
            }
        }
    }

    @Override
    public PacketByteBuf onPacketSend()
    {
        return null;
    }
}
