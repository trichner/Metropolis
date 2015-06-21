package ch.k42.metropolis.generator.decay;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public class CobbleBlockDecay implements BlockDecay {
    @Override
    public void decay(Block block, Random random) {
        if (random.nextDouble() > 0.3) {
            block.setTypeIdAndData(Material.MOSSY_COBBLESTONE.getId(), (byte) random.nextInt(4), true);
        }else {
            block.setTypeIdAndData(Material.COBBLESTONE_STAIRS.getId(), (byte) random.nextInt(8), true);
        }
    }

    @Override
    public Material getMaterial() {
        return Material.COBBLESTONE;
    }
}
