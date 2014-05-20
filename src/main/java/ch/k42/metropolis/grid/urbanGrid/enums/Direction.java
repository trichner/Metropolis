package ch.k42.metropolis.grid.urbanGrid.enums;


import java.util.ArrayList;
import java.util.List;

import ch.k42.metropolis.minions.GridRandom;

/**
 * Possible values: north, south, west, east
 *
 * @author Thomas Richner
 */
public enum Direction {
    NORTH((byte) 0x2),
    WEST((byte) 0x4),
    EAST((byte) 0x5),
    SOUTH((byte) 0x3),
    NONE((byte) 0x0);
    public final byte data;

    Direction(byte data) {
        this.data = data;
    }

    public static Direction getByData(byte data) {
        for (Direction d : Direction.values()) {
            if (d.data == (data))
                return d;
        }
        return NONE;
    }

    public static Direction getRandomDirection(GridRandom random) {
        Direction[] dirs = getDirections();
        Direction output = dirs[random.getRandomInt(dirs.length)];
        return output;
    }

    public static Direction getRandomDirection(GridRandom random, boolean north, boolean south, boolean east, boolean west) {
        List<Direction> dirs = getDirections(north, south, east, west);
        if (!north && !east && !south && !west) return Direction.NONE;
        Direction output = dirs.get(random.getRandomInt(dirs.size()));
        return output;
    }

    public static Direction[] getDirections() {
        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        return dirs;
    }

    public static List<Direction> getDirections(boolean north, boolean south, boolean east, boolean west) {
        ArrayList<Direction> dirs = new ArrayList<Direction>();
        if (north) dirs.add(Direction.NORTH);
        if (south) dirs.add(Direction.SOUTH);
        if (east) dirs.add(Direction.EAST);
        if (west) dirs.add(Direction.WEST);
        return dirs;
    }
}
