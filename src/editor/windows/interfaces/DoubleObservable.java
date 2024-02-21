package editor.windows.interfaces;

public interface DoubleObservable<K, V>
{
    K first();
    void first(K value);

    V second();
    void second(V value);
}
