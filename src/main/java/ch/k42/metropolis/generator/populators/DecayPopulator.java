package ch.k42.metropolis.generator.populators;

import ch.k42.metropolis.generator.decay.BlockDecayer;
import ch.k42.metropolis.minions.DecayOption;
import ch.n1b.vector.Vec2D;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.material.Leaves;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public class DecayPopulator extends BlockPopulator {

    private static final int SAMPLE_RATE = 4;
    private static final int CHUNK_SIZE = 16;

    private BlockDecayer blockDecayer = new BlockDecayer();

    private TreeSpecies[] treeSpecies =  TreeSpecies.values();

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        Vec2D origin = new Vec2D(chunk.getX()<<4,chunk.getZ()<<4);
        HeightStatistics heightStatistics = heightStatistics(world,origin);

    }

    private HeightStatistics heightStatistics(World world, Vec2D chunk) {
        int min = world.getMaxHeight();
        int max = 0;
        int n=0;
        int sum = 0;
        for(int x=0;x<16;x+=SAMPLE_RATE){
            for (int z = 0; z < 16; z+=SAMPLE_RATE) {
                int height = world.getHighestBlockYAt(chunk.X + x, chunk.Y + z);
                sum += height;
                n++;
                min = Math.min(min, height);
                max = Math.max(max, height);
            }
        }

        int avg = (int) (sum /((double) n));

        return new HeightStatistics(min,avg, max);
    }

    private static Set<Material> unsupportingBlocks = ImmutableSet.of(
            Material.LEAVES,
            Material.LEAVES_2,
            Material.DOUBLE_PLANT,
            Material.GRASS,
            Material.LONG_GRASS,
            Material.VINE,
            Material.LOG,
            Material.LOG_2,
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LAVA,
            Material.STATIONARY_LAVA
    );

    private static Set<Material> nonDecayableBlocks = ImmutableSet.<Material>builder().addAll(unsupportingBlocks).build();

    public void destroyChunk(Random random, Chunk chunk, DecayOption
            options) {

        double holeScale = options.getHoleScale();
        double leavesScale = options.getLeavesScale();
        double fulldecay = options.getFullDecay();
        double partialdecay = options.getPartialDecay();
        double leavesdecay = options.getLeavesdecay();

        Vec2D origin = new Vec2D(chunk.getX()<<4,chunk.getZ()<<4);
        HeightStatistics heightStatistics = heightStatistics(chunk.getWorld(),origin);
        double scalingFactor;

        World world = chunk.getWorld();
        int maxHeight = chunk.getWorld().getMaxHeight();
        long seed = world.getSeed();
        SimplexOctaveGenerator simplexOctaveGenerator = new SimplexOctaveGenerator(seed, 2);

        for (int z = 0; z < CHUNK_SIZE; z++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                for (int y = 0; y < maxHeight; y++) { // TODO from street level?
                    Block block = chunk.getBlock(x, y, z);
                    scalingFactor = 0.5 + 0.5 *(y-heightStatistics.min)/(maxHeight-heightStatistics
                            .min); // TODO
                    // do we ignore this type of block? is it already empty?
                    if (!isDecayable(block) || !block.isEmpty() || !options.getExceptions().contains(block.getType())) {

                        double noise = simplexOctaveGenerator.noise(x * holeScale, y * holeScale, z * holeScale, 0.3D, 0.6D, true);

                        if (noise > fulldecay) {
                            block.setType(Material.AIR);
                        }else if (noise > partialdecay) {

                            // alter block
                           blockDecayer.decay(block,random);

                            // can we attach leaves?
                            if(isSupporting(block)){
                                for(Block n1 : getNeighbours(block)){
                                    // add them leaves
                                    addLeavesRec(random,n1,options,2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addLeavesRec(Random random, Block block, DecayOption option, int depth){
        // recursion done?
        if(depth>0){
            // block free?
            if(block.isEmpty()){
                // should we set a leaf?
                if(random.nextDouble()<option.getLeavesScale()) { // should we add leaves?
                    //set leaf here
                    Leaves leaf = getRandomLeaves(random);
                    block.setType(leaf.getItemType());
                    block.setData(leaf.getData());
                    //since this is now supporting, set leafes around
                    for (Block n2 : getNeighbours(block)) {
                        addLeavesRec(random,n2, option, depth - 1);
                    }
                }
            }
        }
    }



    private Leaves getRandomLeaves(Random random){
        return new Leaves(treeSpecies[random.nextInt(treeSpecies.length)]);
    }

    /**
     * Calculates all directly connected blocks, without
     * blocks that would cross chunk borders (prevent endless recursion)
     * @param block
     * @return at maximum 6 blocks, all facing 'block'
     */
    private List<Block> getNeighbours(Block block){
        List<Block> neighbours = new ArrayList<>(6);

        // block above
        if(block.getY()<(block.getWorld().getMaxHeight()-1)){
            neighbours.add(block.getRelative(0,1,0));
        }

        // block below
        if(block.getY()>0){
            neighbours.add(block.getRelative(0,-1,0));
        }

        //block in x direction

        // on chunk border?
        if(block.getX()%16!=0){
            neighbours.add(block.getRelative(-1,0,0));
        }

        if(block.getX()%16!=15){
            neighbours.add(block.getRelative(1,0,0));
        }

        // Z direction
        if(block.getZ()%16!=0){
            neighbours.add(block.getRelative(0,0,-1));
        }

        if(block.getZ()%16!=15){
            neighbours.add(block.getRelative(0,0,1));
        }

        return neighbours;
    }


    protected boolean isDecayable(Block block) {
        return !nonDecayableBlocks.contains(block.getType());
    }

    protected boolean isSupporting(Block block) {
        return !unsupportingBlocks.contains(block.getType()) && !block.isEmpty();
    }

    private static final class HeightStatistics{
        public final int min;
        public final int avg;
        public final int max;

        public HeightStatistics(int min, int avg, int max) {
            this.min = min;
            this.avg = avg;
            this.max = max;
        }
    }

}
