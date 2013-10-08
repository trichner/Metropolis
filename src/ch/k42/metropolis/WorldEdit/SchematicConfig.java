package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.LootType;
import ch.k42.metropolis.model.enums.RoadType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public class RoadCutout {
        public final int startPoint;
        public final int length;
        private RoadCutout(int lengthStart, int lengthEnd) {
            this.startPoint = lengthStart;
            this.length = lengthEnd;
        }
    }

    private int groundLevelY = 1;
    private int oddsOfAppearanceInPercent = 100;
    private LootType[] lootCollections={LootType.STORE,LootType.OFFICE,LootType.INDUSTRIAL,LootType.RESIDENTIAL};
    private int lootMinLevel=1;
    private int lootMaxLevel=5;

    private int chestOddsInPercent = 50;
    private SchematicSpawner[] Spawners = {new SchematicSpawner(EntityType.ZOMBIE,60),new SchematicSpawner(EntityType.CREEPER,20),new SchematicSpawner(EntityType.SKELETON,20)};
    private int spawnerOddsInPercent =50;
    private int decayIntensityInPercent =100;

    private RoadCutout[] cutouts = {};

    private Direction entranceFacing = Direction.NORTH;
    private ContextType[] context ={ContextType.HIGHRISE,ContextType.INDUSTRIAL,ContextType.PARK};
    private RoadType roadType = RoadType.ROAD_X;
    private Set<Material> decayExceptionMaterials = new HashSet<>();
    public SchematicConfig() {}



    public int getGroundLevelY() {
        return groundLevelY;
    }

    public int getLootMaxLevel() {
        return lootMaxLevel;
    }

    public int getLootMinLevel() {
        return lootMinLevel;
    }

    /**
     * The odds of a schematic actually appearing if it can
     * @return odds in percent, [0,100]
     */
    public int getOddsOfAppearance() {
        return oddsOfAppearanceInPercent;
    }

    public LootType[] getLootCollections() {
        return lootCollections;
    }

    public LootType getRandomLootCollection(GridRandom random){
        return lootCollections[random.getRandomInt(lootCollections.length)];
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

        return new DecayOption(intensity,decayExceptionMaterials);
    }

    public List<ContextType> getContext() {
        return Arrays.asList(context);
    }

    public Direction getDirection() {
        return entranceFacing;
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
            //initDefaultSpawners();
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

    private int getSpawnerEntityWeightSum(){
        if(!cachedSpawner)
            cacheSpawnerThresholds();
        return cachedSpawnerLevelSum;
    }

    /**
     * Selects a random EntityType
     * @param rand a number between [0,SUM]
     * @return
     */
    public EntityType getRandomSpawnerEntity(GridRandom rand){
        if(!cachedSpawner)
            cacheSpawnerThresholds();
        int random = rand.getRandomInt(getSpawnerEntityWeightSum());
        for(int i=0;i<Spawners.length;i++){
            if(random<Spawners[i].getThreshold())
                return Spawners[i].getType();
        }
        org.bukkit.Bukkit.getLogger().warning("No EntityType for Spawner could be randomly determinated, random argument too low?");
        return Spawners[0].getType();
    }

    public RoadCutout[] getCutouts() {
        return cutouts;
    }
}
