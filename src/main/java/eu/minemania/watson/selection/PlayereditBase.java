package eu.minemania.watson.selection;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import malilib.listener.TaskCompletionListener;

public abstract class PlayereditBase implements IPlayeredit
{
    protected ImmutableList<PlayereditEntry> playereditAll = ImmutableList.of();
    @Nullable
    protected TaskCompletionListener completionListener;
    protected SortCriteria sortCriteria = SortCriteria.COUNT_TOTAL;
    protected boolean reverse;

    public abstract String getName();

    public abstract String getTitle();

    public ImmutableList<PlayereditEntry> getPlayereditsAll()
    {
        return this.playereditAll;
    }

    public void setCompletionListener(TaskCompletionListener listener)
    {
        this.completionListener = listener;
    }

    public abstract void reCreatePlayeredits();

    public SortCriteria getSortCriteria()
    {
        return this.sortCriteria;
    }

    public boolean getSortInReverse()
    {
        return this.reverse;
    }

    public void setSortCriteria(SortCriteria criteria)
    {
        if (this.sortCriteria == criteria)
        {
            this.reverse = !this.reverse;
        }
        else
        {
            this.sortCriteria = criteria;
            this.reverse = criteria == SortCriteria.NAME;
        }
    }

    public enum SortCriteria
    {
        NAME,
        BROKEN,
        PLACED,
        CONT_ADDED,
        CONT_REMOVED,
        COUNT_TOTAL;

        public static SortCriteria fromStringStatic(String name)
        {
            for (SortCriteria mode : SortCriteria.values())
            {
                if (mode.name().equalsIgnoreCase(name))
                {
                    return mode;
                }
            }

            return SortCriteria.COUNT_TOTAL;
        }
    }
}
