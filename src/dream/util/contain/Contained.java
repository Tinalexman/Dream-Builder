package dream.util.contain;

import java.util.Objects;

public class Contained<T> implements Containable<T>
{
    public String name;
    public T node;

    public Contained(String name, T node)
    {
        this.name = name;
        this.node = node;
    }

    @Override
    public String name()
    {
        return this.name;
    }

    @Override
    public T value()
    {
        return this.node;
    }

    @Override
    public boolean isContainer()
    {
        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contained<?> contained = (Contained<?>) o;
        return name.equals(contained.name) && node.equals(contained.node);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, node);
    }

    @Override
    public String toString()
    {
        return "Contained {" + this.name + "}";
    }
}
