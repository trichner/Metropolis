package ch.k42.metropolis.generator.cuboid;

import ch.k42.metropolis.minions.Minions;
import ch.n1b.vector.Vec3D;
import ch.n1b.worldedit.schematic.block.BaseBlock;
import ch.n1b.worldedit.schematic.schematic.Cuboid;
import ch.n1b.worldedit.schematic.vector.Vector;

/**
 * Created on 07.01.2015.
 *
 * @author Thomas
 */
public class Cuboids {
    public static final Cuboid cut(Cuboid cuboid,Vec3D offset,Vec3D size){
        Cuboid newCuboid = new Cuboid(Minions.toVector(size));
        for(int x=0;x<size.X;x++){
            for(int y=0;y<size.Y;y++){
                for(int z=0;z<size.Z;z++){
                    Vec3D pos = offset.add(new Vec3D(x,y,z));
                    BaseBlock block = cuboid.getBlock(Minions.toVector(pos));
                    newCuboid.setBlock(new Vector(x,y,z),block);
                }
            }
        }
        return newCuboid;
    }
}
