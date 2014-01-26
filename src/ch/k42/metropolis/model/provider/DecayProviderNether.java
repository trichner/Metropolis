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
public class DecayProviderNether extends DecayProvider {

    public DecayProviderNether(MetropolisGenerator generator, Random random) {
        super(generator, random);
    }

    /**
     * Destroys an area with custom decay scale
     * @param x1 start x coordinate
     * @param x2 end x coordinate
     * @param y1 start y coordinate
     * @param y2 end y coordinate
     * @param z1 start z coordinate
     * @param z2 end z coordinate
     * @param options decay options
     */
    public void destroyWithin(int x1, int x2, int y1, int y2, int z1, int z2, DecayOption options) {

        if(y1<0) y1=0;
        if(y2<0) y2=0;

        int MAX = Constants.WORLD_HEIGHT;
        if(y1>MAX) y1=MAX;
        if(y2>MAX) y2=MAX;

        double holeScale = options.getHoleScale();
        double fulldecay = options.getFullDecay() - 0.4;
        double partialdecay = options.getPartialDecay() - 0.4;

        World world = generator.getWorld();

        long seed = generator.getWorldSeed();
        SimplexOctaveGenerator noiseGen = new SimplexOctaveGenerator(seed,2);

        for(int z=z1;z<z2;z++){
            for(int x=x1;x<x2;x++){
                for(int y=y1;y<y2;y++) {

                    double holeNoise = noiseGen.noise(x * holeScale, y * holeScale, z * holeScale, 0.3D, 0.6D, true);

                    Block block = world.getBlockAt(x, y, z);

                    if(options.getExceptions().contains(block.getType())){ // do we ignore this type of block?
                        continue;
                    }

                    if (!block.isEmpty() && ( holeNoise > fulldecay ) ) {
                        block.setType(Material.AIR);
                    } else if ( isValid(block) && holeNoise > partialdecay ) {
                        switch(block.getType()) { //TODO too many hardcoded values
                            case STONE:
                            case SANDSTONE:
                            case COBBLESTONE:
                            case CLAY_BRICK:
                                if(random.nextInt(100)<40) break; // 40% happens nothing
                                block.setType(Material.NETHERRACK);
                                break;
                            case BRICK:
                            case SMOOTH_BRICK:
                                if(random.nextInt(100)<40) break; // 40% happens nothing
                                block.setType(Material.NETHER_BRICK);
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

                        if ((block.getType().isBurnable() || block.getType() == Material.NETHERRACK) && neighbors[0].isEmpty() && random.nextBoolean()) {
                            neighbors[0].setType(Material.FIRE);
                        }

                        if ( block.isEmpty() && !neighbors[5].isEmpty() ) {
                            int prob = 0;
                            for (int i = 0; i < neighbors.length; i++) {
                                prob += neighbors[i].isEmpty() ? 1 : 0;
                            }
                            if(random.nextInt(500)<prob) {
                                block.setType(Material.LAVA);
                            }
                        }
                    }
                }
            }
        }
    }
}
