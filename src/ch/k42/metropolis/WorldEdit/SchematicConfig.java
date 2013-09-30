package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.LootType;
import ch.k42.metropolis.model.enums.SpawnerType;

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
        private SpawnerType Type = SpawnerType.ZOMBIE;
        private int Weight = 10;

        public SchematicSpawner(SpawnerType type, int weight) {
            Type = type;
            Weight = weight;
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
        Spawners = new SchematicSpawner[3];
        Spawners[0] = new SchematicSpawner(SpawnerType.ZOMBIE,60);
        Spawners[1] = new SchematicSpawner(SpawnerType.CREEPER,20);
        Spawners[2] = new SchematicSpawner(SpawnerType.SKELETON,20);
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
}
