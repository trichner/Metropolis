package ch.k42.metropolis.grid.urbanGrid;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.common.Grid;
import ch.k42.metropolis.grid.urbanGrid.provider.ContextProvider;
import ch.k42.metropolis.grid.urbanGrid.provider.ContextProviderVoroni;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.grid.urbanGrid.parcel.DistrictParcel;
import ch.k42.metropolis.grid.urbanGrid.parcel.HighwayParcel;
import ch.k42.metropolis.grid.urbanGrid.parcel.Parcel;
import ch.k42.metropolis.grid.common.GridProvider;

/**
 * Represents a grid occupied fully with buildings
 *
 * @author Thomas Richner
 */
public class UrbanGrid extends Grid {
    private Parcel[][] parcels = new Parcel[GRID_SIZE][GRID_SIZE];

    private ContextProvider contextProvider;
    private DistrictParcel district;
    private GridStatistics statistics;

    public UrbanGrid(GridProvider provider, GridRandom random,MetropolisGenerator generator, Cartesian2D root) {
        super(random,provider,generator, root);
        this.statistics = new AthmosStat();
        contextProvider = new ContextProviderVoroni(random);//generator.getContextProvider();
        placeHighways();
        district = new DistrictParcel(this, root.X + 1, root.Y + 1, GRID_SIZE - 2, GRID_SIZE - 2);
    }

    private void placeHighways() { // places roads all around the grid

        int maxidx = GRID_SIZE - 1;

        // fill in the corners with Highway
        setParcel(0, 0, new HighwayParcel(this, root.X, root.Y, RoadType.HIGHWAY_C_SE));
        setParcel(0, maxidx, new HighwayParcel(this, root.X, root.Y + maxidx, RoadType.HIGHWAY_C_NE));
        setParcel(maxidx, 0, new HighwayParcel(this, root.X + maxidx, root.Y, RoadType.HIGHWAY_C_SW));
        setParcel(maxidx, maxidx, new HighwayParcel(this, root.X + maxidx, root.Y + maxidx, RoadType.HIGHWAY_C_NW));

        // fill in all highways
        for (int i = 1; i < maxidx; i++) {
            setParcel(0, i, new HighwayParcel(this, root.X, root.Y + i, RoadType.HIGHWAY_SIDE_E)); //
            setParcel(i, 0, new HighwayParcel(this, root.X + i, root.Y, RoadType.HIGHWAY_SIDE_S));
            setParcel(i, maxidx, new HighwayParcel(this, root.X + i, root.Y + maxidx, RoadType.HIGHWAY_SIDE_N));
            setParcel(maxidx, i, new HighwayParcel(this, root.X + maxidx, root.Y + i, RoadType.HIGHWAY_SIDE_W));
        }
    }

    @Override
    public Parcel getParcel(int chunkX, int chunkZ) {
        // make sure it's positive and between [0, GRID_SIZE)
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
        return parcels[x][z];
    }

    @Override
    public void setParcel(int chunkX, int chunkZ, Parcel parcel) {
        // make sure it's positive and between [0, GRID_SIZE)
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);

        parcels[x][z] = parcel;

    }

    @Override
    public void fillParcels(int chunkX, int chunkZ, Parcel parcel) {
        for (int x = 0; x < parcel.getChunkSizeX(); x++) {
            for (int z = 0; z < parcel.getChunkSizeZ(); z++) {
                setParcel(chunkX + x, chunkZ + z, parcel);
            }
        }
    }


    /**
     * Calculates the relative coordinates
     *
     * @param chunk
     * @return
     */
    private static int getChunkOffset(int chunk) {
        int ret = chunk % GRID_SIZE;
        if (ret < 0)
            ret += GRID_SIZE;
        return ret;
    }

    public GridStatistics getStatistics() {
        return statistics;
    }

    public ContextProvider getContextProvider() {
        return contextProvider;
    }
}
