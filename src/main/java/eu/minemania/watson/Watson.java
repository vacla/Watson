package eu.minemania.watson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import fi.dy.masa.malilib.event.InitializationHandler;

public class Watson implements InitializationListener {
	public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

	@Override
	public void onInitialization() {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.watson.json");

		InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());

	}
}
