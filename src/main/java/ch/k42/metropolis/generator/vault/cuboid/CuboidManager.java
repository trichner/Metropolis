package ch.k42.metropolis.generator.vault.cuboid;

import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * Created on 11.01.2015.
 *
 * @author Thomas
 */
public class CuboidManager {

    private Multimap<PortalType,PortaledCuboid> cuboids;

    public Collection<PortaledCuboid> forPortal(PortalType portal){
        return cuboids.get(portal);
    }
}
