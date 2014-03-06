package ch.k42.metropolis.grid.urbanGrid.districts;

import ch.k42.metropolis.minions.Cartesian2D;

/**
 * Created by Thomas on 06.03.14.
 */
public class District {
    private Cartesian2D base;
    private Cartesian2D size;
    private Context context;

    public District(Cartesian2D base, Cartesian2D size, Context context) {
        this.base = base;
        this.size = size;
        this.context = context;
    }
}
