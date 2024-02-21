package dream.renderer;

import dream.Engine;
import dream.camera.Camera;
import dream.components.Material;
import dream.components.MeshRenderer;
import dream.components.Transform;
import dream.io.Input;
import dream.light.DirectionalLight;
import dream.light.Light;
import dream.light.PointLight;
import dream.light.SpotLight;
import dream.managers.ResourcePool;
import dream.managers.WindowManager;
import dream.model.Mesh;
import dream.model.Model;
import dream.node.Node;
import dream.node.drawable.Drawable;
import dream.scene.Scene;
import dream.shader.Shader;
import dream.shader.ShaderConstants;
import dream.util.opengl.OpenGlUtils;
import game.Game;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

import static dream.shader.ShaderConstants.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


public class ForwardRenderer extends Renderer
{
    private final float[] position;
    private final float[] size;
    private final Shader shader;

    public ForwardRenderer(float[] position, float[] size)
    {
        super();

        this.position = position;
        this.size = size;
        this.shader = ResourcePool.addAndGetShader("color");
        this.shader.storeUniforms(transformation, view, projection, color);
    }

    @Override
    public void render(Scene scene)
    {
        if(scene == null)
            return;

        Node root = scene.root();
        List<Light> lights = scene.lights();
        if(root == null)
            return;

        if(!this.useCamera)
        {
            this.colorBuffer.stop();
            return;
        }

        picking(root.getChildren());

        start();

        Game.game().getEnvironment().show(this.camera);

        renderColorBuffer(root, lights);
        for(Node node : root.getChildren())
            renderColorBuffer(node, lights);

        stop();
    }

    private void renderColorBuffer(Node node, List<Light> lights)
    {
        Shader shader = (node instanceof Drawable drawable) ? drawable.getShader() : null;
        if(shader == null || !node.visible())
            return;

        MeshRenderer renderer = node.getComponent(MeshRenderer.class);
        Transform transform = node.getComponent(Transform.class);
        Material material = node.getComponent(Material.class);

        if(renderer == null || transform == null || material == null)
            return;

        boolean outline = node.equals(this.selectedNode.value);
        if(outline)
        {
            glStencilFunc(GL_ALWAYS, 1, 0xFF);
            glStencilMask(0xFF);
        }
        else
            glStencilMask(0x00);

        if(material.transparency)
            OpenGlUtils.enableAlphaBlending();

        shader.start();
        material.getPack().start();

        if(!material.transparency)
            loadLights(lights, shader);

        shader.uniform(transformation, transform.getMatrix());
        shader.uniform(view, this.camera.getView());
        shader.uniform(projection, this.camera.getProjection());

        if(!material.transparency)
        {
            Matrix4f temp = new Matrix4f();
            Matrix3f inverse = new Matrix3f(transform.getMatrix().invert(temp));
            inverse.transpose();
            shader.uniform(inverseNormals, inverse);
            shader.uniform(viewPosition, this.camera.getPosition());

            loadMaterial(material, shader);
        }
        if(node instanceof Model model)
            renderModel(model, renderer, camera, transform);
        else
            renderer.render();

        if(outline)
            outline(transform, renderer);

        OpenGlUtils.disableBlending();

        material.getPack().stop();
        shader.stop();
    }

    private void renderModel(Model model, MeshRenderer renderer, Camera camera, Transform transform)
    {
        this.shader.start();

        this.shader.uniform(transformation, transform.getMatrix());
        this.shader.uniform(view, camera.getView());
        this.shader.uniform(projection, camera.getProjection());
        this.shader.uniform(color, 0.24725f, 0.1995f, 0.0745f);

        for(Mesh mesh : model.meshes())
        {
            renderer.setMesh(mesh);
            renderer.render();
        }

        this.shader.stop();
    }

    private void loadLights(List<Light> lights, Shader shader)
    {
        float NONE = 0.0f, DIRECTIONAL_LIGHT = 1.0f, POINT_LIGHT = 2.0f, SPOT_LIGHT = 3.0f;

        for(int i = 0; i < lights.size(); ++i)
        {
            Light light = lights.get(i);
            if(!light.visible())
            {
                shader.uniform("lights[" + i + "].type", NONE);
                continue;
            }

            shader.uniform("lights[" + i + "]." + ambient, light.ambient);
            shader.uniform("lights[" + i + "]." + diffuse, light.diffuse);
            shader.uniform("lights[" + i + "]." + specular, light.specular);

            if(light instanceof DirectionalLight directionalLight)
            {
                shader.uniform("lights[" + i + "].type", DIRECTIONAL_LIGHT);
                shader.uniform("lights[" + i + "]." + direction, directionalLight.direction);
            }
            else if(light instanceof PointLight pointLight)
            {
                shader.uniform("lights[" + i + "].type", POINT_LIGHT);
                shader.uniform("lights[" + i + "]." + ShaderConstants.position, light.position);
                shader.uniform("lights[" + i + "]." + constant, pointLight.constant);
                shader.uniform("lights[" + i + "]." + linear, pointLight.linear);
                shader.uniform("lights[" + i + "]." + quadratic, pointLight.quadratic);
            }
            else if(light instanceof SpotLight spotLight)
            {
                shader.uniform("lights[" + i + "].type", SPOT_LIGHT);
                shader.uniform("lights[" + i + "]." + ShaderConstants.position, light.position);
                shader.uniform("lights[" + i + "]." + direction, spotLight.direction);
                shader.uniform("lights[" + i + "]." + cutoff, spotLight.cutoff);
                shader.uniform("lights[" + i + "]." + outerCutoff, spotLight.outerCutoff);
            }
        }
    }

    private void loadMaterial(Material material, Shader shader)
    {
        shader.uniform(materialDiffuse, material.diffuse);
        shader.uniform(materialSpecular, material.specular);
        shader.uniform(materialReflectance, material.reflectance);

        boolean hasDiffuse = material.hasDiffuse();
        shader.uniform(materialHasDiffuseMap, hasDiffuse);
        if(hasDiffuse)
            shader.uniform(materialDiffuseMap, 0);

        boolean hasSpecular = material.hasSpecular();
        shader.uniform(materialHasSpecularMap, hasSpecular);
        if(hasSpecular)
            shader.uniform(materialSpecularMap, 1);
    }

    @Override
    public void input()
    {
        if(!this.useCamera)
            return;

        int[] winSize = WindowManager.getMainSize();
        float[] coordinates = Input.getScreenCoordinates(winSize, position, size);

        if(coordinates[0] < 0 || coordinates[0] > winSize[0] ||
                coordinates[1] < 0 || coordinates[1] > winSize[1])
            return;

        if(Input.getScrollY() != 0.0f)
            this.camera.incrementZoom(-Input.getScrollY() * 0.1f);

        if(Input.isButtonJustPressed(GLFW_MOUSE_BUTTON_LEFT))
        {
            int x = (int) coordinates[0], y = (int) coordinates[1];
            float[] ID = readPixelAt(x, y);
            this.selectedNode.value = Node.getNode((int) ID[0] - 1);
        }

        if(Input.isButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
        {
            Vector2f mouseDelta = Input.getMouseDelta();
            mouseDelta.mul(0.25f); // Reduce the sensitivity
            this.camera.rotate(mouseDelta.y , mouseDelta.x);
        }

        float speed = (float) (Engine.deltaTime * Engine.nanoSeconds);
        if(Input.isKeyShiftPressed())
            speed *= 2.5f;

        Vector3f temp = new Vector3f();

        if(Input.isKeyPressed(GLFW_KEY_W))
        {
            this.camera.getForward().mul(speed, temp);
            this.camera.incrementPosition(temp);
        }

        if(Input.isKeyPressed(GLFW_KEY_S))
        {
            this.camera.getForward().mul(-speed, temp);
            this.camera.incrementPosition(temp);
        }

        if(Input.isKeyPressed(GLFW_KEY_LEFT))
        {
            this.camera.getRight().mul(-speed, temp);
            this.camera.incrementPosition(temp);
        }

        if(Input.isKeyPressed(GLFW_KEY_RIGHT))
        {
            this.camera.getRight().mul(speed, temp);
            this.camera.incrementPosition(temp);
        }

        if(Input.isKeyPressed(GLFW_KEY_UP))
        {
            this.camera.getUpVector().mul(speed, temp);
            this.camera.incrementPosition(temp);
        }

        if(Input.isKeyPressed(GLFW_KEY_DOWN))
        {
            this.camera.getUpVector().mul(-speed, temp);
            this.camera.incrementPosition(temp);
        }
    }

    private void pickingUniforms(Matrix4f transform, int position, int nodeID)
    {
        this.pickingShader.uniform(transformation, transform);

        if(this.camera.hasViewChanged())
            this.pickingShader.uniform(view, this.camera.getView());
        if(this.camera.hasProjectionChanged())
            this.pickingShader.uniform(projection, this.camera.getProjection());

        this.pickingShader.uniform(drawIndex, (float) position);
        this.pickingShader.uniform(objectIndex, (float) nodeID);
    }

    private void picking(List<Node> nodes)
    {
        this.pickingTexture.start();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        this.pickingShader.start();

        for(int pos = 0; pos < nodes.size(); ++pos)
        {
            Node node = nodes.get(pos);
            if(node instanceof Drawable)
            {
                Transform transform = node.getComponent(Transform.class);
                MeshRenderer renderer = node.getComponent(MeshRenderer.class);

                pickingUniforms(transform.getMatrix(), pos, (node.getID() + 1));

                renderer.render();
            }
        }

        this.pickingShader.stop();
        this.pickingTexture.stop();
    }

    private void shadow(List<Node> nodes)
    {

    }

    @Override
    protected void outline(Transform transform, MeshRenderer renderer)
    {
        glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
        glStencilMask(0x00);
        glDisable(GL_DEPTH_TEST);

        Vector3f originalScale = new Vector3f(transform.scale);
        transform.incrementScale(this.outlineSize, this.outlineSize, this.outlineSize);

        this.outlineShader.start();

        this.outlineShader.uniform(ShaderConstants.color, this.outlineColor.x, this.outlineColor.y, this.outlineColor.z);
        this.outlineShader.uniform(ShaderConstants.transformation, transform.getMatrix());
        this.outlineShader.uniform(ShaderConstants.view, camera.getView());
        this.outlineShader.uniform(ShaderConstants.projection, camera.getProjection());

        renderer.render();

        transform.scale.set(originalScale);
        transform.change();

        this.outlineShader.stop();

        glStencilMask(0xFF);
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glEnable(GL_DEPTH_TEST);
    }

}
