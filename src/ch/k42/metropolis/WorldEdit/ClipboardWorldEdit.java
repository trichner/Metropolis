package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Nimmersatt;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ClipboardWorldEdit extends Clipboard {

	private BaseBlock[][][] blocks;
	private final static String metaExtension = ".json";
    private SchematicsConfig settings;

	public ClipboardWorldEdit(MetropolisGenerator generator, File file) throws Exception {
		super(generator, file);
	}

	
	@Override
	protected void load(MetropolisGenerator generator, File schemfile) throws Exception {
		// TODO load context & facing

        String schemname = schemfile.getAbsolutePath();

//        // prepare to read the meta data
//		YamlConfiguration metaYaml = new YamlConfiguration();
//		metaYaml.options().header("Metropolis/WorldEdit schematic configuration");
//		metaYaml.options().copyDefaults(true);
//
//		// add the defaults
//		metaYaml.addDefault(tagGroundLevelY, groundLevelY);
//		metaYaml.addDefault(tagBroadcastLocation, broadcastLocation);
//		metaYaml.addDefault(tagChestName, chestName);
//		metaYaml.addDefault(tagChestOdds, chestOdds);
//		metaYaml.addDefault(tagSpawnerType, spawnerType);
//		metaYaml.addDefault(tagSpawnerOdds, spawnerOdds);
//        metaYaml.addDefault(tagEntranceFacing, entranceFacing);
//        metaYaml.addDefault(tagDecayIntensity, DecayOption.getDefaultDecayIntensity());
//
//		// start reading it
//		File metaFile = new File(schemname + metaExtension);
//		if (metaFile.exists()) {
//			metaYaml.load(metaFile);
//			groundLevelY = Math.max(0, metaYaml.getInt(tagGroundLevelY, groundLevelY))+ nullspots_constant; // HARDCODED
//			broadcastLocation = metaYaml.getBoolean(tagBroadcastLocation, broadcastLocation);
//			chestName = metaYaml.getString(tagChestName, chestName);
//			chestOdds = Math.max(0.0, Math.min(1.0, metaYaml.getDouble(tagChestOdds, chestOdds)));
//			spawnerType = metaYaml.getString(tagSpawnerType, spawnerType);
//			spawnerOdds = Math.max(0.0, Math.min(1.0, metaYaml.getDouble(tagSpawnerOdds, spawnerOdds)));
//            entranceFacing = metaYaml.getString(tagEntranceFacing,entranceFacing);
//            double intensity = metaYaml.getDouble(tagDecayIntensity);
//            decayOptions = new DecayOption(intensity);
//		}
//
//        if(entranceFacing.equals("south")){
//
//        }
//
//
//        // try and save the meta data if we can
//        try {
//            metaYaml.save(metaFile);
//        } catch (IOException e) {
//            // we can recover from this... so eat it!
//            //generator.reportException("[WorldEdit] Could not resave " + metaFile.getAbsolutePath(), e);
//        }

        loadConfigOrDefault(schemname+metaExtension);

        contextTypes = settings.getContext();
        directions = settings.getDirections();
        groundLevelY = settings.getGroundLevelY();
        decayOptions = settings.getDecayOption();
        chestName = settings.getChestName();
        chestOdds = settings.getChestOdds()/100.0;
        spawnerType = settings.getSpawnerType();
        spawnerOdds = settings.getSpawnerOdds()/100.0;

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

    private void loadConfigOrDefault(String path){
        if(!loadConfig(path)){ // did we succeed?
            Bukkit.getServer().getLogger().warning("Unable to load config of schematic: "+path);
            if(!store(path)){ // no, so just store the default config
                Bukkit.getLogger().severe("Unable to load of save config of schematic: " + path);
            }
        }
    }

    private boolean loadConfig(String path){
        Gson gson = new Gson();
        try {
            String json = new String(Files.readAllBytes(Paths.get(path)));
            json = Nimmersatt.friss(json);
            settings = gson.fromJson(json,SchematicsConfig.class);
            return true;
        } catch (IOException e) {
            settings = new SchematicsConfig(); // couldn't read config file? use default
            Bukkit.getLogger().throwing(this.getClass().getName(),"loadConfig",e);
            return false;
        }
    }

    private boolean store(String path){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String file = gson.toJson(settings);
            Files.write(Paths.get(path),file.getBytes(), StandardOpenOption.CREATE);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().throwing(this.getClass().getName(), "store config", e);
            return false;
        }
    }
}
