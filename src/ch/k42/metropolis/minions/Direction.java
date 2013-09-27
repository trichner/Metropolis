package ch.k42.metropolis.minions;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public enum Direction {
    NORTH("north"),
    WEST("west"),
    EAST("east"),
    SOUTH("south");
    public String string;
    Direction(String str){
        this.string=str;
    }
    public static Direction getByString(String string){
        for(Direction d : Direction.values()){
            if(d.string.equals(string))
                return d;
        }
        return null;
    }
}
