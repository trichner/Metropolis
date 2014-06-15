package ch.k42.metropolis.grid.urbanGrid.provider;


import java.util.Random;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Cartesian3D;
import ch.k42.metropolis.minions.DecayOption;

/**
 * Created by Thomas on 02.04.14.
 */
public class DecayProviderNone extends DecayProvider {
    public DecayProviderNone(MetropolisGenerator generator, Random random) {
        super(generator, random);
    }

    @Override
    public void destroyWithin(int x1, int x2, int y1, int y2, int z1, int z2) {

    }

    @Override
    public void destroyChunks(int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ, int bottom, int height, DecayOption options) {

    }

    @Override
    public void destroyWithin(Cartesian3D start, Cartesian3D end, DecayOption options) {

    }

    @Override
    public void destroyWithin(int x1, int x2, int y1, int y2, int z1, int z2, DecayOption options) {

    }
}
