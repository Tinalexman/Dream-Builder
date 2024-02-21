package dream.generators.noise;

public abstract class Noise
{
    protected int seed;
    protected float roughness;
    protected int octaves;
    protected float amplitude;

    public Noise()
    {
        this.seed = 0;
        this.roughness = 0.0f;
        this.octaves = 0;
        this.amplitude = 0.0f;
    }

    public abstract float getNoise(int x, int y);
}