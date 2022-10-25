package eu.minemania.watson;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.event.Actions;
import eu.minemania.watson.event.KeyCallbacks;
import eu.minemania.watson.event.RenderHandler;
import eu.minemania.watson.event.ClientWorldChangeHandler;
import eu.minemania.watson.gui.ConfigScreen;
import eu.minemania.watson.input.WatsonHotkeyProvider;
import eu.minemania.watson.scheduler.ClientTickHandler;
import malilib.config.JsonModConfig;
import malilib.config.JsonModConfig.ConfigDataUpdater;
import malilib.config.util.ConfigUpdateUtils.KeyBindSettingsResetter;
import malilib.event.InitializationHandler;
import malilib.registry.Registry;
import net.minecraft.client.MinecraftClient;

public class InitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        //Reset all KeyBindSettings when updating to the first post-malilib-refactor version
        ConfigDataUpdater updater = new KeyBindSettingsResetter(WatsonHotkeyProvider.INSTANCE::getAllHotkeys, 0);
        Registry.CONFIG_MANAGER.registerConfigHandler(JsonModConfig.createJsonModConfig(Reference.MOD_INFO, Configs.CURRENT_VERSION, Configs.CATEGORIES, updater));

        Registry.CONFIG_SCREEN.registerConfigScreenFactory(Reference.MOD_INFO, ConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabProvider(Reference.MOD_INFO, ConfigScreen::getConfigTabs);

        Registry.HOTKEY_MANAGER.registerHotkeyProvider(new WatsonHotkeyProvider());

        RenderHandler renderer = new RenderHandler();
        Registry.RENDER_EVENT_DISPATCHER.registerWorldPostRenderer(renderer);

        Registry.TICK_EVENT_DISPATCHER.registerClientTickHandler(new ClientTickHandler());

        Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER.registerClientWorldChangeHandler(new ClientWorldChangeHandler());

        KeyCallbacks.init();
        Actions.init();

        DataManager.getPlayereditsBaseDirectory();
    }


}
