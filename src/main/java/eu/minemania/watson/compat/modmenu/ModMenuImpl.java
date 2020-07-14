package eu.minemania.watson.compat.modmenu;

import eu.minemania.watson.gui.GuiMainMenu;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return (screen) -> {
            GuiMainMenu gui = new GuiMainMenu();
            gui.setParent(screen);
            return gui;
        };
    }
}
