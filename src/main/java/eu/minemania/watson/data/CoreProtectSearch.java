package eu.minemania.watson.data;

import java.util.ArrayList;
import java.util.List;

public class CoreProtectSearch
{
    private final String actions;
    private final String dimensions;
    private final String range;
    private final String sources;
    private final String time;
    private final String coords;
    private final String included;
    private final String excluded;

    public CoreProtectSearch(List<String> actions, List<String> dimension, List<String> block, List<String> entityType, List<String> item, int range, int x, int y, int z, String source, String time)
    {
        List<String> mergedList = mergeLists(block, entityType, item);
        this.actions = setTypeList("a",actions);
        this.included = setTypeList("i", getIncluded(mergedList));
        this.excluded = setTypeList("e", getExcluded(mergedList));
        this.dimensions = setTypeList("r", dimension);
        this.range = setTypeInt("r", range);
        this.sources = setTypeString("u", source);
        this.time = setTypeString("t", time);
        this.coords = setTypeCoords("c", x, y, z);
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

    private String setTypeCoords(String type, int x, int y, int z)
    {
        if (x == 0 || y == 0 || z == 0)
        {
            return "";
        }

        return type+":"+x+","+y+","+z;
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
        if (!included.isEmpty())
        {
            search.append(included);
            search.append(" ");
        }
        if (!excluded.isEmpty())
        {
            search.append(excluded);
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
        if (!time.isEmpty())
        {
            search.append(time);
            search.append(" ");
        }
        if (!coords.isEmpty())
        {
            search.append(coords);
            search.append(" ");
        }
        return search.toString().strip();
    }
}
