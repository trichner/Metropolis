package ch.k42.metropolis.minions;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 29.09.13
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public class Cartesian {
    public final int X;
    public final int Y;
    public final int Z;

    public Cartesian(int x, int y, int z) {
        X = x;
        Y = y;
        Z = z;
    }

    public Cartesian add(Cartesian c){
        return new Cartesian(X+c.X,Y+c.Y,Z+c.Z);
    }
}
