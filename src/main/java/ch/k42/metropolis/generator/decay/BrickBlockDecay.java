package ch.k42.metropolis.generator.decay;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public class BrickBlockDecay implements BlockDecay {
    @Override
    public void decay(Block block, Random random) {
        if (random.nextBoolean()) {
            block.setTypeIdAndData(Material.BRICK_STAIRS.getId(), (byte) random.nextInt(4), true);
        }
    }

    @Override
    public Material getMaterial() {
        return Material.BRICK;
    }
}
