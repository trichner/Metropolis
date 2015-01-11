package ch.k42.metropolis.generator.vault.cuboid;

import ch.n1b.vector.Vec3D;

/**
 * Created on 07.01.2015.
 *
 * @author Thomas
 */
public class PortalType {
    private int type;
    private Vec3D normale;

    public PortalType(int type, Vec3D normale) {
        this.type = type;
        this.normale = normale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PortalType portalType = (PortalType) o;

        if (type != portalType.type) return false;
        if (normale != null ? !normale.equals(portalType.normale) : portalType.normale != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (normale != null ? normale.hashCode() : 0);
        return result;
    }
}
