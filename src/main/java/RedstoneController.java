/**
 * Created by Gazibalonchik on 29.04.2016.
 */
public class RedstoneController
{
    private World world;

    RedstoneController(World world)
    {
        this.world = world;
    }

    public void setSignal(int x, int y, int z, boolean signal)
    {
        world.setBlockMetadata(x, y, z, signal ? 1 : 0);
    }
}
