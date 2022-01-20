import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class Chunk
{
    //Структура данных - ovub bbbb bbbb bbbb, где
    //o - непрозрачность
    //v - видимость
    //b - индекс блока
    //u - требуется обновление
    //private short[] blockData = new short[4096];
    private long[] blockMetadata = new long[256];
    private long[] blockLightness = new long[256];
    private long[] blockIDs = new long[256];
    private long[] blockFlags = new long[256];
    public boolean isEmpty;
    private boolean needDrawListUpdate = true;
    public int listHandle;
    private boolean isFullyUpdated;
    private boolean isFullyNotTickable;

    public Chunk(World world, Random random, boolean fill)
    {
        //blockIDs[0][0][0] = 1;
        /*blockIDs[0][0][2] = 1;
        blockIDs[0][2][0] = 1;
        blockIDs[0][2][2] = 1;
        blockIDs[2][0][0] = 1;
        blockIDs[2][0][2] = 1;
        blockIDs[2][2][0] = 1;
        blockIDs[2][2][2] = 1;*/

        isEmpty = true;
        isFullyNotTickable = true;

        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                for(int k = 0; k < 16; k++)
                {
                    //neighbourChunkOpaque[i * 16 + j] = 0b00000000;
                    setBlockID(i, j, k, (short)0);
                }

        /*if(fill)
        {
            for(int i = 0; i < 16; i++)
                for(int j = 0; j < 16; j++)
                {
                    neighbourChunkOpaque[i * 16 + j] = 0b00000000;

                    for(int k = 0; k < 16; k++)
                    {
                        if(random.nextInt(2) == 0)
                            setBlockID(i, j, k, (short) 1);
                        else
                            setBlockID(i, j, k, (short) 0);
                    }
                }
        }
        else
            for(int i = 0; i < 16; i++)
                for(int j = 0; j < 16; j++)
                {
                    neighbourChunkOpaque[i * 16 + j] = 0b00000000;

                    for(int k = 0; k < 16; k++)
                    {
                        if(random.nextInt(256) == 0)
                            setBlockID(i, j, k, (short) 1);
                        else
                            setBlockID(i, j, k, (short) 0);
                    }
                }*/
    }

    public boolean isEmpty()
    {
        return isEmpty;
    }

    public void draw(World world, Camera camera, int x, int y, int z)
    {
        Direction cameraDirection = camera.getDirection();
        boolean[] neighbourOpaque = new boolean[6];
        int l;
        int m;
        int n;

        if(needDrawListUpdate)
        {
            listHandle = glGenLists(1);
            glNewList(listHandle, GL_COMPILE);

            glBegin(GL_TRIANGLES);

            for(int i = 0; i < 16; i++)
                for(int j = 0; j < 16; j++)
                    for(int k = 0; k < 16; k++)
                        if(getVisible(i, j, k))
                        {
                            l = x * 16 + i;
                            m = y * 16 + j;
                            n = z * 16 + k;

                            if(k == 15)
                                neighbourOpaque[0] = world.getOpaque(l, m, n + 1);
                            else
                                neighbourOpaque[0] = getOpaque(i, j, k + 1);

                            if(k == 0)
                                neighbourOpaque[1] = world.getOpaque(l, m, n - 1);
                            else
                                neighbourOpaque[1] = getOpaque(i, j, k - 1);

                            if(j == 15)
                                neighbourOpaque[2] = world.getOpaque(l, m + 1, n);
                            else
                                neighbourOpaque[2] = getOpaque(i, j + 1, k);

                            if(j == 0)
                                neighbourOpaque[3] = world.getOpaque(l, m - 1, n);
                            else
                                neighbourOpaque[3] = getOpaque(i, j - 1, k);

                            if(i == 15)
                                neighbourOpaque[4] = world.getOpaque(l + 1, m, n);
                            else
                                neighbourOpaque[4] = getOpaque(i + 1, j, k);

                            if(i == 0)
                                neighbourOpaque[5] = world.getOpaque(l - 1, m, n);
                            else
                                neighbourOpaque[5] = getOpaque(i - 1, j, k);

                            world.getBlockFromID(getBlockID(i, j, k)).draw(this, cameraDirection, true, l, m, n, neighbourOpaque, getBlockLightness(i, j, k));
                        }

            glEnd();

            glEndList();
            needDrawListUpdate = false;
        }

        glCallList(listHandle);
    }

    public void tick(World world, int x, int y, int z, Random random)
    {
        if(!isFullyNotTickable)
            for(int l = 0; l < 256; ++l)
            {
                int localX = random.nextInt(16);
                int localY = random.nextInt(16);
                int localZ = random.nextInt(16);

                Block block = world.getBlockFromID(this.getBlockID(localX, localY, localZ));

                if(block.isRandomTickable())
                    block.onRandomTick(world, this, x * 16 + localX, y * 16 + localY, z * 16 + localZ, localX, localY, localZ, random);
            }

        if(!isFullyUpdated)
        {
            int l;
            int m;
            int n;

            for(int i = 0; i < 16; i++)
                for(int j = 0; j < 16; j++)
                    for(int k = 0; k < 16; k++)
                    {

                        if(getUpdateRequired(i, j, k))
                        {
                            l = x * 16 + i;
                            m = y * 16 + j;
                            n = z * 16 + k;
                            Block block = world.getBlock(l, m, n);

                            setUpdateRequired(i, j, k, false);

                            block.update(world, this, l, m, n, i, j, k, random);
                            isFullyNotTickable &= !block.isRandomTickable();
                        }

                    }

            isFullyUpdated = true;
            needDrawListUpdate = true;
        }
    }

    public void setBlockID(int localX, int localY, int localZ, int blockID)
    {
        blockIDs[localX * 16 + localY] &= ~(0b1111L << (long)(localZ * 4));
        blockIDs[localX * 16 + localY] |= (long)blockID << (long)(localZ * 4);

        setBlockLightness(localX, localY, localZ, (byte)15);

        setUpdateRequired(localX, localY, localZ, true);
    }

    public void setBlockMetadata(int localX, int localY, int localZ, int metadata)
    {
        blockMetadata[localX * 16 + localY] &= ~(0b1111L << (long)(localZ * 4));
        blockMetadata[localX * 16 + localY] |= (long)metadata << (long)(localZ * 4);
    }

    public void setBlockLightness(int localX, int localY, int localZ, byte lightness)
    {
        blockLightness[localX * 16 + localY] &= ~(0b1111L << (long)(localZ * 4));
        blockLightness[localX * 16 + localY] |= (long)lightness << (long)(localZ * 4);
    }

    public void setOpaque(int localX, int localY, int localZ, boolean opaque)
    {
        blockFlags[localX * 16 + localY] &= ~(0b1000L << (long)(localZ * 4));

        if(opaque)
            blockFlags[localX * 16 + localY] |= 0b1000L << (long)(localZ * 4);
    }

    public void setVisible(int localX, int localY, int localZ, boolean visible)
    {
        blockFlags[localX * 16 + localY] &= ~(0b0100L << (long)(localZ * 4));

        if(visible)
            blockFlags[localX * 16 + localY] |= 0b0100L << (long)(localZ * 4);
    }

    public void setUpdateRequired(int localX, int localY, int localZ, boolean updateRequired)
    {
        blockFlags[localX * 16 + localY] &= ~(0b0010L << (long)(localZ * 4));

        if(updateRequired)
        {
            isFullyUpdated = false;
            blockFlags[localX * 16 + localY] |= 0b0010L << (long)(localZ * 4);
        }
    }

    public int getBlockID(int localX, int localY, int localZ)
    {
        try
        {
            return (int)((blockIDs[localX * 16 + localY] >>> (long)(localZ * 4)) & 0b1111L);
        }
        catch(ArrayIndexOutOfBoundsException exception)
        {
            return 0;
        }
    }

    public int getBlockMetadata(int localX, int localY, int localZ)
    {
        return (int)((blockMetadata[localX * 16 + localY] >>> (long)(localZ * 4)) & 0b1111L);
    }

    public byte getBlockLightness(int localX, int localY, int localZ)
    {
        return (byte)((blockLightness[localX * 16 + localY] >>> (long)(localZ * 4)) & 0b1111L);
    }

    public boolean getOpaque(int localX, int localY, int localZ)
    {
        return ((blockFlags[localX * 16 + localY] >>> (long)(localZ * 4)) & 0b1000L) > 0;
    }

    public boolean getVisible(int localX, int localY, int localZ)
    {
        return ((blockFlags[localX * 16 + localY] >>> (long)(localZ * 4)) & 0b0100L) > 0;
    }

    public boolean getUpdateRequired(int localX, int localY, int localZ)
    {
        return ((blockFlags[localX * 16 + localY] >>> (long)(localZ * 4)) & 0b0010L) > 0;
    }
}