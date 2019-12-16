package eu.minemania.watson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.event.InitializationHandler;

public class Watson implements InitializationListener {
	public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

	@Override
	public void onInitialization() {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.watson.json");

		try {
			Class.forName("fi.dy.masa.malilib.event.InitializationHandler");
			new MalilibInit().run();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Malilib not found. Requires Malilib from Watson1132", e);
		} catch (LinkageError e) {
			throw new IllegalStateException("Incompatible Malilib version (" + MaLiLibReference.MOD_VERSION + ")" , e);
		}

	}
	
	// separate class to avoid loading malilib classes outside the try-catch
	private static class MalilibInit implements Runnable {
		@Override
		public void run() {
			InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
		}
	}
}
