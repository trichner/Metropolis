package ch.k42.metropolis.generator;

import ch.k42.metropolis.generator.populators.BedrockFloorPopulator;
import ch.k42.metropolis.generator.populators.VaultBlockPopulator;
import ch.k42.metropolis.minions.Minions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import javax.inject.Inject;
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
public class VaultGenerator extends ChunkGenerator {

    @Inject
    private VaultBlockPopulator blockPopulator;

    private World world;

    public String worldName;


    public void setWorld(String worldName){
        this.worldName = worldName;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        List<BlockPopulator> populators = new ArrayList<BlockPopulator>();
        Minions.d("getDefaultPopulators: " + world.toString());

        this.world = world;

        /*
         * We should decouple them from here and decouple
         * the populator config from the plugin config
         */

        populators.add(blockPopulator);
        populators.add(new BedrockFloorPopulator());

        return populators;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public byte[][] generateBlockSections(World aWorld, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        byte[][] chunk = new byte[aWorld.getMaxHeight() / 16][];
        for (int x = 0; x < 16; x++) { //loop through all of the blocks in the chunk that are lower than maxHeight
            for (int z = 0; z < 16; z++) {

                biomes.setBiome(x,z, Biome.PLAINS);

                int maxHeight = 65; //how thick we want out flat terrain to be
                for (int y = 1; y < maxHeight; y++) {
                    setBlock(x, y, z, chunk, Material.AIR);
                }
            }
        }
        return chunk;
    }

    private void setBlock(int x, int y, int z, byte[][] chunk, Material material) {
        if (chunk[y >> 4] == null)
            chunk[y >> 4] = new byte[16 * 16 * 16];

        if (!isInChunkBounds(x, y, z)) { // out of bounds?
            return;
        }

        chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
    }

    private boolean isInChunkBounds(int x, int y, int z){
        return (y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0);
    }

    private final static int SPAWN_RADIUS = 100;

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        int spawnX = random.nextInt(SPAWN_RADIUS * 2) - SPAWN_RADIUS;
        int spawnZ = random.nextInt(SPAWN_RADIUS * 2) - SPAWN_RADIUS;

        // find the first non empty spot;
        int spawnY = world.getHighestBlockYAt(spawnX,spawnZ);
        if(spawnY<60) spawnY=150;
        // return the location
        return new Location(world, spawnX, spawnY, spawnZ);
    }

}
