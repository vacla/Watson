package eu.minemania.watson.data;

import java.util.List;

public class LedgerSearch
{
    private final String actions;
    private final String dimensions;
    private final String blocks;
    private final String entityTypes;
    private final String items;
    private final String tags;
    private final String range;
    private final String sources;
    private final String timeBefore;
    private final String timeAfter;

    public LedgerSearch(List<String> actions, List<String> dimension, List<String> block, List<String> entityType, List<String> item, List<String> tag, int range, String source, String timeBefore, String timeAfter)
    {
        this.actions = setTypeList("action",actions);
        this.dimensions = setTypeList("world", dimension);
        this.blocks = setTypeList("object", block);
        this.entityTypes = setTypeList("object", entityType);
        this.items = setTypeList("object", item);
        this.tags = setTypeList("object", tag);
        this.range = setTypeInt("range", range);
        this.sources = setTypeString("source", source);
        this.timeBefore = setTypeString("before", timeBefore);
        this.timeAfter = setTypeString("after", timeAfter);
    }

    private String setTypeString(String type, String parameterContent)
    {
        if (parameterContent.isEmpty())
        {
            return parameterContent;
        }

        StringBuilder parameterSearch = new StringBuilder();
        String[] parameterList = parameterContent.split(",");
        for (String parameter : parameterList)
        {
            parameterSearch.append(type).append(":").append(parameter).append(" ");
        }
        return parameterSearch.toString().strip();
    }

    private String setTypeList(String type, List<String> parameterContent)
    {
        if (parameterContent.isEmpty())
        {
            return "";
        }

        StringBuilder parameterSearch = new StringBuilder();
        for (String parameter : parameterContent)
        {
            parameterSearch.append(type).append(":").append(parameter).append(" ");
        }
        return parameterSearch.toString().strip();
    }

    private String setTypeInt(String type, Integer parameterContent)
    {
        if (parameterContent == 0)
        {
            return "";
        }

        return type+":"+parameterContent;
    }

    public String getActions()
    {
        return actions;
    }

    public String getDimensions()
    {
        return dimensions;
    }

    public String getBlocks()
    {
        return blocks;
    }

    public String getEntityTypes()
    {
        return entityTypes;
    }

    public String getItems()
    {
        return items;
    }

    public String getTags()
    {
        return tags;
    }

    public String getRange()
    {
        return range;
    }

    public String getSources()
    {
        return sources;
    }

    public String getTimeAfter()
    {
        return timeAfter;
    }

    public String getTimeBefore()
    {
        return timeBefore;
    }

    public String getSearchData()
    {
        StringBuilder search = new StringBuilder();
        if (!actions.isEmpty())
        {
            search.append(actions);
            search.append(" ");
        }
        if (!dimensions.isEmpty())
        {
            search.append(dimensions);
            search.append(" ");
        }
        if (!blocks.isEmpty())
        {
            search.append(blocks);
            search.append(" ");
        }
        if (!entityTypes.isEmpty())
        {
            search.append(entityTypes);
            search.append(" ");
        }
        if (!items.isEmpty())
        {
            search.append(items);
            search.append(" ");
        }
        if (!tags.isEmpty())
        {
            search.append(tags);
            search.append(" ");
        }
        if (!range.isEmpty())
        {
            search.append(range);
            search.append(" ");
        }
        if (!sources.isEmpty())
        {
            search.append(sources);
            search.append(" ");
        }
        if (!timeAfter.isEmpty())
        {
            search.append(timeAfter);
            search.append(" ");
        }
        if (!timeBefore.isEmpty())
        {
            search.append(timeBefore);
            search.append(" ");
        }
        return search.toString().strip();
    }
}
