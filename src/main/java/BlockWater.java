import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 24.01.2016.
 */
public class BlockWater extends Block
{
    public BlockWater()
    {
        super(new Color(0.05f, 0.35f, 0.4f, 0.35f), false);
    }

    @Override
    public void draw(Chunk chunk, Direction cameraDirection, boolean visible, int x, int y, int z, boolean[] neighbourOpaque, int light)
    {
        //Direction cameraDirection = camera.getDirection();
        float lightness = light / 15.0f;

        if(!neighbourOpaque[2])
        {
            glColor4f(color.red * lightness, color.green * lightness, color.blue * lightness, color.alpha);

            // Top
            glNormal3i(0, 1, 0);
            glVertex3f(x, y + 0.875f, z);
            glVertex3f(x, y + 0.875f, z + 1.0f);
            glVertex3f(x + 1.0f, y + 0.875f, z + 1.0f);

            glVertex3f(x, y + 0.875f, z);
            glVertex3f(x + 1.0f, y + 0.875f, z + 1.0f);
            glVertex3f(x + 1.0f, y + 0.875f, z);
        }
    }
}
