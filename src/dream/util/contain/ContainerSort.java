package dream.util.contain;

import java.util.Comparator;

public class ContainerSort implements Comparator<Containable>
{
    @Override
    public int compare(Containable one, Containable two)
    {
        if(one.isContainer() && two.isContainer())
            return one.name().compareTo(one.name());
        else if(one.isContainer() && !two.isContainer())
            return -1;
        else if(!one.isContainer() && two.isContainer())
            return 1;
        else if(!one.isContainer() && !two.isContainer())
            return one.name().compareTo(two.name());
        return 0;
    }
}
