package dream;


import dream.managers.NodeManager;
import dream.postprocessing.FilterManager;
import editor.events.EventManager;
import dream.io.Input;
import dream.managers.ResourcePool;
import dream.managers.WindowManager;
import editor.Editor;
import editor.overrides.WindowOverrides;

public class Engine
{
    public static final String resourcePath;

    public static final double nanoSeconds = 0.000000001;
    public static final long inverseNanoseconds = 1000000000L;
    public static float frameRate = 1000F;
    public static final float frameTime = 1 / frameRate;
    public static float deltaTime = 0.0f;
    public static int framesPerSecond;
    public static int frames = 0;

    public static volatile boolean isRunning = true;

    private static Editor editor;

    static
    {
        resourcePath = System.getProperty("user.dir") + "\\res";
    }

    public static void start()
    {
        preLoop();
        loop();
        postLoop();
    }

    private static void preLoop()
    {
        long mainWindowID = WindowManager.initialize();

        Input.initialize(mainWindowID);

        ResourcePool.loadIcons();
        FilterManager.createFilters();
        NodeManager.loadTree();

        Engine.editor = new Editor();
        Engine.editor.initialize(mainWindowID);
    }

    private static void loop()
    {
        isRunning = true;

        boolean render = false;
        long lastTime = System.nanoTime();
        long frameCounter = 0;
        double unprocessedTime = 0.0;

        while(Engine.isRunning)
        {
            long startTime = System.nanoTime();
            Engine.deltaTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += (Engine.deltaTime * nanoSeconds);
            frameCounter += Engine.deltaTime;

            handleInput();

            while(unprocessedTime > frameTime)
            {
                render = true;
                unprocessedTime -= frameTime;

                if(frameCounter >= inverseNanoseconds)
                {
                    Engine.framesPerSecond = frames;
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if(render)
            {
                updateState((float) (Engine.deltaTime * nanoSeconds));
                frames++;
            }

            if(!WindowManager.isMainRunning())
                Engine.isRunning = false;
        }
    }

    private static void postLoop()
    {
        Engine.editor.destroy();
        WindowManager.destroy();
    }

    private static void handleInput()
    {
        Engine.editor.input();
    }

    private static void updateState(float delta)
    {
        WindowManager.startMain();
        Engine.editor.refresh();
        Input.update();
        WindowManager.endMain();
        EventManager.alert();
    }


}
