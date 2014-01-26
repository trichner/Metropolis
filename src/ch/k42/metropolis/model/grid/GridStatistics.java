package ch.k42.metropolis.model.grid;

import java.util.HashMap;
import java.util.Map;

/**
 * This should keep track of statistics of a Grid, not
 * implemented yet.
 *
 * @author Thomas Richner
 */
public class GridStatistics {

    private class Incrementor {
        private int value = 0;

        public int increment() {
            value++;
            return value;
        }

        public int decrement() {
            value--;
            return value;
        }

        public int getValue() {
            return value;
        }

        public double getDouble() {
            return (double) value;
        }
    }

    private int numberOfEntries = 0;

    private Map<Integer, Incrementor> sizes = new HashMap<Integer, Incrementor>();

    public void incrementSize(int sizeX, int sizeZ) {
        int area = sizeX * sizeZ;
        Incrementor incrementor = sizes.get(area);
        if (incrementor == null) {
            incrementor = new Incrementor();
        }
        incrementor.increment();
        sizes.put(area, incrementor);
        numberOfEntries++;
    }

    public double getDistribution(int sizeX, int sizeZ) {
        if (numberOfEntries == 0) return 0;
        return sizes.get(sizeX * sizeZ).getDouble() / numberOfEntries;
    }

}
