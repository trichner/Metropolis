package ch.k42.metropolis.WorldEdit;

import java.io.File;
import java.util.List;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.DecayOption;
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

	public String name;
	public String chestName = "Chest";
	public double chestOdds = 0.25D;
	public String spawnerType = "ZOMBIE";
	public double spawnerOdds = 0.50D;
	public int groundLevelY = 1;
	public boolean broadcastLocation = false;
    protected List<ContextType> contextTypes;
    protected Direction direction;



    public DecayOption decayOptions = DecayOption.getDefaultDecayOptions();

	public int sizeX;
	public int sizeY;
	public int sizeZ;
	public int blockCount;
	
	public int chunkX; /** Size in chunks */
	public int chunkZ; /** Size in chunks */
	
	public int insetNorth;
	public int insetSouth;
	public int insetWest;
	public int insetEast;

	public Clipboard(MetropolisGenerator generator, File file) throws Exception {
		super();
		this.name = file.getName();
		
		// grab the data
		load(generator, file);
		
		// finish figuring things out
		blockCount = sizeX * sizeY * sizeZ;
		
		chunkX = (sizeX + Constants.CHUNK_SIZE - 1) / Constants.CHUNK_SIZE;
		chunkZ = (sizeZ + Constants.CHUNK_SIZE - 1) / Constants.CHUNK_SIZE;
		
		int leftoverX = chunkX * Constants.CHUNK_SIZE - sizeX;
		int leftoverZ = chunkZ * Constants.CHUNK_SIZE - sizeZ;
		
		insetWest = leftoverX / 2;
		insetEast = leftoverX - insetWest;
		insetNorth = leftoverZ / 2;
		insetSouth = leftoverZ - insetNorth;
	}
	
	protected abstract void load(MetropolisGenerator generator, File file) throws Exception;

	public abstract void paste(MetropolisGenerator generator, int blockX,int blockZ, int streetLevel);

    @Override
    public String toString() {
        return "Clipboard{" +
                "sizeX=" + sizeX +
                ", sizeZ=" + sizeZ +
                ", name='" + name + '\'' +
                ", chunkX=" + chunkX +
                ", chunkZ=" + chunkZ +
                '}';
    }

    public List<ContextType> getContextTypes() {
        return contextTypes;
    }

    public Direction getDirection() {
        return direction;
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
        return decayOptions;
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
}
