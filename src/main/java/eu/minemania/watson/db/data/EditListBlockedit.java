package eu.minemania.watson.db.data;

import java.util.List;
import com.google.common.collect.ImmutableList;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.selection.PlayereditUtils;

public class EditListBlockedit extends BlockeditBase
{
    private final List<BlockEdit> edits;

    public EditListBlockedit(List<BlockEdit> edits, boolean reCreate)
    {
        super();

        this.edits = edits;

        if(reCreate)
        {
            this.reCreateBlockedits();
        }
    }

    @Override
    public void reCreateBlockedits()
    {
        this.blockeditAll = ImmutableList.copyOf(PlayereditUtils.createDisplayListFor(this.edits));
    }
}
