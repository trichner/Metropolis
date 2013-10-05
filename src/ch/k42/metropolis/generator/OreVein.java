package ch.k42.metropolis.generator;

import org.bukkit.Material;

/**
 * Vein Definition file.
 * @author Spaceribs
 */
public class OreVein {

    private Material material = Material.GRAVEL;
    private int iterations = 1;
    private int amount = 1;
    private int maxHeight = 1;

    public OreVein(Material mat, int iter, int a, int max) {
        material = mat;
        iterations = iter;
        amount = a;
        maxHeight = max;
    }

    public Material getMaterial () {
        return material;
    }

    public int getIterations () {
        return iterations;
    }

    public int getAmount () {
        return amount;
    }

    public int getMaxHeight () {
        return maxHeight;
    }
}
