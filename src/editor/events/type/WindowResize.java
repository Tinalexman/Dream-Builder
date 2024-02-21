package editor.events.type;

import editor.events.Event;
import editor.events.EventType;

public class WindowResize extends Event
{
    public int width, height;

    public WindowResize(int newWidth, int newHeight)
    {
        super(EventType.WindowResize);
        this.width = newWidth;
        this.height = newHeight;
    }

    public boolean minimized()
    {
        return this.width == 0 && this.height == 0;
    }
}
