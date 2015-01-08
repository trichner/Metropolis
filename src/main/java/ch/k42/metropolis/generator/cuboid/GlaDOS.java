package ch.k42.metropolis.generator.cuboid;

import ch.k42.metropolis.minions.Minions;
import ch.n1b.bitfield.Bitfield;
import ch.n1b.bitfield.Bitfield4x4;
import ch.n1b.vector.Vec3D;
import ch.n1b.worldedit.schematic.block.BaseBlock;
import ch.n1b.worldedit.schematic.schematic.Cuboid;
import ch.n1b.worldedit.schematic.vector.Vector;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created on 05.01.2015.
 *
 * @author Thomas
 */
public class GlaDOS implements Function<Cuboid, PortaledCuboid> {

    private static final int ENTRANCE = Material.EMERALD_BLOCK.getId();
    private static final int EXIT = Material.DIAMOND_BLOCK.getId();

    @Override
    public PortaledCuboid apply(Cuboid cuboid){
        //;
        //scan faces
        //add portals
        Collection<Portal> portals = Sets.newHashSet();
        Vec3D e1 = new Vec3D(0,1,0);
        Vec3D e2 = new Vec3D(1,0,0);
        Vec3D origin = new Vec3D(0,0,0);
        portals.addAll(scanFace(cuboid,e1,e2,origin));

        
        e1 = new Vec3D(1,0,0);
        e2 = new Vec3D(0,1,0);
        origin = new Vec3D(0,0,cuboid.getSize().getBlockZ());
        portals.addAll(scanFace(cuboid,e1,e2,origin));

        e1 = new Vec3D(0,0,1);
        e2 = new Vec3D(0,1,0);
        origin = new Vec3D(0,0,0);
        portals.addAll(scanFace(cuboid,e1,e2,origin));

        e1 = new Vec3D(0,1,0);
        e2 = new Vec3D(0,0,1);
        origin = new Vec3D(cuboid.getSize().getBlockX(),0,0);
        portals.addAll(scanFace(cuboid,e1,e2,origin));

        Multimap<PortalKey,Portal> portalMap = Multimaps.index(portals,p -> new PortalKey(p.getType(),p
                .getNormal()));

        cuboid = Cuboids.cut(cuboid,new Vec3D(1,1,1),Minions.toVec3D(cuboid.getSize()).add(new Vec3D(-2,-2,
                -2)));

        return new PortaledCuboid(cuboid,portalMap);
    }

    private Collection<Portal> scanFace(Cuboid cuboid,Vec3D e1,Vec3D e2,Vec3D origin){
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

        List<Portal> portals = new ArrayList<>();
        while (field.reduce()){
            Bitfield4x4 cutout = field.cut4x4();
            //type
            int type = cutout.getField();
            Portal portal = new Portal(cutout.getOffset(),e1.cross(e2),cutout.getField());
            portals.add(portal);
        }
        return portals;
    }
}
