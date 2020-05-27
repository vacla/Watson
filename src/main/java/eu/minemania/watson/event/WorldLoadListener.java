package eu.minemania.watson.event;

import javax.annotation.Nullable;
import eu.minemania.watson.analysis.CoreProtectAnalysis;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
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
            if(worldAfter == null && DataManager.getEditSelection().getSelection() != null)
            {
                DataManager.getEditSelection().clearBlockEditSet();
                CoreProtectAnalysis.reset();
            }
        }
        else
        {
            if(worldAfter != null)
            {
                OverlayRenderer.resetRenderTimeout();
            }
        }
    }

    @Override
    public void onWorldLoadPost(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc)
    {
        if(worldBefore == null && worldAfter != null && Configs.Generic.ENABLED.getBooleanValue())
        {
            DataManager.onClientTickStart();
        }
        if (worldAfter != null)
        {
            DataManager.load();
        }
    }
}