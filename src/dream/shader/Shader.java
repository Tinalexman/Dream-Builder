package dream.shader;

import dream.Engine;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL33.*;

public class Shader
{
    protected static final int notFound = -1;
    protected static final String shaderPath = Engine.resourcePath + "\\shaders\\";
    protected transient int programID;

    protected transient Map<String, Integer> uniformVariables;

    public Shader(String shaderFileName)
    {
        onStart(shaderFileName);
    }

    public void destroy()
    {
        stop();
        if(this.programID != 0)
            glDeleteProgram(this.programID);
    }

    public void onStart(String filename)
    {
        String[] shaders = processShader(filename);
        this.programID = glCreateProgram();
        if(programID == 0)
            throw new RuntimeException("Cannot create shader program!");

        int vertexShader = createShader(GL_VERTEX_SHADER, shaders[0]);
        int fragmentShader = createShader(GL_FRAGMENT_SHADER, shaders[1]);

        linkProgram(vertexShader, fragmentShader);
        this.uniformVariables = new HashMap<>();
    }

    private int createShader(int shaderType, String shaderSource)
    {
        int shaderID = glCreateShader(shaderType);
        if(shaderID == 0)
            throw new IllegalStateException("Cannot create shader!");
        if(shaderSource.isBlank())
            return -1;

        glShaderSource(shaderID, shaderSource);
        glCompileShader(shaderID);

        if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0)
            throw new IllegalStateException("Cannot create shader: " + glGetShaderInfoLog(shaderID, 1024));

        glAttachShader(this.programID, shaderID);
        return shaderID;
    }

    private String[] processShader(String shaderFileName)
    {
        String[] shaders = new String[2];
        try
        {
            String vertexPath = shaderPath + "vertex\\" + shaderFileName + ".glsl";
            String fragmentPath = shaderPath + "fragment\\" + shaderFileName + ".glsl";

            shaders[0] = new String(Files.readAllBytes(Paths.get(vertexPath)));
            shaders[1] = new String(Files.readAllBytes(Paths.get(fragmentPath)));
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Cannot load shader: '" + shaderFileName + "' due to " + ex.getMessage());
        }
        return shaders;
    }

    private void linkProgram(int vertexShader, int fragmentShader)
    {
        glLinkProgram(this.programID);
        if(glGetProgrami(this.programID, GL_LINK_STATUS) == 0)
        {
            String errorMessage = "Cannot link program: " + glGetProgramInfoLog(this.programID, 1024);
            throw new IllegalStateException(errorMessage);
        }

        glValidateProgram(this.programID);
        if(glGetProgrami(this.programID, GL_VALIDATE_STATUS) == 0)
            System.err.println("Warning! Shader code validation " + glGetProgramInfoLog(this.programID, 1024));

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void start()
    {
        glUseProgram(this.programID);
    }

    public void stop()
    {
        glUseProgram(0);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Shader shader))
            return false;
        return this.programID == shader.programID;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), this.programID);
    }

    private int uniformLocation(String name)
    {
        return glGetUniformLocation(this.programID, name);
    }

    public final void storeUniforms(String ... uniforms)
    {
        for(String uniform : uniforms)
        {
            int location = uniformLocation(uniform);
            if(location == notFound)
            {
                System.err.println("Uniform " + uniform + " could not be located in the shader! " +
                        "Check whether it was used in the shader code or perhaps it was spelt wrongly");
                continue;
            }
            this.uniformVariables.put(uniform, location);
        }
    }

    public void uniform(String name, int value)
    {
        int location = this.uniformVariables.get(name);
        glUniform1i(location, value);
    }

    public void uniform(String name, float value)
    {
        int location = this.uniformVariables.get(name);
        glUniform1f(location, value);
    }

    public void uniform(String name, boolean value)
    {
        int location = this.uniformVariables.get(name);
        glUniform1f(location, value ? 1.0f : 0.0f);
    }

    public void uniform(String name, Vector2f value)
    {
        uniform(name, value.x, value.y);
    }

    public void uniform(String name, float x, float y)
    {
        int location = this.uniformVariables.get(name);
        glUniform2f(location,x, y);
    }

    public void uniform(String name, Vector3f value)
    {
        uniform(name, value.x, value.y, value.z);
    }

    public void uniform(String name, float x, float y, float z)
    {
        int location = this.uniformVariables.get(name);
        glUniform3f(location, x, y, z);
    }

    public void uniform(String name, Vector4f value)
    {
        uniform(name, value.x, value.y, value.z, value.w);
    }

    public void uniform(String name, float x, float y, float z, float w)
    {
        int location = this.uniformVariables.get(name);
        glUniform4f(location, x, y, z, w);
    }

    public void uniform(String name, Matrix3f value)
    {
        int location = this.uniformVariables.get(name);
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix3fv(location, false, value.get(stack.mallocFloat(9)));
        }
    }

    public void uniform(String name, Matrix4f value)
    {
        int location = this.uniformVariables.get(name);
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix4fv(location, false, value.get(stack.mallocFloat(16)));
        }
    }

}
