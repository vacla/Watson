package eu.minemania.watson.data;

import eu.minemania.watson.db.BlockEdit;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashSet;
import java.util.List;

public class Actions
{
    private static final Actions INSTANCE = new Actions();
    private static final HashSet<MutablePair<String, MutablePair<String, String>>> actions = new HashSet<>();

    private static Actions getInstance()
    {
        return INSTANCE;
    }

    public static void setActionsList(List<String> list)
    {
        actions.clear();

        getInstance().populateActionsList(list);
    }

    /**
     * Populates actions list with config actions list.
     *
     * @param names Config actions list items
     */
    private void populateActionsList(List<String> names)
    {
        for (String str : names)
        {
            try
            {
                if (!str.isEmpty())
                {
                    int index = str.indexOf(";");
                    if (index != -1)
                    {
                        String username = str.substring(0, index);
                        String pattern = str.substring(index + 1);
                        MutablePair<String, MutablePair<String, String>> pr = new MutablePair<>();
                        MutablePair<String, String> pr2 = new MutablePair<>();
                        index = pattern.indexOf(";");
                        if (index != -1)
                        {
                            String original = pattern.substring(0, index);
                            String replacement = pattern.substring(index + 1);
                            pr2.setLeft(original);
                            pr2.setRight(replacement);
                            pr.setRight(pr2);
                        }
                        else
                        {
                            throw new Exception("No ';' found");
                        }
                        pr.setLeft(username);
                        if (pr.getLeft() != null && pr.getRight() != null)
                        {
                            Actions.actions.add(pr);
                        }
                    }
                    else
                    {
                        throw new Exception("No ';' found");
                    }
                }
            }
            catch (Exception e)
            {
                MessageDispatcher.error("watson.error.action", str);
            }
        }
    }

    public static boolean getReverseAction(BlockEdit historyPlayer, BlockEdit blockEdit)
    {
        for (MutablePair<String, MutablePair<String, String>> action : actions) {
            if (action.getLeft().equalsIgnoreCase(historyPlayer.player) && action.getRight().getLeft().equalsIgnoreCase(blockEdit.action)
                    && action.getRight().getRight().equalsIgnoreCase(historyPlayer.action))
            {
                blockEdit.disabled = true;
                return true;
            }
        }
        return false;
    }
}
