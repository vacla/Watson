package eu.minemania.watson.compat.modmenu;

import java.util.function.Function;
import eu.minemania.watson.Reference;
import eu.minemania.watson.gui.GuiMainMenu;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuImpl implements ModMenuApi {
	@Override
	public String getModId() {
		return Reference.MOD_ID;
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return (screen) -> {
			GuiMainMenu gui = new GuiMainMenu();
			gui.setParent(screen);
			return gui;
		};
	}
}
