package ch.k42.metropolis.generator.cuboid;

import ch.k42.metropolis.minions.Minions;
import ch.n1b.bitfield.Bitfield;
import ch.n1b.bitfield.Bitfield4x4;
import ch.n1b.vector.Vec3D;
import ch.n1b.worldedit.schematic.block.BaseBlock;
import ch.n1b.worldedit.schematic.schematic.Cuboid;
import ch.n1b.worldedit.schematic.vector.Vector;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05.01.2015.
 *
 * @author Thomas
 */
public class PortalEnricher {

    private static final int ENTRANCE = Material.EMERALD_BLOCK.getId();
    private static final int EXIT = Material.DIAMOND_BLOCK.getId();

    public PortaledCuboid apply(Cuboid cuboid){
        //PortaledCuboid portaled = new PortaledCuboid(cuboid);
        //scan faces
        //add portals

        return null;
    }

    private void scanFace(Cuboid cuboid,Vec3D e1,Vec3D e2,Vec3D origin){
        // the two limits
        int sizeX = e1.dot(Minions.toVec3D(cuboid.getSize()));
        int sizeY = e2.dot(Minions.toVec3D(cuboid.getSize()));



        Bitfield field = new Bitfield();
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Vec3D pos = origin.add(e1.mult(x)).add(e2.mult(y));
                // scan at position
                BaseBlock block = cuboid.getBlock(new Vector(pos.X, pos.Y, pos.Z));
                if(block.getId()==ENTRANCE || block.getId()==EXIT){
                    field.set(x,y);
                }
            }
        }

        List<Bitfield4x4> entrance = new ArrayList<>();
        while (field.reduce()){
            Bitfield4x4 cutout = field.cut4x4();
            //type
            cutout.getField();
        }

    }
}
