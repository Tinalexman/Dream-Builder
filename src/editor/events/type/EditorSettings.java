package editor.events.type;

import editor.events.Event;
import editor.events.EventType;

public class EditorSettings extends Event
{
    private Runnable task;

    public EditorSettings(Runnable task)
    {
        super(EventType.EditorSettings);
        this.task = task;
    }
}
