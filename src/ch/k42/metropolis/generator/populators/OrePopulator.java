package ch.k42.metropolis.generator.populators;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * Replaces any stone with random veins. Based on the "Nordic" ore populator code.
 *
 * @author Spaceribs
 */
public class OrePopulator extends BlockPopulator {

    /**
     * Vein Definition file.
     *
     * @author Spaceribs
     */
    private static class OreVein {

        private Material material = Material.GRAVEL;
        private int iterations = 1;
        private int amount = 1;
        private int maxHeight = 1;

        public OreVein(Material mat, int iter, int a, int max) {
            material = mat;
            iterations = iter;
            amount = a;
            maxHeight = max;
        }

        public Material getMaterial() {
            return material;
        }

        public int getIterations() {
            return iterations;
        }

        public int getAmount() {
            return amount;
        }

        public int getMaxHeight() {
            return maxHeight;
        }
    }

    public class WorldGenMinable {

        /** The number of blocks to generate. */
        private int numberOfBlocks;

        /** The block ID of the ore to be placed using this generator. */
        private Material oreBlock;

        public WorldGenMinable(int num, Material oreBlock) {
            this.numberOfBlocks = num;
            this.oreBlock = oreBlock;
        }

        public boolean generate(World world, Random random, int x, int y, int z)
        {
            float var6 = random.nextFloat() * (float)Math.PI;
            double var7 = ((float)(x + 8) + Math.sin(var6) * (float)this.numberOfBlocks / 8.0F);
            double var9 = ((float)(x + 8) - Math.sin(var6) * (float)this.numberOfBlocks / 8.0F);
            double var11 = ((float)(z + 8) + Math.cos(var6) * (float)this.numberOfBlocks / 8.0F);
            double var13 = ((float)(z + 8) - Math.cos(var6) * (float)this.numberOfBlocks / 8.0F);
            double var15 = (double)(y + random.nextInt(3) - 2);
            double var17 = (double)(y + random.nextInt(3) - 2);

            for (int var19 = 0; var19 <= this.numberOfBlocks; ++var19)
            {
                double var20 = var7 + (var9 - var7) * (double)var19 / (double)this.numberOfBlocks;
                double var22 = var15 + (var17 - var15) * (double)var19 / (double)this.numberOfBlocks;
                double var24 = var11 + (var13 - var11) * (double)var19 / (double)this.numberOfBlocks;
                double var26 = random.nextDouble() * (double)this.numberOfBlocks / 16.0D;
                double var28 = (Math.sin((float)var19 * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * var26 + 1.0D;
                double var30 = (Math.sin((float)var19 * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * var26 + 1.0D;
                int var32 = (int)Math.floor(var20 - var28 / 2.0D);
                int var33 = (int)Math.floor(var22 - var30 / 2.0D);
                int var34 = (int)Math.floor(var24 - var28 / 2.0D);
                int var35 = (int)Math.floor(var20 + var28 / 2.0D);
                int var36 = (int)Math.floor(var22 + var30 / 2.0D);
                int var37 = (int)Math.floor(var24 + var28 / 2.0D);

                for (int var38 = var32; var38 <= var35; ++var38)
                {
                    double var39 = ((double)var38 + 0.5D - var20) / (var28 / 2.0D);

                    if (var39 * var39 < 1.0D)
                    {
                        for (int var41 = var33; var41 <= var36; ++var41)
                        {
                            double var42 = ((double)var41 + 0.5D - var22) / (var30 / 2.0D);

                            if (var39 * var39 + var42 * var42 < 1.0D)
                            {
                                for (int var44 = var34; var44 <= var37; ++var44)
                                {
                                    double var45 = ((double)var44 + 0.5D - var24) / (var28 / 2.0D);

                                    Block block = world.getBlockAt(var38, var41, var44);
                                    if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D
                                        && block.getType() == Material.STONE)
                                    {
                                        block.setType(oreBlock);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    /* Material, Iterations, Amount, maxHeight */
    private static final OreVein[] veins = new OreVein[]{
        new OreVein(Material.GRAVEL, 10, 32, 64),
        new OreVein(Material.COAL_ORE, 10, 32, 64),
        new OreVein(Material.IRON_ORE, 10, 16, 64),
        new OreVein(Material.GOLD_ORE, 2, 8, 32),
        new OreVein(Material.REDSTONE_ORE, 8, 7, 32),
        new OreVein(Material.DIAMOND_ORE, 1, 7, 16),
        new OreVein(Material.LAPIS_ORE, 1, 6, 32)
    };

    /**
     * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World, java.util.Random, org.bukkit.Chunk)
     */
    @Override
    public void populate(World world, Random random, Chunk source) {
        for (OreVein vein : veins) {
            for (int j = 0; j < vein.getIterations(); j++) {
                internal(source, random, random.nextInt(16), random.nextInt(vein.getMaxHeight()), random.nextInt(16), vein);
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
            if (y > 64 || y < 0) {
                continue;
            }

            Block block = source.getBlock(x, y, z);
            if (block.getType() == Material.STONE) {
                block.setType(vein.getMaterial());
            }
        }
    }
}
