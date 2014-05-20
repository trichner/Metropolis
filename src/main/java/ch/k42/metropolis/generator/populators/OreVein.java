package ch.k42.metropolis.generator.populators;

import org.bukkit.Material;

/**
 * Created by aaronbrewer on 2/4/14.
 */
public class OreVein {

    private Material ore;
    private int amount;
    private int num;
    private double range;
    private double offset;

    public OreVein (Material ore, int amount, int num, double range, double offset) {
        this.ore = ore;
        this.amount = amount;
        this.num = num;
        this.range = range;
        this.offset = offset;
    }

    public OreVein (String ore, int amount, int num, double range, double offset) {
        this(Material.getMaterial(ore), amount, num, range, offset);
    }

    public int getNum(){
        return num;
    }

    public Material getOre() {
        return ore;
    }

    public int getAmount() {
        return amount;
    }

    public int getRange() {
        return (int)(OrePopulator.oreLevel * range);
    }

    public int getOffset() {
        return (int)(OrePopulator.oreLevel * offset);
    }

    @Override
    public String toString() {
        return "OreVein{" +
                "ore=" + ore +
                ", amount=" + amount +
                ", num=" + num +
                ", range=" + range +
                ", offset=" + offset +
                '}';
    }
}
