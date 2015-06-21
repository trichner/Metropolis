package ch.k42.metropolis.grid.urbanGrid.clipboard;


import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.config.SchematicConfig;
import ch.n1b.vector.Vec2D;
import ch.n1b.vector.Vec3D;

/**
 * Created by Thomas on 07.03.14.
 */
public interface Clipboard {
    /**
     *
     * @param generator
     * @param base absolute chunk coordinates
     * @param streetLevel
     */
    public void paste(MetropolisGenerator generator, Vec2D base, int streetLevel);
    public Vec3D getSize();
    public int getBottom(int streetlevel);
    public SchematicConfig getConfig();

    /**
     * A unique ID which is the same for one clipboard and it's rotations
     * @return unique groupId
     */
    public String getGroupId();
}
