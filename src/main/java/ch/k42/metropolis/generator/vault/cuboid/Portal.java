package ch.k42.metropolis.generator.vault.cuboid;

import ch.n1b.vector.Vec2D;
import ch.n1b.vector.Vec3D;

/**
 * Created on 05.01.2015.
 *
 * @author Thomas
 */
public class Portal {
    private final Vec2D offset;
    private final Vec3D normal;
    private final int type;

    public Portal(Vec2D offset, Vec3D normal,int type) {
        this.offset = offset;
        this.normal = normal;
        this.type = type;
    }

    public Vec2D getOffset() {
        return offset;
    }

    public Vec3D getNormal() {
        return normal;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Portal portal = (Portal) o;

        if (type != portal.type) return false;
        if (normal != null ? !normal.equals(portal.normal) : portal.normal != null) return false;
        if (offset != null ? !offset.equals(portal.offset) : portal.offset != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = offset != null ? offset.hashCode() : 0;
        result = 31 * result + (normal != null ? normal.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }
}
