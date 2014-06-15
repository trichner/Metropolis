package ch.k42.metropolis.minions;

import com.sk89q.worldedit.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 29.09.13
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public final class Cartesian3D {
    public final int X;
    public final int Y;
    public final int Z;

    public Cartesian3D(int x, int y, int z) {
        X = x;
        Y = y;
        Z = z;
    }

    public final Cartesian3D add(Cartesian3D c) {
        return new Cartesian3D(X + c.X, Y + c.Y, Z + c.Z);
    }

    public final Cartesian2D get2D(){
        return new Cartesian2D(X,Z);
    }

    public static final Cartesian3D fromVector(Vector v){
        return new Cartesian3D(v.getBlockX(),v.getBlockY(),v.getBlockZ());
    }
}
