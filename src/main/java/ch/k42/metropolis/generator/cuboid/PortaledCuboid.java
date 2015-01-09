package ch.k42.metropolis.generator.cuboid;

import ch.k42.metropolis.minions.Minions;
import ch.n1b.vector.Vec3D;
import ch.n1b.worldedit.schematic.schematic.Cuboid;
import com.google.common.collect.Multimap;

/**
 * Created on 06.01.2015.
 *
 * @author Thomas
 */
public class PortaledCuboid {

    private Cuboid cuboid;

    private Multimap<PortalKey,Portal> portals;

    public PortaledCuboid(Cuboid cuboid,Multimap<PortalKey,Portal> portals) {
        this.cuboid = cuboid;
        this.portals = portals;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public Multimap<PortalKey, Portal> getPortals() {
        return portals;
    }

    public Vec3D getSize(){
        return Minions.toVec3D(cuboid.getSize());
    }
}
