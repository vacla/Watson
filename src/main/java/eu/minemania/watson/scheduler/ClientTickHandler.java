package eu.minemania.watson.scheduler;

import eu.minemania.watson.Reference;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.minecraft.client.MinecraftClient;

public class ClientTickHandler implements IClientTickHandler
{
    @Override
    public void onClientTick(MinecraftClient mc)
    {
        if (mc.world != null && mc.player != null)
        {
            SyncTaskQueue.getInstance().runTasks();
            ChatMessage.getInstance().processServerChatQueue();
            if(DataManager.getClientTickStartTime() != 0 && System.currentTimeMillis() - DataManager.getClientTickStartTime() > 1000)
            {
                ChatMessage.localOutputT("watson.message.join.watson", Reference.MOD_VERSION, Configs.Generic.WATSON_PREFIX.getStringValue(), true);
                ChatMessage.localOutputT("watson.message.join.plugin");
                DataManager.setClientTick(0);
            }
        }
    }
}