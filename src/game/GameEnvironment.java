package game;

import dream.camera.Camera;
import dream.environment.Environment;

public class GameEnvironment
{
    private final Environment environment;

    public GameEnvironment()
    {
        this.environment = new Environment();
    }

    public Environment environment()
    {
        return this.environment;
    }
}
