package eu.minemania.watson.util;

import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class DataUtils
{
    private static final ArrayList<String> setNames = new ArrayList<>();
    public static ArrayList<String> getAllItemEntitiesStringIdentifiers()
    {
        if (!setNames.isEmpty())
        {
            return setNames;
        }

        setNames.addAll(getBlocks());
        setNames.addAll(getItems());
        setNames.addAll(getEntityTypes());

        return setNames;
    }

    public static ArrayList<String> getBlocks()
    {
        ArrayList<String> blocks = new ArrayList<>();

        Registry.BLOCK.forEach(block -> blocks.add(Registry.BLOCK.getId(block).toString()));

        blocks.sort(String::compareTo);

        return blocks;
    }

    public static ArrayList<String> getItems()
    {
        ArrayList<String> items = new ArrayList<>();

        Registry.ITEM.forEach(item -> items.add(Registry.ITEM.getId(item).toString()));

        items.sort(String::compareTo);

        return items;
    }

    public static ArrayList<String> getEntityTypes()
    {
        ArrayList<String> entityTypes = new ArrayList<>();

        Registry.ENTITY_TYPE.forEach(entityType -> entityTypes.add(Registry.ENTITY_TYPE.getId(entityType).toString()));

        entityTypes.sort(String::compareTo);

        return entityTypes;
    }

    public static ArrayList<String> getTags()
    {
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<String> deDupTags = new ArrayList<>();

        Registry.BLOCK.streamTags().forEach((block) -> tags.add("#"+block.id().toString()));
        Registry.ENTITY_TYPE.streamTags().forEach((entity) -> tags.add("#"+entity.id().toString()));
        Registry.ITEM.streamTags().forEach((item) -> tags.add("#"+item.id().toString()));

        for (String tag : tags) {
            if (!deDupTags.contains(tag)) {
                deDupTags.add(tag);
            }
        }

        deDupTags.sort(String::compareTo);

        return deDupTags;
    }
}
