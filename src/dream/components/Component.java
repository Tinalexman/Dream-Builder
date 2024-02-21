package dream.components;

public class Component
{
    protected boolean changed;

    public Component()
    {
        this.changed = true;
    }


    public void onStart()
    {

    }

    public void onChanged()
    {

    }

    public void onStop()
    {

    }

    public void destroy()
    {

    }

    public boolean hasChanged()
    {
        return this.changed;
    }

    public void change(boolean change)
    {
        this.changed = change;
    }
}
