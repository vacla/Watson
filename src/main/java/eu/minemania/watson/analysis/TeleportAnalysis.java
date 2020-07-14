package eu.minemania.watson.analysis;

import java.util.regex.Matcher;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.selection.EditSelection;
import net.minecraft.text.MutableText;

public class TeleportAnalysis extends Analysis
{
    public TeleportAnalysis()
    {
        addMatchedChatHandler(Configs.Analysis.LB_TP, (chat, m) -> {
            lbTp(chat, m);
            return true;
        });
    }

    void lbTp(MutableText chat, Matcher m)
    {
        try
        {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            int z = Integer.parseInt(m.group(3));

            EditSelection selection = DataManager.getEditSelection();
            String player = (String) selection.getVariables().get("player");
            BlockEdit edit = selection.getBlockEditSet().findEdit(x, y, z, player);
            selection.selectBlockEdit(edit);
        }
        catch (Exception e)
        {
        }
    }
}