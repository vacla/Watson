package eu.minemania.watson.network.coreprotect;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Reference;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Plugins;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.network.IPluginChannelHandlerExtended;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PluginCoreProtectPacketRegisteredHandler implements IPluginChannelHandlerExtended
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("coreprotect:handshake"));

    public static final PluginCoreProtectPacketRegisteredHandler INSTANCE = new PluginCoreProtectPacketRegisteredHandler();

    private boolean registered;

    public void reset()
    {
        if (Configs.Plugin.PLUGIN.getOptionListValue() == Plugins.COREPROTECT && registered)
        {
            Configs.Plugin.PLUGIN.resetToDefault();
        }
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
                boolean coreprotectRegistered = dis.readBoolean();
                List<String> actions = readList(dis);
                List<String> worlds = readList(dis);
                String version = dis.readUTF();

                DataManager.setPluginActions(actions);
                DataManager.setPluginVersion(version);
                DataManager.setPluginWorlds(worlds);

                if (coreprotectRegistered) {
                    Configs.Plugin.PLUGIN.setOptionListValue(Plugins.COREPROTECT);
                    if (Configs.Generic.DEBUG.getBooleanValue()) {
                        Watson.logger.info("CoreProtect is registered.");
                    }
                }
            }
            catch (Exception e)
            {
                Watson.logger.error("CoreProtect was not successfully registered", e);
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

    private List<String> readList(DataInputStream dis) throws IOException
    {
        int total = dis.readInt();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < total; i++)
        {
            list.add(dis.readUTF());
        }
        return list;
    }
}
