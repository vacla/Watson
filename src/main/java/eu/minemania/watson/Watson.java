package eu.minemania.watson;

import fi.dy.masa.malilib.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ClientModInitializer;

public class Watson implements ClientModInitializer
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    @Override
    public void onInitializeClient()
    {
        Registry.INITIALIZATION_DISPATCHER.registerInitializationHandler(new InitHandler());
    }
}