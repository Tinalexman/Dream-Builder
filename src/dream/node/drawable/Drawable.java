package dream.node.drawable;

import dream.shader.Shader;

public interface Drawable
{
    Shader getShader();
    void setShader(Shader shader);
}
