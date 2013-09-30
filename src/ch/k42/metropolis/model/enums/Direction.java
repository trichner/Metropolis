package ch.k42.metropolis.model.enums;

/**
 * Possible values: north, south, west, east
 * @author Thomas Richner
 */
public enum Direction {
    NORTH("north",(byte) 0x2),
    WEST("west",(byte) 0x4),
    EAST("east",(byte) 0x5),
    SOUTH("south",(byte) 0x3),
    ROAD("road", (byte) 0x0),
    NONE("none",(byte) 0x0);
    public final String string;
    public final byte data;
    Direction(String str,byte data){
        this.string=str;
        this.data=data;
    }
    public static Direction getByString(String string){
        for(Direction d : Direction.values()){
            if(d.string.equals(string))
                return d;
        }
        return null;
    }
    public static Direction getByData(byte data){
        for(Direction d : Direction.values()){
            if(d.data==(data))
                return d;
        }
        return NONE;
    }
}
