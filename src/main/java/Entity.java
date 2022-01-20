import java.util.Random;

/**
 * Created by Gazibalonchik on 15.02.2016.
 */
public abstract class Entity
{
    protected double positionX;
    protected double positionY;
    protected double positionZ;
    protected double pitch;
    protected double yaw;
    protected double roll;

    public Entity(double positionX, double positionY, double positionZ)
    {
        setPosition(positionX, positionY, positionZ);
    }

    public Entity()
    {
    }

    public void setPosition(double x, double y, double z)
    {
        positionX = x;
        positionY = y;
        positionZ = z;
    }

    public void setPositionX(double x)
    {
        positionX = x;
    }

    public void setPositionY(double y)
    {
        positionY = y;
    }

    public void setPositionZ(double z)
    {
        positionZ = z;
    }

    public double getPositionX()
    {
        return positionX;
    }

    public double getPositionY()
    {
        return positionY;
    }

    public double getPositionZ()
    {
        return positionZ;
    }

    public double getPitch()
    {
        return pitch;
    }

    public double getYaw()
    {
        return yaw;
    }

    public double getRoll()
    {
        return roll;
    }

    public void setPitch(double pitch)
    {
        /*while(pitch >= Math.PI * 2.0)
        {
            if(pitch > 0.0)
                pitch -= Math.PI * 2.0;
            else
                pitch += Math.PI * 2.0;
        }*/

        this.pitch = pitch;
    }

    public void setRoll(double roll)
    {
        /*while(roll >= Math.PI * 2.0)
        {
            if(roll > 0.0)
                roll -= Math.PI * 2.0;
            else
                roll += Math.PI * 2.0;
        }*/

        this.roll = roll;
    }

    public void setYaw(double yaw)
    {
        /*while(yaw >= Math.PI * 2.0)
        {
            if(yaw > 0.0)
                yaw -= Math.PI * 2.0;
            else
                yaw += Math.PI * 2.0;
        }*/

        this.yaw = yaw;
    }

    public abstract void draw();

    public abstract void tick(World world, Random random, double deltaTime);
}
