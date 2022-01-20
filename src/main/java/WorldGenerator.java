import java.util.Random;

/**
 * Created by Gazibalonchik on 24.01.2016.
 */
public class WorldGenerator
{
    private long seed;
    private int seaLevel;
    private float[][] superBigNoise = new float[8][8];
    private float[][] veryBigNoise = new float[32][32];
    private float[][] bigNoise = new float[128][128];
    private float[][] mediumNoise = new float[512][512];
    private float[][] smallNoise = new float[2048][2048];

    public long getSeed()
    {
        return seed;
    }

    WorldGenerator(long seed, Random random, int seaLevel)
    {
        this.seed = seed;
        this.seaLevel = seaLevel;
        random.setSeed(seed);

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                superBigNoise[i][j] = random.nextFloat() - 0.5f;

        for(int i = 0; i < 32; i++)
            for(int j = 0; j < 32; j++)
                veryBigNoise[i][j] = random.nextFloat() - 0.5f;

        for(int i = 0; i < 128; i++)
            for(int j = 0; j < 128; j++)
                bigNoise[i][j] = random.nextFloat() - 0.5f;

        for(int i = 0; i < 512; i++)
            for(int j = 0; j < 512; j++)
                mediumNoise[i][j] = random.nextFloat() - 0.5f;

        for(int i = 0; i < 2048; i++)
            for(int j = 0; j < 2048; j++)
                smallNoise[i][j] = random.nextFloat() - 0.5f;
    }



    private float[][] interpolateNoise(float[][] noise)
    {
        int noiseSize = (int) (Math.sqrt(noise.length - 1)) * 2;
        float[][] resultNoise = new float[noiseSize][noiseSize];

        for(int i = 1; i < noiseSize; i += 2)
            for(int j = 0; j < noiseSize; j += 2)
            {
                resultNoise[i][j] = noise[i / 2][j / 2] + noise[i / 2 + 1][j / 2] / 2;
            }

        for(int i = 0; i < noiseSize; i++)
            for(int j = 1; j < noiseSize; j += 2)
            {
                resultNoise[i][j] = resultNoise[i][j + 1] + noise[i][j - 1] / 2;
            }

        return resultNoise;
    }

    private float interpolateLinear(float a, float b, float x)
    {
        return a * (1.0f - x) + b * x;
    }

    private float interpolateCosine(float a, float b, float x)
    {
        float f = (float)(1.0f - Math.cos(x * Math.PI)) * 0.5f;
        return a * (1.0f - f) + b * f;
    }

    private long pow(int a, int b)
    {
        int result = 1;

        for(int i = b; i > 0; i--)
        {
            result *= a;
        }

        return result;
    }

    private long combinate(int k, int l)
    {
        long result = 0;

        for(int i = 0; i < 32; i++)
        {
            for(int j = 0; j < 2; j++)
            {
                if(j == 0)
                {
                    if((k & pow(2, i)) > 0)
                        result |= pow(2, (i * 2 + j));
                }
                else
                {
                    if((l & pow(2, i)) > 0)
                        result |= pow(2, (i * 2 + j));
                }
            }
        }

        return result;
    }

    public void generateTerrain(World world, Chunk chunk, int chunkX, int chunkY, int chunkZ, Random random)
    {
        int[][] resultNoise = new int[16][16];
        float[][] bigNoiseSegment = new float[2][2];
        float[][] mediumNoiseSegment = new float[5][5];
        float[][] superBigNoiseInterpolation = new float[17][17];
        float[][] veryBigNoiseInterpolation = new float[17][17];
        float[][] bigNoiseInterpolation = new float[16][16];
        float[][] mediumNoiseInterpolation = new float[16][16];

        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 2; j++)
                bigNoiseSegment[i][j] = bigNoise[i + chunkX][j + chunkZ];

        for(int i = 0; i <= 4; i++)
            for(int j = 0; j <= 4; j++)
                mediumNoiseSegment[i][j] = mediumNoise[i + chunkX * 4][j + chunkZ * 4];

        //for(int i = 0; i < 2; i++)
        //    for(int j = 0; j < 16; j++)
        //        bigNoiseInterpolation[i * 16][j] = interpolateLinear(bigNoiseSegment[i][0], bigNoiseSegment[i][1], j / 16.0f);

        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                superBigNoiseInterpolation[i][j] = interpolateLinear(interpolateLinear(superBigNoise[chunkX / 16][chunkZ / 16],      superBigNoise[chunkX / 16][chunkZ / 16 + 1], j / 256.0f + (chunkZ % 16.0f) / 16.0f),
                        interpolateLinear(superBigNoise[chunkX / 16 + 1][chunkZ / 16],                    superBigNoise[chunkX / 16 + 1][chunkZ / 16 + 1], j / 256.0f + (chunkZ % 16.0f) / 16.0f), i / 256.0f + (chunkX % 16.0f) / 16.0f);

        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                veryBigNoiseInterpolation[i][j] = interpolateLinear(interpolateLinear(veryBigNoise[chunkX / 4][chunkZ / 4],     veryBigNoise[chunkX / 4][chunkZ / 4 + 1], j / 64.0f + (chunkZ % 4.0f) / 4.0f),
                        interpolateLinear(veryBigNoise[chunkX / 4 + 1][chunkZ / 4], veryBigNoise[chunkX / 4 + 1][chunkZ / 4 + 1], j / 64.0f + (chunkZ % 4.0f) / 4.0f), i / 64.0f + (chunkX % 4.0f) / 4.0f);

        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                bigNoiseInterpolation[i][j] = interpolateLinear(interpolateLinear(bigNoiseSegment[0][0], bigNoiseSegment[0][1], j / 16.0f), interpolateLinear(bigNoiseSegment[1][0], bigNoiseSegment[1][1], j / 16.0f), i / 16.0f);

        for(int i = 0; i < 4; i++)
            for(int j = 0; j < 4; j++)
                for(int k = 0; k < 4; k++)
                    for(int l = 0; l < 4; l++)
                    {
                        mediumNoiseInterpolation[i * 4 + k][j * 4 + l] = interpolateLinear(interpolateLinear(mediumNoiseSegment[i][j],       mediumNoiseSegment[i][j + 1],       l / 4.0f),
                                interpolateLinear(mediumNoiseSegment[i + 1][j],   mediumNoiseSegment[i + 1][j + 1],   l / 4.0f), k / 4.0f);
                    }

        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                resultNoise[i][j] = Math.round(superBigNoiseInterpolation[i][j] * 256.0f + veryBigNoiseInterpolation[i][j] * 64.0f + bigNoiseInterpolation[i][j] * 16.0f + mediumNoiseInterpolation[i][j] * 4.0f + smallNoise[i + 16 * chunkX][j + 16 * chunkZ] / 4.0f) + 64;

        /*for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                resultNoise[i][j] = 64;*/

        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                for(int k = 0; k < 16; k++)
                {
                    if(chunkY * 16 + j < resultNoise[i][k])
                        chunk.setBlockID(i, j, k, (short)3);
                    else if(chunkY * 16 + j == resultNoise[i][k])
                    {
                        if(chunkY * 16 + j < seaLevel + 3)
                        {
                            chunk.setBlockID(i, j, k, world.getBlockIDFromBlock(Block.sand));

                            if(random.nextInt(64) == 0)
                                world.setBlock((chunkX * 16) + i, (chunkY * 16) + j + 1, (chunkZ * 16) + k, Block.cactus);
                        }
                        else
                            chunk.setBlockID(i, j, k, (short)4);
                    }
                    else if(chunkY * 16 + j <= seaLevel)
                        chunk.setBlockID(i, j, k, (short)2);
                }
    }
}
