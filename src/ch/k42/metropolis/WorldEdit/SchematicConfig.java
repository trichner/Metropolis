package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.LootType;
import ch.k42.metropolis.model.enums.RoadType;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 27.09.13
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public class SchematicConfig {

    private class SchematicSpawner{
        private EntityType Type = EntityType.ZOMBIE;
        private int Weight = 10;

        private transient int threshold=-1;

        public SchematicSpawner(EntityType type, int weight) {
            Type = type;
            Weight = weight;
        }

        public EntityType getType() {
            return Type;
        }

        public int getWeight() {
            return Weight;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }
    }

    private class SchematicLoot {
        private LootType lootType = LootType.RESIDENTIAL;
        private int minLevel = 0;
        private int maxLevel = 5;

        public SchematicLoot(LootType lootType, int minLevel, int maxLevel) {
            this.lootType = lootType;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }
    }

    private int groundLevelY = 1;
    private int oddsOfAppearanceInPercent = 100;
    private LootType standardChestName = LootType.INDUSTRIAL;
    private int chestOddsInPercent = 50;
    private SchematicSpawner[] Spawners;
    private int spawnerOddsInPercent =50;
    private int decayIntensityInPercent =100;
    private boolean needsRoad = false;

    private Direction entranceFacing = Direction.NORTH;
    private ContextType[] context ={ContextType.HIGHRISE,ContextType.INDUSTRIAL,ContextType.PARK};
    private RoadType roadType = RoadType.ROAD_X;

    public SchematicConfig() {
        initDefaultSpawners();
    }

    private void initDefaultSpawners(){
        Spawners = new SchematicSpawner[3];
        Spawners[0] = new SchematicSpawner(EntityType.ZOMBIE,60);
        Spawners[1] = new SchematicSpawner(EntityType.CREEPER,20);
        Spawners[2] = new SchematicSpawner(EntityType.SKELETON,20);
    }

    public int getGroundLevelY() {
        return groundLevelY;
    }

    /**
     * The odds of a schematic actually appearing if it can
     * @return odds in percent, [0,100]
     */
    public int getOddsOfAppearance() {
        return oddsOfAppearanceInPercent;
    }

    public LootType getStandardChestName() {
        return standardChestName;
    }

    /**
     * The odds of a chest actually appearing if it can
     * @return odds in percent, [0,100]
     */
    public int getChestOdds() {
        return chestOddsInPercent;
    }

    public SchematicSpawner[] getSpawners() {
        return Spawners;
    }

    /**
     * The odds of a spawner actually appearing if it can
     * @return odds in percent, [0,100]
     */
    public int getSpawnerOdds() {
        return spawnerOddsInPercent;
    }

    /**
     * The custom decay options for this schematic
     * @return DecayOption according to this schematic
     */
    public DecayOption getDecayOption() {

        double intensity = decayIntensityInPercent /100.0;

        //sanitize
        if(intensity>2) intensity=1;
        else if(intensity<0) intensity =0;

        return new DecayOption(intensity);
    }

    public List<ContextType> getContext() {
        return Arrays.asList(context);
    }

    public Direction getDirection() {
        return entranceFacing;
    }

    public boolean getNeedsRoad() {
        return needsRoad;
    }

    public void setGroundLevelY(int groundLevelY) {
        this.groundLevelY = groundLevelY;
    }

    public RoadType getRoadType() {
        return roadType;
    }

    //================= Spawner random

    private transient int cachedSpawnerLevelSum = -1;
    private transient boolean cachedSpawner = false;

    private void cacheSpawnerThresholds(){
        if(Spawners==null||Spawners.length==0){
            initDefaultSpawners();
        }
        Spawners[0].setThreshold(Spawners[0].getWeight());
        SchematicSpawner current,previous=Spawners[0];
        for(int i=1;i<Spawners.length;i++){
            current=Spawners[i];
            current.setThreshold(previous.getThreshold()+current.getWeight());
            previous=current;
        }
        cachedSpawnerLevelSum=previous.getThreshold();
        cachedSpawner=true;
    }

    public int getSpawnerEntityWeightSum(){
        if(!cachedSpawner)
            cacheSpawnerThresholds();
        return cachedSpawnerLevelSum;
    }

    /**
     * Selects a random EntityType
     * @param random a number between [0,SUM]
     * @return
     */
    public EntityType getRandomSpawnerEntity(int random){
        if(!cachedSpawner)
            cacheSpawnerThresholds();

        for(int i=0;i<Spawners.length;i++){
            if(random<Spawners[i].getThreshold())
                return Spawners[i].getType();
        }
        Bukkit.getLogger().warning("No EntityType for Spawner could be randomly determinated, random argument too low?");
        return Spawners[0].getType();
    }


}
