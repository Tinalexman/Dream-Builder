package dream.node;

import dream.components.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node
{
    private static int counter = 40;
    private static final Map<Integer, Node> nodes = new HashMap<>();

    protected String name;
    protected final int ID;
    protected boolean visible;
    protected boolean changed;
    protected final List<Node> children;
    protected final List<Component> components;

    protected Node parent;

    public Node(String name)
    {
        this.name = name;
        this.ID = Node.counter++;
        this.changed = true;
        this.visible = true;
        this.parent = null;
        this.children = new ArrayList<>();
        this.components = new ArrayList<>();
        Node.nodes.put(this.ID, this);
    }

    public Node()
    {
        this("Node");
    }

    public String name()
    {
        return this.name;
    }

    public void name(String name)
    {
        this.name = name;
    }

    public boolean isChanged()
    {
        return this.changed;
    }

    public void isChanged(boolean changed)
    {
        this.changed = changed;
    }

    public boolean visible()
    {
        return this.visible;
    }

    public void visible(boolean visible)
    {
        this.visible = visible;
    }

    public int getID()
    {
        return this.ID;
    }

    public static Node getNode(int ID)
    {
        return Node.nodes.getOrDefault(ID, null);
    }

    public List<Component> getComponents()
    {
        return this.components;
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for(Component component : this.components)
        {
            if(componentClass.isAssignableFrom(component.getClass()))
            {
                try
                {
                    return componentClass.cast(component);
                }
                catch (ClassCastException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public void addComponent(Component component)
    {
        for(Component c : this.components)
        {
            if(c.getClass().isAssignableFrom(component.getClass()))
                return;
        }

        this.components.add(component);
    }

    public void start()
    {

    }

    public void stop()
    {

    }

    public void parent(Node parent)
    {
        this.parent = parent;
    }

    public Node parent()
    {
        return this.parent;
    }

    public void removeComponent(Component component)
    {
        this.components.remove(component);
    }

    public List<Node> getChildren()
    {
        return this.children;
    }

    public void addChild(Node child)
    {
        this.children.add(child);
        child.parent = this;
        child.start();
    }

    public void removeChild(Node child)
    {
        this.children.remove(child);
        child.parent = null;
    }

    public boolean hasChildren()
    {
        return this.children.size() > 0;
    }
}
