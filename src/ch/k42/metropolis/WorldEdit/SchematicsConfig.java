package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.minions.DecayOption;
import ch.k42.metropolis.minions.Direction;
import ch.k42.metropolis.model.ContextType;

import java.util.ArrayList;
import java.util.LinkedList;
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
    /**
     * Possible values: north, south, west, east
     */
    private String[] EntranceFacing={"north","south","west","east"};
    /**
     * Possible values: road, highrise,midrise,neighbourhood,industrial,farm,park,undefined
     */
    private String[] Context={"road","highrise","midrise","undefined"};


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

    public List<Direction> getDirections() {
        List<Direction> list = new LinkedList<Direction>();
        for(String s : EntranceFacing){
            Direction d = Direction.getByString(s);
            if(d!=null)
                list.add(d);
        }
        return list;
    }

    public List<ContextType> getContext() {
        List<ContextType> list = new LinkedList<ContextType>();
        for(String s : Context){
            ContextType d = ContextType.getByString(s);
            if(d!=null)
                list.add(d);
        }
        return list;
    }
}
