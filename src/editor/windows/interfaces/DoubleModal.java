package editor.windows.interfaces;

import editor.windows.EditorWindow;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class DoubleModal<F, S> extends EditorWindow
{
    protected final String headerMessage;
    protected ImVec2 childSize;
    protected S selection;
    protected F observable;
    protected boolean wrapContent;

    public DoubleModal(String windowTitle, String headerMessage, int windowFlags)
    {
        this(windowTitle, headerMessage, windowFlags, false);
    }

    public DoubleModal(String windowTitle, String headerMessage, int windowFlags, boolean wrapContent)
    {
        super(windowTitle, windowFlags);

        this.headerMessage = headerMessage;
        this.childSize = new ImVec2();
        this.selection = null;
        this.wrapContent = wrapContent;
    }

    public DoubleModal(String windowTitle, String headerMessage)
    {
        this(windowTitle, headerMessage, ImGuiWindowFlags.AlwaysAutoResize);
    }

    public DoubleModal(String windowTitle, String headerMessage, boolean wrapContent)
    {
        this(windowTitle, headerMessage, ImGuiWindowFlags.AlwaysAutoResize, wrapContent);
    }

    public S selection()
    {
        return this.selection;
    }

    public F observable()
    {
        return this.observable;
    }

    public void observable(F observable)
    {
        this.observable = observable;
    }

    public float getBottomHeight()
    {
        return 100.0f;
    }

    @Override
    public void show()
    {
        if(!this.isActive)
            return;

        ImGui.openPopup(this.title);

        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f);

        if(!wrapContent)
        {
            ImVec2 size = ImGui.getMainViewport().getSize();
            ImGui.setNextWindowSize(size.x * 0.75f, size.y * 0.75f);
        }

        ImGui.pushStyleColor(ImGuiCol.ModalWindowDimBg, 0, 0, 0, 200);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 1.0f);
        if (ImGui.beginPopupModal(this.title, this.windowFlags))
        {
            ImGui.popStyleVar();

            ImVec2 tempVector = new ImVec2();
            ImGui.calcTextSize(tempVector, this.headerMessage);
            ImGui.sameLine(20.0f);
            ImGui.textWrapped(this.headerMessage);
            ImGui.separator();
            ImGui.spacing();

            drawTopSheet();

            if(!wrapContent)
            {
                this.childSize = ImGui.getContentRegionAvail();
                ImGui.pushStyleColor(ImGuiCol.ChildBg, 10, 10, 10, 10);
                ImGui.beginChild("##modal" + this.title, this.childSize.x,
                        this.childSize.y - getBottomHeight(), true, ImGuiWindowFlags.HorizontalScrollbar);

                drawContent();

                ImGui.endChild();
                ImGui.popStyleColor(1);
            }

            drawBottomSheet();
            ImGui.endPopup();
        }

        ImGui.popStyleColor(1);
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
