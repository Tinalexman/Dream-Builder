package dream.model;

import dream.graphics.texture.Texture;
import dream.util.buffer.BufferTools;

import java.util.List;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class Mesh
{
    protected VertexData vertexData;
    protected int[] indices;
    protected List<Texture> textures;
    protected boolean loaded;

    protected final int vaoID;
    protected final int[] vboArray;

    public Mesh(VertexData data)
    {
        this.vertexData = data;
        this.vaoID = glGenVertexArrays();
        this.vboArray = new int[4];
        this.loaded = false;
    }

    public Mesh()
    {
        this(null);
    }

    public void indices(int[] indices)
    {
        this.indices = indices;
    }

    public void textures(List<Texture> textures)
    {
        this.textures = textures;
    }

    public int count()
    {
        return this.indices != null ? this.indices.length :
                this.vertexData.position().length;
    }

    public boolean has()
    {
        return this.indices != null;
    }

    public int[] indices()
    {
        return this.indices;
    }

    public List<Texture> textures()
    {
        return this.textures;
    }

    public VertexData vertexData()
    {
        return this.vertexData;
    }

    public void vertexData(VertexData data)
    {
        this.vertexData = data;
    }

    public void load()
    {
        if(this.loaded)
            return;

        glBindVertexArray(this.vaoID);

        this.vboArray[0] = BufferTools.createVBO(0, 3, this.vertexData.position());
        this.vboArray[1] = BufferTools.createVBO(1, 2, this.vertexData.uv());
        this.vboArray[2] = BufferTools.createVBO(2, 3, this.vertexData.normal());
        this.vboArray[3] = BufferTools.createEBO(this.indices);

        stop();

        this.loaded = true;
    }

    public void start()
    {
        glBindVertexArray(this.vaoID);
        if(this.vertexData.position().length > 0)
            glEnableVertexAttribArray(0);
        if(this.vertexData.uv().length > 0)
            glEnableVertexAttribArray(1);
        if(this.vertexData.normal().length > 0)
            glEnableVertexAttribArray(2);
    }

    public void stop()
    {
        if(this.vertexData.position().length > 0)
            glDisableVertexAttribArray(0);
        if(this.vertexData.uv().length > 0)
            glDisableVertexAttribArray(1);
        if(this.vertexData.normal().length > 0)
            glDisableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    public void destroy()
    {
        for (int j : this.vboArray)
        {
            if (j != -1)
                glDeleteBuffers(j);
        }

        if(this.vaoID != -1)
            glDeleteVertexArrays(this.vaoID);
    }
}
