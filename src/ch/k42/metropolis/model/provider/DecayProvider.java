package ch.k42.metropolis.model.provider;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.DecayOption;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

/**
 * Provides decay to area of blocks.
 * Originally written by spaceribs for CityWorld.
 *
 * @author spaceribs, Thomas Richner
 */
public abstract class DecayProvider {

    protected MetropolisGenerator generator;
    protected Random random;

    public DecayProvider(MetropolisGenerator generator, Random random) {
        this.generator = generator;
        this.random = random;
    }

    /**
     * Destroys an area with default decay options
     * @param x1 start x coordinate
     * @param x2 end x coordinate
     * @param y1 start y coordinate
     * @param y2 end y coordinate
     * @param z1 start z coordinate
     * @param z2 end z coordinate
     */
    public void destroyWithin(int x1, int x2, int y1, int y2, int z1, int z2) {
        destroyWithin(x1,x2,y1,y2,z1,z2, DecayOption.getDefaultDecayOptions());
    }

    /**
     * Destroys some chunks with custom decay options
     * @param chunkX position X in chunks
     * @param chunkZ position Z in chunks
     * @param chunkSizeX size in direction X in chunks
     * @param chunkSizeZ size in direction Z in chunks
     * @param bottom bottom floor in blocks
     * @param height height in blocks
     * @param options decay options
     */
    public void destroyChunks(int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ, int bottom, int height, DecayOption options) {
        int x1 = chunkX << 4;
        int x2 = x1 + (chunkSizeX<<4);
        int z1 = chunkZ << 4;
        int z2 = z1 + (chunkSizeZ<<4);
        int y1 = bottom;
        int y2 = bottom+height;
        destroyWithin(x1,x2,y1,y2,z1,z2,options);
    }

    /**
     * Destroys an area with custom decay scale
     * @param x1 start x coordinate
     * @param x2 end x coordinate
     * @param y1 start y coordinate
     * @param y2 end y coordinate
     * @param z1 start z coordinate
     * @param z2 end z coordinate
     * @param options decay options
     */
    public void destroyWithin(int x1, int x2, int y1, int y2, int z1, int z2, DecayOption options) {}

    protected boolean isValid(Block block) {
        return (
                block.getType() != Material.GRASS
                        //&& block.getType() != Material.DIRT
                        && block.getType() != Material.LEAVES
                        && block.getType() != Material.LOG
        );
    }

    protected boolean isSupporting(Block block) {
        return (
                block.getType() != Material.LEAVES
                        && block.getType() != Material.VINE
                        && block.getType() != Material.LOG
                        && !block.isEmpty()
        );
    }
}
