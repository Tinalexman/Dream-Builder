package editor.windows.interfaces;

import editor.windows.EditorWindow;

public class DoubleObservableWindow<K, V> extends EditorWindow implements DoubleObservable<K, V>
{
    protected K first;
    protected V second;

    public DoubleObservableWindow(String name)
    {
        super(name);
    }

    @Override
    public K first()
    {
        return this.first;
    }

    @Override
    public void first(K value)
    {
        this.first = value;
    }

    @Override
    public V second()
    {
        return this.second;
    }

    @Override
    public void second(V value)
    {
        this.second = value;
    }
}
