package ch.k42.metropolis.generator;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * Replaces any stone with random veins. Based on the "Nordic" ore populator code.
 * @author Spaceribs
 */
public class OrePopulator extends BlockPopulator {

    /* Material, Iterations, Amount, maxHeight */
    private static final OreVein[] veins = new OreVein[] {
        new OreVein(Material.GRAVEL,        10,     32,     128),
        new OreVein(Material.SAND,          16,     32,     45),
        new OreVein(Material.COAL_ORE,      20,     16,     128),
        new OreVein(Material.IRON_ORE,      20,     8,      128),
        new OreVein(Material.GOLD_ORE,      2,      8,      32),
        new OreVein(Material.REDSTONE_ORE,  8,      7,      32),
        new OreVein(Material.DIAMOND_ORE,   1,      7,      32),
        new OreVein(Material.LAPIS_ORE,     1,      6,      32)
    };

    /**
     * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World, java.util.Random, org.bukkit.Chunk)
     */
    @Override
    public void populate(World world, Random random, Chunk source) {
        for (OreVein vein : veins) {
            for (int j = 0; j < vein.getIterations(); j++) {
                internal(source, random, random.nextInt(16),random.nextInt(vein.getMaxHeight()), random.nextInt(16), vein);
            }
        }
    }

    private static void internal(Chunk source, Random random, int originX, int originY, int originZ, OreVein vein) {
        int amount = vein.getAmount();
        for (int i = 0; i < amount; i++) {
            int x = originX + random.nextInt(amount / 2) - amount / 4;
            int y = originY + random.nextInt(amount / 4) - amount / 8;
            int z = originZ + random.nextInt(amount / 2) - amount / 4;
            x &= 0xf;
            z &= 0xf;
            if (y > 127 || y < 0) {
                continue;
            }

            Block block = source.getBlock(x, y, z);
            if (block.getType() == Material.STONE) {
                block.setType(vein.getMaterial());
            }
        }
    }
}
