package eu.minemania.watson.gui.widgets;

import malilib.gui.widget.ContainerWidget;
import malilib.gui.widget.LabelWidget;

public class BlockeditEntryHoverInfoWidget extends ContainerWidget
{
    protected final LabelWidget headerLabel;

    public BlockeditEntryHoverInfoWidget()
    {
        super(-1, 54);

        this.headerLabel = new LabelWidget("watson.label.blockedit.hover_info.row_names");
        this.headerLabel.setPosition(6, 8);
        this.headerLabel.setLineHeight(16);

        //TODO add description
        //TODO add edit additional

        int width = this.headerLabel.getWidth();
        // width += Math.max(namelabel width + 22, itemcount label width)
        width += 22;
        this.setWidth(width);

        this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0xFF000000);
        this.getBorderRenderer().getNormalSettings().setEnabled(true);
        this.reAddSubWidgets();
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.headerLabel);
    }
}
