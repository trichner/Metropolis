package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.LootType;
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

    public class SchematicSpawner{
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

    private int GroundLevelY = 1;
    private int OddsOfAppearanceInPercent = 100;
    private LootType StandardChestName = LootType.INDUSTRIAL;
    private int ChestOddsInPercent = 50;
    private int[] ChestLevelWeights = {20,20,20,20,20};
    private SchematicSpawner[] Spawners;
    private int SpawnerOddsInPercent=50;
    private int DecayIntensityInPercent=100;
    private boolean NeedsRoad = false;
    private Direction EntranceFacing= Direction.NORTH;
    private ContextType[] Context={ContextType.HIGHRISE,ContextType.INDUSTRIAL,ContextType.PARK};

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
        return GroundLevelY;
    }

    /**
     * The odds of a schematic actually appearing if it can
     * @return odds in percent, [0,100]
     */
    public int getOddsOfAppearance() {
        return OddsOfAppearanceInPercent;
    }

    public LootType getStandardChestName() {
        return StandardChestName;
    }

    /**
     * The odds of a chest actually appearing if it can
     * @return odds in percent, [0,100]
     */
    public int getChestOdds() {
        return ChestOddsInPercent;
    }

    public SchematicSpawner[] getSpawners() {
        return Spawners;
    }

    /**
     * The odds of a spawner actually appearing if it can
     * @return odds in percent, [0,100]
     */
    public int getSpawnerOdds() {
        return SpawnerOddsInPercent;
    }

    /**
     * The custom decay options for this schematic
     * @return DecayOption according to this schematic
     */
    public DecayOption getDecayOption() {

        double intensity = DecayIntensityInPercent/100.0;

        //sanitize
        if(intensity>2) intensity=1;
        else if(intensity<0) intensity =0;

        return new DecayOption(intensity);
    }

    public List<ContextType> getContext() {
        return Arrays.asList(Context);
    }

    public Direction getDirection() {
        return EntranceFacing;
    }

    public boolean getNeedsRoad() {
        return NeedsRoad;
    }

    public void setGroundLevelY(int groundLevelY) {
        GroundLevelY = groundLevelY;
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
