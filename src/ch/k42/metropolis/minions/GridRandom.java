package ch.k42.metropolis.minions;

import ch.k42.metropolis.model.GridProvider;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
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
        chunkX /= GridProvider.GRID_SIZE;
        chunkX /= GridProvider.GRID_SIZE;
        return seed + (chunkX*7937+chunkZ)*7919;
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
