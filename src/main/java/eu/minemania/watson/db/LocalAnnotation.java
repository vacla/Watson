package eu.minemania.watson.db;

import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.config.Configs;
import java.util.ArrayList;

public class LocalAnnotation
{
    protected ArrayList<Annotation> annotations = new ArrayList<>();
    private final static LocalAnnotation INSTANCE = new LocalAnnotation();
    protected int tpIndexAnno = 0;

    public static LocalAnnotation getInstance()
    {
        return INSTANCE;
    }

    public synchronized void drawAnnotations()
    {
        if (Configs.Generic.ANNOTATION_SHOWN.getBooleanValue() && !annotations.isEmpty())
        {
            for (Annotation annotation : annotations)
            {
                annotation.draw();
            }
        }
    }

    public ArrayList<Annotation> getAnnotations()
    {
        return annotations;
    }

    public void tpNextAnno()
    {
        tpIndexAnno(tpIndexAnno + 1);
    }

    public void tpPrevAnno()
    {
        tpIndexAnno(tpIndexAnno - 1);
    }

    public void tpIndexAnno(int index)
    {
        if (annotations.isEmpty())
        {
            ChatMessage.localErrorT("watson.error.anno.out_range");
        }
        else
        {
            if (index < 1)
            {
                index = annotations.size();
            }
            else if (index > annotations.size())
            {
                index = 1;
            }
            tpIndexAnno = index;
            Annotation annotation = getAnnotations().get(index - 1);
            Teleport.teleport(annotation.getX(), annotation.getY(), annotation.getZ(), annotation.getWorld());
            ChatMessage.localOutputT("watson.message.anno.teleport", index);
        }
    }
}
