package ch.k42.metropolis.generator.decay;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public class WoodenDoorBlockDecay implements BlockDecay {
    @Override
    public void decay(Block block, Random random) {
        if (Material.WOODEN_DOOR.equals(block.getRelative(0, 1, 0).getType())) {
            if (random.nextInt(100) < 80) {
                byte data = block.getData();
                data ^= 4;
                block.setData(data);
            }
        }
    }

    @Override
    public Material getMaterial() {
        return Material.WOODEN_DOOR;
    }
}
