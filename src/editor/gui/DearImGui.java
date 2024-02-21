package editor.gui;

import dream.io.Input;
import dream.managers.WindowManager;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;

import static org.lwjgl.glfw.GLFW.*;

public class DearImGui
{
    private final ImGuiImplGl3 imGuiGl3;
    private final ImGuiImplGlfw imGuiGlfw;
    private Runnable mainMenuBar;

    public DearImGui()
    {
        this.imGuiGl3 = new ImGuiImplGl3();
        this.imGuiGlfw = new ImGuiImplGlfw();
    }

    public void setMenuBarCallBack(Runnable callback)
    {
       this.mainMenuBar = callback;
    }

    public void create(long windowID)
    {
        ImGui.createContext();
        ImGui.styleColorsDark();

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename("dream.ini");
        io.setFontAllowUserScaling(true);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        //io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setBackendPlatformName("imgui_java_impl_glfw"); // For clarity reasons

        // ------------------------------------------------------------
        // Here goes GLFW callbacks to update user input in Dear ImGui

        glfwSetKeyCallback(windowID, (w, key, scancode, action, mods) ->
        {
            if (action == GLFW_PRESS)
                io.setKeysDown(key, true);
            else if (action == GLFW_RELEASE)
                io.setKeysDown(key, false);

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

            Input.keyCallback(w, key, scancode, action, mods);
        });

        glfwSetCharCallback(windowID, (w, c) ->
        {
            if (c != GLFW_KEY_DELETE)
                io.addInputCharacter(c);
        });

        glfwSetMouseButtonCallback(windowID, (w, button, action, mods) ->
        {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1])
                ImGui.setWindowFocus(null);

            Input.mouseButtonCallback(w, button, action, mods);
        });

        glfwSetCursorPosCallback(windowID, Input::mousePositionCallback);

        glfwSetScrollCallback(windowID, (w, xOffset, yOffset) ->
        {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);

            Input.mouseScrollCallback(w, xOffset, yOffset);
        });

        io.setSetClipboardTextFn(new ImStrConsumer()
        {
            @Override
            public void accept(final String s)
            {
                glfwSetClipboardString(windowID, s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier()
        {
            @Override
            public String get()
            {
                String clipboardString = glfwGetClipboardString(windowID);
                return (clipboardString != null) ? clipboardString : "";
            }
        });

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setRasterizerMultiply(1.5f);

        fontAtlas.addFontFromFileTTF("res/fonts/Poppins.ttf", 16, fontConfig);
        fontConfig.destroy();

        imGuiGlfw.init(windowID, false);
        imGuiGl3.init("#version 330 core");

        setEditorStyle();
    }

    private void setEditorStyle()
    {
        ImGuiStyle currentStyle = ImGui.getStyle();
        // Adjust Borders
        currentStyle.setWindowBorderSize(1.0f);
        currentStyle.setFrameBorderSize(1.0f);
        currentStyle.setChildBorderSize(1.0f);
        currentStyle.setPopupBorderSize(1.0f);
        currentStyle.setTabBorderSize(1.0f);

        // Adjust Rounding
        currentStyle.setFrameRounding(2.0f);
        currentStyle.setWindowRounding(2.0f);
        currentStyle.setChildRounding(2.0f);
        currentStyle.setPopupRounding(2.0f);
        currentStyle.setTabRounding(2.0f);
        currentStyle.setGrabRounding(2.0f);
        currentStyle.setScrollbarRounding(2.0f);

        // Adjust Padding
        currentStyle.setFramePadding(10.0f, 4.0f);
        currentStyle.setWindowPadding(4.0f, 4.0f);

        // Adjust Spacing
        currentStyle.setItemSpacing(15.0f, 5.0f);
        currentStyle.setItemInnerSpacing(10.0f, 5.0f);
        currentStyle.setIndentSpacing(10.0f);

        // Adjust Size
        currentStyle.setScrollbarSize(10.0f);
        currentStyle.setGrabMinSize(7.0f);

        // Misc
        currentStyle.setWindowTitleAlign(0.5f, 0.5f);
        currentStyle.setButtonTextAlign(1.0f, 0.5f);

        // Colors
        currentStyle.setColor(ImGuiCol.DockingEmptyBg, 0.0f, 0.0f, 0.0f, 1.0f);

        currentStyle.setColor(ImGuiCol.Header, 0.2f, 0.2f, 0.2f, 0.8f);
        currentStyle.setColor(ImGuiCol.HeaderActive, 0.3f, 0.3f, 0.3f, 1.0f);
        currentStyle.setColor(ImGuiCol.HeaderHovered, 0.25f, 0.25f, 0.25f, 0.9f);

        currentStyle.setColor(ImGuiCol.MenuBarBg, 0, 0, 0, 0);
        currentStyle.setColor(ImGuiCol.ModalWindowDimBg, 50, 50, 50, 50);

    }

    public void destroy()
    {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    private void drawDockSpace()
    {
        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
        ImGui.setNextWindowViewport(mainViewport.getID());

        ImGui.setNextWindowPos(0.0f, 0.0f);
        int[] windowSize = WindowManager.getMainSize();
        ImGui.setNextWindowSize(windowSize[0], windowSize[1]);

        int windowFlags = ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoTitleBar
                | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("DockSpace Window", new ImBoolean(true), windowFlags);

        this.mainMenuBar.run();

        ImGui.dockSpace(ImGui.getID("DockSpace"));

        ImGui.end();
    }

    public void start()
    {
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        drawDockSpace();
    }

    public void end()
    {
        ImGui.render();

        imGuiGl3.renderDrawData(ImGui.getDrawData());
        ImGui.endFrame();
    }
}
