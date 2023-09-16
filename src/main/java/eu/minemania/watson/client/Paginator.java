package eu.minemania.watson.client;

import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;

public class Paginator
{
    protected int currentPage = 0;
    protected int pageCount = 0;
    private static final Paginator INSTANCE = new Paginator();
    protected boolean firstPageLoop = true;

    public static Paginator getInstance()
    {
        return INSTANCE;
    }

    public void prRequestNextPage()
    {
        if (Configs.Plugin.AUTO_PAGE.getBooleanValue())
        {
            if (currentPage != 0 && currentPage < pageCount && pageCount <= Configs.Plugin.MAX_AUTO_PAGES.getIntegerValue())
            {
                if (currentPage >= Configs.Plugin.MAX_AUTO_PAGES_LOOP.getIntegerValue()) {
                    reset();
                    ChatMessage.localOutputT("watson.message.autopage.finished");
                    return;
                }
                ChatMessage.getInstance().serverChat("pr page n", currentPage == 1);

                reset();
            }
        }
    }

    public void lbRequestNextPage()
    {
        if (Configs.Plugin.AUTO_PAGE.getBooleanValue())
        {
            if (currentPage != 0 && currentPage < pageCount && pageCount <= Configs.Plugin.MAX_AUTO_PAGES.getIntegerValue())
            {
                if (currentPage >= Configs.Plugin.MAX_AUTO_PAGES_LOOP.getIntegerValue()) {
                    reset();
                    ChatMessage.localOutputT("watson.message.autopage.finished");
                    return;
                }
                ChatMessage.getInstance().serverChat(String.format("lb page %d", currentPage + 1), currentPage == 1);

                reset();
            }
        }
    }

    public void cpRequestNextPage()
    {
        if (currentPage != 0 && currentPage < pageCount)
        {
            if (currentPage >= Configs.Plugin.MAX_AUTO_PAGES_LOOP.getIntegerValue()) {
                reset();
                ChatMessage.localOutputT("watson.message.autopage.finished");
                return;
            }
            if (currentPage == 1 && firstPageLoop)
            {
                ChatMessage.getInstance().serverChat(String.format("%s l %d:%d", Configs.Plugin.COREPROTECT_COMMAND.getStringValue(), 1, Configs.Plugin.AMOUNT_ROWS.getIntegerValue()), currentPage == 1);
                firstPageLoop = false;
                return;
            }
            ChatMessage.getInstance().serverChat(String.format("%s l %d:%d", Configs.Plugin.COREPROTECT_COMMAND.getStringValue(), currentPage + 1, Configs.Plugin.AMOUNT_ROWS.getIntegerValue()), currentPage == 1);
        }
    }

    public void setPageCount(int pageCount)
    {
        this.pageCount = pageCount;
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    public void reset()
    {
        this.currentPage = this.pageCount = 0;
    }
}
