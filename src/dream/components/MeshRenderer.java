package dream.components;

import dream.graphics.texture.Texture;
import dream.model.Mesh;
import dream.shader.Shader;

import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class MeshRenderer extends Component
{
    protected Mesh mesh;
    private final Configuration configuration;

    public MeshRenderer()
    {
        this.configuration = new Configuration();
    }

    public void setMesh(Mesh m)
    {
        this.mesh = m;
        if(m != null)
            m.load();
    }

    public void render()
    {
        if(this.mesh == null)
            return;

        this.mesh.start();
        this.configuration.activate();

        if(this.mesh.has())
            glDrawElements(GL_TRIANGLES, this.mesh.count(), GL_UNSIGNED_INT, 0);
        else
            glDrawArrays(GL_TRIANGLES, 0, this.mesh.count());

        this.configuration.deactivate();
        this.mesh.stop();
    }

    @Override
    public void destroy()
    {
        if(this.mesh != null)
            this.mesh.destroy();
    }

    public int getCull()
    {
        return this.configuration.getCull();
    }

    public void setCull(int val)
    {
        this.configuration.setCull(val);
    }

    public int getFace()
    {
        return this.configuration.getFace();
    }

    public void setFace(int val)
    {
        this.configuration.setFace(val);
    }

    public Mesh getMesh()
    {
        return this.mesh;
    }

    public void render(Shader shader)
    {
        int diffuseNr = 1, specularNr = 1;
        List<Texture> textures = this.mesh.textures();
        for(int i = 0; i < textures.size(); i++)
        {
            glActiveTexture(GL_TEXTURE0 + i); // activate texture unit first
            // retrieve texture number (the N in diffuse_textureN)
            String number = "", name = textures.get(i).type;
            if (name.equals(Texture.diffuseTexture))
                number = "" + (diffuseNr++);
            else if (name.equals(Texture.specularTexture))
                number = "" + (specularNr++);

            shader.uniform("material." + name + number, i);
            glBindTexture(GL_TEXTURE_2D, textures.get(i).ID);
        }

        glActiveTexture(GL_TEXTURE0);
        // draw mesh
        onStart();
        glDrawElements(GL_TRIANGLES, this.mesh.count(), GL_UNSIGNED_INT, 0);
        onStop();
    }

    public static class Configuration
    {
        public static final String[] faceOptions = new String[] { "Wireframe", "Solid" };
        public static final String[] cullOptions = new String[] { "None", "Front Face", "Back Face", "Both Faces" };

        public int cullFlag;
        public int faceFlag;

        public Configuration()
        {
            this.cullFlag = -1;
            this.faceFlag = GL_FILL;

            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);
        }

        public void activate()
        {
            glPolygonMode(GL_FRONT_AND_BACK, this.faceFlag);
            if(this.cullFlag > 0)
            {
                glEnable(GL_CULL_FACE);
                glCullFace(this.cullFlag);
            }
        }

        public void deactivate()
        {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glDisable(GL_CULL_FACE);
        }

        public int getCull()
        {
            if(this.cullFlag == GL_FRONT)
                return 1;
            else if(this.cullFlag == GL_BACK)
                return 2;
            else if(this.cullFlag == GL_FRONT_AND_BACK)
                return 3;
            return 0;
        }

        public void setCull(int val)
        {
            if(val == 1)
                this.cullFlag = GL_FRONT;
            else if(val == 2)
                this.cullFlag = GL_BACK;
            else if(val == 3)
                this.cullFlag = GL_FRONT_AND_BACK;
            else
                this.cullFlag = 0;
        }

        public int getFace()
        {
            if(this.faceFlag == GL_LINE)
                return 0;
            else if(this.faceFlag == GL_FILL)
                return 1;
            return -1;
        }

        public void setFace(int val)
        {
            if(val == 0)
                this.faceFlag = GL_LINE;
            else if(val == 1)
                this.faceFlag = GL_FILL;
        }
    }
}


