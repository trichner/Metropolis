package ch.k42.metropolis.generator;

import ch.k42.metropolis.grid.urbanGrid.clipboard.ClipboardProvider;
import ch.k42.metropolis.generator.populators.BedrockFloorPopulator;
import ch.k42.metropolis.generator.populators.CavePopulator;
import ch.k42.metropolis.generator.populators.OrePopulator;
import ch.k42.metropolis.grid.common.Factory;
import ch.k42.metropolis.grid.common.GridProvider;
import ch.k42.metropolis.grid.urbanGrid.context.ContextProvider;
import ch.k42.metropolis.grid.urbanGrid.provider.*;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import ch.k42.metropolis.plugin.PluginConfig;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
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

        @Override
        public void populate(World aWorld, Random random, Chunk chunk) {
            gridProvider.getGrid(chunk.getX(),chunk.getZ()).populate(chunk);
            gridProvider.getGrid(chunk.getX(),chunk.getZ()).postPopulate(chunk);
        }
    }

    private MetropolisPlugin plugin;
    private Long worldSeed;
    private World world;

    public String worldName;

    private ClipboardProvider clipboardProvider;
    private GridProvider gridProvider;
    private ContextProvider contextProvider;

    public DecayProvider decayProvider;
    public EnvironmentProvider natureDecay;

    public MetropolisGenerator(MetropolisPlugin plugin, String worldName, ClipboardProvider clipboardProvider) {
        this.clipboardProvider = clipboardProvider;
        this.plugin = plugin;
        this.worldName = worldName;
        plugin.getLogger().info("Running MetropolisGenerator.");
    }

    public ClipboardProvider getClipboardProvider() {
        return clipboardProvider;
    }

    public GridProvider getGridProvider() {
        return gridProvider;
    }

    public ContextProvider getContextProvider() {
        return contextProvider;
    }

    public DecayProvider getDecayProvider() {
        return decayProvider;
    }

    public EnvironmentProvider getNatureDecayProvider() {
        return natureDecay;
    }

    public World getWorld() {
        return world;
    }

    public MetropolisPlugin getPlugin() {
        return plugin;
    }

    public Long getWorldSeed() {
        return worldSeed;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        List<BlockPopulator> populators = new ArrayList<BlockPopulator>();
        plugin.getLogger().info("getDefaultPopulators: " + world.toString());
        this.world = world;
        this.worldSeed = world.getSeed();
        this.gridProvider = new GridProvider(this);
        this.contextProvider = Factory.getDefaultContextProvider(this, plugin.getContextConfig());

        /*
         * We should decouple them from here and decouple
         * the populator config from the plugin config
         */
        populators.add(new MetropolisBlockPopulator());
        populators.add(new CavePopulator());
        populators.add(new OrePopulator(world, plugin.getPopulatorConfig().getOres())); // last place some ore
        populators.add(new BedrockFloorPopulator());

        return populators;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public byte[][] generateBlockSections(World aWorld, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        if (natureDecay == null || decayProvider == null) {
            if (aWorld.getEnvironment() == World.Environment.NETHER) {
                decayProvider = new DecayProviderNether(this, new Random(aWorld.getSeed() + 6));
                natureDecay = new NetherEnvironmentProvider(aWorld.getSeed());
            } else {
                decayProvider = new DecayProviderNormal(this, new Random(aWorld.getSeed() + 6));
                natureDecay = new NormalEnvironmentProvider(aWorld.getSeed());
            }
        }

        try {

            byte[][] chunk = new byte[aWorld.getMaxHeight() / 16][];
            for (int x = 0; x < 16; x++) { //loop through all of the blocks in the chunk that are lower than maxHeight
                for (int z = 0; z < 16; z++) {

                    biomes.setBiome(x,z, Biome.PLAINS);

                    int maxHeight = 65; //how thick we want out flat terrain to be
                    for (int y = 1; y < maxHeight; y++) {
                        Material decay = natureDecay.checkBlock(aWorld, (chunkX * 16) + x, y, (chunkZ * 16) + z);
                        if (decay != null) {
                            setBlock(x, y, z, chunk, decay);
                        } else {
                            setBlock(x, y, z, chunk, Material.STONE);
                        }
                    }
                }
            }
            return chunk;//byteChunk.blocks;

        } catch (NullPointerException e) {
            Minions.e(e);
            return null;
        }
    }

    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        return super.generate(world, random, x, z);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public short[][] generateExtBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        return super.generateExtBlockSections(world, random, x, z, biomes);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private void setBlock(int x, int y, int z, byte[][] chunk, Material material) {
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

}
