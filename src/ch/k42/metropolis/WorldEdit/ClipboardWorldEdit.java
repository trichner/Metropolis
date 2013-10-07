package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.populators.MetropolisGenerator;
import ch.k42.metropolis.minions.Cartesian;
import ch.k42.metropolis.minions.DirtyHacks;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.minions.Nimmersatt;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.entity.EntityType;

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


	public ClipboardWorldEdit(MetropolisGenerator generator, File file,GlobalSchematicConfig globalSettings) throws Exception {
		super(generator, file, globalSettings);
	}


    private boolean hasBootstrappedConfig = false;
	
	@Override
	protected void load(MetropolisGenerator generator, File schemfile) throws Exception {

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

        // allocate room
        blocks = new BaseBlock[sizeX][sizeY][sizeZ];

        // copy the cube
        copyCuboid(cuboid);

        if(globalSettings.isEstimationOn() && hasBootstrappedConfig){ // estimate street level? good for bootstrapping config
            int streetLvlEstimate = estimateStreetLevel();
            settings.setGroundLevelY(streetLvlEstimate);
            if(!storeConfig(schemname + metaExtension)){
                generator.reportDebug("Can't storeConfig config file.");
            }
        }
	}

    /**
     * estimates the street level of a schematic, useful for bootstrapping settings
     * @return
     */
    private int estimateStreetLevel(){
        if(sizeY-2<0) return 1;

        for(int y=sizeY-2;y>=0;y--){
            int b = blocks[0][y][0].getType();
            if(b!=Material.AIR.getId()&&b!=Material.LONG_GRASS.getId()&&b!=Material.LEAVES.getId())
                return y+1;
        }
        return 1;
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

                if(generator.getPlugin().getMetropolisConfig().isChestRenaming()){ //do we really want to name them all?
                    for(Cartesian c : chests){
                        temp = base.add(c);
                        Block block = world.getBlockAt(temp.X, temp.Y, temp.Z);
                        if(block.getState() instanceof Chest){
                            if(!rand.getChance(settings.getChestOdds())){ //we were unlucky, chest doesn't get placed{
                                block.setType(Material.AIR);
                            }else { //rename chest
                                Chest chest = (Chest) block.getState(); //block has to be a chest
                                //chest.getInventory()
                                String name = DirtyHacks.getChestName(chest);
                                generator.reportDebug("Was name: [" + name + "]");
                                name = validateChestName(rand, name);
                                generator.reportDebug("New name: " + name);

                                nameChest(chest, name);
                                //generator.reportDebug("Placed a chest!");
                            }
                        }else {
                            generator.reportDebug("Chest coordinates were wrong!");
                        }
                    }
                }

                //set spawners

                if(generator.getPlugin().getMetropolisConfig().isSpawnerPlacing()){ // do we even place any?
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
                }
            }catch (Exception e){
                generator.reportException("Unable to place chests/spawner.",e);
            }

        } catch (Exception e) {
            generator.reportException("placing schematic failed",e);
        }


	}

    private EntityType getSpawnedEntity(GridRandom random){
        return settings.getRandomSpawnerEntity(random);
    }

    private void nameChest(Chest chest, String name){ //FIXME there might be no better way...
        //Bukkit.getLogger().warning("---- RENAMING CHEST ----");
        DirtyHacks.setChestName(chest, name);

//        CraftItemStack cis = CraftItemStack.a
//        NBTTagCompound tag = cis.getHandle().getTag();
//        if (tag == null) {
//            cis.getHandle().setTag(new NBTTagCompound());
//        }
//            Field inventoryField = chest.getClass().getDeclaredField("chest"); //This get's the CraftChest variable 'chest' which is the TileEntityChest that is stored within it
//            inventoryField.setAccessible(true); //Allows you to access that field since it's declared as private
//            TileEntityChest teChest = ((TileEntityChest) inventoryField.get(chest)); //obtains the field and casts it to a TileEntityChest
//            teChest.a(name); //The a(String) method sets the title of the chest
    }

    private static final char COLOR = ChatColor.GREEN.getChar();

    private String validateChestName(GridRandom rand,String name){

        //chest has level? -> Assumption: Chest fully named

        char lastchar = name.charAt(name.length()-1);
        boolean fail = false;
        try {

            int level = Integer.getInteger(String.valueOf(lastchar));
            fail = level>5 || level<1;
        }catch (Exception e){
            //Bukkit.getLogger().warning("---- unnamed chest: ["+name+"], lchar:" + lchar +" ---" + e.getMessage());
            fail = true;
        }

        if(fail){
            if(lastchar=='_'){ //append only level
                name+=Integer.toString(randomChestLevel(rand)); //add a random chest level
            }else { // set name and level
                name = getNameAndLevel(rand);
            }
        }
        return name;
    }

    private String getNameAndLevel(GridRandom rand){
        StringBuffer buf = new StringBuffer();
        buf.append('§')
                .append(COLOR)
                .append(settings.getRandomLootCollection(rand).name)
                .append('_')
                .append(Integer.toString(randomChestLevel(rand)));
        return buf.toString();
    }

    private int randomChestLevel(GridRandom random){
        int min = settings.getLootMinLevel();
        int max = settings.getLootMaxLevel();
        return globalSettings.getRandomChestLevel(random,min,max);
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
            hasBootstrappedConfig = true;
            Bukkit.getServer().getLogger().warning("Unable to load config of schematic: "+path);
            if(!storeConfig(path)){ // no, so just storeConfig the default config
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
            if(Files.exists(Paths.get(path))){
                try {
                    Files.copy(Paths.get(path),Paths.get(path+".bak"));
                } catch (IOException e1) {
                    Bukkit.getLogger().throwing(this.getClass().getName(),"loadConfig",e);
                }
            }
            settings = new SchematicConfig(); // couldn't read config file? use default
            Bukkit.getLogger().throwing(this.getClass().getName(),"loadConfig",e);
            return false;
        }
    }

    private boolean storeConfig(String path){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String file = gson.toJson(settings);
            Files.write(Paths.get(path),file.getBytes()); //overwrite exsisting stuff
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().throwing(this.getClass().getName(), "storeConfig config", e);
            return false;
        }
    }


}
