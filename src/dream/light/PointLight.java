package dream.light;

import org.joml.Vector3f;

import java.util.Objects;

public class PointLight extends Light
{
    public float constant;
    public float linear;
    public float quadratic;

    public PointLight()
    {
        super();
        this.name = "Point Light";
        this.constant = 1.0f;
        this.linear = 0.09f;
        this.quadratic = 0.032f;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointLight that = (PointLight) o;
        return Float.compare(that.constant, constant) == 0 && Float.compare(that.linear, linear) == 0 && Float.compare(that.quadratic, quadratic) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(constant, linear, quadratic);
    }
}
