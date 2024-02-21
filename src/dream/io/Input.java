package dream.io;

import dream.managers.WindowManager;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Input
{
    private static Input instance;

    private final float[] scrolls;
    private float currentXPosition, currentYPosition;
    private float previousXPosition, previousYPosition;
    private boolean dragging;

    private final boolean[] currentKeys;
    private final boolean[] previousKeys;

    private final boolean[] currentMouseButtons;
    private final boolean[] previousMouseButtons;

    private long currentWindowID;

    private Input()
    {
        this.currentKeys = new boolean[GLFW_KEY_LAST];
        this.previousKeys = new boolean[GLFW_KEY_LAST];

        this.currentMouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
        this.previousMouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];

        this.scrolls = new float[2];
        this.previousXPosition = -1.0f;
        this.previousYPosition = -1.0f;
        this.currentXPosition = 0.0f;
        this.currentYPosition = 0.0f;

        this.currentWindowID = -1L;
    }

    public static void initialize(long windowID)
    {
        Input.instance = new Input();
        Input.instance.currentWindowID = windowID;
    }

    public static void update()
    {
        Input.instance.scrolls[0] = 0.0f;
        Input.instance.scrolls[1] = 0.0f;
        System.arraycopy(Input.instance.currentKeys, 0, Input.instance.previousKeys, 0, GLFW_KEY_LAST);
        System.arraycopy(Input.instance.currentMouseButtons, 0,
                Input.instance.previousMouseButtons, 0, GLFW_MOUSE_BUTTON_LAST);
        Input.instance.previousXPosition = Input.instance.currentXPosition;
        Input.instance.previousYPosition = Input.instance.currentYPosition;
    }

    // KEY FUNCTIONS
    public static void keyCallback(long windowID, int key, int scancode, int action, int mods)
    {
        if(action == GLFW_PRESS)
            Input.instance.currentKeys[key] = true;
        else if(action == GLFW_RELEASE)
            Input.instance.currentKeys[key] = false;
    }

    public static boolean isKeyControlPressed()
    {
        return Input.isKeyPressed(GLFW_KEY_LEFT_CONTROL) || Input.isKeyPressed(GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isKeyShiftPressed()
    {
        return Input.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || Input.isKeyPressed(GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isKeyPressed(int keyCode)
    {
        return Input.instance.currentKeys[keyCode];
    }

    public static boolean isKeyReleased(int keyCode)
    {
        return !Input.instance.currentKeys[keyCode];
    }

    public static boolean isKeyJustPressed(int keyCode)
    {
        return Input.instance.currentKeys[keyCode] && !Input.instance.previousKeys[keyCode];
    }

    public static boolean isKeyJustReleased(int keyCode)
    {
        return !Input.instance.currentKeys[keyCode] && Input.instance.previousKeys[keyCode];
    }

    // MOUSE FUNCTIONS
    public static boolean isButtonJustPressed(int mouseButton)
    {
        return Input.instance.currentMouseButtons[mouseButton] && !Input.instance.previousMouseButtons[mouseButton];
    }

    public static boolean isButtonJustReleased(int mouseButton)
    {
        return !Input.instance.currentMouseButtons[mouseButton] && Input.instance.previousMouseButtons[mouseButton];
    }

    public static void mousePositionCallback(long windowID, double xPosition, double yPosition)
    {
        Input.instance.currentXPosition = (float) xPosition;
        Input.instance.currentYPosition = (float) yPosition;

        Input.instance.dragging = Input.instance.currentMouseButtons[0] ||
                Input.instance.currentMouseButtons[1] || Input.instance.currentMouseButtons[2];
    }

    public static void mouseButtonCallback(long windowID, int button, int action, int mods)
    {
        if(action == GLFW_PRESS)
            Input.instance.currentMouseButtons[button] = true;
        else if(action == GLFW_RELEASE)
        {
            Input.instance.currentMouseButtons[button] = false;
            Input.instance.dragging = false;
        }
    }

    public static void mouseScrollCallback(long windowID, double xOffset, double yOffset)
    {
        Input.instance.scrolls[0] = (float) xOffset;
        Input.instance.scrolls[1] = (float) yOffset;
    }

    public static void caputureMouse()
    {
        glfwSetInputMode(Input.instance.currentWindowID, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public static void hideMouse()
    {
        glfwSetInputMode(Input.instance.currentWindowID, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    public static void releaseMouse()
    {
        glfwSetInputMode(Input.instance.currentWindowID, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    public static void unavailableMouse()
    {
        glfwSetInputMode(Input.instance.currentWindowID, GLFW_CURSOR, GLFW_CURSOR_UNAVAILABLE);
    }

    public static float[] getCurrentMouseCoordinates()
    {
        return new float[] {Input.instance.currentXPosition, Input.instance.currentYPosition};
    }

    public static float[] getPreviousMouseCoordinates()
    {
        return new float[] {Input.instance.previousXPosition, Input.instance.previousYPosition};
    }

    public static Vector2f getMouseDelta()
    {
        float deltaX = Input.instance.currentXPosition - Input.instance.previousXPosition;
        float deltaY = Input.instance.currentYPosition - Input.instance.previousYPosition;
        return new Vector2f(deltaX, deltaY);
    }

    public static float getScrollX()
    {
        return Input.instance.scrolls[0];
    }

    public static float getScrollY()
    {
        return Input.instance.scrolls[1];
    }

    public static boolean isDragging()
    {
        return Input.instance.dragging;
    }

    public static boolean isButtonPressed(int button)
    {
        return Input.instance.currentMouseButtons[button];
    }

    public static boolean isButtonReleased(int button)
    {
        return !Input.instance.currentMouseButtons[button];
    }

    public static float[] getScreenCoordinates(int[] windowSize, float[] viewportPosition, float[] viewportSize)
    {
        float[] coordinates = new float[] {0.0f, 0.0f};

        coordinates[0] = Input.instance.currentXPosition - viewportPosition[0];
        coordinates[0] = (windowSize[0] / viewportSize[0]) * coordinates[0];

        coordinates[1] = Input.instance.currentYPosition - viewportPosition[1];
        coordinates[1] = windowSize[1] - ((windowSize[1] / viewportSize[1]) * coordinates[1]);

        return coordinates;
    }

}
