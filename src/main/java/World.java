import java.util.ArrayList;
import java.util.Random;

public class World
{
    public int time;
    private final int width = 12;
    private final int height = 8;
    private final int depth = 12;
    private int blocksCount;
    private Block blocks[] = new Block[256];
    private Chunk chunks[] = new Chunk[width * height * depth];
    private Cell cells[] = new Cell[65536];
    private ArrayList<Entity> entities = new ArrayList<>();
    private Random random = new Random();
    private WorldGenerator worldGenerator = new WorldGenerator(65536, random, 68);
    public RedstoneController controller;

    public World()
    {
        addBlock(Block.air);
        addBlock(Block.stone);
        addBlock(Block.water);
        addBlock(Block.dirt);
        addBlock(Block.grass);
        addBlock(Block.sand);
        addBlock(Block.rail);
        addBlock(Block.brick);
        addBlock(Block.cactus);
        addBlock(Block.redstoneWire);
        addBlock(Block.redstone);

        controller = new RedstoneController(this);

        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++)
                for(int k = 0; k < depth; k++)
                {
                    chunks[(i * width * height) + (j * depth) + k] = new Chunk(this, random, i % 2 == 0 && j % 2 == 0 && k % 2 == 0);

                    /*setBlockID(i * 16, j * 16, k * 16, (byte) 1);
                    setBlockID(i * 16, j * 16, k * 16 + 8, (byte) 1);
                    setBlockID(i * 16, j * 16 + 8, k * 16, (byte) 1);
                    setBlockID(i * 16, j * 16 + 8, k * 16 + 8, (byte) 1);
                    setBlockID(i * 16 + 8, j * 16, k * 16, (byte) 1);
                    setBlockID(i * 16 + 8, j * 16, k * 16 + 8, (byte) 1);
                    setBlockID(i * 16 + 8, j * 16 + 8, k * 16, (byte) 1);
                    setBlockID(i * 16 + 8, j * 16 + 8, k * 16 + 8, (byte) 1);*/
                }

        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++)
                for(int k = 0; k < depth; k++)
                    worldGenerator.generateTerrain(this, chunks[(i * width * height) + (j * depth) + k], i, j, k, random);
    }

    public void addEntity(Entity entity)
    {
        entities.add(entity);
    }

    public int getBlockID(int x, int y, int z)
    {
        try
        {
            return chunks[(x / 16) * width * height + (y / 16) * depth + (z / 16)].getBlockID(x % 16, y % 16, z % 16);
        }
        catch(Exception exception)
        {
            return 0;
        }
    }

    public void setBlockID(int x, int y, int z, int blockID)
    {
        chunks[(x / 16) * width * height + (y / 16) * depth + (z / 16)].setBlockID(x % 16, y % 16, z % 16, blockID);
        //chunks[(x / 16) * width * height + (y / 16) * depth + (z / 16)].setBlockMetadata(x % 16, y % 16, z % 16, 0);
        setUpdateRequired(x, y, z + 1, true);
        setUpdateRequired(x, y, z - 1, true);
        setUpdateRequired(x, y + 1, z, true);
        setUpdateRequired(x, y - 1, z, true);
        setUpdateRequired(x + 1, y, z, true);
        setUpdateRequired(x - 1, y, z, true);
    }

    public void breakBlock(int x, int y, int z)
    {
        Block block = getBlock(x, y, z);
        setBlock(x, y, z, Block.air);
        block.onBreak(this, x, y, z, random);
    }

    public boolean inBoundingBox(double x, double y, double z)
    {
        return getBlock((int)x, (int)y, (int)z).inBoundingBox(x - (int)x, y - (int)y, z - (int)z, getBlockMetadata((int)x, (int)y, (int)z));
    }

    public Direction getSideOnWhichPoint(double x, double y, double z) throws Exception
    {
        return getBlock((int)x, (int)y, (int)z).getSideOnWhichPoint(x - (int)x, y - (int)y, z - (int)z, getBlockMetadata((int)x, (int)y, (int)z));
    }

    public int getBlockMetadata(int x, int y, int z)
    {
        return chunks[(x / 16) * width * height + (y / 16) * depth + (z / 16)].getBlockMetadata(x % 16, y % 16, z % 16);
    }

    public void setBlockMetadata(int x, int y, int z, int metadata)
    {
        chunks[(x / 16) * width * height + (y / 16) * depth + (z / 16)].setBlockMetadata(x % 16, y % 16, z % 16, metadata);
    }

    public void setUpdateRequired(int x, int y, int z, boolean updateRequired)
    {
        try
        {
            chunks[(x / 16) * width * height + (y / 16) * depth + (z / 16)].setUpdateRequired(x % 16, y % 16, z % 16, updateRequired);
        }
        catch(Exception exception)
        {
            System.out.println(exception.toString());
        }
    }

    public void setBlock(int x, int y, int z, Block block)
    {
        setBlockID(x, y, z, getBlockIDFromBlock(block));
    }

    public boolean getOpaque(int x, int y, int z)
    {
        try
        {
            return chunks[(x / 16) * width * height + (y / 16) * depth + (z / 16)].getOpaque(x % 16, y % 16, z % 16);
        }
        catch(ArrayIndexOutOfBoundsException exception)
        {
            return true;
        }
        catch(NullPointerException exception)
        {
            return true;
        }
    }

    public Block getBlock(int x, int y, int z)
    {
        try
        {
            return blocks[getBlockID(x, y, z)];
        }
        catch(NullPointerException exception)
        {
            return blocks[0];
        }
    }

    public short getBlockIDFromBlock(Block block)
    {
        for(short i = 0; i < 128; i ++)
            if(blocks[i] == block)
                return i;

        return 0;
    }

    public Block getBlockFromID(int blockID)
    {
        return blocks[blockID];
    }

    private void addBlock(Block block)
    {
        blocks[blocksCount] = block;
        blocksCount++;
    }

    public int getBlocksCount()
    {
        return blocksCount;
    }

    public void tick(double deltaTime)
    {
        for(Entity entity: entities)
            entity.tick(this, random, deltaTime);

        for(int i = 0; i < width; ++i)
            for(int j = 0; j < height; ++j)
                for(int k = 0; k < depth; ++k)
                    chunks[(i * width * height) + (j * depth) + k].tick(this, i, j, k, random);
    }

    public void draw(Camera camera)
    {
        int index;

        for(Entity entity: entities)
            entity.draw();

        /*double SPRBX;
        double SPRBY;
        double SPRBZ;

        Matrix rotateMatrix = new Matrix(new double[][]{{Math.cos(camera.getYaw()), 0.0, Math.sin(camera.getYaw())}, {0.0, 1.0, 0.0}, {-Math.sin(camera.getYaw()), 0.0, Math.cos(camera.getYaw())}});
        Matrix coordinatesMatrix = new Matrix(new double[][]{{camera.getPositionX() - (0 * 16 + 8)}, {camera.getPositionY() - (0 * 16 + 8)}, {camera.getPositionZ() - (0 * 16 + 8)}});
        Matrix resultMatrix = rotateMatrix.times(coordinatesMatrix);
*/

        try
        {
            for(int i = 0; i < width; ++i)
                for(int j = 0; j < height; ++j)
                    for(int k = 0; k < depth; ++k)
                    {
                        //SPRBX = (camera.getPositionX() - (i * 16 + 8) / 1);//Math.sin(Math.toRadians(camera.getFov())));
                        //SPRBY = (camera.getPositionY() - (j * 16 + 8) / 1); //Math.sin(Math.toRadians(camera.getFov())));
                        //SPRBZ = (camera.getPositionZ() - (k * 16 + 8) / 1); //Math.sin(Math.toRadians(camera.getFov())));

                        //SPRBX = resultMatrix.get(0, 0) + (i * 16 + 8);
                        //SPRBY = resultMatrix.get(1, 0) + (j * 16 + 8);
                        //SPRBZ = resultMatrix.get(2, 0) + (k * 16 + 8);

                        index = (i * width * height) + (j * depth) + k;
                        if(!chunks[index].isEmpty() /*&& SPRBZ > SPRBX && SPRBZ > -SPRBX && SPRBZ > SPRBY && SPRBZ > -SPRBY*/)
                            chunks[index].draw(this, camera, i, j, k);
                    }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
