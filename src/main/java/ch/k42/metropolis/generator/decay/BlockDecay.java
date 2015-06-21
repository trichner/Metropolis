package ch.k42.metropolis.generator.decay;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public interface BlockDecay {
    void decay(Block block,Random random);
    Material getMaterial();
}
