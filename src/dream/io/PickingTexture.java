package dream.io;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

public class PickingTexture
{
    private static final float maxColorRange = 255.0f;

    private int pickingTextureID;
    private int FBO_ID;
    private int depthTextureID;

    public PickingTexture(int width, int height)
    {
        if(!init(width, height))
            throw new RuntimeException("Picking Texture's FrameBuffer could not be completed!");
    }

    private boolean init(int width, int height)
    {
        this.FBO_ID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.FBO_ID);

        this.pickingTextureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.pickingTextureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.pickingTextureID, 0);

        glEnable(GL_TEXTURE_2D);
        this.depthTextureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.depthTextureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.depthTextureID, 0);

        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            return false;

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return true;
    }

    public void start()
    {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.FBO_ID);
    }

    public void stop()
    {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public float[] readPixel(int xCoordinate, int yCoordinate)
    {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.FBO_ID);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pixels = new float[3];
        glReadPixels(xCoordinate, yCoordinate, 1, 1, GL_RGB, GL_FLOAT, pixels);
        pixels[0] = Math.round(pixels[0] * maxColorRange);
        pixels[1] = Math.round(pixels[1] * maxColorRange);
        pixels[2] = Math.round(pixels[2] * maxColorRange);

        return pixels;
    }

    public void destroy()
    {
        glDeleteTextures(this.pickingTextureID);
        glDeleteTextures(this.depthTextureID);
        glDeleteFramebuffers(this.FBO_ID);
    }

    public int getID()
    {
        return this.pickingTextureID;
    }

}
