package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Cartesian3D;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.minions.md5checksum;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.plugin.MetropolisPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a structure defined by a loaded file in a cuboid.
 *
 * @author Daddy Churchill, Thomas Richner
 */

/*
 * TODO
 * - remove all those public variables, use getter&setter
 * - remove unused variables
 *
 */


public abstract class Clipboard {
    protected SchematicConfig settings;
    protected GlobalSchematicConfig globalSettings;
    protected List<Cartesian3D> chests = new ArrayList<Cartesian3D>();
    protected List<Cartesian3D> spawners = new ArrayList<Cartesian3D>();

    protected String name;
    protected String hashstring;
    protected File cache;
    protected int groundLevelY = 1;
    protected List<ContextType> contextTypes;

    protected int sizeX;
    protected int sizeY;
    protected int sizeZ;
    protected int blockCount;

    protected int chunkSizeX;
    /**
     * Size in chunks
     */
    protected int chunkSizeZ;

    /**
     * Size in chunks
     */

    public Clipboard(MetropolisPlugin plugin, File file, File cacheFolder, GlobalSchematicConfig globalSettings, List<SchematicConfig> batchedConfigs) throws Exception {

        this.name = file.getName();
        this.globalSettings = globalSettings;
        this.hashstring = md5checksum.getMD5Checksum(file);
        this.cache = cacheFolder;

        plugin.getLogger().info(this.name + ": " + hashstring);

        // grab the data
        load(plugin, file, batchedConfigs);

        // finish figuring things out
        blockCount = sizeX * sizeY * sizeZ;

        chunkSizeX = (sizeX + Constants.CHUNK_SIZE - 1) / Constants.CHUNK_SIZE;
        chunkSizeZ = (sizeZ + Constants.CHUNK_SIZE - 1) / Constants.CHUNK_SIZE;

    }

    protected abstract void load(MetropolisPlugin plugin, File file, List<SchematicConfig> batchedConfigs) throws Exception;

    public abstract void paste(MetropolisGenerator generator, int blockX, int blockZ, int streetLevel, Direction direction);

    //public abstract void setTrickOrTreat(MetropolisGenerator generator);

    @Override
    public String toString() {
        return "Clipboard{" +
                "sizeX=" + sizeX +
                ", sizeZ=" + sizeZ +
                ", name='" + name + '\'' +
                ", chunkSizeX=" + chunkSizeX +
                ", chunkSizeZ=" + chunkSizeZ +
                '}';
    }

    public List<ContextType> getContextTypes() {
        return contextTypes;
    }

    /**
     * Returns the block height between 0 and the first block
     *
     * @param streetLevel the desired street level
     * @return height in blocks
     */
    public int getBottom(int streetLevel) {
        return streetLevel - groundLevelY;
    }

    public DecayOption getDecayOptions() {
        return settings.getDecayOption();
    }

    public int getSizeX(Direction direction) {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return sizeZ;
        } else {
            return sizeX;
        }
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ(Direction direction) {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return sizeX;
        } else {
            return sizeZ;
        }
    }

    public File getCache() {
        return cache;
    }

    public SchematicConfig getSettings() {
        return settings;
    }

    public List<Cartesian3D> getChests() {
        return chests;
    }

    public List<Cartesian3D> getSpawners() {
        return spawners;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hashstring;
    }

    public int getChunkSizeX() {
        return chunkSizeX;
    }

    public int getChunkSizeZ() {
        return chunkSizeZ;
    }

    public int getBlockSizeX() {
        return chunkSizeX << 4;
    }

    public int getBlockSizeZ() {
        return chunkSizeZ << 4;
    }
}
