import java.util.Random;

/**
 * Created by Gazibalonchik on 11.01.2016.
 */
public class BlockWiring extends Block
{
    private boolean isSource;

    BlockWiring(Color color, boolean isSource, boolean isOpaque, boolean isVisible)
    {
        super(color, isOpaque, isVisible);
        this.isSource = isSource;
    }

    public static boolean signal(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        BlockWiring b = null;

        if(block instanceof BlockWiring)
            b = (BlockWiring)block;

        if(b != null)
            return world.getBlockMetadata(x, y, z) != 0 || b.isSource();

        return false;
    }

    @Override
    protected void onUpdate(World world, Chunk chunk, int x, int y, int z, int localX, int localY, int localZ, Random random)
    {
        if(world.getBlock(x, y - 1, z) instanceof BlockWiring)
        {
            signalUpdate(world, x, y - 1, z, random);
            //clearMeta(world, x, y - 1, z);
        }
    }

    @Override
    public void onBreak(World world, int x, int y, int z, Random random)
    {
        if(world.getBlock(x, y - 1, z) instanceof BlockWiring)
        {
            //signalUpdate(world, x, y - 1, z, random);
            clearMeta(world, x, y - 1, z);
        }
    }

    protected boolean signalUpdate(World world, int x, int y, int z, Random random)
    {
        Block block = world.getBlock(x, y, z);

        if(block instanceof BlockWiring && ((BlockWiring)block).isSource)
        {
            world.setBlockMetadata(x, y, z, 1);
            return true;
        }
        else
        {
            Block blocks[] = new Block[]{world.getBlock(x, y, z + 1), world.getBlock(x, y, z - 1), world.getBlock(x, y + 1, z), world.getBlock(x, y - 1, z), world.getBlock(x + 1, y, z), world.getBlock(x - 1, y, z)};
            int meta = world.getBlockMetadata(x, y, z);
            meta |= 2;
            world.setBlockMetadata(x, y, z, meta);

            if(blocks[0] instanceof BlockWiring && (world.getBlockMetadata(x, y, z + 1) & 2) == 0 && ((BlockWiring)blocks[0]).signalUpdate(world, x, y, z + 1, random))
            {
                world.setBlockMetadata(x, y, z, meta | 1);
                return true;
            }

            if(blocks[1] instanceof BlockWiring && (world.getBlockMetadata(x, y, z - 1) & 2) == 0 && ((BlockWiring)blocks[1]).signalUpdate(world, x, y, z - 1, random))
            {
                world.setBlockMetadata(x, y, z, meta | 1);
                return true;
            }

            if(blocks[4] instanceof BlockWiring && (world.getBlockMetadata(x + 1, y, z) & 2) == 0 && ((BlockWiring)blocks[4]).signalUpdate(world, x + 1, y, z, random))
            {
                world.setBlockMetadata(x, y, z, meta | 1);
                return true;
            }

            if(blocks[5] instanceof BlockWiring && (world.getBlockMetadata(x - 1, y, z) & 2) == 0 && ((BlockWiring)blocks[5]).signalUpdate(world, x - 1, y, z, random))
            {
                world.setBlockMetadata(x, y, z, meta | 1);
                return true;
            }
        }

        world.setBlockMetadata(x, y, z, 0);
        return false;
    }

    protected void clearMeta(World world, int x, int y, int z)
    {
        Block blocks[] = new Block[]{world.getBlock(x, y, z + 1), world.getBlock(x, y, z - 1), world.getBlock(x, y + 1, z), world.getBlock(x, y - 1, z), world.getBlock(x + 1, y, z), world.getBlock(x - 1, y, z)};
        world.setBlockMetadata(x, y, z, 0);

        if(blocks[0] instanceof BlockWiring && (world.getBlockMetadata(x, y, z + 1) & 2) > 0)
            ((BlockWiring)blocks[0]).clearMeta(world, x, y, z + 1);

        if(blocks[1] instanceof BlockWiring && (world.getBlockMetadata(x, y, z - 1) & 2) > 0)
            ((BlockWiring)blocks[1]).clearMeta(world, x, y, z - 1);

        if(blocks[4] instanceof BlockWiring && (world.getBlockMetadata(x + 1, y, z) & 2) > 0)
            ((BlockWiring)blocks[4]).clearMeta(world, x + 1, y, z);

        if(blocks[5] instanceof BlockWiring && (world.getBlockMetadata(x - 1, y, z) & 2) > 0)
            ((BlockWiring)blocks[5]).clearMeta(world, x - 1, y, z);
    }

    public boolean isSource()
    {
        return isSource;
    }
}
