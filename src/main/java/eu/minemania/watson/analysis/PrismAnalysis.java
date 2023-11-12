package eu.minemania.watson.analysis;

import eu.minemania.watson.Watson;
import eu.minemania.watson.client.Paginator;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.TimeStamp;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.scheduler.SyncTaskQueue;
import eu.minemania.watson.scheduler.tasks.AddBlockEditTask;
import net.minecraft.text.*;
import java.util.regex.Matcher;

public class PrismAnalysis extends Analysis
{
    protected String _world;
    protected int _x;
    protected int _y;
    protected int _z;

    public PrismAnalysis()
    {
        addMatchedChatHandler(Configs.Analysis.PRISM_DATA, (chat, m) -> {
            prData(chat, m);
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.PRISM_PAGE, (chat, m) -> {
            prPage(m);
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.PRISM_PAGINATION, (chat, m) -> {
            Paginator.getInstance().prRequestNextPage();
            return sendMessage();
        });
    }

    void prData(Text chat, Matcher m)
    {
        try
        {
            String dateTime = m.group("when");
            long millis = 0;
            if(dateTime.equals("just now"))
            {
                millis = TimeStamp.parseTimeExpression("", dateTime);
            }
            else if(dateTime.contains("ago"))
            {
                millis = TimeStamp.parseTimeExpression("", dateTime);
            }
            String player = m.group("instigator");
            StringBuilder action = new StringBuilder(m.group("action"));
            String totalItemBlockName = m.group("target").trim();
            int count = 1;
            if (totalItemBlockName.contains(" "))
            {
                if (totalItemBlockName.matches("\\d+\\s.*"))
                {
                    String[] item = totalItemBlockName.split(" ");
                    count = Integer.parseInt(item[0]);
                    StringBuilder text = new StringBuilder();
                    for (int i = 1; i < item.length; i++)
                    {
                        if (item[i].contains("[") || (item[i].contains("_") && Character.isUpperCase(item[i].charAt(0))) || Character.isUpperCase(item[i].charAt(0)))
                        {
                            continue;
                        }
                        text.append(item[i]);
                        if (i != item.length - 1)
                        {
                            text.append(" ");
                        }
                    }
                    totalItemBlockName = text.toString();
                }
                else if (totalItemBlockName.matches("([^A-Z]+)\\s+([A-Za-z\\[\\]=,_0-9]+)"))
                {
                    String[] item = totalItemBlockName.split(" ");
                    StringBuilder text = new StringBuilder();
                    for (int i = 0; i < item.length; i++)
                    {
                        if (item[i].contains("[") || (item[i].contains("_") && Character.isUpperCase(item[i].charAt(0))) || Character.isUpperCase(item[i].charAt(0)))
                        {
                            continue;
                        }
                        text.append(item[i]);
                        if (i != item.length - 1)
                        {
                            text.append(" ");
                        }
                    }
                    totalItemBlockName = text.toString();
                }
                else if (!totalItemBlockName.startsWith("/"))
                {
                    if (totalItemBlockName.contains("Sheep"))
                    {
                        totalItemBlockName = "sheep";

                    }
                    else
                    {
                        String[] item = totalItemBlockName.split(" x");
                        count = Integer.parseInt(item[1]);
                        totalItemBlockName = item[0];
                    }
                }
                if (!totalItemBlockName.contains("sign ("))
                {
                    totalItemBlockName = totalItemBlockName.trim().replace(" ", "_");
                }
            }
            if (totalItemBlockName.contains("sign ("))
            {
                String signSubtext = totalItemBlockName.substring(totalItemBlockName.indexOf("(")+1, totalItemBlockName.indexOf(")"));
                if (!signSubtext.equals(""))
                {
                    StringBuilder textSign = new StringBuilder();
                    String[] signText = signSubtext.split(",");
                    for (String text : signText)
                    {
                        textSign.append(text);
                        if(!signText[signText.length - 1].equals(text))
                        {
                            textSign.append(" ");
                        }
                    }
                    action = textSign;
                    totalItemBlockName = "oak_"+totalItemBlockName.substring(0, totalItemBlockName.indexOf(" ("));

                }
            }
            String date = m.group("date");
            String time = m.group("time");
            if (date != null)
            {
                if (time != null)
                {
                    millis = TimeStamp.parseTimeExpression("", date + " " + time);
                }
            }

            if (chat.getString().contains("@"))
            {
                _x = Integer.parseInt(m.group("x"));
                _y = Integer.parseInt(m.group("y"));
                _z = Integer.parseInt(m.group("z"));
            }

            WatsonBlock type;
            if (!action.toString().equals("killed"))
            {
                if (action.toString().equals("splash"))
                {
                    totalItemBlockName = "splash_potion";
                }
                else if (action.toString().equals("fireball"))
                {
                    totalItemBlockName = "fireball";
                }
                else if (totalItemBlockName.equals("leash_hitch"))
                {
                    totalItemBlockName = "leash_knot";
                }
                type = WatsonBlockRegistery.getInstance().getWatsonBlockByName(totalItemBlockName);
            }
            else
            {
                type = WatsonBlockRegistery.getInstance().getBlockKillTypeByName(totalItemBlockName);
            }

            if (m.group("world") != null)
            {
                _world = m.group("world");
            }
            if (DataManager.getFilters().isAcceptedPlayer(player))
            {
                BlockEdit edit = new BlockEdit(millis, player, action.toString(), _x, _y, _z, type, _world, count);
                SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, true));
            }
        }
        catch (Exception ex)
        {
            Watson.logger.info("error parsing prism data", ex);
        }
    }

    void prPage(Matcher m)
    {
        int currentPage = Integer.parseInt(m.group("current"));
        int pageCount = Integer.parseInt(m.group("max"));

        if (pageCount <= Configs.Plugin.MAX_AUTO_PAGES.getIntegerValue())
        {
            Paginator.getInstance().setCurrentPage(currentPage);
            Paginator.getInstance().setPageCount(pageCount);
        }
        else
        {
            Paginator.getInstance().reset();
        }
    }

    private boolean sendMessage()
    {
        return !Configs.Messages.DISABLE_PR_MESSAGES.getBooleanValue();
    }
}