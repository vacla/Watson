package eu.minemania.watson;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.event.InputHandler;
import eu.minemania.watson.event.KeyCallbacks;
import eu.minemania.watson.event.RenderHandler;
import eu.minemania.watson.event.WorldLoadListener;
import eu.minemania.watson.scheduler.ClientTickHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.client.Minecraft;

public class InitHandler implements IInitializationHandler{
	@Override
	public void registerModHandlers() {
		ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());

		InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
		InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
		InputEventHandler.getInputManager().registerMouseInputHandler(InputHandler.getInstance());

		TickHandler.getInstance().registerClientTickHandler(new ClientTickHandler());

		IRenderer renderer = new RenderHandler();
		RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
		RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);

		WorldLoadListener listener = new WorldLoadListener();
		WorldLoadHandler.getInstance().registerWorldLoadPreHandler(listener);
		WorldLoadHandler.getInstance().registerWorldLoadPostHandler(listener);

		KeyCallbacks.init(Minecraft.getInstance());
		//StatusInfoRenderer.init();

		DataManager.getPlayereditsBaseDirectory();
	}
}
