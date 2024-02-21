package dream.util.contain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Container<T> implements Containable<T>
{
    private final String name;
    private final T value;
    private List<Containable<T>> items;

    public Container(String name, T value)
    {
        this.name = name;
        this.value = value;
        this.items = null;
    }

    public Container(String name)
    {
        this(name, null);
    }

    public Container(Container<T> container)
    {
        this.name = container.name;
        this.value = container.value;
        this.items = null;
    }

    public int size()
    {
        return this.items == null ? 0 : this.items.size();
    }

    @Override
    public T value()
    {
        return this.value;
    }

    @Override
    public boolean isContainer()
    {
        return true;
    }

    public List<Containable<T>> getItems()
    {
        return this.items;
    }

    public boolean hasChildren()
    {
        return this.items != null && this.items.size() > 0;
    }

    @Override
    public String name()
    {
        return this.name;
    }

    public void add(Containable<T> containable)
    {
        if(this.items == null)
            this.items = new ArrayList<>();
        this.items.add(containable);
    }

    public void addAll(List<Containable<T>> containableList)
    {
        if(this.items == null)
            this.items = new ArrayList<>();
        this.items.addAll(containableList);
    }

    public void remove(Containable<T> containable)
    {
        if(this.items != null)
            this.items.remove(containable);
    }

    public void sort()
    {
        if(this.items != null)
            this.items.sort(new ContainerSort());
    }

    public void clear()
    {
        if(this.items != null)
            this.items.clear();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container<?> container = (Container<?>) o;
        return name.equals(container.name) && value.equals(container.value) && Objects.equals(items, container.items);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, value, items);
    }

    @Override
    public String toString()
    {
        return "Container {" + this.name + ", " + this.value + "}";
    }
}
