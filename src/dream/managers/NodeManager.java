package dream.managers;

import dream.components.Material;
import dream.model.Mesh;
import dream.model.MeshFactory;
import dream.components.MeshRenderer;
import dream.components.Transform;
import dream.light.DirectionalLight;
import dream.light.PointLight;
import dream.light.SpotLight;
import dream.node.Node;
import dream.node.drawable.DrawableNode;
import dream.util.contain.Contained;
import dream.util.contain.Container;

public class NodeManager
{
    private static final Container<String> nodeTree = new Container<>("Node", "The parent of all objects");

    public static void loadTree()
    {
        Container<String> category = new Container<>("Shape", "A geometric node, with meshes, that can be drawn onscreen");
        Contained<String> item = new Contained<>("Cube", "A cube shape");
        category.add(item);
        item = new Contained<>("Plane", "A plane shape");
        category.add(item);
        NodeManager.nodeTree.add(category);

        category = new Container<>("Light", "A source of lighting to your scene");
        item = new Contained<>("Directional Light", "A non positional light source that shines in a certain direction");
        category.add(item);
        item = new Contained<>("Point Light", "A positional light source that shines in all directions");
        category.add(item);
        item = new Contained<>("Spot Light", "A positional light source that shines in a certain direction");
        category.add(item);
        NodeManager.nodeTree.add(category);


        NodeManager.nodeTree.sort();
    }

    public static Node createNode(String nodeClass)
    {
        if(nodeClass == null)
            return null;

        switch (nodeClass)
        {
            case "Cube" ->
            {
                DrawableNode cube = new DrawableNode();
                cube.name("Cube");
                Mesh cubeMesh = MeshFactory.cube(1.0f);
                MeshRenderer cubeRenderer = new MeshRenderer();
                cubeRenderer.setMesh(cubeMesh);
                cube.addComponent(new Material());
                cube.addComponent(new Transform());
                cube.addComponent(cubeRenderer);
                return cube;
            }
            case "Plane" ->
            {
                DrawableNode plane = new DrawableNode();
                plane.name("Plane");
                Mesh planeMesh = MeshFactory.plane(1, 1.0f);
                MeshRenderer planeRenderer = new MeshRenderer();
                planeRenderer.setMesh(planeMesh);
                plane.addComponent(new Material());
                plane.addComponent(new Transform());
                plane.addComponent(planeRenderer);
                return plane;
            }
            case "Directional Light" ->
            {
                return new DirectionalLight();
            }
            case "Point Light" ->
            {
                return new PointLight();
            }
            case "SpotLight" ->
            {
                return new SpotLight();
            }
            case "Node" ->
            {
                return new Node();
            }
            default ->
            {
                return null;
            }
        }

    }

    public static Container<String> tree()
    {
        return NodeManager.nodeTree;
    }

}
