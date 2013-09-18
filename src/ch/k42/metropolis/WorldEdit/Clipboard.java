package ch.k42.metropolis.WorldEdit;

import java.io.File;
import java.util.List;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.minions.Direction;
import ch.k42.metropolis.model.ContextType;
import ch.k42.metropolis.model.GridProvider;
import org.bukkit.Material;


public abstract class Clipboard {


    protected final static int nullspots_constant = 1; //HARDCODED

	public String name;
	public String chestName = "Chest";
	public double chestOdds = 0.25D;
	public String spawnerType = "ZOMBIE";
	public double spawnerOdds = 0.50D;
	//public double oddsOfAppearance = DataContext.oddsSomewhatUnlikely;
	public int groundLevelY = 1;
    //public String entranceFacing = "south";
	public boolean broadcastLocation = false;
	public boolean decayable = true;

    protected List<ContextType> contextTypes;
    protected List<Direction> directions;



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
	
	public Material edgeType = Material.AIR;
	public byte edgeData = 0;
	public int edgeRise = 0;
	
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

	public abstract void paste(MetropolisGenerator generator, int blockX, int blockY, int blockZ);

    @Override
    public String toString() {
        return name;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public List<ContextType> getContextTypes() {
        return contextTypes;
    }

    public List<Direction> getDirections() {
        return directions;
    }
}
