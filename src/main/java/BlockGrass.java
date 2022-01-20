import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 24.02.2016.
 */
public class BlockGrass extends Block
{
    public BlockGrass()
    {
        super(new Color(0.5f, 0.8f, 0.3f), true);
    }

    @Override
    public void draw(Chunk chunk, Direction cameraDirection, boolean visible, int x, int y, int z, boolean[] neighbourOpaque, int light)
    {
        float lightness = light / 15.0f;
        //Direction cameraDirection = camera.getDirection();

        //new Color(0.5f, 0.4f, 0.25f)


        if(!neighbourOpaque[0])
        {
            glColor3f(0.5f * lightness, 0.4f * lightness, 0.25f * lightness);

            // Front
            glNormal3f(0, 0, 1);
            glVertex3i(x, y, z + 1);
            glVertex3i(x + 1, y, z + 1);
            glVertex3f(x + 1, y + 0.875f, z + 1);

            glVertex3i(x, y, z + 1);
            glVertex3f(x + 1, y + 0.875f, z + 1);
            glVertex3f(x, y + 0.875f, z + 1);

            glColor3f(color.red * lightness, color.green * lightness, color.blue * lightness);

            // Front
            //glNormal3f(0, 0, 1);
            glVertex3f(x, y + 0.875f, z + 1);
            glVertex3f(x + 1, y + 0.875f, z + 1);
            glVertex3i(x + 1, y + 1, z + 1);

            glVertex3f(x, y + 0.875f, z + 1);
            glVertex3i(x + 1, y + 1, z + 1);
            glVertex3i(x, y + 1, z + 1);
        }

        if(!neighbourOpaque[1])
        {
            glColor3f(0.5f * lightness, 0.4f * lightness, 0.25f * lightness);

            // Back
            glNormal3f(0, 0, -1);
            glVertex3f(x, y + 0.875f, z);
            glVertex3f(x + 1, y + 0.875f, z);
            glVertex3i(x + 1, y, z);

            glVertex3f(x, y + 0.875f, z);
            glVertex3i(x + 1, y, z);
            glVertex3i(x, y, z);

            glColor3f(color.red * lightness, color.green * lightness, color.blue * lightness);

            // Back
            //glNormal3f(0, 0, -1);
            glVertex3i(x, y + 1, z);
            glVertex3i(x + 1, y + 1, z);
            glVertex3f(x + 1, y + 0.875f, z);

            glVertex3i(x, y + 1, z);
            glVertex3f(x + 1, y + 0.875f, z);
            glVertex3f(x, y + 0.875f, z);
        }

        if(!neighbourOpaque[3])
        {
            glColor3f(0.5f * lightness, 0.4f * lightness, 0.25f * lightness);

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
            glColor3f(0.5f * lightness, 0.4f * lightness, 0.25f * lightness);

            // Right
            glNormal3f(1, 0, 0);
            glVertex3i(x + 1, y, z);
            glVertex3f(x + 1, y + 0.875f, z);
            glVertex3f(x + 1, y + 0.875f, z + 1);

            glVertex3i(x + 1, y, z);
            glVertex3f(x + 1, y + 0.875f, z + 1);
            glVertex3i(x + 1, y, z + 1);

            glColor3f(color.red * lightness, color.green * lightness, color.blue * lightness);

            // Right
            //glNormal3f(1, 0, 0);
            glVertex3f(x + 1, y + 0.875f, z);
            glVertex3i(x + 1, y + 1, z);
            glVertex3i(x + 1, y + 1, z + 1);

            glVertex3f(x + 1, y + 0.875f, z);
            glVertex3i(x + 1, y + 1, z + 1);
            glVertex3f(x + 1, y + 0.875f, z + 1);
        }

        if(!neighbourOpaque[5])
        {
            glColor3f(0.5f * lightness, 0.4f * lightness, 0.25f * lightness);

            // Left
            glNormal3f(-1, 0, 0);
            glVertex3i(x, y, z + 1);
            glVertex3f(x, y + 0.875f, z + 1);
            glVertex3f(x, y + 0.875f, z);

            glVertex3i(x, y, z + 1);
            glVertex3f(x, y + 0.875f, z);
            glVertex3i(x, y, z);

            glColor3f(color.red * lightness, color.green * lightness, color.blue * lightness);

            // Left
            //glNormal3f(-1, 0, 0);
            glVertex3f(x, y + 0.875f, z + 1);
            glVertex3i(x, y + 1, z + 1);
            glVertex3i(x, y + 1, z);

            glVertex3f(x, y + 0.875f, z + 1);
            glVertex3i(x, y + 1, z);
            glVertex3f(x, y + 0.875f, z);
        }


        /*if(!neighbourOpaque[0])
        {

        }

        if(!neighbourOpaque[1])
        {

        }

        if(!neighbourOpaque[4])
        {

        }

        if(!neighbourOpaque[5])
        {

        }*/
    }
}
