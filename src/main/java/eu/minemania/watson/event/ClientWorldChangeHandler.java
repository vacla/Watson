package eu.minemania.watson.event;

import javax.annotation.Nullable;

import eu.minemania.watson.analysis.CoreProtectAnalysis;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.network.ClientPacketChannelHandler;
import eu.minemania.watson.network.PluginWorldPacketHandler;
import eu.minemania.watson.network.ledger.PluginActionPacketHandler;
import eu.minemania.watson.network.ledger.PluginHandshakePacketHandler;
import eu.minemania.watson.network.ledger.PluginResponsePacketHandler;
import eu.minemania.watson.render.OverlayRenderer;
import malilib.util.game.wrap.GameUtils;
import net.minecraft.client.world.ClientWorld;

public class ClientWorldChangeHandler implements malilib.event.ClientWorldChangeHandler
{
    @Override
    public void onPreClientWorldChange(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter)
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
                PluginWorldPacketHandler.INSTANCE.reset();
                PluginHandshakePacketHandler.INSTANCE.reset();
                PluginActionPacketHandler.INSTANCE.reset();
                PluginResponsePacketHandler.INSTANCE.reset();
                if (DataManager.getEditSelection().getSelection() != null)
                {
                    DataManager.getEditSelection().clearBlockEditSet();
                    CoreProtectAnalysis.reset();
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
    public void onPostClientWorldChange(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter)
    {
        if (worldBefore == null && worldAfter != null && Configs.Generic.ENABLED.getBooleanValue())
        {
            DataManager.onClientTickStart();
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginWorldPacketHandler.INSTANCE);
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginHandshakePacketHandler.INSTANCE);
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginActionPacketHandler.INSTANCE);
            ClientPacketChannelHandler.getInstance().registerClientChannelHandler(PluginResponsePacketHandler.INSTANCE);
            ((ClientPacketChannelHandler) ClientPacketChannelHandler.getInstance()).processPacketFromClient(GameUtils.getClient().getNetworkHandler());
        }
        if (worldAfter != null)
        {
            DataManager.load();
        }
    }
}