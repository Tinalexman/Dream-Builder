package dream.managers;

import dream.io.Window;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class WindowManager
{
    private static Window mainWindow;

    public static long initialize()
    {
        WindowManager.mainWindow = new Window("Dream", 1210, 720);
        WindowManager.mainWindow.show();
        return WindowManager.mainWindow.getId();
    }

    public static void startMain()
    {
        glClearColor(0.0f,  0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void endMain()
    {
        glfwSwapBuffers(WindowManager.mainWindow.getId());
        glfwPollEvents();
    }

    public static int[] getMainSize()
    {
        return WindowManager.mainWindow.getWindowSize();
    }

    public static float getMainRatio()
    {
        return WindowManager.mainWindow.getAspectRatio();
    }

    public static void closeMain()
    {
        WindowManager.mainWindow.close();
    }

    public static boolean isMainRunning()
    {
        return !WindowManager.mainWindow.shouldClose();
    }

    public static void destroy()
    {
        WindowManager.mainWindow.destroy();
    }
}
