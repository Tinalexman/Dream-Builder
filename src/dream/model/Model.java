package dream.model;

import dream.node.drawable.DrawableNode;

import java.util.ArrayList;
import java.util.List;

public class Model extends DrawableNode
{
    private final List<Mesh> meshes;
    private String filePath;

    public Model()
    {
        this.meshes = new ArrayList<>();
    }

    public String filePath()
    {
        return this.filePath;
    }

    public void filePath(String filePath)
    {
        this.filePath = filePath;
    }

    public List<Mesh> meshes()
    {
        return this.meshes;
    }


}
