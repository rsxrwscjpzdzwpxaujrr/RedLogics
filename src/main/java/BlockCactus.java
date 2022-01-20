import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 08.04.2016.
 */
public class BlockCactus extends Block
{
    private Color thornsColor;
    private Model thornsSidesModel;
    private Model thornsCapModel;

    public BlockCactus()
    {
        super(new Color(0.125f, 0.35f, 0.1f), false, true, true);
        thornsColor = new Color(0.0625f, 0.0625f, 0.0625f);
        thornsSidesModel = new Model();
        thornsCapModel = new Model();
        thornsSidesModel.readFromObj("cactusSides.obj");
        thornsCapModel.readFromObj("cactusCap.obj");
    }

    @Override
    public void draw(Chunk chunk, Direction cameraDirection, boolean visible, int x, int y, int z, boolean[] neighbourOpaque, int light)
    {
        float lightness = light / 15.0f;
        int metadata = chunk.getBlockMetadata(x % 16, y % 16, z % 16);
        //Direction cameraDirection = camera.getDirection();

        glColor3f(color.red * lightness, color.green * lightness, color.blue * lightness);

        // Front
        glNormal3f(0, 0, 1);
        glVertex3f(x + 0.0625f, y, z + 0.9375f);
        glVertex3f(x + 0.9375f, y, z + 0.9375f);
        glVertex3f(x + 0.9375f, y + 1, z + 0.9375f);

        glVertex3f(x + 0.0625f, y, z + 0.9375f);
        glVertex3f(x + 0.9375f, y + 1, z + 0.9375f);
        glVertex3f(x + 0.0625f, y + 1, z + 0.9375f);

        // Back
        glNormal3f(0, 0, -1);
        glVertex3f(x + 0.0625f, y + 1, z + 0.0625f);
        glVertex3f(x + 0.9375f, y + 1, z + 0.0625f);
        glVertex3f(x + 0.9375f, y, z + 0.0625f);

        glVertex3f(x + 0.0625f, y + 1, z + 0.0625f);
        glVertex3f(x + 0.9375f, y, z + 0.0625f);
        glVertex3f(x + 0.0625f, y, z + 0.0625f);

        if((metadata & 0b0100) == 0)
        {
            // Top
            glNormal3f(0, 1, 0);
            glVertex3f(x + 0.0625f, y + 1, z + 0.0625f);
            glVertex3f(x + 0.0625f, y + 1, z + 0.9375f);
            glVertex3f(x + 0.9375f, y + 1, z + 0.9375f);

            glVertex3f(x + 0.0625f, y + 1, z + 0.0625f);
            glVertex3f(x + 0.9375f, y + 1, z + 0.9375f);
            glVertex3f(x + 0.9375f, y + 1, z + 0.0625f);
        }

        // Right
        glNormal3f(1, 0, 0);
        glVertex3f(x + 0.9375f, y, z + 0.0625f);
        glVertex3f(x + 0.9375f, y + 1, z + 0.0625f);
        glVertex3f(x + 0.9375f, y + 1, z + 0.9375f);

        glVertex3f(x + 0.9375f, y, z + 0.0625f);
        glVertex3f(x + 0.9375f, y + 1, z + 0.9375f);
        glVertex3f(x + 0.9375f, y, z + 0.9375f);

        // Left
        glNormal3f(-1, 0, 0);
        glVertex3f(x + 0.0625f, y, z + 0.9375f);
        glVertex3f(x + 0.0625f, y + 1, z + 0.9375f);
        glVertex3f(x + 0.0625f, y + 1, z + 0.0625f);

        glVertex3f(x + 0.0625f, y, z + 0.9375f);
        glVertex3f(x + 0.0625f, y + 1, z + 0.0625f);
        glVertex3f(x + 0.0625f, y, z + 0.0625f);

        glEnd();
        glColor3f(thornsColor.red * lightness, thornsColor.green * lightness, thornsColor.blue * lightness);
        glPushMatrix();
        glTranslated(x, y, z);
        thornsSidesModel.draw();
        if((metadata & 0b0100) == 0)
            thornsCapModel.draw();
        glPopMatrix();
        glBegin(GL_TRIANGLES);
    }

    @Override
    public void onRandomTick(World world, Chunk chunk, int x, int y, int z, int localX, int localY, int localZ, Random random)
    {
        if(random.nextInt(2048) == 0)
        {
            int metadata = chunk.getBlockMetadata(localX, localY, localZ);

            if((metadata & 0b0110) == 0)
            {
                chunk.setBlockMetadata(localX, localY, localZ, metadata |= 1);

                if(metadata % 2 == 1 && world.getBlock(x, y + 1, z) == Block.air)
                    world.setBlock(x, y + 1, z, Block.cactus);
            }
        }
    }

    @Override
    public void onUpdate(World world, Chunk chunk, int x, int y, int z, int localX, int localY, int localZ, Random random)
    {
        int metadata = chunk.getBlockMetadata(localX, localY, localZ);

        if(world.getBlock(x, y, z + 1) != Block.air || world.getBlock(x, y, z - 1) != Block.air || world.getBlock(x + 1, y, z) != Block.air || world.getBlock(x - 1, y, z) != Block.air || !(world.getBlock(x, y - 1, z) == Block.sand || world.getBlock(x, y - 1, z) == this))
            world.setBlock(x, y, z, Block.air);
        else
        {
            if(world.getBlock(x, y + 1, z) != Block.air)
                chunk.setBlockMetadata(localX, localY, localZ, metadata |= 0b0100);
            else
                chunk.setBlockMetadata(localX, localY, localZ, metadata &= 0b1011);

            if(world.getBlock(x, y - 1, z) == Block.cactus && world.getBlock(x, y - 2, z) == Block.cactus)
                chunk.setBlockMetadata(localX, localY, localZ, metadata |= 0b0010);
            else
                chunk.setBlockMetadata(localX, localY, localZ, metadata &= 0b1101);
        }
    }
}