package eu.minemania.watson.event;

import javax.annotation.Nullable;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.network.ClientPacketChannelHandler;
import eu.minemania.watson.network.coreprotect.PluginCoreProtectPacketHandler;
import eu.minemania.watson.network.coreprotect.PluginCoreProtectPacketRegisteredHandler;
import eu.minemania.watson.network.PluginWorldPacketHandler;
import eu.minemania.watson.network.ledger.PluginActionPacketHandler;
import eu.minemania.watson.network.ledger.PluginHandshakePacketHandler;
import eu.minemania.watson.network.ledger.PluginResponsePacketHandler;
import eu.minemania.watson.render.OverlayRenderer;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class WorldLoadListener implements IWorldLoadListener
{
    @Override
    public void onWorldLoadPre(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc)
    {
        // Save the settings before the integrated server gets shut down
        if (worldBefore != null)
        {
            DataManager.save();
            if (worldAfter == null)
            {
                ClientPacketChannelHandler.getInstance().unregisterClientChannelHandler(PluginWorldPacketHandler.INSTANCE);
                ClientPacketChannelHandler.getInstance().unregisterClientChannelHandler(PluginHandshakePacketHandler.INSTANCE);
                ClientPacketChannelHandler.getInstance().unregisterClientChannelHandler(PluginActionPacketHandler.INSTANCE);
                ClientPacketChannelHandler.getInstance().unregisterClientChannelHandler(PluginResponsePacketHandler.INSTANCE);
                ClientPacketChannelHandler.getInstance().unregisterClientChannelHandler(PluginCoreProtectPacketRegisteredHandler.INSTANCE);
                ClientPacketChannelHandler.getInstance().unregisterClientChannelHandler(PluginCoreProtectPacketHandler.INSTANCE);
                PluginWorldPacketHandler.INSTANCE.reset();
                PluginHandshakePacketHandler.INSTANCE.reset();
                PluginActionPacketHandler.INSTANCE.reset();
                PluginResponsePacketHandler.INSTANCE.reset();
                PluginCoreProtectPacketRegisteredHandler.INSTANCE.reset();
                PluginCoreProtectPacketHandler.INSTANCE.reset();
                if (DataManager.getEditSelection().getSelection() != null)
                {
                    DataManager.getEditSelection().clearBlockEditSet();
                }
            }
            else
            {
                DataManager.setWorldPlugin("");
            }
        }
        else
        {
            if (worldAfter != null)
            {
                OverlayRenderer.resetRenderTimeout();
            }
        }
    }

    @Override
    public void onWorldLoadPost(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc)
    {
        if (worldBefore == null && worldAfter != null && Configs.Generic.ENABLED.getBooleanValue())
        {
            DataManager.onClientTickStart();
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginWorldPacketHandler.INSTANCE);
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginHandshakePacketHandler.INSTANCE);
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginActionPacketHandler.INSTANCE);
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginResponsePacketHandler.INSTANCE);
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginCoreProtectPacketRegisteredHandler.INSTANCE);
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginCoreProtectPacketHandler.INSTANCE);
            ((ClientPacketChannelHandler) ClientPacketChannelHandler.getInstance()).processPacketFromClient(mc.getNetworkHandler());
        }
        if (worldAfter != null)
        {
            DataManager.load();
        }
    }
}