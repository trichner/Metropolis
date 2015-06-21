package ch.k42.metropolis.grid.urbanGrid.districts;


import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.n1b.vector.Vec2D;

/**
 * Created by Thomas on 11.03.14.
 */
public interface IDistrict {
    void initDistrict(Vec2D base, Vec2D size, UrbanGrid grid);
    void fillDistrict();
}
