package editor.windows;

import dream.graphics.icon.Icons;
import dream.managers.ResourcePool;
import dream.managers.WindowManager;
import dream.renderer.Renderer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;

public class Viewport<T> extends EditorWindow
{
    protected float[] position;
    protected float[] size;
    protected final int menuIcon;
    protected T value;

    public Viewport()
    {
        this("Viewport");
    }

    public Viewport(String name)
    {
        super(name);
        this.windowFlags =
                ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar |
                ImGuiWindowFlags.NoScrollWithMouse;
        this.menuIcon = ResourcePool.getIcon(Icons.menu);
    }

    public void set(T val)
    {
        this.value = val;
    }

    public T get()
    {
        return this.value;
    }

    @Override
    protected void menu()
    {
        ImGui.pushStyleColor(ImGuiCol.MenuBarBg, 0, 0, 0, 20);
        if(ImGui.beginMenuBar())
        {
            ImGui.pushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 20);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);

            showMenu();

            ImGui.imageButton(this.menuIcon, 16.0f, 16.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            ImGui.openPopupOnItemClick("viewportSettings", ImGuiPopupFlags.MouseButtonLeft);

            ImGui.popStyleColor(4);
            ImGui.endMenuBar();
        }
        ImGui.popStyleColor();
    }

    @Override
    protected void contents()
    {
        ImVec2 framebufferSizeInWindow = getLargestSizeForViewPort();
        ImVec2 framebufferPositionInWindow = getCenteredPositionForViewPort(framebufferSizeInWindow);

        ImVec2 actualWindowPosition = ImGui.getWindowPos();
        ImVec2 actualWindowSize = ImGui.getWindowSize();

        ImGui.setCursorPos(framebufferPositionInWindow.x, framebufferPositionInWindow.y);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        showImage(framebufferSizeInWindow.x, framebufferSizeInWindow.y);
        ImGui.popStyleVar();

        if(hasChanged(actualWindowSize, actualWindowPosition))
        {
            this.size[0] = framebufferSizeInWindow.x;
            this.size[1] = framebufferSizeInWindow.y;

            this.position[0] = actualWindowPosition.x + framebufferPositionInWindow.x;
            this.position[1] = actualWindowPosition.y + framebufferPositionInWindow.y;
        }
    }

    protected void showImage(float width, float height)
    {
        ImGui.image(0, width, height, 0, 1, 1, 0);
    }

    protected void showMenu()
    {

    }

    public Renderer renderer()
    {
        return null;
    }

    private ImVec2 getCenteredPositionForViewPort(ImVec2 aspectSize)
    {
        ImVec2 windowSize = setup();
        float viewportX = (windowSize.x * 0.5f) - (aspectSize.x * 0.5f);
        float viewportY = (windowSize.y * 0.5f) - (aspectSize.y * 0.5f);
        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

    private ImVec2 getLargestSizeForViewPort()
    {
        ImVec2 windowSize = setup();
        float aspectRatio = WindowManager.getMainRatio();
        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / aspectRatio;
        if(aspectHeight > windowSize.y)
        {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * aspectRatio;
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 setup()
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();
        return windowSize;
    }

    private boolean hasChanged(ImVec2 newWindowSize, ImVec2 newWindowPos)
    {
        boolean equalSize = (this.size[0] == newWindowSize.x)
                && (this.size[1] == newWindowSize.y);

        boolean samePosition = (this.position[0] == newWindowPos.x)
                && (this.position[1] == newWindowPos.y);

        return !(equalSize && samePosition);
    }

    /*
     * protected static Matrix4f getTotalTransform(Node node)
     *     {
     *         Matrix4f totalTransform = new Matrix4f().identity();
     *         List<Node> parentNodes = new ArrayList<>();
     *         parentNodes.add(node);
     *
     *         Node parentNode = node.parent;
     *         while(parentNode != null)
     *         {
     *             parentNodes.add(parentNode);
     *             parentNode = parentNode.parent;
     *         }
     *
     *         ListIterator<Node> iterator = parentNodes.listIterator(parentNodes.size());
     *         while(iterator.hasPrevious())
     *         {
     *             Transform t;
     *             if((t = iterator.previous().getComponent(Transform.class)) != null)
     *                 totalTransform.mul(t.getMatrix());
     *         }
     *
     *         return totalTransform;
     *     }
     */
}

