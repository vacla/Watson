package eu.minemania.watson.network;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Reference;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Plugins;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

public class PluginCoreProtectPacketRegisteredHandler implements IPluginChannelHandlerExtended
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("coreprotect:handshake"));

    public static final PluginCoreProtectPacketRegisteredHandler INSTANCE = new PluginCoreProtectPacketRegisteredHandler();

    private boolean registered;

    public void reset()
    {
        registered = false;
        if (Configs.Plugin.PLUGIN.getOptionListValue() == Plugins.COREPROTECT)
        {
            Configs.Plugin.PLUGIN.resetToDefault();
        }
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
            boolean coreprotectRegistered = buf.readBoolean();

            if (coreprotectRegistered) {
                Configs.Plugin.PLUGIN.setOptionListValue(Plugins.COREPROTECT);
                if (Configs.Generic.DEBUG.getBooleanValue()) {
                    Watson.logger.info("CoreProtect is registered.");
                }
            }
        }
    }

    @Override
    public PacketByteBuf onPacketSend()
    {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try
        {
            msgOut.writeUTF(Reference.MOD_VERSION);
            msgOut.writeUTF(Reference.MOD_ID);
            msgOut.writeInt(Reference.COREPROTECT_PROTOCOL);
            packetByteBuf.writeBytes(msgBytes.toByteArray());
        } catch (Exception ignored) {
        }
        return packetByteBuf;
    }
}
