package ch.k42.metropolis.minions;

import java.util.Random;

/**
 * Provides a Pseudo Random generator with
 * a deterministic seed. Depending on location and worldseed.
 *
 * Basically a wrapper around java.util.Random.
 *
 * @author Thomas Richner
 *
 */
public class GridRandom {
    private Random random;

    public GridRandom(long seed) {
        this.random = new Random(seed);
    }

    public GridRandom(long worldseed,int chunkX,int chunkZ) {
        this.random = new Random(generateLocalSeed(worldseed,chunkX,chunkZ));
    }

    public static long generateLocalSeed(long seed,int chunkX,int chunkZ){
        return seed + (chunkX*31+chunkZ)*17;
    }

    /**
     *
     * @param range
     * @return [0,range)
     */
    public int getRandomInt(int range){
        return random.nextInt(range);
    }

    /**
     *
     * @param min
     * @param max
     * @return [min,max)
     */
    public int getRandomInt(int min,int max){
        return random.nextInt(max-min)+min;
    }

    /**
     *
     * @param percent [0,100]
     * @return true in 'percent' tries
     */
    public boolean getChance(int percent){
        return random.nextInt(100)<percent;
    }
}
