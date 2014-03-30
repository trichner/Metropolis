package ch.k42.metropolis.grid.urbanGrid.provider;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.DecayOption;
import com.google.common.collect.ImmutableSet;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.LongGrass;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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

                    if (block.getType() == Material.WOODEN_DOOR && block.getRelative(0, 1, 0).getType() == Material.WOODEN_DOOR) {
                        if (random.nextInt(100) < 80) {
                            byte data = block.getData();
                            data ^= 4;
                            block.setData(data);
                        }
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

                            for (int n = 0; n < neighbors.length; n++) {
                                if (n == 0) {
                                    double holeAboveNoise = noiseGen.noise(x * holeScale, (y+1) * holeScale, z * holeScale, 0.3D, 0.6D, true);
                                    support += this.isSupporting(neighbors[n], holeAboveNoise, fulldecay) ? 1 : 0;
                                } else if (n == 3) {
                                    double holeAsideNoise = noiseGen.noise((x+1) * holeScale, y * holeScale, z * holeScale, 0.3D, 0.6D, true);
                                    support += this.isSupporting(neighbors[n], holeAsideNoise, fulldecay) ? 1 : 0;
                                } else {
                                support += this.isSupporting(neighbors[n]) ? 1 : 0;
                                }
                            }

                            if (support == 1) {
                                if (random.nextBoolean())
                                    block.setTypeIdAndData(Material.LEAVES.getId(), (byte) (4 + random.nextInt(4)), true);
                            } else if (support > 2 && block.getLightLevel() > 7 && neighbors[0].isEmpty()) {
                                block.setType(Material.GRASS);
                                if (random.nextDouble() < 0.1) {
                                    world.generateTree(neighbors[0].getLocation(), TreeType.TREE);
                                } else {
                                    if (random.nextDouble() < 0.1) {
                                        neighbors[0].setTypeIdAndData(Material.RED_ROSE.getId(), (byte) random.nextInt(8), true);
                                    } else {
                                        LongGrass grass = new LongGrass();
                                        grass.setSpecies(GrassSpecies.NORMAL);
                                        neighbors[0].setType(grass.getItemType());
                                        neighbors[0].setData(grass.getData());
                                    }
                                }
                            } else if (support > 1) {
                                block.setTypeIdAndData(Material.LEAVES.getId(), (byte) (4 + random.nextInt(4)), true);
                            }
                        }

                        if (block.isEmpty() && random.nextBoolean() && !neighbors[5].isEmpty()) {

                            byte vineMeta = 0;

                            if (neighbors[1].getType().isSolid() && neighbors[1].getType() != Material.VINE)
                                vineMeta += 4;
                            if (neighbors[2].getType().isSolid() && neighbors[2].getType() != Material.VINE)
                                vineMeta += 1;
                            if (neighbors[3].getType().isSolid() && neighbors[3].getType() != Material.VINE)
                                vineMeta += 8;
                            if (neighbors[4].getType().isSolid() && neighbors[4].getType() != Material.VINE)
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
        return !invalidBlocks.contains(block.getType());
    }

    private static Set<Material> unsupportingBlocks = ImmutableSet.of(Material.LEAVES_2, Material.LEAVES, Material.DOUBLE_PLANT,Material.LONG_GRASS,Material.VINE,Material.LOG,Material.LOG_2,Material.WATER,Material.STATIONARY_WATER,Material.LAVA,Material.STATIONARY_LAVA);

    private static Set<Material> invalidBlocks = ImmutableSet.<Material>builder().addAll(unsupportingBlocks).add(Material.GRASS).build();

    protected boolean isSupporting(Block block) {
        return !unsupportingBlocks.contains(block.getType()) && !block.isEmpty();
    }

    protected boolean isSupporting(Block block, double holeAbove, double fullDecay) {
        return (isSupporting(block) && (holeAbove < fullDecay));
    }
}
