import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 11.01.2016.
 */
public class Block
{
    Color color;
    private boolean isOpaque;
    private boolean isVisible;
    private boolean isRandomTickable;
    protected double[] boundingBox = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, 1.0};
    public static final Block air = new Block(new Color(0.0f, 0.0f, 0.0f), false, false);
    public static final Block stone = new Block(new Color(0.5f, 0.5f, 0.5f), true);
    public static final Block water = new BlockWater();
    public static final Block dirt = new Block(new Color(0.5f, 0.4f, 0.25f), true);
    public static final Block grass = new BlockWithDifferentColors(dirt.color, dirt.color, new Color(0.5f, 0.8f, 0.3f), dirt.color, dirt.color, dirt.color, true);
    public static final Block sand = new Block(new Color(1.0f, 1.0f, 0.75f), true);
    public static final Block rail = new BlockRail(new Color(0.3f, 0.1f, 0.1f), new Color(0.5f, 0.5f, 0.5f));
    public static final Block brick = new BlockBrick();
    public static final Block cactus = new BlockCactus();
    public static final Block redstoneWire = new BlockRedstoneWire();
    public static final Block redstone = new BlockWiring(new Color(0.9f, 0.1f, 0.1f), true, true, true);

    public Block(Color color, boolean isOpaque, boolean isVisible, boolean isRandomTickable)
    {
        setColor(color);
        this.isOpaque = isOpaque;
        this.isVisible = isVisible;
        this.isRandomTickable = isRandomTickable;
    }

    public Block(Color color, boolean isOpaque, boolean isVisible)
    {
        this(color, isOpaque, isVisible, false);
    }

    public Block(Color color, boolean isOpaque)
    {
        this(color, isOpaque, true);
    }

    public boolean inBoundingBox(double localX, double localY, double localZ, int metadata)
    {
        return localX > boundingBox[0] && localX < boundingBox[3] && localY > boundingBox[1] && localY < boundingBox[4] && localZ > boundingBox[2] && localZ < boundingBox[5] && isVisible();
    }

    public Direction getSideOnWhichPoint(double localX, double localY, double localZ, int metadata) throws Exception
    {
        double SPRBX = (localX - ((boundingBox[0] + boundingBox[3]) / 2)) / (boundingBox[3] - boundingBox[0]);
        double SPRBY = (localY - ((boundingBox[1] + boundingBox[4]) / 2)) / (boundingBox[4] - boundingBox[1]);
        double SPRBZ = (localZ - ((boundingBox[2] + boundingBox[5]) / 2)) / (boundingBox[5] - boundingBox[2]);

        if(SPRBZ > SPRBX && SPRBZ > -SPRBX && SPRBZ > SPRBY && SPRBZ > -SPRBY)
            return Direction.FRONT;
        else if(SPRBZ < SPRBX && SPRBZ < -SPRBX && SPRBZ < SPRBY && SPRBZ < -SPRBY)
            return Direction.BACK;
        else if(SPRBZ < SPRBY && -SPRBZ < SPRBY && SPRBX < SPRBY && -SPRBX < SPRBY)
            return Direction.TOP;
        else if(SPRBZ > SPRBY && -SPRBZ > SPRBY && SPRBX > SPRBY && -SPRBX > SPRBY)
            return Direction.BOTTOM;
        else if(SPRBX > -SPRBZ && SPRBX > SPRBZ && SPRBX > -SPRBY && SPRBX > SPRBY)
            return Direction.RIGHT;
        else if(SPRBX < -SPRBZ && SPRBX < SPRBZ && SPRBX < -SPRBY && SPRBX < SPRBY)
            return Direction.LEFT;

        throw new Exception("Invalid side");
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public boolean isOpaque()
    {
        return isOpaque;
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public boolean isRandomTickable()
    {
        return isRandomTickable;
    }

    /**
     * @param visible Без этого работает медленнее. Причины неизвестны
     */
    public void draw(Chunk chunk, Direction cameraDirection, boolean visible, int x, int y, int z, boolean[] neighbourOpaque, int light)
    {
        float lightness = light / 15.0f;
        //Direction cameraDirection = camera.getDirection();

        if(!neighbourOpaque[0])
        {
            glColor4f(color.red * lightness, color.green * lightness, color.blue * lightness, color.alpha);
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
            glColor4f(color.red * lightness, color.green * lightness, color.blue * lightness, color.alpha);
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
            glColor4f(color.red * lightness, color.green * lightness, color.blue * lightness, color.alpha);
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
            glColor4f(color.red * lightness, color.green * lightness, color.blue * lightness, color.alpha);
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
            glColor4f(color.red * lightness, color.green * lightness, color.blue * lightness, color.alpha);
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
            glColor4f(color.red * lightness, color.green * lightness, color.blue * lightness, color.alpha);
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

    public void update(World world, Chunk chunk, int x, int y, int z, int localX, int localY, int localZ, Random random)
    {
        onUpdate(world, chunk, x, y, z, localX, localY, localZ, random);

        Block newBlock = world.getBlockFromID(chunk.getBlockID(localX, localY, localZ));
        boolean visible = newBlock.isVisible();// && !(world.getBlock(x, y, z + 1).isOpaque() && world.getBlock(x, y, z - 1).isOpaque() && world.getBlock(x, y + 1, z).isOpaque() && world.getBlock(x, y + 1, z).isOpaque() && world.getBlock(x - 1, y, z).isOpaque() && world.getBlock(x + 1, y, z).isOpaque());
        chunk.setOpaque(localX, localY, localZ, newBlock.isOpaque());
        chunk.setVisible(localX, localY, localZ, visible);
        chunk.isEmpty &= !visible;

        /*if(localZ == 15)
            chunk.setNeighbourFrontChunkOpaque  (localX, localY, world.getBlock(x, y, z + 1).isOpaque());
        if(localZ == 0)
            chunk.setNeighbourBackChunkOpaque   (localX, localY, world.getBlock(x, y, z - 1).isOpaque());

        if(localY == 15)
            chunk.setNeighbourTopChunkOpaque    (localX, localZ, world.getBlock(x, y + 1, z).isOpaque());
        if(localY == 0)
            chunk.setNeighbourBottomChunkOpaque (localX, localZ, world.getBlock(x, y - 1, z).isOpaque());

        if(localX == 15)
            chunk.setNeighbourRightChunkOpaque  (localY, localZ, world.getBlock(x + 1, y, z).isOpaque());
        if(localX == 0)
            chunk.setNeighbourLeftChunkOpaque   (localY, localZ, world.getBlock(x - 1, y, z).isOpaque());*/
    }

    protected void onUpdate(World world, Chunk chunk, int x, int y, int z, int localX, int localY, int localZ, Random random)
    {

    }

    public void onBreak(World world, int x, int y, int z, Random random)
    {

    }

    public void onRandomTick(World world, Chunk chunk, int x, int y, int z, int localX, int localY, int localZ, Random random)
    {

    }

    public double[] getBoundingBox()
    {
        return boundingBox;
    }
}
