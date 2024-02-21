package editor.windows.interfaces;

public interface Observable<T>
{
    T get();
    void set(T value);
}
