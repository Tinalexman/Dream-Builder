package dream.io;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer
{
    private final int fboID;
    private final int rboID;
    private final int texture;

    public FrameBuffer(int width, int height)
    {
        this.fboID = glGenFramebuffers();
        start();

        this.texture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, this.texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        this.rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("FrameBuffer is not complete!");

        stop();
    }

    public void start()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboID);
    }

    public void stop()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getID()
    {
        return this.texture;
    }

    public void destroy()
    {
        glDeleteRenderbuffers(this.rboID);
        glDeleteFramebuffers(this.fboID);
        glDeleteTextures(this.texture);
    }

}
