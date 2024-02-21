package dream.components;

import dream.constants.MaterialConstants;
import dream.graphics.texture.TexturePack;
import org.joml.Vector3f;

public class Material extends Component
{
    public final Vector3f ambient;
    public final Vector3f diffuse;
    public final Vector3f specular;
    public TexturePack pack;
    public boolean transparency;
    public float reflectance;

    public Material()
    {
        this(MaterialConstants.defaultMaterial);
    }

    public Material(Material material)
    {
        super();
        this.ambient = new Vector3f(material.ambient);
        this.diffuse = new Vector3f(material.diffuse);
        this.specular = new Vector3f(material.specular);
        this.reflectance = material.reflectance;
        this.transparency = material.transparency;
        this.pack = new TexturePack();
    }

    public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, float reflectance)
    {
        super();
        this.ambient = new Vector3f(ambient);
        this.diffuse = new Vector3f(diffuse);
        this.specular = new Vector3f(specular);
        this.reflectance = reflectance;
        this.pack = new TexturePack();
        this.transparency = false;
    }

    public void set(Material material)
    {
        this.ambient.set(material.ambient);
        this.diffuse.set(material.diffuse);
        this.specular.set(material.specular);
        this.reflectance = material.reflectance;
        this.pack = material.pack;
        this.transparency = material.transparency;
        this.changed = true;
    }

    public Material(Vector3f color, float reflectance)
    {
        this(color, color, color, reflectance);
    }

    @Override
    public boolean equals(Object object)
    {
        if(!(object instanceof Material material))
            return false;
        return material.diffuse.equals(this.diffuse)
                && material.ambient.equals(this.ambient)
                && material.specular.equals(this.specular)
                && material.reflectance == this.reflectance;
    }

    @Override
    public int hashCode()
    {
        return (int) (this.diffuse.hashCode() + this.ambient.hashCode() + this.specular.hashCode() + this.reflectance);
    }

    public TexturePack getPack()
    {
        return this.pack;
    }

    public boolean hasDiffuse()
    {
        return this.pack.diffuse != null;
    }

    public boolean hasSpecular()
    {
        return this.pack.specular != null;
    }

}
