package ch.k42.metropolis.grid.urbanGrid.provider;

import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Cartesian3D;
import ch.k42.metropolis.minions.DecayOption;
import com.google.common.collect.ImmutableSet;

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
     *
     * @param x1 start x coordinate
     * @param x2 end x coordinate
     * @param y1 start y coordinate
     * @param y2 end y coordinate
     * @param z1 start z coordinate
     * @param z2 end z coordinate
     */
    public void destroyWithin(int x1, int x2, int y1, int y2, int z1, int z2) {
        destroyWithin(x1, x2, y1, y2, z1, z2, DecayOption.getDefaultDecayOptions());
    }

    /**
     * Destroys some chunks with custom decay options
     *
     * @param chunkX     position X in chunks
     * @param chunkZ     position Z in chunks
     * @param chunkSizeX size in direction X in chunks
     * @param chunkSizeZ size in direction Z in chunks
     * @param bottom     bottom floor in blocks
     * @param height     height in blocks
     * @param options    decay options
     */
    public void destroyChunks(int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ, int bottom, int height, DecayOption options) {
        int x1 = chunkX << 4;
        int x2 = x1 + (chunkSizeX << 4);
        int z1 = chunkZ << 4;
        int z2 = z1 + (chunkSizeZ << 4);
        int y1 = bottom;
        int y2 = bottom + height;
        destroyWithin(x1, x2, y1, y2, z1, z2, options);
    }

    public void destroyWithin(Cartesian3D start,Cartesian3D end, DecayOption options) {
        destroyWithin(start.X,end.X,start.Y,end.Y,start.Z,end.Z,options);
    }

    /**
     * Destroys an area with custom decay scale
     *
     * @param x1      start x coordinate
     * @param x2      end x coordinate
     * @param y1      start y coordinate
     * @param y2      end y coordinate
     * @param z1      start z coordinate
     * @param z2      end z coordinate
     * @param options decay options
     */
    @Deprecated
    public void destroyWithin(int x1, int x2, int y1, int y2, int z1, int z2, DecayOption options) {
    }

    private static Set<Material> invalidBlocks = ImmutableSet.of(Material.GRASS,Material.LEAVES,Material.LOG);
    private static Set<Material> unsupportingBlocks = ImmutableSet.of(Material.VINE,Material.LEAVES,Material.LOG);

    protected boolean isValid(Block block) {
        return !invalidBlocks.contains(block.getType());
    }

    protected boolean isSupporting(Block block) {
        return !unsupportingBlocks.contains(block.getType()) && !block.isEmpty();
    }
}
