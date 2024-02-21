package dream.shader;

public class ShaderConstants
{
    // General Uniforms
    public static final String transformation = "transformation";
    public static final String view = "view";
    public static final String projection = "projection";

    public static final String color = "color";
    public static final String position = "position";
    public static final String direction = "direction";
    public static final String viewPosition = "viewPosition";

    public static final String lightColor = "lightColor";
    public static final String inverseNormals = "inverseNormals";

    public static final String resolution = "resolution";
    public static final String mouse = "mouse";
    public static final String sampler = "sampler";

    public static final String objectIndex = "objectIndex";
    public static final String drawIndex = "drawIndex";


    public static final String material = "material";
    public static final String light = "light";
    public static final String diffuse = "diffuse";
    public static final String ambient = "ambient";
    public static final String specular = "specular";
    public static final String reflectance = "reflectance";
    public static final String constant = "constant";
    public static final String linear = "linear";
    public static final String quadratic = "quadratic";
    public static final String cutoff = "cutoff";
    public static final String outerCutoff = "outerCutoff";
    public static final String highlight = "highlight";

    // Combined
    public static final String materialDiffuse = material + "." + diffuse;
    public static final String materialSpecular = material + "." + specular;
    public static final String materialReflectance = material + "." + reflectance;
    public static final String materialDiffuseMap = material + ".diffuseMap";
    public static final String materialHasDiffuseMap = material + ".hasDiffuseMap";
    public static final String materialHasSpecularMap = material + ".hasSpecularMap";
    public static final String materialSpecularMap = material + ".specularMap";

    public static final String lightDiffuse = light + "." + diffuse;
    public static final String lightSpecular = light + "." + specular;
    public static final String lightAmbient = light + "." + ambient;
    public static final String lightPosition = light + "." + position;
    public static final String lightDirection = light + "." + direction;
    public static final String lightType = light + ".type";
    public static final String lightConstant = light + "." + "constant";
    public static final String lightLinear = light + "." + "linear";
    public static final String lightQuadratic = light + "." + "quadratic";
    public static final String lightCutoff = light + "." + "cutoff";
    public static final String lightOuterCutoff = light + "." + "outerCutoff";

}
