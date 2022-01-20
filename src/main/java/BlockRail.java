import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 16.02.2016.
 */
public class BlockRail extends Block
{
    protected double[] raisingBoundingBox = new double[]{0.0, 0.0, 0.0, 1.0, 1.125, 1.0};
    private Color tieColor;
    private Color profileColor;
    private Model normalModel = new Model();
    private Model turnModel = new Model();
    private Model TIntersectionModel = new Model();
    private Model IntersectionModel = new Model();
    private Model RaisingModel = new Model();

    public BlockRail(Color tieColor, Color profileColor)
    {
        super(null, false);
        this.tieColor = tieColor;
        this.profileColor = profileColor;
        this.color = profileColor;
        boundingBox = new double[]{0.0, 0.0, 0.0, 1.0, 0.125, 1.0};
        normalModel.readFromObj("rail.obj");
        turnModel.readFromObj("railTurn.obj");
        TIntersectionModel.readFromObj("railTIntersection.obj");
        IntersectionModel.readFromObj("railIntersection.obj");
        RaisingModel.readFromObj("railRaising.obj");
    }

    @Override
    public boolean inBoundingBox(double localX, double localY, double localZ, int metadata)
    {
        if(metadata >= 11)
            return localX > raisingBoundingBox[0] && localX < raisingBoundingBox[3] && localY > raisingBoundingBox[1] && localY < raisingBoundingBox[4] && localZ > raisingBoundingBox[2] && localZ < raisingBoundingBox[5] && isVisible();
        else
            return localX > boundingBox[0] && localX < boundingBox[3] && localY > boundingBox[1] && localY < boundingBox[4] && localZ > boundingBox[2] && localZ < boundingBox[5] && isVisible();
    }

    private double angle(double x1, double x2, double y1, double y2)
    {
        double a = Math.atan((-(y1 - y2) / (x2 - x1)));
        a = x1 < x2 ? a + Math.PI : a;
        a = a < 0.0 ?  (2.0 * Math.PI) + a : a;
        return a;
    }

    public double[] getDirection(World world, Random random, double x, double y, double z, double vx, double vy, double vz) throws Exception
    {
        double localX = x - (int)x;
        double localY = y - (int)y;
        double localZ = z - (int)z;

        switch(world.getBlockMetadata((int)x, (int)y, (int)z))
        {
        case 0:
            if(vz > 0.0)
                return new double[]{Math.PI * 0.0, Math.PI * 0.0, 0.0};
            else
                return new double[]{Math.PI * 0.0, Math.PI * 1.0, 0.0};

        case 1:
            if(vx > 0.0)
                return new double[]{Math.PI * 0.0, Math.PI * 0.5, 0.0};
            else
                return new double[]{Math.PI * 0.0, Math.PI * 1.5, 0.0};

        case 2:
            if(vx <= 0.0 && vz <= 0.0)
                return new double[]{Math.PI * 0.0, angle(0.0, localZ, 1.0, localX) + Math.PI * 0.5, 0.0};
            else
                return new double[]{Math.PI * 0.0, angle(0.0, localZ, 1.0, localX) - Math.PI * 0.5, 0.0};

        case 3:
            if(vx > vz)
                return new double[]{Math.PI * 0.0, angle(0.0, localZ, 0.0, localX) - Math.PI * 0.5, 0.0};
            else
                return new double[]{Math.PI * 0.0, angle(0.0, localZ, 0.0, localX) + Math.PI * 0.5, 0.0};

        case 4:
            if(vx > vz)
                return new double[]{Math.PI * 0.0, angle(1.0, localZ, 1.0, localX) + Math.PI * 0.5, 0.0};
            else
                return new double[]{Math.PI * 0.0, angle(1.0, localZ, 1.0, localX) - Math.PI * 0.5, 0.0};

        case 5:
            if(vx >= 0.0 && vz >= 0.0)
                return new double[]{Math.PI * 0.0, angle(1.0, localZ, 0.0, localX) + Math.PI * 0.5, 0.0};
            else
                return new double[]{Math.PI * 0.0, angle(1.0, localZ, 0.0, localX) - Math.PI * 0.5, 0.0};

        case 10:
            if(Math.abs(vz) > Math.abs(vx))
            {
                if(vz > 0.0)
                    return new double[]{Math.PI * 0.0, Math.PI * 0.0, 0.0};
                else
                    return new double[]{Math.PI * 0.0, Math.PI * 1.0, 0.0};
            }
            else
            {
                if(vx > 0.0)
                    return new double[]{Math.PI * 0.0, Math.PI * 0.5, 0.0};
                else
                    return new double[]{Math.PI * 0.0, Math.PI * 1.5, 0.0};
            }

        case 11:
            if(vz < 0.0)
                return new double[]{Math.PI * 0.25, Math.PI * 1.0, 0.0};
            else
                return new double[]{Math.PI * 1.75, Math.PI * 0.0, 0.0};

        case 12:
            if(vz < 0.0)
                return new double[]{Math.PI * 1.75, Math.PI * 1.0, 0.0};
            else
                return new double[]{Math.PI * 0.25, Math.PI * 0.0, 0.0};

        case 13:
            if(vx < 0.0)
                return new double[]{Math.PI * 0.25, Math.PI * 1.5, 0.0};
            else
                return new double[]{Math.PI * 1.75, Math.PI * 0.5, 0.0};

        case 14:
            if(vx < 0.0)
                return new double[]{Math.PI * 1.75, Math.PI * 1.5, 0.0};
            else
                return new double[]{Math.PI * 0.25, Math.PI * 0.5, 0.0};

        default:
            throw new Exception("Invalid metadata");
        }
    }

    public double getFriction(World world, Random random, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);

        if(metadata >= 2 && metadata <= 5)
            return 0.5;
        else
            return 0.25;
    }

    @Override
    public void draw(Chunk chunk, Direction cameraDirection, boolean visible, int x, int y, int z, boolean[] neighbourOpaque, int light)
    {
        glEnd();
        glPushMatrix();

        int meta = chunk.getBlockMetadata(x % 16, y % 16, z % 16);
        float lightness = light / 15.0f;
        glColor4f(color.red * lightness, color.green * lightness, color.blue * lightness, color.alpha);

        switch(meta)
        {
        case 0:
            glTranslated(x, y, z + 1);
            glRotated(90.0, 0.0, 1.0, 0.0);
            normalModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 1:
            glTranslated(x, y, z);
            normalModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 2:
            glTranslated(x + 1, y, z);
            glRotated(270.0, 0.0, 1.0, 0.0);
            turnModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 3:
            glTranslated(x, y, z);
            turnModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 4:
            glTranslated(x + 1, y, z + 1);
            glRotated(180.0, 0.0, 1.0, 0.0);
            turnModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 5:
            glEnd();
            glPushMatrix();
            glTranslated(x, y, z + 1);
            glRotated(90.0, 0.0, 1.0, 0.0);
            turnModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 6:
            glTranslated(x + 1, y, z + 1);
            glRotated(180.0, 0.0, 1.0, 0.0);
            TIntersectionModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 7:
            glTranslated(x, y, z);
            TIntersectionModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 8:
            glTranslated(x + 1, y, z);
            glRotated(270.0, 0.0, 1.0, 0.0);
            TIntersectionModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 9:
            glTranslated(x, y, z + 1);
            glRotated(90.0, 0.0, 1.0, 0.0);
            TIntersectionModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 10:
            glTranslated(x, y, z);
            IntersectionModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 11:
            glTranslated(x, y, z + 1);
            glRotated(90.0, 0.0, 1.0, 0.0);
            RaisingModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 12:
            glTranslated(x + 1, y, z);
            glRotated(270.0, 0.0, 1.0, 0.0);
            RaisingModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 13:
            glTranslated(x + 1, y, z + 1);
            glRotated(180.0, 0.0, 1.0, 0.0);
            RaisingModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;

        case 14:
            glTranslated(x, y, z);
            RaisingModel.draw();
            glPopMatrix();
            glBegin(GL_TRIANGLES);
            break;
        }
    }

    @Override
    protected void onUpdate(World world, Chunk chunk, int x, int y, int z, int localX, int localY, int localZ, Random random)
    {
        int result = 0;

        boolean nyp = world.getBlock(x, y + 1, z) == Block.air;

        boolean ypzp = world.getBlock(x, y + 1, z + 1) == Block.rail && nyp;
        boolean ypzm = world.getBlock(x, y + 1, z - 1) == Block.rail && nyp;
        boolean ypxp = world.getBlock(x + 1, y + 1, z) == Block.rail && nyp;
        boolean ypxm = world.getBlock(x - 1, y + 1, z) == Block.rail && nyp;

        boolean ymzp = world.getBlock(x, y - 1, z + 1) == Block.rail && world.getBlock(x, y, z + 1) == Block.air;
        boolean ymzm = world.getBlock(x, y - 1, z - 1) == Block.rail && world.getBlock(x, y, z - 1) == Block.air;
        boolean ymxp = world.getBlock(x + 1, y - 1, z) == Block.rail && world.getBlock(x + 1, y, z) == Block.air;
        boolean ymxm = world.getBlock(x - 1, y - 1, z) == Block.rail && world.getBlock(x - 1, y, z) == Block.air;

        boolean zp = world.getBlock(x, y, z + 1) == Block.rail || ymzp;
        boolean zm = world.getBlock(x, y, z - 1) == Block.rail || ymzm;
        boolean xp = world.getBlock(x + 1, y, z) == Block.rail || ymxp;
        boolean xm = world.getBlock(x - 1, y, z) == Block.rail || ymxm;

        if(xp)
        {
            if(zm)
                result = 2;
            else if(zp)
                result = 4;
            else
                result = 1;
        }

        if(xm)
        {
            if(zm)
                result = 3;
            else if(zp)
                result = 5;
            else
                result = 1;
        }

        if(zp && xp && xm)
            result = 6;

        if(zm && xp && xm)
            result = 7;

        if(zm && zp && xp)
            result = 8;

        if(zm && zp && xm)
            result = 9;

        if(zm && zp && xp && xm)
            result = 10;

        if(ypzp)
            result = 11;

        if(ypzm)
            result = 12;

        if(ypxp)
            result = 13;

        if(ypxm)
            result = 14;

        world.setBlockMetadata(x, y, z, result);
    }
}
