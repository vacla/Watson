package eu.minemania.watson.selection;

import java.util.Comparator;

import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.gui.GuiPlayereditLoadedList;

public class LoadedPlayereditSorter implements Comparator<PlayereditSet>
{
    private final GuiPlayereditLoadedList parent;

    public LoadedPlayereditSorter(GuiPlayereditLoadedList parent)
    {
        this.parent = parent;
    }

    @Override
    public int compare(PlayereditSet entry1, PlayereditSet entry2)
    {
        int nameCompare = entry1.getPlayer().compareTo(entry2.getPlayer());

        if (parent.isChecked())
        {
            return entry1.getBlockEditCount() == entry2.getBlockEditCount() ? nameCompare : ((entry1.getBlockEditCount() > entry2.getBlockEditCount()) ? -1 : 1);
        }

        return nameCompare * -1;
    }
}
