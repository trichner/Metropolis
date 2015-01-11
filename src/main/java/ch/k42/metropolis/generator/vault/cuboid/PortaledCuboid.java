package ch.k42.metropolis.generator.vault.cuboid;

import ch.n1b.worldedit.schematic.schematic.Cuboid;
import com.google.common.collect.Multimap;

/**
 * Created on 06.01.2015.
 *
 * @author Thomas
 */
public class PortaledCuboid extends Cuboid {

    private Multimap<PortalType,Portal> portals;

    public PortaledCuboid(Cuboid cuboid,Multimap<PortalType,Portal> portals) {
        super(cuboid);
        this.portals = portals;
    }

    public Multimap<PortalType, Portal> getPortals() {
        return portals;
    }
}
