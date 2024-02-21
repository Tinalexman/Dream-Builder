package dream.light;

import dream.node.Node;
import org.joml.Vector3f;

public class Light extends Node
{
    public static final int maxLights = 5;

    public final Vector3f position;
    public final Vector3f ambient;
    public final Vector3f diffuse;
    public final Vector3f specular;

    public Light()
    {
        super("Light");
        this.position = new Vector3f();
        this.ambient = new Vector3f(0.2f);
        this.diffuse = new Vector3f(0.5f);
        this.specular = new Vector3f(1.0f);
    }
}
