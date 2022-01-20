/**
 * Created by Gazibalonchik on 11.01.2016.
 */
public class Camera
{
    public double positionX;
    public double positionY;
    public double positionZ;
    private double pitch;
    private double yaw;
    private double roll;
    public float fov;

    private Direction direction;

    public Camera(double positionX, double positionY, double positionZ, double pitch, double yaw, double roll, float fov)
    {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.fov = fov;
    }

    private void updateDirection()
    {
        if(pitch > Math.PI * 0.25)
            direction = Direction.BOTTOM;
        else if(pitch < Math.PI * -0.25)
            direction = Direction.TOP;
        else
        {
            if(yaw > Math.PI * 1.75 || yaw < Math.PI * 0.25)
                direction = Direction.BACK;
            else if(yaw > Math.PI * 0.25 && yaw < Math.PI * 0.75)
                direction = Direction.RIGHT;
            else if(yaw > Math.PI * 0.75 && yaw < Math.PI * 1.25)
                direction = Direction.FRONT;
            else
                direction = Direction.LEFT;
        }
    }

    public Direction getDirection()
    {
        return direction;
    }

    public double getPitch()
    {
        return pitch;
    }

    public void setPitch(double pitch)
    {
        this.pitch = pitch;
        updateDirection();
    }

    public double getYaw()
    {
        return yaw;
    }

    public double getRoll()
    {
        return roll;
    }

    public void setRoll(double roll)
    {
        this.roll = roll;
    }

    public void setYaw(double yaw)
    {
        while(yaw >= Math.PI * 2.0)
        {
            if(yaw > 0.0)
                yaw -= Math.PI * 2.0;
            else
                yaw += Math.PI * 2.0;
        }

        this.yaw = yaw;
        updateDirection();
    }

    public double getPositionX()
    {
        return positionX;
    }

    public void setPositionX(double positionX)
    {
        this.positionX = positionX;
    }

    public double getPositionY()
    {
        return positionY;
    }

    public void setPositionY(double positionY)
    {
        this.positionY = positionY;
    }

    public double getPositionZ()
    {
        return positionZ;
    }

    public void setPositionZ(double positionZ)
    {
        this.positionZ = positionZ;
    }

    public float getFov()
    {
        return fov;
    }

    public void setFov(float fov)
    {
        this.fov = fov;
    }
}
