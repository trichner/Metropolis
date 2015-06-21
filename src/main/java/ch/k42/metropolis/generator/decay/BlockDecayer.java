package ch.k42.metropolis.generator.decay;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public class BlockDecayer {

    private Map<Material, BlockDecay> blockDecay = new HashMap<>();

    public BlockDecayer() {
        List<BlockDecay> blockDecayList = new ArrayList<>();

        blockDecayList.add(new BrickBlockDecay());
        blockDecayList.add(new CobbleBlockDecay());
        blockDecayList.add(new SandstoneBlockDecay());
        blockDecayList.add(new SmoothBrickBlockDecay());
        blockDecayList.add(new StoneBlockDecay());
        blockDecayList.add(new WoodBlockDecay());
        blockDecayList.add(new WoodenDoorBlockDecay());

        blockDecay = blockDecayList.stream().collect(Collectors.toMap(b -> b.getMaterial(), b -> b));
    }

    public void decay(Block block, Random random) {
        if (blockDecay.containsKey(block.getType())) {
            blockDecay.get(block.getType()).decay(block,random);
        }else {
            block.setType(Material.AIR);
        }
    }
}
