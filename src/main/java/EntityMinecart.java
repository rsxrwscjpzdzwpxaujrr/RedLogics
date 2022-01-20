import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 15.02.2016.
 */
public class EntityMinecart extends Entity
{
    private double speed;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private Entity passenger;
    private float mileage;
    private boolean onRail;
    BlockRail railOnWhichMinecart;
    private Model normalModel = new Model();

    public EntityMinecart(double positionX, double positionY, double positionZ)
    {
        super(positionX, positionY, positionZ);
        normalModel.readFromObj("minecart.obj");
        onRail = false;
        setSpeed(0.0f);
        setVelocityX(-0.25);
        setVelocityZ(-0.25);
    }

    public Entity getPassenger()
    {
        return passenger;
    }

    public void setPassenger(Entity passenger)
    {
        this.passenger = passenger;
    }

    @Override
    public void draw()
    {
        glPushMatrix();
        glTranslated(getPositionX(), getPositionY(), getPositionZ());
        glRotated(Math.toDegrees(getYaw()), 0.0, 1.0, 0.0);
        glRotated(Math.toDegrees(getPitch()), 1.0, 0.0, 0.0);
        glRotated(Math.toDegrees(getRoll()), 0.0, 0.0, 1.0);
        glColor3f(0.5f, 0.5f, 0.5f);
        normalModel.draw();
        glPopMatrix();
    }

    @Override
    public void tick(World world, Random random, double deltaTime)
    {
        if(!world.inBoundingBox(getPositionX(), getPositionY(), getPositionZ()))
        {
            velocityY -= 9.8 * deltaTime;
            onRail = false;
        }
        else
        {
            if(world.getBlock((int)getPositionX(), (int)getPositionY(), (int)getPositionZ()) instanceof BlockRail)
            {
                railOnWhichMinecart = (BlockRail)world.getBlock((int)getPositionX(), (int)getPositionY(), (int)getPositionZ());
                onRail = true;

                if(getPositionY() - (int)getPositionY() < Block.rail.getBoundingBox()[4])
                    setPositionY((int)getPositionY() + Block.rail.getBoundingBox()[4]);
            }
            else
            {
                onRail = false;
                //setPositionY(getPositionY() + 0.01);
                setVelocityX(0.0);
                setVelocityZ(0.0);
            }

            velocityY = 0.0;
        }

        if(onRail)
        {
            double[] railDirection = new double[3];
            double friction = railOnWhichMinecart.getFriction(world, random, (int)getPositionX(), (int)getPositionY(), (int)getPositionZ());

            if(     world.getBlockMetadata((int)getPositionX(), (int)getPositionY(), (int)getPositionZ()) == 0 ||
                    world.getBlockMetadata((int)getPositionX(), (int)getPositionY(), (int)getPositionZ()) == 10 ||
                    world.getBlockMetadata((int)getPositionX(), (int)getPositionY(), (int)getPositionZ()) == 11 ||
                    world.getBlockMetadata((int)getPositionX(), (int)getPositionY(), (int)getPositionZ()) == 12)
                setPositionX((int)getPositionX() + 0.5);
            else if(world.getBlockMetadata((int)getPositionX(), (int)getPositionY(), (int)getPositionZ()) == 1 ||
                    world.getBlockMetadata((int)getPositionX(), (int)getPositionY(), (int)getPositionZ()) == 10 ||
                    world.getBlockMetadata((int)getPositionX(), (int)getPositionY(), (int)getPositionZ()) == 13 ||
                    world.getBlockMetadata((int)getPositionX(), (int)getPositionY(), (int)getPositionZ()) == 14)
                setPositionZ((int)getPositionZ() + 0.5);

            try
            {
                railDirection = railOnWhichMinecart.getDirection(world, random, getPositionX(), getPositionY(), getPositionZ(), getVelocityX(), getVelocityY(), getVelocityZ());
            }
            catch(Exception exception)
            {
                System.out.println(exception.getMessage());
            }

            setPitch(railDirection[0]);
            setYaw(railDirection[1]);
            setRoll(railDirection[2]);

            setSpeed(getSpeed() + (Math.sin(getPitch()) * 9.8));

            setSpeed(getSpeed() * (1.0 - (friction * deltaTime)));

            setVelocityX(Math.cos(getPitch()) * Math.sin(getYaw()) * getSpeed() * deltaTime);
            setVelocityY(-Math.sin(getPitch()) * getSpeed() * deltaTime);
            setVelocityZ(Math.cos(getPitch()) * Math.cos(getYaw()) * getSpeed() * deltaTime);
        }
        else
        {
            //setVelocityX(0.0);
            //setVelocityZ(0.0);
        }

        setPositionX(getPositionX() + velocityX * deltaTime);
        setPositionY(getPositionY() + velocityY * deltaTime);
        setPositionZ(getPositionZ() + velocityZ * deltaTime);

        if(passenger != null)
        {
            passenger.setPositionX(getPositionX());
            passenger.setPositionY(getPositionY());
            passenger.setPositionZ(getPositionZ());
            passenger.setPitch(passenger.getPitch() + getPitch());
            passenger.setYaw(passenger.getYaw() + getYaw());
            passenger.setRoll(passenger.getRoll() + getRoll());
        }
    }

    public double getSpeed()
    {
        return speed;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public float getMileage()
    {
        return mileage;
    }

    public void setMileage(float mileage)
    {
        this.mileage = mileage;
    }

    public double getVelocityZ()
    {
        return velocityZ;
    }

    public void setVelocityZ(double velocityZ)
    {
        this.velocityZ = velocityZ;
    }

    public double getVelocityY()
    {
        return velocityY;
    }

    public void setVelocityY(double velocityY)
    {
        this.velocityY = velocityY;
    }

    public double getVelocityX()
    {
        return velocityX;
    }

    public void setVelocityX(double velocityX)
    {
        this.velocityX = velocityX;
    }
}
