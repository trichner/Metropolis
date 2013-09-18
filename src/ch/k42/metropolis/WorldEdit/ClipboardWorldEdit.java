package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.minions.Direction;
import ch.k42.metropolis.model.ContextType;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ClipboardWorldEdit extends Clipboard {

	private BaseBlock[][][] blocks;
	private int facingCount;
//  private boolean Rotatable = false;
//	private boolean ScalableXZ = false;
//	private boolean ScalableY = false;
//	private int FloorHeightY = DataContext.FloorHeight;

	private final static String metaExtension = ".yml";
	private final static String tagGroundLevelY = "GroundLevelY";
//	private final static String tagScalableX = "ScalableX";
//	private final static String tagScalableZ = "ScalableZ";
//	private final static String tagScalableY = "ScalableY";
//	private final static String tagFloorHeightY = "FloorHeightY";
	private final static String tagOddsOfAppearance = "OddsOfAppearance";
	private final static String tagBroadcastLocation = "BroadcastLocation";
	private final static String tagDecayable = "Decayable";
	private final static String tagChestName = "ChestName";
	private final static String tagChestOdds = "ChestOdds";
	private final static String tagSpawnerType = "SpawnerType";
	private final static String tagSpawnerOdds = "SpawnerOdds";
//    private final static String tagDecayOptionFullThreshold = "DecayFullThreshold";
//    private final static String tagDecayOptionPartialThreshold = "DecayPartialThreshold";
//    private final static String tagDecayOptionLeavesThreshold = "DecayLeavesThreshold";
//    private final static String tagDecayOptionHoleScale = "DecayHoleScale";
//    private final static String tagDecayOptionLeavesScale = "DecayLeavesScale";
    private final static String tagDecayIntensity = "DecayIntensity";
    //private final static String tagEntranceFacing = "EntranceFacing";


	public ClipboardWorldEdit(MetropolisGenerator generator, File file) throws Exception {
		super(generator, file);
	}

	
	@Override
	protected void load(MetropolisGenerator generator, File schemfile) throws Exception {
		// TODO load context & facing

        String schemname = schemfile.getAbsolutePath();

        // prepare to read the meta data
		YamlConfiguration metaYaml = new YamlConfiguration();
		metaYaml.options().header("Metropolis/WorldEdit schematic configuration");
		metaYaml.options().copyDefaults(true);
		
		// add the defaults
		metaYaml.addDefault(tagGroundLevelY, groundLevelY);
		//metaYaml.addDefault(tagOddsOfAppearance, oddsOfAppearance);
		metaYaml.addDefault(tagBroadcastLocation, broadcastLocation);
		metaYaml.addDefault(tagDecayable, decayable);
		metaYaml.addDefault(tagChestName, chestName);
		metaYaml.addDefault(tagChestOdds, chestOdds);
		metaYaml.addDefault(tagSpawnerType, spawnerType);
		metaYaml.addDefault(tagSpawnerOdds, spawnerOdds);
        //metaYaml.addDefault(tagEntranceFacing, entranceFacing);
        // DecayOptions, Thresholds
        metaYaml.addDefault(tagDecayIntensity, DecayOption.getDefaultDecayIntensity());
		
		// start reading it
		File metaFile = new File(schemname + metaExtension);
		if (metaFile.exists()) {
			metaYaml.load(metaFile);
			groundLevelY = Math.max(0, metaYaml.getInt(tagGroundLevelY, groundLevelY))+ nullspots_constant; // HARDCODED
			//oddsOfAppearance = Math.max(0.0, Math.min(1.0, metaYaml.getDouble(tagOddsOfAppearance, oddsOfAppearance)));
			broadcastLocation = metaYaml.getBoolean(tagBroadcastLocation, broadcastLocation);
			decayable = metaYaml.getBoolean(tagDecayable, decayable);
			chestName = metaYaml.getString(tagChestName, chestName);
			chestOdds = Math.max(0.0, Math.min(1.0, metaYaml.getDouble(tagChestOdds, chestOdds)));
			spawnerType = metaYaml.getString(tagSpawnerType, spawnerType);
			spawnerOdds = Math.max(0.0, Math.min(1.0, metaYaml.getDouble(tagSpawnerOdds, spawnerOdds)));
            //entranceFacing = metaYaml.getString(tagEntranceFacing,entranceFacing);

            //Decay Options
            double intensity = metaYaml.getDouble(tagDecayIntensity);
            decayOptions = new DecayOption(intensity);
		}

        // try and save the meta data if we can
        try {
            metaYaml.save(metaFile);
        } catch (IOException e) {
            // we can recover from this... so eat it!
            //generator.reportException("[WorldEdit] Could not resave " + metaFile.getAbsolutePath(), e);
        }

        // TODO load from yaml
        contextTypes = new ArrayList<ContextType>();
        contextTypes.add(ContextType.HIGHRISE);
        contextTypes.add(ContextType.ROAD);
        directions = new ArrayList<Direction>();
        directions.add(Direction.ALL);
        directions.add(Direction.NONE);

        // load the actual blocks
        CuboidClipboard cuboid= SchematicFormat.getFormat(schemfile).load(schemfile);

        // how big is it?
        sizeX = cuboid.getWidth();
        sizeZ = cuboid.getLength();
        sizeY = cuboid.getHeight();

        // grab the edge block
        BaseBlock edge = cuboid.getPoint(new Vector(0, groundLevelY, 0));
        edgeType = Material.getMaterial(edge.getType());
        edgeData = (byte) edge.getData(); //TODO I think that data can be integers... one of these days
        //edgeData = (byte)((edge.getData() & 0x000000ff)); // this would make overflows not error out but let's not do that
 //       edgeRise = generator.oreProvider.surfaceId == edgeType.getId() ? 0 : 1;

        // allocate room
        blocks = new BaseBlock[sizeX][sizeY][sizeZ];

        // copy the cube
        copyCuboid(cuboid);

	}
	
	private void copyCuboid(CuboidClipboard cuboid) {
	    for (int x = 0; x < sizeX; x++)
	        for (int y = 0; y < sizeY; y++)
	          for (int z = 0; z < sizeZ; z++)
	        	  blocks[x][y][z] = cuboid.getPoint(new Vector(x, y, z));
	}
	
	private EditSession getEditSession(MetropolisGenerator generator) {
		return new EditSession(new BukkitWorld(generator.getWorld()), blockCount);
	}

	@Override
	public void paste(MetropolisGenerator generator, int blockX, int blockY, int blockZ) {
		Vector at = new Vector(blockX, blockY, blockZ);
		try {
			EditSession editSession = getEditSession(generator);
			//editSession.setFastMode(true);
			place(editSession,at, true);
		} catch (Exception e) {
			//generator.reportException("[WorldEdit] Place schematic " + name + " at " + at + " failed", e);
		}
	}

	
//	@Override
//	public void paste(WorldGenerator generator, RealChunk chunk, Direction.Facing facing, 
//			int blockX, int blockY, int blockZ,
//			int x1, int x2, int y1, int y2, int z1, int z2) {
//		
////		generator.reportMessage("Partial paste: origin = " + at + " min = " + min + " max = " + max);
//		
//		try {
//			int iFacing = getFacingIndex(facing);
//			EditSession editSession = getEditSession(generator);
//			//editSession.setFastMode(true);
//			for (int x = x1; x < x2; x++)
//				for (int y = y1; y < y2; y++)
//					for (int z = z1; z < z2; z++) {
////						generator.reportMessage("facing = " + iFacing + 
////								" x = " + x +
////								" y = " + y + 
////								" z = " + z);
//						if (blocks[iFacing][x][y][z].isAir()) {
//							continue;
//						}
//						editSession.setBlock(new Vector(x, y, z).add(blockX, blockY, blockZ), 
//								blocks[iFacing][x][y][z]);
//					}
//		} catch (Exception e) {
//			e.printStackTrace();
//			generator.reportException("[WorldEdit] Partial place schematic " + name + " failed", e);
//		}
//	}

	//TODO remove the editSession need by directly setting the blocks in the chunk

	public void paste(MetropolisGenerator generator, int blockX, int blockY, int blockZ,int x1, int x2, int y1, int y2, int z1, int z2) {
		Vector at = new Vector(blockX, blockY, blockZ);
//		Vector min = new Vector(x1, y1, z1);
//		Vector max = new Vector(x2, y2, z2);
//		generator.reportMessage("Partial paste: origin = " + at + " min = " + min + " max = " + max);

		try {
			EditSession editSession = getEditSession(generator);
			//editSession.setFastMode(true);
			place(editSession,at, true, x1, x2, y1, y2, z1, z2);
		} catch (Exception e) {
//			generator.reportException("[WorldEdit] Partial place schematic " + name + " at " + at + " failed", e);
//			generator.reportMessage("Info: " +
//									" facing = " + facing +
//									" size = " + sizeX + ", " + sizeZ +
//									" chunk = " + chunkX + ", " + chunkZ +
////									" origin = "+ blockX + ", " + blockY + ", " + blockZ +
//									" min = " + x1 + ", "+ y1 + ", "+ z1 +
//									" max = " + x2 + ", "+ y2 + ", "+ z2);

			e.printStackTrace();
		}
	}

	//TODO Pilfered from WorldEdit's CuboidClipboard... I need to remove this once the other Place function is used
	private void place(EditSession editSession,Vector pos, boolean noAir)
			throws MaxChangedBlocksException {
		for (int x = 0; x < sizeX; x++)
			for (int y = 0; y < sizeY; y++)
				for (int z = 0; z < sizeZ; z++) {
					if ((noAir) && (blocks[x][y][z].isAir())) {
						continue;
					}
					editSession.setBlock(new Vector(x, y, z).add(pos),
							blocks[x][y][z]);
				}
	}

	//TODO if WorldEdit ever gets this functionality I need to remove the modified code
	private void place(EditSession editSession,Vector pos, boolean noAir,	int x1, int x2, int y1, int y2, int z1, int z2) throws MaxChangedBlocksException {
		x1 = Math.max(x1, 0);
		x2 = Math.min(x2, sizeX);
		y1 = Math.max(y1, 0);
		y2 = Math.min(y2, sizeY);
		z1 = Math.max(z1, 0);
		z2 = Math.min(z2, sizeZ);
		for (int x = x1; x < x2; x++)
			for (int y = y1; y < y2; y++)
				for (int z = z1; z < z2; z++) {
					if ((noAir) && (blocks[x][y][z].isAir())) {
						continue;
					}
					editSession.setBlock(new Vector(x, y, z).add(pos), blocks[x][y][z]);
				}
	}	
}
