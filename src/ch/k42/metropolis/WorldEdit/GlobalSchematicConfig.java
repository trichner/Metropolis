package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.LootType;
import org.bukkit.entity.EntityType;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 03.10.13
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class GlobalSchematicConfig {
    private LootType[] SupportedLootTypes;
    private ContextType[] SupportedContextTypes;
    private EntityType[] SupportedSpawnerEntities;
    private int[] ChestLevelWeights = {20,20,20,20,20};
    private boolean EnableGroundLevelEstimation = true;

    public GlobalSchematicConfig() {
        SupportedLootTypes = LootType.values();
        SupportedContextTypes = ContextType.values();
        SupportedSpawnerEntities = EntityType.values();
    }

    public boolean isEstimationOn() {
        return EnableGroundLevelEstimation;
    }

    public int getChestLevelWeight(int i){
        if(i<0||i>(ChestLevelWeights.length-1))
            return 20;
        return ChestLevelWeights[i];
    }

    private transient int chestThreshold2 = -1;
    private transient int chestThreshold3 = -1;
    private transient int chestThreshold4 = -1;
    private transient int cachedChestLevelSum = -1;
    private transient boolean cachedChest = false;

    private void cacheChestThresholds(){
        chestThreshold2 = ChestLevelWeights[0]+ChestLevelWeights[1];
        chestThreshold3 = chestThreshold2 + ChestLevelWeights[2];
        chestThreshold4 = chestThreshold3 + ChestLevelWeights[3];
        cachedChestLevelSum = chestThreshold4 +ChestLevelWeights[4];
        cachedChest = true;
    }

    /**
     *
     * @return
     */
    public int getChestLevelWeightSum(){
        if(!cachedChest) cacheChestThresholds();
        return cachedChestLevelSum;
    }


    /**
     * Gives back a weighted random level [1,5]
     * @param random a random between 0 and ChestLevelWeightSum [0,SUM]
     * @return the random level [1,5]
     */
    public int getRandomChestLevel(int random){
        if(!cachedChest) cacheChestThresholds();
        if(random<ChestLevelWeights[0])
            return 1;

        if(random< chestThreshold2)
            return 2;

        if(random< chestThreshold3)
            return 3;

        if(random< chestThreshold4)
            return 4;

        return 5;
    }
}
