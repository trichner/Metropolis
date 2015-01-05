package ch.k42.metropolis.grid.urbanGrid.config;


import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.LootType;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.minions.Nimmersatt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 03.10.13
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class GlobalSchematicConfig extends AbstractSchematicConfig {
    private LootType[] supportedLootTypes = LootType.values();
    private ContextType[] supportedContextTypes = ContextType.values();
    private EntityType[] supportedSpawnerEntities = EntityType.values();
    private RoadType[] supportedRoadTypes = RoadType.values();

    private int[] chestLevelWeights = {20, 20, 20, 20, 20};

    private SchematicSpawner[] defaultSpawners = {new SchematicSpawner(EntityType.ZOMBIE, 60), new SchematicSpawner(EntityType.CREEPER, 20), new SchematicSpawner(EntityType.SKELETON, 20)};

    public GlobalSchematicConfig() {
        Spawners = defaultSpawners;
    }

    public int getChestLevelWeight(int i) {
        if (i < 0 || i > (chestLevelWeights.length - 1))
            return 20;
        return chestLevelWeights[i];
    }

    private transient int chestThreshold2 = -1;
    private transient int chestThreshold3 = -1;
    private transient int chestThreshold4 = -1;
    private transient int cachedChestLevelSum = -1;
    private transient boolean cachedChest = false;

    private void cacheChestThresholds() {
        chestThreshold2 = chestLevelWeights[0] + chestLevelWeights[1];
        chestThreshold3 = chestThreshold2 + chestLevelWeights[2];
        chestThreshold4 = chestThreshold3 + chestLevelWeights[3];
        cachedChestLevelSum = chestThreshold4 + chestLevelWeights[4];
        cachedChest = true;
    }

    /**
     * @return
     */
    private int getChestLevelWeightSum() {
        if (!cachedChest) cacheChestThresholds();
        return cachedChestLevelSum;
    }


    /**
     * Gives back a weighted random level [1,5]
     *
     * @param rand a random
     * @return the random level [1,5]
     */
    public int getRandomChestLevel(GridRandom rand) {
        int random = rand.getRandomInt(getChestLevelWeightSum());
        return getLevel(random);
    }

    private int getLevel(int random) {
        if (!cachedChest) cacheChestThresholds();
        if (random < chestLevelWeights[0])
            return 1;

        if (random < chestThreshold2)
            return 2;

        if (random < chestThreshold3)
            return 3;

        if (random < chestThreshold4)
            return 4;

        return 5;
    }


    /**
     * Gives back a weighted random level in a given intervall
     * Note: in rare cases, this might fail and it will return
     * a random level between [1,5]
     *
     * @param rand a random
     * @param min  minimum Level
     * @param max  maximum Level
     * @return the random level [min,max]
     */
    public int getRandomChestLevel(GridRandom rand, int min, int max) {
        if (min < 1 || min > 5 || max < 1 || max > 5) {
            Minions.w("Invalid config option for a schematic, minChestLevel or maxChestLevel are out of range! Valid: [1,5]");
            return 1;
        }
        if (min >= max) return min; // can't choose?

        int minthreshold, maxthreshold;
        if (!cachedChest) cacheChestThresholds(); // make sure thresholds are cached
        switch (min) {
            case 1:
                minthreshold = chestLevelWeights[0];
                break;
            case 2:
                minthreshold = chestThreshold2;
                break;
            case 3:
                minthreshold = chestThreshold3;
                break;
            case 4:
                minthreshold = chestThreshold4;
                break;
            default:
                minthreshold = 0;
                break;
        }
        switch (max) {
            case 2:
                maxthreshold = chestThreshold2;
                break;
            case 3:
                maxthreshold = chestThreshold3;
                break;
            case 4:
                maxthreshold = chestThreshold4;
                break;
            default:
                maxthreshold = cachedChestLevelSum;
                break;
        }
        int random = rand.getRandomInt(minthreshold, maxthreshold);
        return getLevel(random);
    }

    public static GlobalSchematicConfig fromFile(String path) {
        Gson gson = new Gson();
        try {
            String json = new String(Files.readAllBytes(Paths.get(path)));
            json = Nimmersatt.friss(json);
            return gson.fromJson(json, GlobalSchematicConfig.class);

        } catch (JsonSyntaxException e) { // catch all exceptions, inclusive any JSON fails
            Bukkit.getLogger().throwing(GlobalSchematicConfig.class.getName(), "Couldn't load global schematic config.", e);
        } catch (IOException e) {
            Bukkit.getLogger().throwing(GlobalSchematicConfig.class.getName(),"Couldn't load global schematic config.",e);
        }finally {
            return  new GlobalSchematicConfig(); // couldn't read config file? use default
        }
    }

    public boolean toFile(String path) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String file = gson.toJson(this);
            Files.write(Paths.get(path), file.getBytes()); //overwrite existing stuff
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().throwing(this.getClass().getName(), "storeConfig config", e);
            return false;
        }
    }

}
