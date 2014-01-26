package ch.k42.metropolis.model.provider;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.DecayOption;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

/**
 * Provides decay to area of blocks.
 * Originally written by spaceribs for CityWorld.
 *
 * @author spaceribs, Thomas Richner
 */
public class DecayProviderNormal extends DecayProvider {

    public DecayProviderNormal(MetropolisGenerator generator, Random random) {
        super(generator, random);
    }

    public void destroyWithin(int x1, int x2, int y1, int y2, int z1, int z2, DecayOption options) {

        if (y1 < 0) y1 = 0;
        if (y2 < 0) y2 = 0;

        int MAX = Constants.WORLD_HEIGHT;
        if (y1 > MAX) y1 = MAX;
        if (y2 > MAX) y2 = MAX;

        double holeScale = options.getHoleScale();
        double leavesScale = options.getLeavesScale();
        double fulldecay = options.getFullDecay();
        double partialdecay = options.getPartialDecay();
        double leavesdecay = options.getLeavesdecay();

        World world = generator.getWorld();

        long seed = generator.getWorldSeed();
        SimplexOctaveGenerator noiseGen = new SimplexOctaveGenerator(seed, 2);

        for (int z = z1; z < z2; z++) {
            for (int x = x1; x < x2; x++) {
                for (int y = y1; y < y2; y++) {

                    double holeNoise = noiseGen.noise(x * holeScale, y * holeScale, z * holeScale, 0.3D, 0.6D, true);
                    double leavesNoise = noiseGen.noise(x * leavesScale, y * leavesScale, z * leavesScale, 0.3D, 0.6D, false);

                    Block block = world.getBlockAt(x, y, z);

                    if (options.getExceptions().contains(block.getType())) { // do we ignore this type of block?
                        continue;
                    }

                    if (!block.isEmpty() && isValid(block) && (holeNoise > fulldecay)) {
                        block.setType(Material.AIR);
                    } else if (isValid(block) && holeNoise > partialdecay) {
                        switch (block.getType()) { //TODO too many hardcoded values
                            case STONE:
                                if (random.nextInt(100) < 40) break; // 20% happens nothing
                                if (random.nextBoolean())
                                    block.setType(Material.COBBLESTONE);
                                else
                                    block.setType(Material.MOSSY_COBBLESTONE);
                                break;
                            case SANDSTONE:
                                if (random.nextBoolean()) break; // not too much stairs
                                block.setTypeIdAndData(Material.SANDSTONE_STAIRS.getId(), (byte) random.nextInt(4), true);
                                break;
                            case BRICK:
                                if (random.nextBoolean()) break; // not too much stairs
                                block.setTypeIdAndData(Material.BRICK_STAIRS.getId(), (byte) random.nextInt(4), true);
                                break;
                            case COBBLESTONE:
                                block.setTypeIdAndData(Material.MOSSY_COBBLESTONE.getId(), (byte) random.nextInt(4), true);
                                break;
                            case SMOOTH_BRICK:
                                block.setTypeIdAndData(Material.SMOOTH_BRICK.getId(), (byte) random.nextInt(3), true);
                                break;
                            case WOOD:
                                if (random.nextBoolean()) break; // not too much stairs
                                switch (block.getData()) {
                                    case 0:
                                        block.setTypeIdAndData(Material.WOOD_STAIRS.getId(), (byte) random.nextInt(4), true);
                                        break;
                                    case 1:
                                        block.setTypeIdAndData(Material.SPRUCE_WOOD_STAIRS.getId(), (byte) random.nextInt(4), true);
                                        break;
                                    case 2:
                                        block.setTypeIdAndData(Material.BIRCH_WOOD_STAIRS.getId(), (byte) random.nextInt(4), true);
                                        break;
                                    default:
                                        block.setTypeIdAndData(Material.JUNGLE_WOOD_STAIRS.getId(), (byte) random.nextInt(4), true);
                                        break;
                                }
                                break;
                            default:
                                block.setType(Material.AIR);
                                break;
                        }

                        Block[] neighbors = {
                                block.getRelative(0, 1, 0),
                                block.getRelative(0, 0, -1),
                                block.getRelative(0, 0, 1),
                                block.getRelative(1, 0, 0),
                                block.getRelative(-1, 0, 0),
                                block.getRelative(0, -1, 0)
                        };

                        if (leavesNoise > leavesdecay && holeNoise > partialdecay && block.isEmpty()) {
                            int support = 0;

                            for (int n = 0; n < neighbors.length; n++)
                                support += this.isSupporting(neighbors[n]) ? 1 : 0;

                            if (support > 0)
                                block.setTypeIdAndData(Material.LEAVES.getId(), (byte) random.nextInt(4), true);
                        }

                        if (block.isEmpty() && random.nextBoolean() && !neighbors[5].isEmpty()) {

                            byte vineMeta = 0;

                            if (!neighbors[1].isEmpty() && neighbors[1].getType() != Material.VINE)
                                vineMeta += 4;
                            if (!neighbors[2].isEmpty() && neighbors[2].getType() != Material.VINE)
                                vineMeta += 1;
                            if (!neighbors[3].isEmpty() && neighbors[3].getType() != Material.VINE)
                                vineMeta += 8;
                            if (!neighbors[4].isEmpty() && neighbors[4].getType() != Material.VINE)
                                vineMeta += 2;

                            if (vineMeta > 0)
                                block.setTypeIdAndData(Material.VINE.getId(), vineMeta, true);
                        }
                    }
                }
            }
        }
    }

    protected boolean isValid(Block block) {
        return (
                block.getType() != Material.GRASS
                        //&& block.getType() != Material.DIRT
                        && block.getType() != Material.LEAVES
                        && block.getType() != Material.LOG
        );
    }

    protected boolean isSupporting(Block block) {
        return (
                block.getType() != Material.LEAVES
                        && block.getType() != Material.VINE
                        && block.getType() != Material.LOG
                        && !block.isEmpty()
        );
    }
}
