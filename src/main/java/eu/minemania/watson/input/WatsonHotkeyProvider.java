package eu.minemania.watson.input;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.Reference;
import eu.minemania.watson.config.Hotkeys;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.input.HotkeyProvider;

import java.util.List;

public class WatsonHotkeyProvider implements HotkeyProvider
{
    public static final ImmutableList<Hotkey> ALL_HOTKEYS = buildFullHotkeyList();
    public static final WatsonHotkeyProvider INSTANCE = new WatsonHotkeyProvider();

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        return ALL_HOTKEYS;
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        return ImmutableList.of(new HotkeyCategory(Reference.MOD_INFO, "watson.hotkeys.category.hotkeys", Hotkeys.HOTKEY_LIST));
    }

    private static ImmutableList<Hotkey> buildFullHotkeyList()
    {
        ImmutableList.Builder<Hotkey> builder = ImmutableList.builder();

        builder.addAll(Hotkeys.HOTKEY_LIST);

        return builder.build();
    }
}
