package eu.minemania.watson.data;

import java.util.ArrayList;
import java.util.List;

public class CoreProtectInput
{
    private final String actions;
    private final String dimensions;
    private final String range;
    private final String sources;
    private final String time;
    private final String coords;
    private final String included;
    private final String excluded;
    private final String type;
    private final String amountRows;
    private final int page;
    private final boolean optimize;
    private final boolean silentChat;

    public CoreProtectInput(String type, List<String> actions, List<String> dimension, List<String> block, List<String> entityType, List<String> item, int range, int x, int y, int z, String source, String time, boolean optimize, boolean silentChat, int page, int amountRows)
    {
        List<String> mergedList = mergeLists(block, entityType, item);
        this.type = type;
        this.actions = setTypeList("a",actions);
        this.included = setTypeList("i", getIncluded(mergedList));
        this.excluded = setTypeList("e", getExcluded(mergedList));
        this.dimensions = setTypeList("r", dimension);
        this.range = setTypeInt("r", range);
        this.sources = setTypeString("u", source);
        this.time = setTypeString("t", time);
        this.coords = setTypeCoords(x, y, z);
        this.optimize = optimize;
        this.silentChat = silentChat;
        this.page = page;
        this.amountRows = setTypeInt("rows", amountRows);
    }

    private String setTypeString(String type, String parameterContent)
    {
        if (parameterContent.isEmpty())
        {
            return parameterContent;
        }

        return type + ":" + parameterContent;
    }

    private String setTypeList(String type, List<String> parameterContent)
    {
        if (parameterContent.isEmpty())
        {
            return "";
        }

        return type + ":" + String.join(",", parameterContent);
    }

    private List<String> getIncluded(List<String> list)
    {
        List<String> copy = new ArrayList<>(list);
        copy.removeIf((string) -> string.contains("!"));
        return copy;
    }

    private List<String> getExcluded(List<String> list)
    {
        List<String> copy = new ArrayList<>(list);
        copy.removeIf((string) -> !string.contains("!"));
        return copy;
    }

    @SafeVarargs
    private <T> List<T> mergeLists(List<T>... lists)
    {
        List<T> list = new ArrayList<>();
        for (List<T> value : lists)
        {
            if (value == null)
            {
                continue;
            }
            list.addAll(value);
        }

        return list;
    }

    private String setTypeInt(String type, Integer parameterContent)
    {
        if (parameterContent == 0)
        {
            return "";
        }

        return type+":"+parameterContent;
    }

    private String setTypeCoords(int x, int y, int z)
    {
        if (x == 0 || y == 0 || z == 0)
        {
            return "";
        }

        return "c:"+x+","+y+","+z;
    }

    public String getActions()
    {
        return actions;
    }

    public String getDimensions()
    {
        return dimensions;
    }

    public String getRange()
    {
        return range;
    }

    public String getSources()
    {
        return sources;
    }

    public String getTime()
    {
        return time;
    }

    public String getCoords()
    {
        return coords;
    }

    public String getIncluded()
    {
        return included;
    }

    public String getExcluded()
    {
        return excluded;
    }

    public String getType()
    {
        return type;
    }

    public boolean getOptimize()
    {
        return optimize;
    }

    public boolean getSilentChat()
    {
        return silentChat;
    }

    public int getPage()
    {
        return page;
    }

    public String getAmountRows()
    {
        return amountRows;
    }

    public String getSearchData()
    {
        StringBuilder search = new StringBuilder();
        if (!type.isEmpty())
        {
            search.append(type);
        }
        if (!actions.isEmpty())
        {
            search.append(" ").append(actions);
        }
        if (!dimensions.isEmpty())
        {
            search.append(" ").append(dimensions);
        }
        if (!included.isEmpty())
        {
            search.append(" ").append(included);
        }
        if (!excluded.isEmpty())
        {
            search.append(" ").append(excluded);
        }
        if (!range.isEmpty())
        {
            search.append(" ").append(range);
        }
        if (!sources.isEmpty())
        {
            search.append(" ").append(sources);
        }
        if (!time.isEmpty())
        {
            search.append(" ").append(time);
        }
        if (!coords.isEmpty())
        {
            search.append(" ").append(coords);
        }
        if (optimize)
        {
            search.append(" #optimize");
        }
        if (type.equals("lookup"))
        {
            if (page != 0)
            {
                search.append(" ").append(page);
            }
            if (!amountRows.isEmpty())
            {
                search.append(" ").append(amountRows);
            }
        }
        return search.toString().strip();
    }
}
