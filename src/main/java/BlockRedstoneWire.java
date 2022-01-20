import static org.lwjgl.opengl.GL11.glNormal3i;
import static org.lwjgl.opengl.GL11.glVertex3f;

/**
 * Created by Gazibalonchik on 26.04.2016.
 */
public class BlockRedstoneWire extends BlockWiring
{
    private Color signalColor;

    BlockRedstoneWire()
    {
        super(new Color(0.3f, 0.033f, 0.033f), false, false, true);
        signalColor = new Color(0.9f, 0.1f, 0.1f);
        boundingBox = new double[]{0.0, 0.0, 0.0, 1.0, 0.0625, 1.0};
    }

    /*@Override
    protected void onUpdate(World world, Chunk chunk, int x, int y, int z, int localX, int localY, int localZ, Random random)
    {
        if( BlockWiring.signal(world, x, y, z + 1) ||
            BlockWiring.signal(world, x, y, z - 1) ||
            BlockWiring.signal(world, x, y - 1, z) ||
            BlockWiring.signal(world, x + 1, y, z) ||
            BlockWiring.signal(world, x - 1, y, z) )
            chunk.setBlockMetadata(localX, localY, localZ, 1);

        world.setUpdateRequired(x, y, z + 1, true);
        world.setUpdateRequired(x, y, z - 1, true);
        world.setUpdateRequired(x, y + 1, z, true);
        world.setUpdateRequired(x, y - 1, z, true);
        world.setUpdateRequired(x + 1, y, z, true);
        world.setUpdateRequired(x - 1, y, z, true);
    }*/

    @Override
    public void draw(Chunk chunk, Direction cameraDirection, boolean visible, int x, int y, int z, boolean[] neighbourOpaque, int light)
    {
        //Direction cameraDirection = camera.getDirection();
        float lightness = light / 15.0f;

        if((chunk.getBlockMetadata(x % 16, y % 16, z % 16) & 1) == 0)
            color.apply();
        else
            signalColor.apply();

        // Top
        glNormal3i(0, 1, 0);
        glVertex3f(x, y + 0.0625f, z);
        glVertex3f(x, y + 0.0625f, z + 1.0f);
        glVertex3f(x + 1.0f, y + 0.0625f, z + 1.0f);

        glVertex3f(x, y + 0.0625f, z);
        glVertex3f(x + 1.0f, y + 0.0625f, z + 1.0f);
        glVertex3f(x + 1.0f, y + 0.0625f, z);
    }
}
