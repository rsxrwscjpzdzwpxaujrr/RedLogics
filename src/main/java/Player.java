import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Gazibalonchik on 11.01.2016.
 * Класс игрока
 */
public class Player extends Entity
{
    private float height;
    public float speed;
    public Camera camera;
    private int handsBlockID;
    private double handsDistance;
    private Entity minecart;

    public Player()
    {
        height = 1.5f;
        camera = new Camera(positionX, positionY + height, positionZ, 0.0, 0.0, 0.0, 80.0f);
        setPosition(0.0, 128.0, 0.0);
        speed = 32.0f;
        handsBlockID = 1;
        handsDistance = 16.0;
    }

    public void tick(World world, KeyState[] keys, KeyState[] buttons, double deltaTime)
    {
        double testX = camera.positionX;
        double testY = camera.positionY;
        double testZ = camera.positionZ;
        double distance = 0.0;
        double step = 0.001;

        for(int i = 1; i < 9; i++)
            if(keys[GLFW_KEY_0 + i] == KeyState.PRESSEDNOW)
                handsBlockID = i;

        if(keys[Settings.jumpKey].pressed() && buttons[Settings.blockPlaceButton] == KeyState.PRESSEDNOW)
            world.addEntity(new EntityMinecart(getPositionX(), getPositionY(), getPositionZ()));

        if(keys[Settings.jumpKey].pressed() && keys[Settings.sneakKey] == KeyState.PRESSEDNOW || keys[Settings.jumpKey] == KeyState.PRESSEDNOW && keys[Settings.sneakKey].pressed())
        {
            if(minecart != null)
            {
                ((EntityMinecart)minecart).setPassenger(null);
            }

            minecart = new EntityMinecart(getPositionX(), getPositionY(), getPositionZ());
            ((EntityMinecart)minecart).setPassenger(this);
            world.addEntity(minecart);
        }

        if(keys[Settings.jumpKey].pressed() && buttons[Settings.blockBreakButton] == KeyState.PRESSEDNOW && minecart != null)
            ((EntityMinecart)minecart).setPassenger(null);

        if(keys[Settings.upKey].pressed())
            setPosition(positionX + (deltaTime * speed * Math.sin(camera.getYaw()) * Math.cos(camera.getPitch())),
                        positionY - (deltaTime * speed * Math.sin(camera.getPitch())),
                        positionZ - (deltaTime * speed * Math.cos(camera.getYaw()) * Math.cos(camera.getPitch())));

        if(keys[Settings.downKey].pressed())
            setPosition(positionX - (deltaTime * speed * Math.sin(camera.getYaw()) * Math.cos(camera.getPitch())),
                        positionY + (deltaTime * speed * Math.sin(camera.getPitch())),
                        positionZ + (deltaTime * speed * Math.cos(camera.getYaw()) * Math.cos(camera.getPitch())));

        if(keys[Settings.leftKey].pressed())
            setPosition(positionX - (deltaTime * speed * Math.cos(camera.getYaw())),
                        positionY,
                        positionZ - (deltaTime * speed * Math.sin(camera.getYaw())));

        if(keys[Settings.rightKey].pressed())
            setPosition(positionX + (deltaTime * speed * Math.cos(camera.getYaw())),
                        positionY,
                        positionZ + (deltaTime * speed * Math.sin(camera.getYaw())));

        if(keys[Settings.jumpKey].pressed())
            setPositionY(positionY + (deltaTime * speed));

        if(keys[Settings.sneakKey].pressed())
            setPositionY(positionY - (deltaTime * speed));

        if(buttons[Settings.blockBreakButton] == KeyState.PRESSEDNOW)
        {
            while(!world.inBoundingBox(testX, testY, testZ) && handsDistance >= distance)
            {
                testX += (step * Math.sin(camera.getYaw()) * Math.cos(camera.getPitch()));
                testY -= (step * Math.sin(camera.getPitch()));
                testZ -= (step * Math.cos(camera.getYaw()) * Math.cos(camera.getPitch()));
                distance += step;
            }

            world.breakBlock((int) testX, (int) testY, (int) testZ);
        }

        if(buttons[Settings.blockPlaceButton] == KeyState.PRESSEDNOW)
        {
            while(!world.inBoundingBox(testX, testY, testZ) && handsDistance >= distance)
            {
                testX += (step * Math.sin(camera.getYaw()) * Math.cos(camera.getPitch()));
                testY -= (step * Math.sin(camera.getPitch()));
                testZ -= (step * Math.cos(camera.getYaw()) * Math.cos(camera.getPitch()));
                distance += step;
            }

            if(world.inBoundingBox(testX, testY, testZ))
            {
                try
                {
                    switch(world.getSideOnWhichPoint(testX, testY, testZ))
                    {
                    case FRONT:
                        world.setBlockID((int)testX, ((int)testY), (int)testZ + 1, handsBlockID);
                        break;

                    case BACK:
                        world.setBlockID((int)testX, ((int)testY), (int)testZ - 1, handsBlockID);
                        break;

                    case TOP:
                        world.setBlockID((int)testX, ((int)testY) + 1, (int)testZ, handsBlockID);
                        break;

                    case BOTTOM:
                        world.setBlockID((int)testX, ((int)testY) - 1, (int)testZ, handsBlockID);
                        break;

                    case RIGHT:
                        world.setBlockID((int)testX + 1, ((int)testY), (int)testZ, handsBlockID);
                        break;

                    case LEFT:
                        world.setBlockID((int)testX - 1, ((int)testY), (int)testZ, handsBlockID);
                        break;
                    }
                }
                catch(Exception exception)
                {
                    System.out.println(exception.getMessage());
                }
            }
        }
    }

    @Override
    public void setPosition(double x, double y, double z)
    {
        positionX = x;
        camera.positionX = x;

        positionY = y;
        camera.positionY = y + height;

        positionZ = z;
        camera.positionZ = z;
    }

    @Override
    public void setPositionX(double x)
    {
        positionX = x;
        camera.positionX = x;
    }

    @Override
    public void setPositionY(double y)
    {
        positionY = y;
        camera.positionY = y + height;
    }

    @Override
    public void setPositionZ(double z)
    {
        positionZ = z;
        camera.positionZ = z;
    }

    @Override
    public void draw()
    {
    }

    @Override
    public void tick(World world, Random random, double deltaTime)
    {

    }

    public void setHeight(float height)
    {
        this.height = height;
        camera.positionY = positionY + height;
    }

    public float getHeight()
    {
        return height;
    }
}
