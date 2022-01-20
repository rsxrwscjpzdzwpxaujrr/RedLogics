import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 26.01.2016.
 */
public class BlockWithDifferentColors extends Block
{
    Color frontColor;
    Color backColor;
    Color topColor;
    Color bottomColor;
    Color rightColor;
    Color leftColor;

    public BlockWithDifferentColors(Color frontColor, Color backColor, Color topColor, Color bottomColor, Color rightColor, Color leftColor, boolean isOpaque)
    {
        super(null, isOpaque);
        this.frontColor = frontColor;
        this.backColor = backColor;
        this.topColor = topColor;
        this.bottomColor = bottomColor;
        this.rightColor = rightColor;
        this.leftColor = leftColor;
    }

    public void draw(Chunk chunk, Direction cameraDirection, boolean visible, int x, int y, int z, boolean[] neighbourOpaque, int light)
    {
        //Direction cameraDirection = camera.getDirection();

        float lightness = light / 15.0f;

        if(!neighbourOpaque[0])
        {
            glColor4f(frontColor.red * lightness, frontColor.green * lightness, frontColor.blue * lightness, frontColor.alpha);
            //glColor3f(1.0f, 0.0f, 0.0f);

            // Front
            glNormal3f(0, 0, 1);
            glVertex3i(x, y, z + 1);
            glVertex3i(x + 1, y, z + 1);
            glVertex3i(x + 1, y + 1, z + 1);

            glVertex3i(x, y, z + 1);
            glVertex3i(x + 1, y + 1, z + 1);
            glVertex3i(x, y + 1, z + 1);
        }

        if(!neighbourOpaque[1])
        {
            glColor4f(backColor.red * lightness, backColor.green * lightness, backColor.blue * lightness, backColor.alpha);
            //glColor3f(1.0f, 1.0f, 0.0f);

            // Back
            glNormal3f(0, 0, -1);
            glVertex3i(x, y + 1, z);
            glVertex3i(x + 1, y + 1, z);
            glVertex3i(x + 1, y, z);

            glVertex3i(x, y + 1, z);
            glVertex3i(x + 1, y, z);
            glVertex3i(x, y, z);
        }

        if(!neighbourOpaque[2])
        {
            glColor4f(topColor.red * lightness, topColor.green * lightness, topColor.blue * lightness, topColor.alpha);
            //glColor3f(0.0f, 1.0f, 0.0f);

            // Top
            glNormal3f(0, 1, 0);
            glVertex3i(x, y + 1, z);
            glVertex3i(x, y + 1, z + 1);
            glVertex3i(x + 1, y + 1, z + 1);

            glVertex3i(x, y + 1, z);
            glVertex3i(x + 1, y + 1, z + 1);
            glVertex3i(x + 1, y + 1, z);
        }

        if(!neighbourOpaque[3])
        {
            glColor4f(bottomColor.red * lightness, bottomColor.green * lightness, bottomColor.blue * lightness, bottomColor.alpha);
            //glColor3f(0.0f, 1.0f, 1.0f);

            // Bottom
            glNormal3f(0, -1, 0);
            glVertex3i(x + 1, y, z);
            glVertex3i(x + 1, y, z + 1);
            glVertex3i(x, y, z + 1);

            glVertex3i(x + 1, y, z);
            glVertex3i(x, y, z + 1);
            glVertex3i(x, y, z);
        }

        if(!neighbourOpaque[4])
        {
            glColor4f(rightColor.red * lightness, rightColor.green * lightness, rightColor.blue * lightness, rightColor.alpha);
            //glColor3f(0.0f, 0.0f, 1.0f);

            // Right
            glNormal3f(1, 0, 0);
            glVertex3i(x + 1, y, z);
            glVertex3i(x + 1, y + 1, z);
            glVertex3i(x + 1, y + 1, z + 1);

            glVertex3i(x + 1, y, z);
            glVertex3i(x + 1, y + 1, z + 1);
            glVertex3i(x + 1, y, z + 1);
        }

        if(!neighbourOpaque[5])
        {
            glColor4f(leftColor.red * lightness, leftColor.green * lightness, leftColor.blue * lightness, leftColor.alpha);
            //glColor3f(1.0f, 0.0f, 1.0f);

            // Left
            glNormal3f(-1, 0, 0);
            glVertex3i(x, y, z + 1);
            glVertex3i(x, y + 1, z + 1);
            glVertex3i(x, y + 1, z);

            glVertex3i(x, y, z + 1);
            glVertex3i(x, y + 1, z);
            glVertex3i(x, y, z);
        }
    }
}
