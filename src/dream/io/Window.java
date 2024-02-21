package dream.io;

import editor.events.EventManager;
import editor.events.type.WindowResize;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
    private final int[] windowSize;
    private final long windowID;
    private boolean close;

    public Window(String windowTitle, int width, int height)
    {
        this.windowSize = new int[] {width, height};

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW!");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        this.windowID = glfwCreateWindow(this.windowSize[0], this.windowSize[1], windowTitle, NULL, NULL);
        if (this.windowID == NULL)
            throw new RuntimeException("Failed to create the GLFW Window!");

        try (MemoryStack stack = stackPush())
        {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(this.windowID, pWidth, pHeight);

            final GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            assert vidMode != null : "Video Mode Error!!";

            int centerX = (int) ((vidMode.width() - pWidth.get(0)) * 0.5f);
            int centerY = (int) ((vidMode.height() - pHeight.get(0)) * 0.5f);
            glfwSetWindowPos(this.windowID, centerX, centerY);

            glfwSetWindowSizeCallback(this.windowID, (window, w, h) -> onResize(w, h));
            glfwSetFramebufferSizeCallback(this.windowID, (window, w, h) -> onResize(w, h));
            glfwSetWindowCloseCallback(this.windowID, window -> this.close = true);
            glfwMakeContextCurrent(this.windowID);
        }
        GL.createCapabilities();

        this.close = false;
    }

    public float getAspectRatio()
    {
        return (float) this.windowSize[0] / this.windowSize[1];
    }

    public void show()
    {
        glfwShowWindow(this.windowID);
    }

    public boolean shouldClose()
    {
        return this.close;
    }

    public void close()
    {
        this.close = true;
    }

    public long getId()
    {
        return this.windowID;
    }

    public void destroy()
    {
        glfwFreeCallbacks(this.windowID);
        glfwDestroyWindow(this.windowID);
    }

    private void onResize(int newWidth, int newHeight)
    {
        this.windowSize[0] = newWidth;
        this.windowSize[1] = newHeight;
        EventManager.push(new WindowResize(newWidth, newHeight));
    }

    public int[] getWindowSize()
    {
        return this.windowSize;
    }
}
