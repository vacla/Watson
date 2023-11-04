package eu.minemania.watson.compat.modmenu;

import eu.minemania.watson.gui.GuiMainMenu;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

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
