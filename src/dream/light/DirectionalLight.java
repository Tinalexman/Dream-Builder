package dream.light;

import org.joml.Vector3f;

import java.util.Objects;

public final class DirectionalLight extends Light
{
    public final Vector3f direction;

    public DirectionalLight()
    {
        super();
        this.name = "Directional Light";
        this.direction = new Vector3f(-0.2f, -1.0f, -0.3f);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectionalLight that = (DirectionalLight) o;
        return direction.equals(that.direction);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(direction);
    }
}
