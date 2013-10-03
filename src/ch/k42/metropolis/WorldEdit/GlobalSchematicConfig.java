package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.LootType;
import ch.k42.metropolis.model.enums.RoadType;
import org.bukkit.entity.EntityType;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 03.10.13
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class GlobalSchematicConfig {
    private LootType[] supportedLootTypes;
    private ContextType[] supportedContextTypes;
    private EntityType[] supportedSpawnerEntities;
    private RoadType[] supportedRoadTypes;
    private int[] chestLevelWeights = {20,20,20,20,20};
    private boolean enableGroundLevelEstimation = true;

    public GlobalSchematicConfig() {
        supportedLootTypes = LootType.values();
        supportedContextTypes = ContextType.values();
        supportedSpawnerEntities = EntityType.values();
        supportedRoadTypes = RoadType.values();
    }

    public boolean isEstimationOn() {
        return enableGroundLevelEstimation;
    }

    public int getChestLevelWeight(int i){
        if(i<0||i>(chestLevelWeights.length-1))
            return 20;
        return chestLevelWeights[i];
    }

    private transient int chestThreshold2 = -1;
    private transient int chestThreshold3 = -1;
    private transient int chestThreshold4 = -1;
    private transient int cachedChestLevelSum = -1;
    private transient boolean cachedChest = false;

    private void cacheChestThresholds(){
        chestThreshold2 = chestLevelWeights[0]+ chestLevelWeights[1];
        chestThreshold3 = chestThreshold2 + chestLevelWeights[2];
        chestThreshold4 = chestThreshold3 + chestLevelWeights[3];
        cachedChestLevelSum = chestThreshold4 + chestLevelWeights[4];
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
        if(random< chestLevelWeights[0])
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
