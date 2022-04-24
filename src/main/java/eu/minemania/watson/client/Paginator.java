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
                ChatMessage.sendToServerChat("/pr page n");

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
                ChatMessage.sendToServerChat(String.format("/lb page %d", currentPage + 1));

                reset();
            }
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
