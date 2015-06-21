package ch.k42.metropolis.generator.decay;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public class SmoothBrickBlockDecay implements BlockDecay {
    @Override
    public void decay(Block block, Random random) {
        block.setTypeIdAndData(Material.SMOOTH_BRICK.getId(), (byte) random.nextInt(3), true);
    }

    @Override
    public Material getMaterial() {
        return Material.SMOOTH_BRICK;
    }
}
