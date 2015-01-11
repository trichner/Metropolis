package ch.k42.metropolis.generator.cuboid;

import ch.n1b.worldedit.schematic.schematic.Cuboid;
import com.google.common.collect.Multimap;

/**
 * Created on 06.01.2015.
 *
 * @author Thomas
 */
public class PortaledCuboid extends Cuboid {

    private Multimap<PortalKey,Portal> portals;

    public PortaledCuboid(Cuboid cuboid,Multimap<PortalKey,Portal> portals) {
        super(cuboid);
        this.portals = portals;
    }

    public Multimap<PortalKey, Portal> getPortals() {
        return portals;
    }
}
