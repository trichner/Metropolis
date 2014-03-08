package ch.k42.metropolis.minions;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 29.09.13
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public final class Cartesian2D {
    public final int X;
    public final int Y;

    public Cartesian2D(int x, int y) {
        X = x;
        Y = y;
    }

    public final Cartesian2D add(Cartesian2D c) {
        return new Cartesian2D(X + c.X, Y + c.Y);
    }

    public final  int manhattanDistance(Cartesian2D c){
        return Math.abs(c.X-X)+Math.abs(c.Y-Y);
    }

    @Override
    public String toString() {
        return "["+X+"/"+Y+"]";
    }
}
