package eu.minemania.watson.selection;

import java.util.Comparator;

import eu.minemania.watson.selection.PlayereditBase.SortCriteria;

public class PlayereditSorter implements Comparator<PlayereditEntry>
{
    private final PlayereditBase edits;

    public PlayereditSorter(PlayereditBase edits)
    {
        this.edits = edits;
    }

    @Override
    public int compare(PlayereditEntry entry1, PlayereditEntry entry2)
    {
        boolean reverse = this.edits.getSortInReverse();
        SortCriteria sortCriteria = this.edits.getSortCriteria();
        int nameCompare = entry1.getStack().getName().getString().compareTo(entry2.getStack().getName().getString());

        if (sortCriteria == SortCriteria.COUNT_TOTAL)
        {
            return entry1.getCountTotal() == entry2.getCountTotal() ? nameCompare : ((entry1.getCountTotal() > entry2.getCountTotal()) != reverse ? -1 : 1);
        }

        return !reverse ? nameCompare * -1 : nameCompare;
    }
}
