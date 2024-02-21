package dream.graphics.texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

public class TexturePack
{
    public Texture diffuse;
    public Texture specular;
    public Texture glow;

    public TexturePack()
    {

    }

    public void start()
    {
        if(this.diffuse != null)
        {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, this.diffuse.ID);
        }

        if(this.specular != null)
        {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, this.specular.ID);
        }

        if(this.glow != null)
        {
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, this.glow.ID);
        }
    }

    public void stop()
    {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, 0);

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void destroy()
    {
        if(this.diffuse != null)
            this.diffuse.destroy();

        if(this.specular != null)
            this.specular.destroy();

        if(this.glow != null)
            this.glow.destroy();
    }
}
