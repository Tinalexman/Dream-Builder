package dream.light;

import org.joml.Vector3f;

import java.util.Objects;

public class SpotLight extends Light
{
    public Vector3f direction;
    public float cutoff;
    public float outerCutoff;

    public SpotLight()
    {
        super();
        this.name = "Spot Light";
        this.direction = new Vector3f(0.0f, -1.0f, 0.0f);
        this.cutoff = (float) Math.cos(Math.toRadians(12.5f));
        this.outerCutoff = (float) Math.cos(Math.toRadians(17.5f));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpotLight spotLight = (SpotLight) o;
        return Float.compare(spotLight.cutoff, cutoff) == 0 && Float.compare(spotLight.outerCutoff, outerCutoff) == 0 && direction.equals(spotLight.direction);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(direction, cutoff, outerCutoff);
    }
}
