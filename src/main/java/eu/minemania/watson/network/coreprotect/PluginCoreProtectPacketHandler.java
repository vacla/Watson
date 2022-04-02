package eu.minemania.watson.network.coreprotect;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.network.IPluginChannelHandlerExtended;
import eu.minemania.watson.scheduler.SyncTaskQueue;
import eu.minemania.watson.scheduler.tasks.AddBlockEditTask;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.List;

public class PluginCoreProtectPacketHandler implements IPluginChannelHandlerExtended
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("coreprotect:data"));

    public static final PluginCoreProtectPacketHandler INSTANCE = new PluginCoreProtectPacketHandler();

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
                String coreProtectData = buf.toString(Charsets.UTF_8);
                int type = dis.readInt();
                if (Configs.Generic.DEBUG.getBooleanValue()) {
                    Watson.logger.info("Data: " + coreProtectData);
                    Watson.logger.info("type: " + type);
                }
                long time;
                String resultUser, action, target, worldName;
                int x, y, z, count;
                boolean rolledBack;
                switch (type) {
                    case 2 -> {
                        time = dis.readLong();
                        action = "session"+StringUtils.translate("watson.message.cp.logged", dis.readUTF());
                        resultUser = dis.readUTF();
                        target = "minecraft:player";
                        count = dis.readInt();
                        x = dis.readInt();
                        y = dis.readInt();
                        z = dis.readInt();
                        worldName = dis.readUTF();
                        rolledBack = false;
                    }
                    case 3 -> {
                        time = dis.readLong();
                        resultUser = dis.readUTF();
                        action = dis.readUTF();
                        target = dis.readBoolean() ? "minecraft:oak_sign" : "minecraft:player";
                        x = dis.readInt();
                        y = dis.readInt();
                        z = dis.readInt();
                        worldName = dis.readUTF();
                        count = 0;
                        rolledBack = false;
                    }
                    case 4 -> {
                        time = dis.readLong();
                        resultUser = dis.readUTF();
                        target = "minecraft:player";
                        action = dis.readUTF();
                        x = 0;
                        y = 0;
                        z = 0;
                        worldName = "unknown";
                        count = 0;
                        rolledBack = false;
                    }
                    default -> {
                        time = dis.readLong();
                        action = dis.readUTF();
                        resultUser = dis.readUTF();
                        target = dis.readUTF();
                        count = dis.readInt();
                        x = dis.readInt();
                        y = dis.readInt();
                        z = dis.readInt();
                        worldName = dis.readUTF();
                        rolledBack = dis.readBoolean();
                    }
                }
                count = count == -1 ? 1 : count;
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("time: " + time);
                    Watson.logger.info("action: " + action);
                    Watson.logger.info("result user: " + resultUser);
                    Watson.logger.info("target: " + target);
                    Watson.logger.info("count: " + count);
                    Watson.logger.info("x: " + x);
                    Watson.logger.info("y: " + y);
                    Watson.logger.info("z: " + z);
                    Watson.logger.info("worldName: " + worldName);
                    Watson.logger.info("rolled back: " + rolledBack);
                }
                WatsonBlock watsonBlock = WatsonBlockRegistery.getInstance().getWatsonBlockByName(target);
                BlockEdit edit = new BlockEdit(time, resultUser, action, x, y, z, watsonBlock, worldName, count);
                HashMap<String, Object> addition = new HashMap<>();
                addition.put("rolledBack", rolledBack);
                if (type == 1)
                {
                    boolean isContainer = dis.readBoolean();
                    boolean added = dis.readBoolean();
                    addition.put("isContainer", isContainer);
                    addition.put("added", added);
                    if (Configs.Generic.DEBUG.getBooleanValue())
                    {
                        Watson.logger.info("isContainer: " + isContainer);
                        Watson.logger.info("added: " + added);
                    }
                }
                edit.setAdditional(addition);
                SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, false));
            } catch (Exception e) {
                Watson.logger.info(e);
            }
        }
    }

    @Override
    public PacketByteBuf onPacketSend()
    {
        return null;
    }
}
