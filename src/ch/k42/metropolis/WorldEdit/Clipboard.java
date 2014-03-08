package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Cartesian3D;

/**
 * Created by Thomas on 07.03.14.
 */
public interface Clipboard {
    public void paste(MetropolisGenerator generator, Cartesian2D base, int streetLevel);
    public Cartesian3D getSize();
    public int getBottom(int streetlevel);
    public SchematicConfig getConfig();
}
