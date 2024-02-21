//package dream.renderer;
//
//import dream.model.Mesh;
//import dream.managers.ResourcePool;
//import dream.managers.WindowManager;
//import dream.scene.Scene;
//import dream.shader.Shader;
//import dream.shader.ShaderConstants;
//import dream.util.buffer.BufferTools;
//import org.lwjgl.system.MemoryUtil;
//
//import java.nio.FloatBuffer;
//
//import static org.lwjgl.opengl.GL11.GL_FLOAT;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL30.*;
//
//public class RayTracer extends Renderer
//{
//    private final Shader shader;
//    private final Mesh mesh;
//    private final int meshVAO, meshVBO;
//
//    public RayTracer()
//    {
//        super();
//
//        this.shader = ResourcePool.addAndGetShader("raytracer.glsl");
//        this.shader.onStart();
//        this.shader.storeUniforms(ShaderConstants.resolution /*, ShaderConstants.mouse */);
//
//        int[] windowSize = WindowManager.getMainSize();
//        this.shader.loadUniform(ShaderConstants.resolution, (float) windowSize[0], (float) windowSize[1]);
//
//        this.mesh = new Mesh();
//        //MeshFactory.createPlane(this.mesh, 600, 1.0f, MeshFactory.Orientation.zAxis);
//        this.mesh.setVertices(new float[]
//            {
//                -1.0f, 1.0f, 0.0f,
//                 -1.0f, -1.0f, 0.0f,
//                 1.0f, 1.0f, 0.0f,
//                 1.0f, 1.0f, 0.0f,
//                 -1.0f, -1.0f, 0.0f,
//                 1.0f, -1.0f, 0.0f
//            }
//        );
//
//        this.meshVAO = glGenVertexArrays();
//        glBindVertexArray(this.meshVAO);
//        this.meshVBO = glGenBuffers();
//        glBindBuffer(GL_ARRAY_BUFFER, this.meshVBO);
//        FloatBuffer buffer = BufferTools.createFloatBuffer(this.mesh.getVertices());
//        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
//        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
//        MemoryUtil.memFree(buffer);
//    }
//
//    @Override
//    public void input()
//    {
//
//    }
//
//    @Override
//    public void render(Scene scene)
//    {
//        this.colorBuffer.start();
//        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//        if(!this.useCamera)
//        {
//            this.colorBuffer.stop();
//            return;
//        }
//
//        glBindVertexArray(this.meshVAO);
//        glEnableVertexAttribArray(0);
//        this.shader.start();
//
//        if(this.mesh.hasIndices())
//            glDrawElements(GL_TRIANGLES, this.mesh.count(), GL_UNSIGNED_INT, 0);
//        else
//            glDrawArrays(GL_TRIANGLES, 0, this.mesh.count());
//
//        this.shader.stop();
//        glDisableVertexAttribArray(0);
//        glBindVertexArray(0);
//
//        this.colorBuffer.stop();
//    }
//
//    @Override
//    public void destroy()
//    {
//        glDeleteBuffers(this.meshVBO);
//        glDeleteVertexArrays(this.meshVAO);
//        this.shader.destroy();
//        super.destroy();
//    }
//
//}
