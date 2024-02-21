package dream.managers;

import dream.Engine;
import dream.graphics.icon.Icons;
import dream.graphics.texture.Texture;
import dream.light.Light;
import dream.shader.Shader;
import dream.shader.ShaderConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static dream.shader.ShaderConstants.*;

public final class ResourcePool
{
    private static final int[] systemIcons = new int[Icons.total];

    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Texture> textures = new HashMap<>();

    private static Shader mesh;

    public static Shader defaultMesh()
    {
        if(ResourcePool.mesh == null)
        {
            ResourcePool.mesh = addAndGetShader("default");
            ResourcePool.mesh.storeUniforms
            (
                    // Vertex Data
                    projection, view, transformation, inverseNormals, viewPosition,

                    // Material Data
                    materialDiffuseMap, materialHasDiffuseMap, materialDiffuse,
                    materialSpecular, materialHasSpecularMap, materialSpecularMap,
                    materialReflectance
            );

            for(int i = 0; i < Light.maxLights; ++i)
            {
                ResourcePool.mesh.storeUniforms
                (
                    "lights[" + i + "]." + ambient, "lights[" + i + "]." + diffuse,
                    "lights[" + i + "]." + specular, "lights[" + i + "].type",
                    "lights[" + i + "]." + direction, "lights[" + i + "]." + position,
                    "lights[" + i + "]." + constant, "lights[" + i + "]." + linear,
                    "lights[" + i + "]." + quadratic, "lights[" + i + "]." + cutoff,
                    "lights[" + i + "]." + outerCutoff
                );
            }
        }
        return ResourcePool.mesh;
    }

    public static Shader getShader(String resourceName)
    {
        return ResourcePool.shaders.getOrDefault(resourceName, null);
    }

    public static void addShader(String resourceName)
    {
        if(ResourcePool.shaders.containsKey(resourceName))
            return;

        Shader shader = new Shader(resourceName);
        ResourcePool.shaders.put(resourceName, shader);
    }

    public static Shader addAndGetShader(String resourceName)
    {
        if(ResourcePool.shaders.containsKey(resourceName))
            return ResourcePool.shaders.get(resourceName);

        Shader shader = new Shader(resourceName);
        ResourcePool.shaders.put(resourceName, shader);
        return shader;
    }

    public static Texture getTexture(String resourceName)
    {
        String path = Engine.resourcePath + "\\textures\\" + resourceName;
        return ResourcePool.textures.getOrDefault(path, null);
    }

    public static int getIcon(int icon)
    {
        return ResourcePool.systemIcons[icon];
    }

    public static void loadIcons()
    {
        String path = Engine.resourcePath + "\\icons";

        String[] files = new File(path).list();
        if(files == null)
        {
            System.err.println("The required editor textures are absent!");
            return;
        }

        for(String file : files)
        {
            String filePath = path + "\\" + file;
            if(ResourcePool.textures.containsKey(filePath))
                return;

            Texture texture = new Texture(filePath);
            ResourcePool.textures.put(filePath, texture);

            int systemIconID = createIcon(file);
            if(systemIconID >= 0)
                systemIcons[systemIconID] = texture.ID;
        }
    }

    public static Texture addAndGetTexture(String resourceName)
    {
        String path = Engine.resourcePath + "\\textures\\" + resourceName;

        if(ResourcePool.textures.containsKey(path))
            return ResourcePool.textures.get(path);

        Texture texture = new Texture(path);
        ResourcePool.textures.put(path, texture);
        return texture;
    }

    public static ArrayList<Texture> getAllTextures()
    {
        return new ArrayList<>(ResourcePool.textures.values());
    }

    public static ArrayList<Shader> getAllShaders()
    {
        return new ArrayList<>(ResourcePool.shaders.values());
    }

    private static int createIcon(String textureName)
    {
        // Note: If you want to add a new system icon, add it here, make sure it is named appropriately
        // E.G (SystemLightBlahBlahBlah.whatever)
        // And also change the total variable in the Icons class.
        int ID = -1;
        if(textureName.startsWith("menu"))
            ID = Icons.menu;
        else if(textureName.startsWith("about"))
            ID = Icons.about;
        else if(textureName.startsWith("done"))
            ID = Icons.done;
        else if(textureName.startsWith("close"))
            ID = Icons.close;
        else if(textureName.startsWith("plus"))
            ID = Icons.plus;
        else if(textureName.startsWith("minus"))
            ID = Icons.minus;
        else if(textureName.startsWith("folder"))
            ID = Icons.folder;
        else if(textureName.startsWith("toolbox"))
            ID = Icons.toolbox;
        else if(textureName.startsWith("trash"))
            ID = Icons.trash;

        return ID;
    }

    public static int[] getIcons()
    {
        return ResourcePool.systemIcons;
    }
}
