import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 23.02.2016.
 */
public class BlockBrick extends Block
{
    Model model = new Model();
    Color cementColor;

    public BlockBrick()
    {
        super(new Color(0.5f, 0.3f, 0.25f), false);
        cementColor = new Color(0.6f, 0.6f, 0.6f);
        model.readFromObj("brick.obj");
    }

    @Override
    public void draw(Chunk chunk, Direction cameraDirection, boolean visible, int x, int y, int z, boolean[] neighbourOpaque, int light)
    {
        float lightness = light / 15.0f;

        int meta = chunk.getBlockMetadata(x % 16, y % 16, z % 16);
        neighbourOpaque[0] |= (meta & 0b0001) > 0;
        neighbourOpaque[1] |= (meta & 0b0010) > 0;
        neighbourOpaque[4] |= (meta & 0b0100) > 0;
        neighbourOpaque[5] |= (meta & 0b1000) > 0;

        float xbig;
        float xsml;
        float zbig;
        float zsml;

        if(neighbourOpaque[0])
            zbig = z + 1.0f;
        else
            zbig = z + 0.9375f;

        if(neighbourOpaque[1])
            zsml = z;
        else
            zsml = z + 0.0625f;

        if(neighbourOpaque[4])
            xbig = x + 1.0f;
        else
            xbig = x + 0.9375f;

        if(neighbourOpaque[5])
            xsml = x;
        else
            xsml = x + 0.0625f;

        glColor3f(cementColor.red * lightness, cementColor.green * lightness, cementColor.blue * lightness);
        
        if(!neighbourOpaque[0])
        {
            // Front
            glNormal3f(0, 0, 1);
            glVertex3f(xsml, y, zbig);
            glVertex3f(xbig, y, zbig);
            glVertex3f(xbig, y + 1, zbig);

            glVertex3f(xsml, y, zbig);
            glVertex3f(xbig, y + 1, zbig);
            glVertex3f(xsml, y + 1, zbig);
        }

        if(!neighbourOpaque[1])
        {
            // Back
            glNormal3f(0, 0, -1);
            glVertex3f(xsml, y + 1, zsml);
            glVertex3f(xbig, y + 1, zsml);
            glVertex3f(xbig, y, zsml);

            glVertex3f(xsml, y + 1, zsml);
            glVertex3f(xbig, y, zsml);
            glVertex3f(xsml, y, zsml);
        }

        if(!neighbourOpaque[2])
        {
            // Top
            glNormal3f(0, 1, 0);
            glVertex3f(xsml, y + 1, zsml);
            glVertex3f(xsml, y + 1, zbig);
            glVertex3f(xbig, y + 1, zbig);

            glVertex3f(xsml, y + 1, zsml);
            glVertex3f(xbig, y + 1, zbig);
            glVertex3f(xbig, y + 1, zsml);
        }

        if(!neighbourOpaque[3])
        {
            // Bottom
            glNormal3f(0, -1, 0);
            glVertex3f(xbig, y, zsml);
            glVertex3f(xbig, y, zbig);
            glVertex3f(xsml, y, zbig);

            glVertex3f(xbig, y, zsml);
            glVertex3f(xsml, y, zbig);
            glVertex3f(xsml, y, zsml);
        }

        if(!neighbourOpaque[4])
        {
            // Right
            glNormal3f(1, 0, 0);
            glVertex3f(xbig, y, zsml);
            glVertex3f(xbig, y + 1, zsml);
            glVertex3f(xbig, y + 1, zbig);

            glVertex3f(xbig, y, zsml);
            glVertex3f(xbig, y + 1, zbig);
            glVertex3f(xbig, y, zbig);
        }

        if(!neighbourOpaque[5])
        {
            // Left
            glNormal3f(-1, 0, 0);
            glVertex3f(xsml, y, zbig);
            glVertex3f(xsml, y + 1, zbig);
            glVertex3f(xsml, y + 1, zsml);

            glVertex3f(xsml, y, zbig);
            glVertex3f(xsml, y + 1, zsml);
            glVertex3f(xsml, y, zsml);
        }
        
        glEnd();
        
        glPushMatrix();

        glColor3f(color.red * lightness, color.green * lightness, color.blue * lightness);
        glTranslated(x + 0.5, y + 0.5, z + 0.5);

        for(int i = 0; i < 2; i++)
        {
            glRotated(180.0, 0.0, 1.0, 0.0);

            if(!neighbourOpaque[i])
                model.draw();
        }

        glPopMatrix();
        glPushMatrix();

        glTranslated(x + 0.5, y + 0.5, z + 0.5);
        glRotated(90.0, 0.0, 1.0, 0.0);

        for(int i = 4; i < 6; i++)
        {
            glRotated(180.0, 0.0, 1.0, 0.0);

            if(!neighbourOpaque[i])
                model.draw();
        }

        glPopMatrix();
        glBegin(GL_TRIANGLES);
    }

    @Override
    public void onUpdate(World world, Chunk chunk, int x, int y, int z, int localX, int localY, int localZ, Random random)
    {
        int meta = 0;

        if(world.getBlock(x, y, z + 1) == Block.brick)
            meta |= 0b0001;

        if(world.getBlock(x, y, z - 1) == Block.brick)
            meta |= 0b0010;

        if(world.getBlock(x + 1, y, z) == Block.brick)
            meta |= 0b0100;

        if(world.getBlock(x - 1, y, z) == Block.brick)
            meta |= 0b1000;

        chunk.setBlockMetadata(localX, localY, localZ, meta);
    }
}
