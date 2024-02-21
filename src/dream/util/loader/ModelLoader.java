package dream.util.loader;

import dream.model.Mesh;
import dream.model.Model;
import dream.model.VertexData;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader
{
    public static Model load(String filePath)
    {
        AIScene scene = Assimp.aiImportFile(filePath, aiProcess_Triangulate | aiProcess_FlipUVs);

        if(scene == null || scene.mFlags() == AI_SCENE_FLAGS_INCOMPLETE || scene.mRootNode() == null)
        {
            System.err.println("Assimp Error: " + Assimp.aiGetErrorString());
            return null;
        }

        Model model = new Model();
        model.filePath(filePath);
        processNode(scene, model.meshes());

        return model;
    }

    private static void processNode(AIScene scene, List<Mesh> meshes)
    {
        PointerBuffer mesh = scene.mMeshes();
        if(mesh == null)
            return;

        for(int i = 0; i < mesh.limit(); i++)
        {
            AIMesh aiMesh = AIMesh.create(mesh.get(i));
            meshes.add(processMesh(aiMesh));
        }
    }

    private static Mesh processMesh(AIMesh mesh)
    {
        VertexData data = new VertexData();
        List<Float> vertices = new ArrayList<>();
        List<Float> uv = new ArrayList<>();
        List<Float> normal = new ArrayList<>();

        AIVector3D.Buffer buffer = mesh.mVertices();
        for(int i = 0; i < buffer.limit(); i++)
        {
            AIVector3D vector = buffer.get(i);
            vertices.add(vector.x());
            vertices.add(vector.y());
            vertices.add(vector.z());
        }

        buffer = mesh.mTextureCoords(0);
        for(int i = 0; i < buffer.limit(); i++)
        {
            AIVector3D vector = buffer.get(i);
            uv.add(vector.x());
            uv.add(vector.y());
        }

        buffer = mesh.mNormals();
        for(int i = 0; i < buffer.limit(); i++)
        {
            AIVector3D vector = buffer.get(i);
            normal.add(vector.x());
            normal.add(vector.y());
            normal.add(vector.z());
        }

        data.position(toFloatArray(vertices));
        data.uv(toFloatArray(uv));
        data.normal(toFloatArray(normal));

        Mesh m = new Mesh();
        m.vertexData(data);
        return m;
    }


    private static int[] toIntArray(List<Integer> data)
    {
        int[] res = new int[data.size()];
        for(int i = 0; i < data.size(); ++i)
            res[i] = data.get(i);
        return res;
    }

    private static float[] toFloatArray(List<Float> data)
    {
        float[] res = new float[data.size()];
        for(int i = 0; i < data.size(); ++i)
            res[i] = data.get(i);
        return res;
    }
}
