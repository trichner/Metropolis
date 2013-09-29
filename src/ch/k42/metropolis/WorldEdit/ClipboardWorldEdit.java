package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Nimmersatt;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.ChestBlock;
import com.sk89q.worldedit.blocks.MobSpawnerBlock;
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

/**
 * Represents a structure defined by a loaded file in a cuboid.
 *
 * @author Daddy Churchill, Thomas Richner
 */
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

        loadConfigOrDefault(schemname+metaExtension);

        contextTypes = settings.getContext();
        direction = settings.getDirection();
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

        //edgeData = (byte)((edge.getData() & 0x000000ff)); // this would make overflows not error out but let's not do that
 //       edgeRise = generator.oreProvider.surfaceId == edgeType.getId() ? 0 : 1;

        // allocate room
        blocks = new BaseBlock[sizeX][sizeY][sizeZ];

        // copy the cube
        copyCuboid(cuboid);

        if(groundLevelY==1){ // steet level on default? bootstrap! FIXME hardcoded, should be option
            int streetLvlEstimate = estimateStreetLevel();
            settings.setGroundLevelY(streetLvlEstimate);
            if(!store(schemname+metaExtension)){
                generator.reportDebug("Can't store config file.");
            }
        }

	}

    /**
     * estimates the street level of a schematic, useful for bootstrapping settings
     * @return
     */
    private int estimateStreetLevel(){
        for(int y=0;y<sizeY;y++){
            int b = blocks[0][y][0].getType();
            if(b==Material.AIR.getId()||b==Material.LONG_GRASS.getId())
                return y;
        }
        return 0;
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
	public void paste(MetropolisGenerator generator, int blockX, int blockZ, int streetLevel) {
		Vector at = new Vector(blockX, getBottom(streetLevel), blockZ);
		try {
			EditSession editSession = getEditSession(generator);
			//editSession.setFastMode(true);
			place(editSession,at, true);
		} catch (Exception e) {
			//generator.reportException("[WorldEdit] Place schematic " + name + " at " + at + " failed", e);
		}
	}

	private void place(EditSession editSession,Vector pos, boolean noAir)
			throws MaxChangedBlocksException {
		for (int x = 0; x < sizeX; x++){
			for (int y = 0; y < sizeY; y++){
				for (int z = 0; z < sizeZ; z++) {
//					if ((noAir) && (blocks[x][y][z].isAir())) {
//						continue;
//					}

                    editSession.setBlock(new Vector(x, y, z).add(pos),
							blocks[x][y][z]);

				}
            }
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
        } catch (Exception e) { // catch all exceptions, inclusive any JSON fails
            settings = new SchematicsConfig(); // couldn't read config file? use default
            Bukkit.getLogger().throwing(this.getClass().getName(),"loadConfig",e);
            return false;
        }
    }

    private boolean store(String path){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String file = gson.toJson(settings);
            Files.write(Paths.get(path),file.getBytes()); //overwrite exsisting stuff
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().throwing(this.getClass().getName(), "store config", e);
            return false;
        }
    }

    private BaseBlock getTrick(){
        return new MobSpawnerBlock();
    }

    private BaseBlock getTreat(){
        return new ChestBlock();
    }
}
