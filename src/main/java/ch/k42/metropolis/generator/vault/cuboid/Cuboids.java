package ch.k42.metropolis.generator.vault.cuboid;

import ch.n1b.vector.Vec3D;
import ch.n1b.worldedit.schematic.block.BaseBlock;
import ch.n1b.worldedit.schematic.schematic.Cuboid;
import org.bukkit.World;

/**
 * Created on 07.01.2015.
 *
 * @author Thomas
 */
public class Cuboids {
    public static final Cuboid cut(Cuboid cuboid,Vec3D offset,Vec3D size){
        Cuboid newCuboid = new Cuboid(size);
        for(int x=0;x<size.X;x++){
            for(int y=0;y<size.Y;y++){
                for(int z=0;z<size.Z;z++){
                    Vec3D pos = offset.add(new Vec3D(x,y,z));
                    BaseBlock block = cuboid.getBlock(pos);
                    newCuboid.setBlock(new Vec3D(x,y,z),block);
                }
            }
        }
        return newCuboid;
    }
    public static final void placeAt(World world, Vec3D position, Cuboid cuboid){
        for (int x = 0; x < cuboid.getSize().X; x++) {
            for (int y = 0; y < cuboid.getSize().Y; y++) {
                for (int z = 0; z < cuboid.getSize().Z; z++) {
                    world.getBlockAt(position.X+x,position.Y + y,position.Z + z).setTypeIdAndData(
                            cuboid.getBlock(new Vec3D(x, y, z)).getType(),
                            (byte) cuboid.getBlock(new Vec3D(x, y, z)).getData(),
                            false);
                }
            }
        }
    }
}
