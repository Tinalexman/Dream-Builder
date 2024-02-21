package editor;

import dream.environment.Environment;
import dream.model.MeshFactory;
import dream.model.VertexData;
import dream.node.Node;
import dream.scene.Scene;
import dream.util.collection.Join;
import editor.gui.DearImGui;
import editor.overrides.WindowOverrides;
import editor.windows.interfaces.ContentWindow;
import editor.windows.Viewport;
import editor.windows.EditorWindow;
import editor.windows.interfaces.DoubleObservableWindow;
import editor.windows.interfaces.ObservableWindow;
import game.Game;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

import java.util.*;

import static editor.util.Constants.menuBarTabs;

public class Editor
{
    private final DearImGui dearImGui;
    private final Map<Integer, List<EditorWindow>> editorWindows;
    private int currentMenuIndex;

    public Editor()
    {
        this.dearImGui = new DearImGui();
        this.editorWindows = new HashMap<>();
        this.currentMenuIndex = 1;
    }

    public void initialize(long windowID)
    {
        this.dearImGui.create(windowID);
        this.dearImGui.setMenuBarCallBack(() ->
        {
            if(ImGui.beginMenuBar())
            {
                for(int i = 0; i < menuBarTabs.length; i++)
                {
                    boolean selected = this.currentMenuIndex == i;
                    ImGui.pushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 50, 50, 50, 150);

                    if(selected)
                    {
                        ImGui.pushStyleColor(ImGuiCol.Button, 40, 40, 40, 150);
                        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 80, 80, 80, 200);
                    }
                    else
                    {
                        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                    }

                    if(ImGui.button(menuBarTabs[i]))
                        currentMenuIndex = i;
                    ImGui.popStyleColor(4);
                }

                ImGui.endMenuBar();
            }
        });

        // Scenes Windows
        List<EditorWindow> sceneWindows = new ArrayList<>();
        DoubleObservableWindow<Scene, Join<Node>> nodeGraph = WindowOverrides.sceneNodeGraph();
        ObservableWindow<Join<Node>> inspector = WindowOverrides.sceneInspector();
        Viewport<Scene> viewport = WindowOverrides.sceneViewport();
        ContentWindow<Scene> contents = WindowOverrides.sceneContent();
        Collections.addAll(sceneWindows, viewport, contents, inspector, nodeGraph);
        this.editorWindows.put(1, sceneWindows);

        // Environment
        List<EditorWindow> environmentWindows = new ArrayList<>();
        Viewport<Environment> environmentViewport = WindowOverrides.environmentViewport();
        ObservableWindow<Environment> environmentSettings = WindowOverrides.environmentSettings();
        Collections.addAll(environmentWindows, environmentSettings, environmentViewport);
        this.editorWindows.put(2, environmentWindows);

        // Materials
        List<EditorWindow> materialWindows = new ArrayList<>();
        EditorWindow materialInspector = new EditorWindow("Material Inspector");
        EditorWindow materialView = new EditorWindow("Material View");
        Collections.addAll(materialWindows, materialView, materialInspector);
        this.editorWindows.put(3, materialWindows);

        Scene scene = Game.game().mainScene();

        WindowOverrides.addNodes.observable(scene);

        Environment environment = Game.game().getEnvironment();
        environmentViewport.set(environment);
        environmentSettings.set(environment);

        viewport.set(scene);
        contents.addContent(scene);

        Join<Node> join = new Join<>(scene.root());
        nodeGraph.first(scene);
        nodeGraph.second(join);
        inspector.set(join);

        viewport.renderer().setSelectedNode(nodeGraph.second());
    }

    public void refresh()
    {
        this.dearImGui.start();

        List<EditorWindow> windows = this.editorWindows.get(this.currentMenuIndex);
        if(windows != null)
            windows.forEach(EditorWindow::show);

        WindowOverrides.showMiscellaneous();

        //ImGui.showStyleEditor();
        //ImGui.showDemoWindow();

        this.dearImGui.end();
    }

    public void input()
    {
        List<EditorWindow> windows = this.editorWindows.get(this.currentMenuIndex);
        if(windows != null)
            windows.forEach(EditorWindow::input);
    }

    public void destroy()
    {
        this.dearImGui.destroy();
        for(List<EditorWindow> window : this.editorWindows.values())
            window.forEach(EditorWindow::destroy);
    }

}
