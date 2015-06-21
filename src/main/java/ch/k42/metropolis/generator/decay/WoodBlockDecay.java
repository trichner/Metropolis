package ch.k42.metropolis.generator.decay;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public class WoodBlockDecay implements BlockDecay {
    @Override
    public void decay(Block block,Random random) {
        if (random.nextBoolean()) return; // not too much stairs
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
    }

    @Override
    public Material getMaterial() {
        return Material.WOOD;
    }
}
