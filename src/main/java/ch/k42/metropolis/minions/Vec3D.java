package ch.k42.metropolis.minions;

import com.sk89q.worldedit.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 29.09.13
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public final class Vec3D {
    public final int X;
    public final int Y;
    public final int Z;

    public Vec3D(int x, int y, int z) {
        X = x;
        Y = y;
        Z = z;
    }

    public final Vec3D add(Vec3D c) {
        return new Vec3D(X + c.X, Y + c.Y, Z + c.Z);
    }

    public final Vec2D get2D(){
        return new Vec2D(X,Z);
    }

    public static final Vec3D fromVector(Vector v){
        return new Vec3D(v.getBlockX(),v.getBlockY(),v.getBlockZ());
    }
}
