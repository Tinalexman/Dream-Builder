package dream.io;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL33.*;

public class DepthBuffer
{
    protected int depthMapFBO;
    protected int depthMap;
    protected int width;
    protected int height;

    public DepthBuffer(int width, int height)
    {
        this.depthMapFBO = glGenFramebuffers();
        this.depthMap = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.depthMap);
        glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height,
                0, GL11.GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glBindFramebuffer(GL_FRAMEBUFFER, this.depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D,
                this.depthMap, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }





}
