package ch.k42.metropolis.model.enums;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Random;

/**
 * Possible values: north, south, west, east
 * @author Thomas Richner
 */
public enum Direction {
    NORTH((byte) 0x2),
    WEST((byte) 0x4),
    EAST((byte) 0x5),
    SOUTH((byte) 0x3),
    NONE((byte) 0x0);
    public final byte data;
    Direction(byte data){
        this.data=data;
    }
    public static Direction getByData(byte data){
        for(Direction d : Direction.values()){
            if(d.data==(data))
                return d;
        }
        return NONE;
    }
    public static Direction getRandomDirection() {
        Random random = new Random();
        Direction output = values()[random.nextInt(values().length)];
        return output;
    }
    public static Direction[] getDirections(){
        Direction[] dirs = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
        return dirs;
    }
}
