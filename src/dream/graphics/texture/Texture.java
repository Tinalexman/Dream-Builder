package dream.graphics.texture;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture
{
    public static final String diffuseTexture = "texture_diffuse";
    public static final String specularTexture = "texture_specular";

    public transient int width;
    public transient int height;
    public transient int ID;

    public String filePath;
    public String type;

    public Texture(String filePath)
    {
        this.width = 0;
        this.height = 0;
        this.filePath = filePath;
        this.type = diffuseTexture;
        load();
    }

    public Texture()
    {
        this.width = this.height = this.ID = 0;
        this.filePath = "Generated Texture";
    }

    public void set(int ID, int width, int height)
    {
        this.ID = ID;
        this.width = width;
        this.height = height;
    }

    public void destroy()
    {
        glDeleteTextures(this.ID);
    }

    private ByteBuffer loadTextureData()
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer buffer = STBImage.stbi_load(this.filePath, w, h, channels, 4);
            if(buffer == null)
                throw new Exception("Cannot load texture file: " + this.filePath
                        + " due to " + STBImage.stbi_failure_reason());

            this.width = w.get();
            this.height = h.get();
            return buffer;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    private void load()
    {
        ByteBuffer buffer = loadTextureData();

        if(buffer == null)
            return;

        this.ID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.ID);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
    }

}
