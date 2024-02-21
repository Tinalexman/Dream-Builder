package editor.windows.interfaces;

import editor.windows.EditorWindow;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class Popup extends EditorWindow
{
    protected final String headerMessage;
    protected ImVec2 childSize;

    public Popup(String title, String headerMessage)
    {
        this(title, headerMessage, ImGuiWindowFlags.NoCollapse);
    }

    public Popup(String title, String headerMessage, int flags)
    {
        super(title, flags);
        this.headerMessage = headerMessage;
    }

    @Override
    public void show()
    {
        if(!this.isActive)
            return;

        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f);
        ImVec2 size = ImGui.getMainViewport().getSize();
        ImGui.setNextWindowSize(size.x * 0.75f, size.y * 0.75f);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 1.0f);
        ImGui.begin(this.title, this.windowFlags);
        ImGui.popStyleVar();

        ImVec2 tempVector = new ImVec2();
        ImGui.calcTextSize(tempVector, this.headerMessage);
        ImGui.sameLine(20.0f);
        ImGui.textWrapped(this.headerMessage);
        ImGui.separator();
        ImGui.spacing();

        drawTopSheet();

        this.childSize = ImGui.getContentRegionAvail();
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 10, 10, 10, 10);
        ImGui.beginChild("##popup" + this.title, this.childSize.x,
                this.childSize.y - 100.0f, true, ImGuiWindowFlags.HorizontalScrollbar);

        drawContent();

        ImGui.endChild();
        ImGui.popStyleColor(1);

        drawBottomSheet();

        ImGui.end();
    }

    public void drawTopSheet()
    {

    }

    public void drawContent()
    {

    }

    public void drawBottomSheet()
    {

    }
}
