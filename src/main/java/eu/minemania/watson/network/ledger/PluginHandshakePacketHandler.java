package eu.minemania.watson.network.ledger;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Reference;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Plugins;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.network.IPluginChannelHandlerExtended;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PluginHandshakePacketHandler implements IPluginChannelHandlerExtended
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("ledger:handshake"));

    public static final PluginHandshakePacketHandler INSTANCE = new PluginHandshakePacketHandler();

    private boolean registered;

    public void reset()
    {
        registered = false;
        if (Configs.Plugin.PLUGIN.getOptionListValue() == Plugins.LEDGER)
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
            int protocolVersion = buf.readInt();
            String ledgerVersion = buf.readString();
            int totalActions = buf.readInt();
            List<String> actionsList = new ArrayList<>();
            if (totalActions > 0)
            {
                for (int i = 0; i <= totalActions; i++)
                {
                    String action = buf.readString();
                    actionsList.add(action);
                }
            }

            if (Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info("protocol version: " + protocolVersion);
                Watson.logger.info("ledger version: " + ledgerVersion);
                Watson.logger.info("total actions: " + totalActions);
                Watson.logger.info("allowed actions: " + actionsList);
            }

            DataManager.setLedgerVersion(ledgerVersion);
            DataManager.setLedgerActions(actionsList);
            Configs.Plugin.PLUGIN.setOptionListValue(Plugins.LEDGER);
        }
    }

    @Override
    public PacketByteBuf onPacketSend()
    {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("version", Reference.MOD_VERSION);
        nbtCompound.putString("modid", Reference.MOD_ID);
        nbtCompound.putInt("protocol_version", Reference.LEDGER_PROTOCOL);
        packetByteBuf.writeNbt(nbtCompound);
        return packetByteBuf;
    }
}
