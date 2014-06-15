package ch.k42.metropolis.grid.urbanGrid.config;


import org.bukkit.entity.EntityType;

import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.minions.Minions;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 15.10.13
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSchematicConfig {

    protected SchematicSpawner[] Spawners = null;

    public class RoadCutout {
        public final int startPoint;
        public final int length;

        private RoadCutout(int lengthStart, int lengthEnd) {
            this.startPoint = lengthStart;
            this.length = lengthEnd;
        }
    }

    protected class SchematicSpawner {
        private EntityType Type = EntityType.ZOMBIE;
        private int Weight = 10;

        private transient int threshold = -1;

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

    //================= Spawner random

    protected transient int cachedSpawnerLevelSum = -1;
    protected transient boolean cachedSpawner = false;

    protected void cacheSpawnerThresholds() {
        if (Spawners == null || Spawners.length == 0) {
            //initDefaultSpawners();
        }
        Spawners[0].setThreshold(Spawners[0].getWeight());
        SchematicSpawner current, previous = Spawners[0];
        for (int i = 1; i < Spawners.length; i++) {
            current = Spawners[i];
            current.setThreshold(previous.getThreshold() + current.getWeight());
            previous = current;
        }
        cachedSpawnerLevelSum = previous.getThreshold();
        cachedSpawner = true;
    }

    protected int getSpawnerEntityWeightSum() {
        if (!cachedSpawner)
            cacheSpawnerThresholds();
        return cachedSpawnerLevelSum;
    }


    /**
     * Selects a random EntityType
     *
     * @param rand a number between [0,SUM]
     * @return
     */
    public EntityType getRandomSpawnerEntity(GridRandom rand) {
        if (!cachedSpawner)
            cacheSpawnerThresholds();
        int random = rand.getRandomInt(getSpawnerEntityWeightSum());
        for (int i = 0; i < Spawners.length; i++) {
            if (random < Spawners[i].getThreshold())
                return Spawners[i].getType();
        }
        Minions.w("No EntityType for Spawner could be randomly determinated, random argument too low?");
        return Spawners[0].getType();
    }

    public void setSpawners(SchematicSpawner[] spawners) {
        Spawners = spawners;
    }


    public SchematicSpawner[] getSpawners() {
        return Spawners;
    }
}
