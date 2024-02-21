package dream.util.contain;

public interface Containable<T>
{
    String name();
    T value();
    boolean isContainer();
}
