package editor.windows.interfaces;

import dream.graphics.icon.Icons;
import dream.managers.ResourcePool;
import editor.windows.EditorWindow;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public abstract class ContentWindow<T> extends EditorWindow
{
    protected List<T> content;
    protected final float size = 80.0f;

    public ContentWindow(String title)
    {
        super(title);
        this.content = new ArrayList<>();
    }

    public void addContent(T value)
    {
        this.content.add(value);
    }

    public void removeContent(T value)
    {
        this.content.remove(value);
    }

    @Override
    public void show()
    {
        this.isActive = ImGui.begin(this.title, this.windowFlags);

        String simpleName = this.content.get(0).getClass().getSimpleName();
        for(int i = 0; i < content.size(); i++)
            showSingle(i);

        ImGui.end();
    }

    public abstract void showSingle(int index);

    protected void beginDragSource(String name, int image, T value)
    {
        if (ImGui.beginDragDropSource())
        {
            ImGui.setDragDropPayload("Content: " + name , value, 0);
            ImGui.image(image, 32.0f, 32.0f, 0, 1, 1, 0);
            ImGui.endDragDropSource();
        }
    }

    protected void beginDragTarget(String name)
    {
        if (ImGui.beginDragDropTarget())
        {
            Object payload = ImGui.acceptDragDropPayload("Content: " + name);
            if(payload != null)
            {

            }

            ImGui.endDragDropTarget();
        }
    }
}
