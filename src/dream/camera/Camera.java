package dream.camera;

import dream.managers.WindowManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera
{
    public static final float minimumCameraNearPlane = 0.001f;
    public static final float maximumCameraFarPlane = 10000.0f;
    public static final float minimumFieldOfView = 50.0f;
    public static final float maximumFieldOfView = 120.0f;

    protected boolean active;
    protected boolean viewChange;
    protected boolean projectionChange;

    protected float nearPlane;
    protected float farPlane;
    protected float fieldOfView;
    protected float aspectRatio;

    protected final Vector3f position;
    protected final Vector3f upVector;
    protected final Vector3f right;
    protected final Vector3f forward;

    protected final Matrix4f projection;
    protected final Matrix4f view;

    protected float pitch;
    protected float yaw;

    //protected CameraRay cameraRay;

    public Camera()
    {
        this.active = false;
        this.viewChange = true;
        this.projectionChange = true;

        this.nearPlane = 0.001f;
        this.farPlane = 1000.0f;
        this.fieldOfView = 55.0f; // In degrees
        this.aspectRatio = WindowManager.getMainRatio();

        this.pitch = 0.0f;
        this.yaw = -90.0f;

        this.position = new Vector3f(0.0f, 0.5f, 3.0f);
        this.right = new Vector3f(1.0f, 0.0f, 0.0f);
        this.forward = new Vector3f(0.0f, 0.0f, -1.0f);
        this.upVector = new Vector3f(0.0f, 1.0f, 0.0f);

        this.projection = new Matrix4f().identity();
        this.view = new Matrix4f().identity();
    }

    public Matrix4f getProjection()
    {
        if(this.projectionChange)
        {
            this.projection.identity()
                    .perspective((float) Math.toRadians(this.fieldOfView), this.aspectRatio,
                            this.nearPlane, this.farPlane);
            this.projectionChange = false;
        }
        return this.projection;
    }

    public Matrix4f getView()
    {
        if(this.viewChange)
        {
            Vector3f temp = new Vector3f();
            this.position.add(this.forward, temp);
            this.view.identity()
                    .lookAt(this.position, temp, this.upVector);
            this.viewChange = false;
        }
        return this.view;
    }

    public float getNearPlane()
    {
        return this.nearPlane;
    }

    public float getFarPlane()
    {
        return this.farPlane;
    }

    public void setNearPlane(float nearPlane)
    {
        if(nearPlane >= Camera.minimumCameraNearPlane)
        {
            this.nearPlane = nearPlane;
            this.projectionChange = true;
        }
    }

    public void setFarPlane(float farPlane)
    {
        if(farPlane <= Camera.maximumCameraFarPlane)
        {
            this.farPlane = farPlane;
            this.projectionChange = true;
        }
    }

    public Vector3f getPosition()
    {
        return this.position;
    }

    public Vector3f getForward()
    {
        return this.forward;
    }

    public Vector3f getRight()
    {
        return this.right;
    }

    public Vector3f getUpVector()
    {
        return this.upVector;
    }

    public void setPosition(float xPosition, float yPosition, float zPosition)
    {
        this.position.x = xPosition;
        this.position.y = yPosition;
        this.position.z = zPosition;
        this.viewChange = true;
    }

    public void incrementPosition(Vector3f offset)
    {
        this.position.add(offset);
        this.viewChange = true;
    }

    public void rotate(float pitchOffset, float yawOffset)
    {
        this.pitch -= pitchOffset;
        this.yaw += yawOffset;

        this.pitch = Math.max(-89.0f, Math.min(89.0f, this.pitch));

        float yawRadians = (float) Math.toRadians(this.yaw);
        float pitchRadians = (float) Math.toRadians(this.pitch);

        this.forward.x = (float) (Math.cos(yawRadians) * Math.cos(pitchRadians));
        this.forward.y = (float) (Math.sin(pitchRadians));
        this.forward.z = (float) (Math.sin(yawRadians) * Math.cos(pitchRadians));

        Vector3f temp = new Vector3f();

        this.forward.normalize();

        this.forward.cross(new Vector3f(0.0f, 1.0f, 0.0f), temp);
        temp.normalize();
        this.right.set(temp);

        this.right.cross(this.forward, temp);
        temp.normalize();
        this.upVector.set(temp);

        this.viewChange = true;
    }

    public float getFieldOfView()
    {
        return this.fieldOfView;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public float getYaw()
    {
        return this.yaw;
    }

    public boolean hasViewChanged()
    {
        return this.viewChange;
    }

    public void incrementZoom(float increment)
    {
        this.fieldOfView += increment;
        if(this.fieldOfView < Camera.minimumFieldOfView)
            this.fieldOfView = Camera.minimumFieldOfView;
        if(this.fieldOfView > Camera.maximumFieldOfView)
            this.fieldOfView = Camera.maximumFieldOfView;
        this.projectionChange = true;
    }

    public void setFieldOfView(float FOV)
    {
        this.fieldOfView = FOV;
    }

    public void setAspectRatio(float ratio)
    {
        this.aspectRatio = ratio;
        this.projectionChange = true;
    }

    public void hasViewChanged(boolean change)
    {
        this.viewChange = change;
    }

    public void resetView()
    {
        this.viewChange = false;
    }

    public boolean hasProjectionChanged()
    {
        return this.projectionChange;
    }

    public void hasProjectionChanged(boolean change)
    {
        this.projectionChange = change;
    }

    public void resetProjection()
    {
        this.projectionChange = false;
    }

}
