package ch.k42.metropolis.generator;

import ch.k42.metropolis.WorldEdit.ClipboardProviderWorldEdit;
import ch.k42.metropolis.minions.ByteChunk;
import ch.k42.metropolis.minions.DecayProvider;
import ch.k42.metropolis.model.ContextProvider;
import ch.k42.metropolis.model.GridProvider;
import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 16.09.13
 * Time: 00:29
 * To change this template use File | Settings | File Templates.
 */
public class MetropolisGenerator extends ChunkGenerator {

    private class MetropolisBlockPopulator extends BlockPopulator {


        public MetropolisBlockPopulator() {}

        @Override
        public void populate(World aWorld, Random random, Chunk chunk) {
            try {
                Bukkit.getLogger().info("!!! populate !!!");
                MetropolisGenerator.this.initializeWorldInfo(aWorld);

                gridProvider.populate(MetropolisGenerator.this,chunk);


//                if (platmap != null) {
//                    platmap.generateBlocks(realChunk);
//
//                    // Originally by Sablednah
//                    // Moved and modified a bit by DaddyChurchill
//                    CityWorldEvent event = new CityWorldEvent(chunk, platmap, platmap.getMapLot(chunkX, chunkZ));
//                    Bukkit.getServer().getPluginManager().callEvent(event);
//                }

            } catch (Exception e) {
                reportException("BlockPopulator FAILED", e);
            }
        }
    }

    private Plugin plugin;
    private World world;
    private Long worldSeed;

    public String worldName;
    public World.Environment worldEnvironment;


    private ClipboardProviderWorldEdit clipboardProvider;
    private GridProvider gridProvider;
    private ContextProvider contextProvider;


    public DecayProvider decayProvider;

    public int streetLevel;

    public int deepseaLevel;
    public int seaLevel;
    public int structureLevel;
    public int treeLevel;
    public int evergreenLevel;
    public int deciduousRange;
    public int evergreenRange;
    public int height = 256;
    public int snowLevel;
    public int landRange;
    public int seaRange;

    public long connectedKeyForPavedRoads;
    public long connectedKeyForParks;


    public MetropolisGenerator(Plugin plugin, String worldName) {
        this.plugin = plugin;
        this.worldName = worldName;
    }

    public ClipboardProviderWorldEdit getClipboardProvider() {
        return clipboardProvider;
    }

    public GridProvider getGridProvider() {
        return gridProvider;
    }

    public ContextProvider getContextProvider() {
        return contextProvider;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getPluginName() {
        return plugin.getName();
    }

    public World getWorld() {
        return world;
    }

    public Long getWorldSeed() {
        return worldSeed;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        Bukkit.getLogger().warning("!!! added block populator !!!");
        return Arrays.asList((BlockPopulator) new MetropolisBlockPopulator());
    }

    public void initializeWorldInfo(World aWorld) {

        // initialize the shaping logic
        if (world == null) {
            world = aWorld;

            //CityWorldSettings settings = new CityWorldSettings(this);

            worldSeed = world.getSeed();
            decayProvider = new DecayProvider(this, new Random(worldSeed + 6));
            gridProvider = new GridProvider(this);
            contextProvider = new ContextProvider(this);
            try {
                clipboardProvider = new ClipboardProviderWorldEdit(this);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load clipboard: " + e.getMessage());
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

//            connectionKeyGen = new Odds(worldSeed + 1);
//
//            shapeProvider = ShapeProvider.loadProvider(this, new Odds(worldSeed + 2));
//            lootProvider = LootProvider.loadProvider(this);
//            spawnProvider = SpawnProvider.loadProvider(this);
//            oreProvider = OreProvider.loadProvider(this);
//            foliageProvider = FoliageProvider.loadProvider(this, new Odds(worldSeed + 3));
//            odonymProvider = OdonymProvider.loadProvider(this, new Odds(worldSeed + 4));
//            surfaceProvider = SurfaceProvider.loadProvider(this, new Odds(worldSeed + 5));
//            balloonProvider = BalloonProvider.loadProvider(this);
//            houseProvider = HouseProvider.loadProvider(this);





//            // get ranges and contexts
//            height = shapeProvider.getWorldHeight();
//            seaLevel = shapeProvider.getSeaLevel();
//            landRange = shapeProvider.getLandRange();
//            seaRange = shapeProvider.getSeaRange();
//            structureLevel = shapeProvider.getStructureLevel();
//            streetLevel = shapeProvider.getStreetLevel();
//
//            // did we load any schematics?
//            clipboardProvider.reportStatus(this);

        }
    }

    @Override
    public byte[][] generateBlockSections(World aWorld, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        try {

                initializeWorldInfo(aWorld);
            Bukkit.getLogger().info("!!! generateBlockSections !!!");
////            // place to work
//            ByteChunk byteChunk = new ByteChunk(this, chunkX, chunkZ);
//            //gridProvider.populate(this,byteChunk);
//            byteChunk.setAllBlocks((byte)Material.AIR.getId());
//
//            // figure out what everything looks like
//            PlatMap platmap = getPlatMap(chunkX, chunkZ);
//            if (platmap != null) {
//                //CityWorld.reportMessage("generate X,Z = " + chunkX + "," + chunkZ);
//                platmap.generateChunk(byteChunk, biomes);
//            }
//
//            // This was added by Sablednah
//            // https://github.com/echurchill/CityWorld/pull/5
//            // MOVED to the chunk populator by DaddyChurchill 10/27/12
//            //CityWorldEvent event = new CityWorldEvent(chunkX, chunkZ, platmap.context, platmap.getPlatLots()[chunkX - platmap.originX][chunkZ - platmap.originZ]);
//            //Bukkit.getServer().getPluginManager().callEvent(event);
//
            byte[][] chunk = new byte[world.getMaxHeight() / 16][];
            for (int x=0; x<16; x++) { //loop through all of the blocks in the chunk that are lower than maxHeight
                for (int z=0; z<16; z++) {
                    int maxHeight = 64; //how thick we want out flat terrain to be
                    for (int y=0;y<maxHeight;y++) {
                        setBlock(x,y,z,chunk,Material.STONE);
                    }
                }
            }
            return chunk;//byteChunk.blocks;

        } catch (Exception e) {
            reportException("ChunkPopulator FAILED: " + e.getMessage(), e);
            return null;
        }
    }

//    @Override
//	public short[][] generateExtBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
////		try {
////
////			initializeWorldInfo(world);
////
////			// place to work
////			ShortChunk shortChunk = new ShortChunk(this, chunkX, chunkZ);
////
////			// figure out what everything looks like
////			PlatMap platmap = getPlatMap(chunkX, chunkZ);
////			if (platmap != null) {
////				//CityWorld.reportMessage("generate X,Z = " + chunkX + "," + chunkZ);
////				platmap.generateChunk(shortChunk, biomes);
////			}
////
////			// This was added by Sablednah
////			// https://github.com/echurchill/CityWorld/pull/5
////			// MOVED to the chunk populator by DaddyChurchill 10/27/12
////			//CityWorldEvent event = new CityWorldEvent(chunkX, chunkZ, platmap.context, platmap.getPlatLots()[chunkX - platmap.originX][chunkZ - platmap.originZ]);
////			//Bukkit.getServer().getPluginManager().callEvent(event);
////
////			return shortChunk.blocks;
////
////		} catch (Exception e) {
////			reportException("ChunkPopulator FAILED", e);
////			return null;
////		}
//        return null;
//	}


    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        Bukkit.getLogger().warning("!!! generate !!!");
        return super.generate(world, random, x, z);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public short[][] generateExtBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        Bukkit.getLogger().warning("!!! generateExtBlockSections !!!");
        return super.generateExtBlockSections(world, random, x, z, biomes);    //To change body of overridden methods use File | Settings | File Templates.
    }

    void setBlock(int x, int y, int z, byte[][] chunk, Material material) {
        if (chunk[y >> 4] == null)
            chunk[y >> 4] = new byte[16 * 16 * 16];
        if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0))
            return;
        try {
            chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte) material
                    .getId();
        } catch (Exception e) {
            // do nothing
        }
    }

//    @Override
//    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
//        Bukkit.getLogger().warning("!!! generateBlockSections !!!");
//        return super.generateBlockSections(world, random, x, z, biomes);    //To change body of overridden methods use File | Settings | File Templates.
//    }

    private final static int spawnRadius = 100;

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        int spawnX = random.nextInt(spawnRadius * 2) - spawnRadius;
        int spawnZ = random.nextInt(spawnRadius * 2) - spawnRadius;

        // find the first non empty spot;
        int spawnY = world.getMaxHeight();
        while ((spawnY > 0) && world.getBlockAt(spawnX, spawnY - 1, spawnZ).isEmpty()) {
            spawnY--;
        }

        // return the location
        return new Location(world, spawnX, spawnY, spawnZ);
    }



    public void reportMessage(String message) {
        plugin.getLogger().info(message);
    }

    public void reportDebug(String message) {
        plugin.getLogger().info("[====DEBUG====]" + message);
    }

    public void reportMessage(String message1, String message2) {
        //plugin.reportMessage(message1, message2);
        plugin.getLogger().info(message1 + "     " +message2);
    }

    public void reportException(String message, Exception e) {
        plugin.getLogger().warning(message + " ---- " + e.getMessage());
        e.printStackTrace();
    }


}
