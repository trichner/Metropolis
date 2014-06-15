package ch.k42.metropolis.grid.urbanGrid.districts;


import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.minions.Cartesian2D;

/**
 * Created by Thomas on 11.03.14.
 */
public interface IDistrict {
    void initDistrict(Cartesian2D base, Cartesian2D size, UrbanGrid grid);
    void fillDistrict();
}
