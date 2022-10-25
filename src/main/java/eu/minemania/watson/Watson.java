package eu.minemania.watson;

import eu.minemania.watson.chat.command.Command;
import malilib.registry.Registry;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ClientModInitializer;

public class Watson implements ClientModInitializer
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    @Override
    public void onInitializeClient()
    {
        ClientCommandRegistrationCallback.EVENT.register(Command::registerCommands);
        Registry.INITIALIZATION_DISPATCHER.registerInitializationHandler(new InitHandler());
    }
}