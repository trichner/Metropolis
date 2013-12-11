package ch.k42.metropolis.WorldEdit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Cartesian;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.minions.md5checksum;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.ContextType;


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


    protected final static int nullspots_constant = 1; //HARDCODED

    protected SchematicConfig settings;
    protected GlobalSchematicConfig globalSettings;
    protected List<Cartesian> chests = new ArrayList<Cartesian>();
    protected List<Cartesian> spawners = new ArrayList<Cartesian>();

    protected String name;
    protected boolean westEast;
    protected String hashstring;
    protected File cache;
    protected int groundLevelY = 1;
    protected List<ContextType> contextTypes;

    protected int sizeX;
    protected int sizeY;
    protected int sizeZ;
    protected int blockCount;

    protected int chunkSizeX; /** Size in chunks */
    protected int chunkSizeZ; /** Size in chunks */

	public Clipboard(MetropolisGenerator generator, File file, File cacheFolder, GlobalSchematicConfig globalSettings) throws Exception {

        this.name = file.getName();
        this.globalSettings = globalSettings;
        this.hashstring = md5checksum.getMD5Checksum(file);
        this.cache = cacheFolder;

        generator.reportDebug(this.name+": "+hashstring);

        // grab the data
		load(generator, file);
		
		// finish figuring things out
		blockCount = sizeX * sizeY * sizeZ;
		
		chunkSizeX = (sizeX + Constants.CHUNK_SIZE - 1) / Constants.CHUNK_SIZE;
		chunkSizeZ = (sizeZ + Constants.CHUNK_SIZE - 1) / Constants.CHUNK_SIZE;

	}
	
	protected abstract void load(MetropolisGenerator generator, File file) throws Exception;

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
     * @param streetLevel the desired street level
     * @return height in blocks
     */
    public int getBottom(int streetLevel){
        return streetLevel-groundLevelY;
    }

    public DecayOption getDecayOptions() {
        return settings.getDecayOption();
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public File getCache() {
        return cache;
    }

    public SchematicConfig getSettings() {
        return settings;
    }

    public List<Cartesian> getChests() {
        return chests;
    }

    public List<Cartesian> getSpawners() {
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
