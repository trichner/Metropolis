package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.model.enums.LootType;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 30.09.13
 * Time: 00:00
 * To change this template use File | Settings | File Templates.
 */
public class SchematicLoot {
    private LootType lootType = LootType.RESIDENTIAL;
    private int minLevel = 0;
    private int maxLevel = 5;

    public SchematicLoot(LootType lootType, int minLevel, int maxLevel) {
        this.lootType = lootType;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }
}
