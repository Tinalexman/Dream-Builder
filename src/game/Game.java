package game;

import dream.Engine;
import dream.components.Material;
import dream.components.MeshRenderer;
import dream.components.Transform;
import dream.environment.Environment;
import dream.light.PointLight;
import dream.model.Mesh;
import dream.model.MeshFactory;
import dream.model.Model;
import dream.node.drawable.DrawableNode;
import dream.scene.Scene;
import dream.util.loader.ModelLoader;

import java.util.HashMap;
import java.util.Map;

public class Game
{
    private static Game game;

    public static Game game()
    {
        if(Game.game == null)
            Game.game = new Game();

        return Game.game;
    }

    private final GameEnvironment gameEnvironment;
    private final Map<String, Scene> scenes;

    private Game()
    {
        this.gameEnvironment = new GameEnvironment();
        this.scenes = new HashMap<>();

        defaultScene();
    }

    private void defaultScene()
    {
        Scene scene = new Scene();

        DrawableNode cube = new DrawableNode();
        cube.name("Cube");
        Mesh cubeMesh = MeshFactory.cube(1.0f);
        MeshRenderer cubeRenderer = new MeshRenderer();
        cubeRenderer.setMesh(cubeMesh);
        cube.addComponent(new Material());
        Transform t = new Transform();
        t.scale.set(0.1f);
        t.incrementRotation(0.0f, 30.0f, 0.0f);
        cube.addComponent(t);
        cube.addComponent(cubeRenderer);
        scene.add(cube);

        DrawableNode plane = new DrawableNode();
        plane.name("Plane");
        Mesh planeMesh = MeshFactory.plane(1, 1.0f);
        MeshRenderer planeRenderer = new MeshRenderer();
        planeRenderer.setMesh(planeMesh);
        plane.addComponent(new Material());
        t = new Transform();
        t.position.set(0.0f, -0.5f, 0.0f);
        plane.addComponent(t);
        plane.addComponent(planeRenderer);
        scene.add(plane);

        PointLight light = new PointLight();
        light.position.set(0.0f, 1.0f, 1.0f);
        scene.add(light);

        Model model = ModelLoader.load(Engine.resourcePath + "\\models\\barrel.obj");
        if(model != null)
        {
            model.addComponent(new Transform());
            model.addComponent(new Material());
            model.addComponent(new MeshRenderer());
            scene.add(model);
        }


        this.scenes.put(Constants.mainScene, scene);
    }

    public Scene mainScene()
    {
        return this.scenes.get(Constants.mainScene);
    }

    public Environment getEnvironment()
    {
        return this.gameEnvironment.environment();
    }
}
