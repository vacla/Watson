package eu.minemania.watson.event;

import javax.annotation.Nullable;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.render.OverlayRenderer;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldLoadListener implements IWorldLoadListener {
	@Override
    public void onWorldLoadPre(@Nullable WorldClient worldBefore, @Nullable WorldClient worldAfter, Minecraft mc) {
        // Save the settings before the integrated server gets shut down
		if (worldBefore != null) {
            DataManager.save();
            if(worldAfter == null && DataManager.getEditSelection().getSelection() != null) {
            	DataManager.getEditSelection().clearBlockEditSet();
            }
        } else {
        	if(worldAfter != null) {
        		OverlayRenderer.resetRenderTimeout();
        	}
        }
    }
	
    @Override
    public void onWorldLoadPost(@Nullable WorldClient worldBefore, @Nullable WorldClient worldAfter, Minecraft mc) {
    	if(worldBefore == null && worldAfter != null && Configs.Generic.ENABLED.getBooleanValue()) {
    		DataManager.onClientTickStart();
    		DataManager.configure(mc.world.getWorldInfo().getGameType());
    	}
        if (worldAfter != null) {
            DataManager.load();
        }
        
    }
}
