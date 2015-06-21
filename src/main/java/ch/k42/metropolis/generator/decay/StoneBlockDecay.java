package ch.k42.metropolis.generator.decay;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public class StoneBlockDecay implements BlockDecay {
    @Override
    public void decay(Block block, Random random) {
        if (random.nextInt(100) > 40) { // 40% happens nothing
            if (random.nextBoolean()){
                block.setType(Material.COBBLESTONE);
            }else{
                block.setType(Material.MOSSY_COBBLESTONE);
            }
        }
    }

    @Override
    public Material getMaterial() {
        return Material.STONE;
    }
}
