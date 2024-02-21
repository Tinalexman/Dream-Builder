package editor.overrides;

import dream.camera.Camera;
import dream.components.Component;
import dream.components.Material;
import dream.environment.SkyBox;
import dream.components.MeshRenderer;
import dream.components.Transform;
import dream.environment.Environment;
import dream.graphics.icon.Icons;
import dream.graphics.texture.Texture;
import dream.light.DirectionalLight;
import dream.light.Light;
import dream.light.PointLight;
import dream.light.SpotLight;
import dream.managers.NodeManager;
import dream.managers.ResourcePool;
import dream.model.Mesh;
import dream.model.VertexData;
import dream.node.Node;
import dream.postprocessing.FilterManager;
import dream.renderer.ForwardRenderer;
import dream.renderer.Renderer;
import dream.scene.Scene;
import dream.util.collection.Join;
import dream.util.contain.Containable;
import dream.util.contain.Contained;
import dream.util.contain.Container;
import dream.util.opengl.OpenGlUtils;
import editor.util.Controls;
import editor.windows.interfaces.*;
import editor.windows.Viewport;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;

public class WindowOverrides
{

    /*
        MISCELLANEOUS WINDOWS
     */

    private static boolean showAddNodes = false;

    public static void showMiscellaneous()
    {
        if(WindowOverrides.showAddNodes)
            WindowOverrides.addNodes.show();
    }

    public static DoubleModal<Scene,Node> addNodes = new DoubleModal<>("Add Nodes",
            "Add a new node to your scene")
    {
        private Container<String> root = new Container<>(NodeManager.tree());
        private String selectedNodeDescription = null;
        private String selectedNode = null;
        private final ImString filteredText = new ImString();
        private final int defaultTreeFlags = ImGuiTreeNodeFlags.OpenOnArrow
                | ImGuiTreeNodeFlags.OpenOnDoubleClick
                | ImGuiTreeNodeFlags.DefaultOpen;

        private void filterNodes()
        {
            this.root.clear();
            this.root = searchNode(NodeManager.tree(), this.filteredText.get());
        }

        private Container<String> searchNode(Container<String> root, String filter)
        {
            Container<String> result = new Container<>(root.name(), root.value());
            for(Containable<String> containable : root.getItems())
            {
                if(containable instanceof Container<String> container)
                {
                    Container<String> filtered = searchNode(container, filter);
                    if(filtered.size() > 0)
                        result.add(filtered);
                }
                else if(containable instanceof Contained<String> contained)
                {
                    if(contained.name().contains(filter))
                        result.add(contained);
                }
            }
            return result;
        }

        private void showRootNode()
        {
            if(this.root.size() == 0)
            {
                String description = "Invalid Search Parameters";
                ImVec2 size = ImGui.getContentRegionAvail();
                ImVec2 textSize = new ImVec2();
                ImGui.calcTextSize(textSize, description);
                ImGui.setCursorPos((size.x - textSize.x) * 0.5f, size.y * 0.5f);
                ImGui.text(description);
                this.selectedNode = null;
                return;
            }

            int flags = this.defaultTreeFlags;
            boolean isSelected = this.root.name().equals(this.selectedNode);
            if (isSelected)
                flags |= ImGuiTreeNodeFlags.Selected;

            boolean hasChildren = this.root.hasChildren();
            if(!hasChildren)
                flags |= ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.Bullet;

            boolean nodeOpen = ImGui.treeNodeEx(root.hashCode(), flags, root.name());
            if (ImGui.isItemClicked() && !ImGui.isItemToggledOpen())
            {
                this.selectedNode = root.name();
                this.selectedNodeDescription = root.value();
            }

            if(hasChildren && nodeOpen)
            {
                showFilteredNodes(root.getItems());
                ImGui.treePop();
            }
        }

        private void showFilteredNodes(List<Containable<String>> nodes)
        {
            for (int i = 0; i < nodes.size(); i++)
            {
                int flags = defaultTreeFlags;
                Containable<String> currentNode = nodes.get(i);
                boolean isSelected = currentNode.name().equals(this.selectedNode);
                if (isSelected)
                    flags |= ImGuiTreeNodeFlags.Selected;

                Container<String> container = null;
                boolean isContainer = currentNode.isContainer();
                boolean hasChildren = isContainer && (container = (Container<String>) currentNode).getItems().size() > 0;

                if(!hasChildren)
                    flags |= ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.Bullet;

                boolean nodeOpen = ImGui.treeNodeEx(i, flags, currentNode.name());
                if (ImGui.isItemClicked() && !ImGui.isItemToggledOpen())
                {
                    this.selectedNode = currentNode.name();
                    this.selectedNodeDescription = currentNode.value();
                }

                if(hasChildren && nodeOpen)
                {
                    showFilteredNodes(container.getItems());
                    ImGui.treePop();
                }
            }
        }

        @Override
        public void drawTopSheet()
        {
            ImVec2 size = ImGui.getContentRegionAvail();
            ImGui.pushItemWidth(size.x);
            ImGui.inputTextWithHint("##label", "Search For Node", this.filteredText);
            ImGui.spacing();
            ImGui.popItemWidth();
        }

        @Override
        public float getBottomHeight()
        {
            return 100.0f;
        }

        @Override
        public void drawContent()
        {
            ImGui.pushItemWidth(this.childSize.x - 10.0f);
            filterNodes();
            showRootNode();
            this.selection = NodeManager.createNode(this.selectedNode);
            ImGui.popItemWidth();
        }

        @Override
        public void drawBottomSheet()
        {
            ImGui.text("DESCRIPTION");
            ImGui.spacing();

            if(this.selectedNodeDescription != null)
                ImGui.text(this.selectedNodeDescription);

            ImGui.spacing();

            if (ImGui.button("Cancel", 120.0f, 0.0f))
            {
                WindowOverrides.showAddNodes = false;
                ImGui.closeCurrentPopup();
            }
            ImGui.sameLine();

            if (ImGui.button("Add Node", 120.0f, 0.0f))
            {
                WindowOverrides.showAddNodes = false;
                this.selection = NodeManager.createNode(this.selectedNode);
                this.observable.add(this.selection);
                ImGui.closeCurrentPopup();
            }
            ImGui.setItemDefaultFocus();
        }
    };


    /*
        SCENE WINDOWS
     */
    public static Viewport<Scene> sceneViewport()
    {
        return new Viewport<>()
        {
            private Renderer renderer;
            private Renderer.IDType idType;
            private ImInt rendererIDSelection, rendererFilterSelection;

            @Override
            protected void onConstructorEnd()
            {
                this.size = new float[] {0.0f, 0.0f};
                this.position = new float[] {0.0f, 0.0f};
                this.renderer = new ForwardRenderer(this.position, this.size);
                this.idType = Renderer.IDType.colorFramebuffer;
                this.rendererIDSelection = new ImInt(0);
                this.rendererFilterSelection = new ImInt(0);
                this.title = "Scene Viewport";

                OpenGlUtils.enableStencil();
            }

            @Override
            public Renderer renderer()
            {
                return this.renderer;
            }

            @Override
            public void preview()
            {
                this.renderer.render(this.value);
            }

            @Override
            protected void showImage(float width, float height)
            {
                ImGui.image(this.renderer.getID(this.idType), width, height , 0, 1, 1, 0);
            }

            @Override
            public void destroy()
            {
                this.renderer.destroy();
            }

            @Override
            public void input()
            {
                if(this.isActive && this.isFocused)
                    this.renderer.input();
            }

            @Override
            protected void showMenu()
            {
                ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 1.0f);
                if(ImGui.beginPopupContextItem("viewportSettings"))
                {
                    ImGui.textWrapped("Camera");
                    ImGui.separator();
                    this.renderer.useCamera(Controls.drawBooleanControl(this.renderer.useCamera(), "Use Camera"));
                    ImGui.beginDisabled(!this.renderer.useCamera());

                    Camera cam = this.renderer.getCamera();
                    Vector3f pos = cam.getPosition();

                    ImGui.text("Position: ");
                    ImGui.sameLine();
                    ImGui.text(pos.x + "\t" + pos.y + "\t" + pos.z);

                    ImGui.text("Pitch: " + cam.getPitch() + "\t\tYaw: " + cam.getYaw());

                    ImGui.text("Field Of View: ");
                    ImGui.sameLine();
                    ImGui.text("" + cam.getFieldOfView());

                    float[] arr = new float[] { cam.getNearPlane() };
                    boolean res = Controls.dragFloat("Near Plane", arr);
                    ImGui.sameLine();
                    Controls.drawHelpMarker("This is the minimum distance from the camera in which an object can be seen.");
                    if(res)
                        this.renderer.getCamera().setNearPlane(Math.max(0.001f, arr[0]));

                    arr = new float[] { cam.getFarPlane() };
                    res = Controls.dragFloat("Far Plane", arr);
                    ImGui.sameLine();
                    Controls.drawHelpMarker("This is the maximum distance from the camera in which an object can be seen.");
                    if(res)
                        this.renderer.getCamera().setFarPlane(Math.min(1000000.0f, arr[0]));

                    ImGui.endDisabled();

                    ImGui.newLine();

                    ImGui.textWrapped("Options");
                    ImGui.separator();
                    Controls.colorPicker4("Viewport Color", this.renderer.getClearColor());
                    this.renderer.postProcess(Controls.drawBooleanControl(this.renderer.postProcess(), "PostProcess"));
                    ImGui.beginDisabled(!this.renderer.postProcess());
                    ImGui.text("Filters:");
                    ImGui.sameLine();
                    ImGui.pushItemWidth(180.0f);
                    String[] filterNames = FilterManager.getFilters().keySet().toArray(new String[0]);
                    ImGui.combo("##Filters", this.rendererFilterSelection, filterNames, ImGuiComboFlags.NoArrowButton);
                    this.renderer.setFilterIndex(this.rendererFilterSelection.get());
                    ImGui.popItemWidth();
                    ImGui.endDisabled();

                    ImGui.text("Framebuffer:");
                    ImGui.sameLine();
                    ImGui.pushItemWidth(180.0f);
                    ImGui.combo("##IDType", this.rendererIDSelection, new String[] {"Color Framebuffer", "Picking Texture"},
                            ImGuiComboFlags.NoArrowButton);
                    ImGui.popItemWidth();
                    this.idType = switch (this.rendererIDSelection.get())
                            {
                                case 0 -> Renderer.IDType.colorFramebuffer;
                                case 1 -> Renderer.IDType.pickingTexture;
                                default -> throw new IllegalStateException("Unexpected value: " + this.rendererIDSelection.get());
                            };

                    ImGui.endPopup();
                }
                ImGui.popStyleVar();
            }
        };
    }

    public static ContentWindow<Scene> sceneContent()
    {
        return new ContentWindow<>("Scene Browser")
        {
            final int folderIcon = ResourcePool.getIcon(Icons.folder);
            final float imageSize = this.size - 30.0f;

            @Override
            public void showSingle(int index)
            {
                ImGui.beginChild("##" + this.title + ":" + index, this.size, this.size, false, 0);
                ImGui.image(this.folderIcon, this.imageSize, this.imageSize, 0, 1, 1, 0);
                Scene value = this.content.get(index);
                //beginDragSource("Scene", this.folderIcon, value);
                //beginDragTarget(content.get(i));
                ImGui.spacing();
                ImGui.sameLine(5.0f);
                ImGui.text(value.name());
                ImGui.endChild();
            }
        };
    }

    public static ObservableWindow<Join<Node>> sceneInspector()
    {
        return new ObservableWindow<>("Scene Inspector")
        {

            @Override
            protected void onConstructorEnd()
            {
                this.windowFlags |= ImGuiWindowFlags.MenuBar;
            }

            private void light(Light light)
            {
                if(light instanceof DirectionalLight directionalLight)
                {
                    ImGui.text("Direction:");
                    ImGui.sameLine();
                    Controls.drawVector3Control("##pos", directionalLight.direction);
                }
                else
                {
                    ImGui.text("Position:");
                    ImGui.sameLine();
                    Controls.drawVector3Control("##pos", light.position);
                }

                ImGui.text("Ambience:");
                ImGui.sameLine();
                Controls.colorPicker3("##ambient", light.ambient);

                ImGui.text("Diffuse:");
                ImGui.sameLine();
                Controls.colorPicker3("##diffuse", light.diffuse);

                ImGui.text("Specular:");
                ImGui.sameLine();
                Controls.colorPicker3("##specular", light.specular);

                if(light instanceof PointLight pointLight)
                {
                    float[] val = { pointLight.constant };
                    boolean res = Controls.dragFloat("Constant", val);
                    if(res)
                        pointLight.constant = val[0];

                    val[0] = pointLight.linear;
                    res = Controls.dragFloat("Linear", val);
                    if(res)
                        pointLight.linear = val[0];

                    val[0] = pointLight.quadratic;
                    res = Controls.dragFloat("Quadratic" ,val);
                    if(res)
                        pointLight.quadratic = val[0];
                }
                else if(light instanceof SpotLight spotLight)
                {
                    float[] val = { spotLight.cutoff };
                    boolean res = Controls.dragFloat("CutOff", val);
                    if(res)
                        spotLight.cutoff = val[0];

                    val[0] = spotLight.outerCutoff;
                    res = Controls.dragFloat("Outer CutOff", val);
                    if(res)
                        spotLight.outerCutoff = val[0];
                }
            }

            private void meshRenderer(MeshRenderer renderer)
            {
                Mesh mesh = renderer.getMesh();
                VertexData data = mesh.vertexData();
                float indent = 10.0f;

                ImGui.text("Vertices: ");
                ImGui.indent(indent);

                int length = data.position().length;
                ImGui.text("Position: " + length + " ( " +
                        (Float.BYTES * length) + " bytes )");
                length = data.uv().length;
                ImGui.text("UV: " + length + " ( " +
                        (Float.BYTES * length) + " bytes )");
                length = data.normal().length;
                ImGui.text("Normals: " + length + " ( " +
                        (Float.BYTES * length) + " bytes )");

                ImGui.unindent(indent);

                ImGui.text("Indices: " + mesh.count() + " ( "
                        + (Integer.BYTES * mesh.count()) + " bytes )");
                ImGui.indent(indent);

                ImGui.text("1 subMesh:");
                ImGui.text("#0 " + (mesh.count() / 3) + " triangles ( " + mesh.count()
                        + " indices starting from 0 )");

                ImGui.unindent(indent);

                float width = 120.0f;
                ImGui.text("Culling:");
                ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 66, 150, 250, 102);
                ImGui.sameLine();
                ImInt selection = new ImInt(renderer.getCull());
                ImGui.pushItemWidth(width);
                boolean res = ImGui.combo("##cull", selection, MeshRenderer.Configuration.cullOptions,
                        ImGuiComboFlags.NoArrowButton);
                ImGui.popItemWidth();
                ImGui.sameLine();
                Controls.drawHelpMarker("Culling determines which faces of a mesh are rendered.");
                if(res)
                    renderer.setCull(selection.get());

                ImGui.text("Face Mode:");
                ImGui.sameLine();
                selection.set(renderer.getFace());
                ImGui.pushItemWidth(width);
                res = ImGui.combo("##face", selection, MeshRenderer.Configuration.faceOptions,
                        ImGuiComboFlags.NoArrowButton);
                ImGui.popItemWidth();
                ImGui.sameLine();
                Controls.drawHelpMarker("Face Mode determines how the faces of a mesh are rendered.");
                if(res)
                    renderer.setFace(selection.get());
                ImGui.popStyleColor();
            }

            private void transform(Transform transform)
            {
                ImGui.text("Position:");
                boolean pos = Controls.drawVector3Control("##pos", transform.getPosition());

                ImGui.text("Rotation:");
                boolean rot = Controls.drawVector3Control("##rot", transform.getOrientation());
                Vector3f orientation = transform.getOrientation();
                orientation.x %= 360.0;
                orientation.y %= 360.0;
                orientation.z %= 360.0;

                ImGui.text("Scale:");
                boolean sc = Controls.drawVector3Control("##sc", transform.getScale());

                transform.change(pos || rot || sc);
            }

            private void material(Material material)
            {
                ImGui.text("Diffuse Color:");
                ImGui.sameLine();
                Controls.colorPicker3("##diffuse", material.diffuse);

                ImGui.text("Specular Color:");
                ImGui.sameLine();
                Controls.colorPicker3("##specular", material.specular);

                float width = 150.0f;

                if(material.hasDiffuse())
                {
                    Texture diffuse = material.getPack().diffuse;
                    int index = diffuse.filePath.lastIndexOf("\\");
                    String textureName = diffuse.filePath.substring(index + 1);

                    ImGui.text("Diffuse Map:");
                    ImGui.sameLine();
                    ImGui.pushItemWidth(width);
                    ImGui.combo("##" + textureName, new ImInt(1), new String[]{"None", textureName},
                            ImGuiComboFlags.NoArrowButton);
                    ImGui.popItemWidth();

                    if(ImGui.isItemHovered())
                    {
                        ImGui.beginTooltip();
                        ImGui.text(textureName);
                        ImGui.separator();
                        ImGui.sameLine(10.0f);
                        ImGui.image(diffuse.ID, 80.0f, 80.0f, 0, 1, 1, 0);
                        ImGui.endTooltip();
                    }
                }

                if(material.hasSpecular())
                {
                    Texture specular = material.getPack().specular;
                    int index = specular.filePath.lastIndexOf("\\");
                    String textureName = specular.filePath.substring(index + 1);

                    ImGui.text("Specular Map:");
                    ImGui.sameLine();
                    ImGui.pushItemWidth(width);
                    ImGui.combo("##" + textureName, new ImInt(1), new String[]{"None", textureName},
                            ImGuiComboFlags.NoArrowButton);
                    ImGui.popItemWidth();

                    if(ImGui.isItemHovered())
                    {
                        ImGui.beginTooltip();
                        ImGui.text(textureName);
                        ImGui.separator();
                        ImGui.newLine();
                        ImGui.sameLine(10.0f);
                        ImGui.image(specular.ID, 80.0f, 80.0f, 0, 1, 1, 0);
                        ImGui.endTooltip();
                    }
                }
            }

            @Override
            protected void contents()
            {
                Node selected;
                if((selected = this.value.value) == null)
                    return;

                ImGui.indent(5.0f);
                boolean visibility = Controls.drawBooleanControl(selected.visible(), "Visibility");
                selected.visible(visibility);
                ImGui.unindent(5.0f);

                ImGui.pushStyleColor(ImGuiCol.Header, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0, 0, 0, 0);
                for (Component c : selected.getComponents())
                {
                    ImGui.indent(5.0f);
                    if (ImGui.collapsingHeader(c.getClass().getSimpleName(), ImGuiTreeNodeFlags.DefaultOpen))
                    {
                        if (c.getClass().isAssignableFrom(Transform.class))
                            transform((Transform) c);
                        else if (c.getClass().isAssignableFrom(Material.class))
                            material((Material) c);
                        else if (c.getClass().isAssignableFrom(MeshRenderer.class))
                            meshRenderer((MeshRenderer) c);
                    }
                    ImGui.unindent(5.0f);
                }

                if(selected instanceof Light light)
                    light(light);
                ImGui.popStyleColor(3);

            }
        };
    }

    public static DoubleObservableWindow<Scene, Join<Node>> sceneNodeGraph()
    {
        return new DoubleObservableWindow<>("NodeGraph")
        {
            enum DragMode
            {
                move,
                swap
            }

            private DragMode dragMode;

            @Override
            protected void onConstructorEnd()
            {
                this.dragMode = DragMode.swap;
            }

            private void contextMenu()
            {
                if(ImGui.beginPopupContextWindow())
                {
                    if(ImGui.menuItem("Add Child"))
                    {
                        if(this.second.value != null)
                        {
                            WindowOverrides.showAddNodes = true;
                            //this.first.add(new Node());
                        }
                    }

                    ImGui.separator();

                    ImGui.menuItem("Copy");
                    ImGui.menuItem("Paste Into");

                    ImGui.separator();

                    if(ImGui.beginMenu("Drag Mode"))
                    {
                        if(ImGui.menuItem("Swap", "", this.dragMode == DragMode.swap))
                            this.dragMode = DragMode.swap;

                        if(ImGui.menuItem("Move Into", "", this.dragMode == DragMode.move))
                            this.dragMode = DragMode.move;

                        ImGui.endMenu();
                    }

                    ImGui.separator();

                    if(ImGui.menuItem("Delete Selected"))
                    {
                        if(this.second.value != null)
                        {
                            this.first.remove(this.second.value);
                            this.second.value = null;
                        }
                    }

                    ImGui.endPopup();
                }
            }

            private void root(Node root)
            {
                if(root == null)
                    return;

                int groupFlag = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen;
                if(this.second.value != null && this.second.value.equals(root))
                    groupFlag |= ImGuiTreeNodeFlags.Selected;

                ImGui.pushItemWidth(80.0f);

                boolean hasChildren = root.hasChildren();
                if(!hasChildren)
                    groupFlag |= ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.Bullet;

                boolean groupOpen = ImGui.treeNodeEx(root.hashCode(), groupFlag, root.name());
                if(ImGui.isItemClicked() || ImGui.isItemToggledOpen())
                    this.second.value = root;

                beginDragSource(root, 0);
                beginDragTarget(root, 0);

                ImGui.popItemWidth();

                if(groupOpen && root.hasChildren())
                {
                    for(Node node : root.getChildren())
                        root(node);
                    ImGui.treePop();
                }
            }

            private void beginDragSource(Node currentNode, int i)
            {
                if (ImGui.beginDragDropSource())
                {
                    if(this.dragMode == DragMode.swap)
                    {
                        ImGui.setDragDropPayload("NodeIndex", i, 0);
                        ImGui.text("Swapping " + currentNode.name());
                    }
                    else if(this.dragMode == DragMode.move)
                    {
                        ImGui.setDragDropPayload("NodeObject", currentNode, 0);
                        ImGui.text("Moving " + currentNode.name());
                    }
                    ImGui.endDragDropSource();
                }
            }

            private void beginDragTarget(Node currentNode, int i)
            {
                if (ImGui.beginDragDropTarget())
                {
                    Object payload = ImGui.acceptDragDropPayload("NodeIndex");
                    if(payload != null)
                    {
                        int oldIndex = (Integer) payload;
                        if(i != oldIndex)
                            Collections.swap(this.first.root().getChildren(), oldIndex, i);
                    }

                    payload = ImGui.acceptDragDropPayload("NodeObject");
                    if(payload != null)
                    {
                        Node node = (Node) payload;
                        currentNode.getChildren().add(node);
                        node.parent(currentNode);
                        this.first.remove(node);
                    }

                    ImGui.endDragDropTarget();
                }
            }

            @Override
            protected void contents()
            {
                contextMenu();
                root(this.first.root());
            }
        };
    }

    /*
        ENVIRONMENT WINDOWS
     */
    public static Viewport<Environment> environmentViewport()
    {
        return new Viewport<>()
        {
            private Renderer renderer;

            @Override
            protected void onConstructorEnd()
            {
                this.size = new float[] {0.0f, 0.0f};
                this.position = new float[] {0.0f, 0.0f};
                this.renderer = new ForwardRenderer(this.position, this.size);
                this.title = "Environment Viewport";
            }

            @Override
            protected void preview()
            {
                this.renderer.start();
                this.value.show(this.renderer.getCamera());
                this.renderer.stop();
            }

            @Override
            protected void showImage(float width, float height)
            {
                ImGui.image(this.renderer.getID(Renderer.IDType.colorFramebuffer), width, height , 0, 1, 1, 0);
            }

            @Override
            public void destroy()
            {
                this.renderer.destroy();
            }

            @Override
            public void input()
            {
                if(this.isActive && this.isFocused)
                    this.renderer.input();
            }
        };
    }

    public static ObservableWindow<Environment> environmentSettings()
    {
        return new ObservableWindow<>("Environment Settings")
        {
            @Override
            protected void contents()
            {
                if(ImGui.collapsingHeader("SkyBox"))
                {
                    SkyBox skyBox = this.value.getSkyBox();
                    skyBox.active(Controls.drawBooleanControl(skyBox.active(), "Active"));

                    String[] images = skyBox.getPaths();
                    String[] descriptions = { "Right", "Left", "Top", "Bottom", "Front", "Back" };
                    for(int i = 0; i < images.length; ++i)
                    {
                        ImGui.text(descriptions[i] + ":");
                        ImGui.sameLine();
                        String filename = images[i].substring(images[i].lastIndexOf("\\") + 1);
                        ImGui.inputText("##text" + i, new ImString(filename));
                    }

                    if(ImGui.button("Load"))
                        skyBox.load();
                }
            }
        };
    }

}
