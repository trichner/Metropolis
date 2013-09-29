package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.ContextType;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 27.09.13
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public class SchematicsConfig {
    private int GroundLevelY = 1;
    private int OddsOfAppearanceInPercent = 100;
    //private final static String tagBroadcastLocation = "BroadcastLocation";
    private String ChestName= "Chest";
    private int ChestOddsInPercent = 50;
    private String SpawnerType= "ZOMBIE"; //unused
    private int SpawnerOddsInPercent=50;
    private int DecayIntensityInPercent=100;
    private boolean NeedsRoad = false;
    /**
     * Possible values: north, south, west, east
     */
    private Direction EntranceFacing= Direction.NORTH;
    /**
     * Possible values: road, highrise,midrise,neighbourhood,industrial,farm,park,undefined
     */
    private ContextType[] Context={ContextType.HIGHRISE,ContextType.INDUSTRIAL,ContextType.PARK};


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

    public String getChestName() {
        return ChestName;
    }

    /**
     * The odds of a chest actually appearing if it can
     * @return odds in percent, [0,100]
     */
    public int getChestOdds() {
        return ChestOddsInPercent;
    }

    public String getSpawnerType() {
        return SpawnerType;
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
