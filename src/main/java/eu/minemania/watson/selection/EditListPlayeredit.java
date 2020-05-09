package eu.minemania.watson.selection;

import com.google.common.collect.ImmutableList;
import eu.minemania.watson.db.PlayereditSet;
import fi.dy.masa.malilib.util.StringUtils;

public class EditListPlayeredit extends PlayereditBase
{
    private final PlayereditSet playeredit;

    public EditListPlayeredit(PlayereditSet playeredit, boolean reCreate)
    {
        super();

        this.playeredit = playeredit;

        if(reCreate)
        {
            this.reCreatePlayeredits();
        }
    }

    @Override
    public void reCreatePlayeredits()
    {
        this.playereditAll = ImmutableList.copyOf(PlayereditUtils.createPlayereditListFor(this.playeredit));
        
    }

    @Override
    public String getName()
    {
        return this.playeredit.getPlayer();
    }

    @Override
    public String getTitle()
    {
        return StringUtils.translate("watson.gui.title.edits", this.getName(), this.playeredit.getBlockEditCount());
    }
}
