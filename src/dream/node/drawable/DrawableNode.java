package dream.node.drawable;

import dream.managers.ResourcePool;
import dream.node.Node;
import dream.shader.Shader;

public class DrawableNode extends Node implements Drawable
{
    protected Shader shader;

    public DrawableNode()
    {
        super("Drawable Node");
        this.shader = ResourcePool.defaultMesh();
    }

    @Override
    public Shader getShader()
    {
        return this.shader;
    }

    @Override
    public void setShader(Shader shader)
    {
        this.shader = shader;
    }
}
