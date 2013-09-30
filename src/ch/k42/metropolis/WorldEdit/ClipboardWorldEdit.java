package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Cartesian;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.minions.Nimmersatt;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import net.minecraft.server.v1_5_R3.TileEntityChest;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.v1_5_R3.block.CraftChest;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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

	public ClipboardWorldEdit(MetropolisGenerator generator, File file) throws Exception {
		super(generator, file);
	}

	
	@Override
	protected void load(MetropolisGenerator generator, File schemfile) throws Exception {
		// TODO load context & facing

        String schemname = schemfile.getAbsolutePath();

        loadConfigOrDefault(schemname+metaExtension);

        contextTypes = settings.getContext();
        groundLevelY = settings.getGroundLevelY();

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

    private final static int SPAWNER_SUBSTITUTE = Material.SPONGE.getId();
	private void copyCuboid(CuboidClipboard cuboid) {
	    for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++)
                for (int z = 0; z < sizeZ; z++){
                    BaseBlock block = cuboid.getPoint(new Vector(x, y, z));
                    if(block.getId()==Material.CHEST.getId()){
                        chests.add(new Cartesian(x,y,z));
                    }else if(block.getId()==SPAWNER_SUBSTITUTE){
                        spawners.add(new Cartesian(x,y,z));
                    }
                    blocks[x][y][z] = block;
                }
	}
	
	private EditSession getEditSession(MetropolisGenerator generator) {
		return new EditSession(new BukkitWorld(generator.getWorld()), blockCount);
	}

	@Override
	public void paste(MetropolisGenerator generator, int blockX, int blockZ, int streetLevel) {
		int blockY = getBottom(streetLevel);
        Vector at = new Vector(blockX, blockY, blockZ);
		try {
			EditSession editSession = getEditSession(generator);
			//editSession.setFastMode(true);
            //place Schematic
			place(editSession,at, true);

            try {
                //fill chests
                World world = generator.getWorld();
                GridRandom rand = generator.getGridProvider().getRandom(blockX>>4,blockZ>>4);
                Cartesian base = new Cartesian(blockX,blockY,blockZ);
                Cartesian temp;
                for(Cartesian c : chests){
                    temp = base.add(c);
                    Block block = world.getBlockAt(temp.X, temp.Y, temp.Z);
                    if(block.getState() instanceof CraftChest){
                        if(!rand.getChance(settings.getChestOdds())){ //we were unlucky, chest doesn't get placed{
                            block.setType(Material.AIR);
                        }else { //rename chest
                            CraftChest chest = (CraftChest) block.getState(); //block has to be a chest
                            String name = validateChestName(rand, chest.getInventory().getName());
                            renameChest(chest,name);
                            generator.reportDebug("Placed a chest!");
                        }
                    }else {
                        generator.reportDebug("Chest coordinates were wrong!");
                    }
                }

                //set spawners

                for(Cartesian c : spawners){
                    temp = base.add(c);
                    Block block = world.getBlockAt(temp.X, temp.Y, temp.Z);

                    if(block.getType().equals(Material.SPONGE)){
                        if(!rand.getChance(settings.getSpawnerOdds())){ //we were unlucky, chest doesn't get placed{
                            block.setType(Material.AIR);
                        }else { //set spawn type
                            block.setType(Material.MOB_SPAWNER);
                            if(block.getState() instanceof CreatureSpawner){
                            CreatureSpawner spawner = (CreatureSpawner) block.getState(); //block has to be a chest
                            spawner.setSpawnedType(getSpawnedEntity(rand));
                            generator.reportDebug("Placed a spawner!");
                            }else{
                                generator.reportDebug("Unable to place Spawner.");
                            }
                        }
                    }else {
                        generator.reportDebug("Chest coordinates were wrong!");
                    }
                }
            }catch (Exception e){
                generator.reportException("Unable to place chests/spawner.",e);
            }

        } catch (Exception e) {
            generator.reportException("placing schematic failed",e);
        }


	}

    private EntityType getSpawnedEntity(GridRandom random){
        return settings.getRandomSpawnerEntity(random.getRandomInt(settings.getSpawnerEntityWeightSum()));
    }

    private void renameChest(CraftChest chest, String name) throws Exception{ //FIXME there might be no better way...
            Field inventoryField = chest.getClass().getDeclaredField("chest"); //This get's the CraftChest variable 'chest' which is the TileEntityChest that is stored within it
            inventoryField.setAccessible(true); //Allows you to access that field since it's declared as private
            TileEntityChest teChest = ((TileEntityChest) inventoryField.get(chest)); //obtains the field and casts it to a TileEntityChest
            teChest.a(name); //The a(String) method sets the title of the chest
    }

    private static final String color= "§a";

    private String validateChestName(GridRandom rand,String name){
        //chest has level? -> Assumption: Chest fully named
        char lastchar = name.charAt(name.length()-1);

        boolean fail = false;
        try {
            int level = Integer.getInteger(String.valueOf(lastchar));
            fail = level>5 || level<1;
        }catch (Exception e){
            fail = true;
        }

        if(fail){
            if(lastchar=='_'){ //append only level
                name+=Integer.toString(randomChestLevel(rand)); //add a random chest level
            }else { // set name and level
                StringBuffer buf = new StringBuffer();
                buf.append(color)
                        .append(settings.getStandardChestName())
                        .append('_')
                        .append(Integer.toString(randomChestLevel(rand)));
                name = buf.toString();
            }
        }
        return name;
    }

    private int randomChestLevel(GridRandom random){
        return settings.getRandomChestLevel(random.getRandomInt(settings.getChestLevelWeightSum()));
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
            settings = gson.fromJson(json,SchematicConfig.class);
            return true;
        } catch (Exception e) { // catch all exceptions, inclusive any JSON fails
            settings = new SchematicConfig(); // couldn't read config file? use default
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


}
