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
public class SchematicConfig extends AbstractSchematicConfig{

    private int groundLevelY = 1;
    private int oddsOfAppearanceInPercent = 100;
    private LootType[] lootCollections={LootType.RESIDENTIAL};
    private int lootMinLevel=1;
    private int lootMaxLevel=5;
    private boolean rotate = true;
    private String rotateScript = "saferotate.js";

    private int chestOddsInPercent = 50;
    private int spawnerOddsInPercent =50;
    private int decayIntensityInPercent =100;

    private RoadCutout[] cutouts = {};

    private Direction entranceFacing = Direction.NONE;
    private ContextType[] context ={ContextType.HIGHRISE,ContextType.INDUSTRIAL,ContextType.PARK};
    private RoadType roadType = RoadType.STREET_X;
    private Set<Material> decayExceptionMaterials = new HashSet();
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

        double intensity = decayIntensityInPercent / 100.0;

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

    public boolean getRotate() {
        return rotate;
    }

    public String getRotateScript() {
        return rotateScript;
    }

    public RoadCutout[] getCutouts() {
        return cutouts;
    }


}
