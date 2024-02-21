package editor.windows.interfaces;

import editor.windows.EditorWindow;

public class ObservableWindow<T> extends EditorWindow implements Observable<T>
{
    protected T value;

    public ObservableWindow(String title)
    {
        super(title);
    }

    @Override
    public T get()
    {
        return this.value;
    }

    @Override
    public void set(T value)
    {
        this.value = value;
    }
}
