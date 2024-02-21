package dream.constants;

import dream.components.Material;
import org.joml.Vector3f;

public class MaterialConstants
{
    public static final Material emerald = new Material(new Vector3f(0.0215f, 0.1745f, 0.0215f),
            new Vector3f(0.07568f, 0.61424f, 0.07568f), new Vector3f(0.633f, 0.727811f, 0.633f), 76.8f);

    public static final Material chrome = new Material(new Vector3f(0.25f, 0.25f, 0.25f),
            new Vector3f(0.4f, 0.4f, 0.4f), new Vector3f(0.774597f, 0.774597f, 0.774597f), 76.8f);

    public static final Material gold = new Material(new Vector3f(0.24725f, 0.1995f, 0.0745f),
            new Vector3f(0.75164f, 0.60648f, 0.22648f), new Vector3f(0.628281f, 0.555802f, 0.366065f), 51.2f);

    public static final Material silver = new Material(new Vector3f(0.19225f, 0.19225f, 0.19225f),
            new Vector3f(0.50754f, 0.50754f, 0.50754f), new Vector3f(0.508273f, 0.508273f, 0.508273f), 51.2f);

    public static final Material defaultMaterial = new Material(new Vector3f(1.0f, 0.5f, 0.3f),
            new Vector3f(1.0f, 0.5f, 0.3f), new Vector3f(0.5f, 0.5f, 0.5f), 16.0f);

}
