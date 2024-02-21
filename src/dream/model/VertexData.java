package dream.model;

public class VertexData
{
    protected float[] position;
    protected float[] uv;
    protected float[] normal;

    public VertexData()
    {
        this.position = new float[3];
        this.uv = new float[2];
        this.normal = new float[3];
    }

    public float[] position()
    {
        return this.position;
    }

    public float[] uv()
    {
        return this.uv;
    }

    public float[] normal()
    {
        return this.normal;
    }

    public void position(float[] position)
    {
        this.position = position;
    }

    public void uv(float[] uv)
    {
        this.uv = uv;
    }

    public void normal(float[] normal)
    {
        this.normal = normal;
    }
}
