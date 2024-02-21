package dream.environment;

import dream.Engine;
import dream.camera.Camera;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL20.*;

public class Environment
{
    private final SkyBox skyBox;

    public Environment()
    {
        this.skyBox = new SkyBox();
        String path = Engine.resourcePath + "\\skybox\\";
        this.skyBox.setImagePaths(path + "right.jpg", path + "left.jpg", path + "top.jpg",
                path + "bottom.jpg", path + "front.jpg", path + "back.jpg");
    }

    public void show(Camera camera)
    {
        glDepthMask(false);
        glDepthFunc(GL_LEQUAL);

        this.skyBox.start();
        Matrix4f view = new Matrix4f(new Matrix3f(camera.getView()));
        this.skyBox.loadUniforms(camera.getProjection(), view);
        this.skyBox.show();
        this.skyBox.stop();

        glDepthMask(true);
        glDepthFunc(GL_LESS);
    }

    public SkyBox getSkyBox()
    {
        return this.skyBox;
    }


}
