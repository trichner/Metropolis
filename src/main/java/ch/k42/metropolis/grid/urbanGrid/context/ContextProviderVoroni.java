package ch.k42.metropolis.grid.urbanGrid.context;


import java.util.ArrayList;
import java.util.List;

import ch.k42.metropolis.grid.common.GridProvider;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;

/**
 * Created by Thomas on 04.03.14.
 */
public class ContextProviderVoroni implements ContextProvider {

    private static final double CONTEXT_DIAMETER = 20.0;
    private static final int DIAMETER_STDDEVIATION = 5; //should be smaller than CONTEXT_DIAMETER/2
    private static final int GRID_SIZE = GridProvider.GRID_SIZE;

    private static List<ContextType> CONTEXTS = new ArrayList<>(5);
    static {
        CONTEXTS.add(ContextType.RESIDENTIAL);
        CONTEXTS.add(ContextType.LOWRISE);
        CONTEXTS.add(ContextType.MIDRISE);
        CONTEXTS.add(ContextType.HIGHRISE);
        CONTEXTS.add(ContextType.INDUSTRIAL);
    }

    private static final class VoronoiVertex{

        private Cartesian2D vertex;
        private ContextType context;

        private VoronoiVertex(Cartesian2D vertex, ContextType context) {
            this.vertex = vertex;
            this.context = context;
        }
    }

    private static final int limit(int x){
        if(x<0) return 0;
        else if(x>=GRID_SIZE) return GRID_SIZE-1;
        return x;
    }

    private List<VoronoiVertex> voronoiVertices = new ArrayList<>(9);

    public ContextProviderVoroni(GridRandom random) {
        int numPoints = (int) Math.round((GRID_SIZE-1)/CONTEXT_DIAMETER);
        int x,y;
        for(int i=0;i<numPoints;i++){
            for(int j=0;j<numPoints;j++){
                x= (int) ( i* CONTEXT_DIAMETER+ CONTEXT_DIAMETER/2);
                y= (int) ( j* CONTEXT_DIAMETER+ CONTEXT_DIAMETER/2);
                x+= random.getRandomGaussian()*DIAMETER_STDDEVIATION;
                y+= random.getRandomGaussian()*DIAMETER_STDDEVIATION;
                x = limit(x);
                y = limit(y);
                voronoiVertices.add(new VoronoiVertex(new Cartesian2D(x,y),getRandomContext(random)));
            }
        }
    }



    private static final ContextType getRandomContext(GridRandom random){
        return CONTEXTS.get(random.getRandomInt(CONTEXTS.size()));
    }


    @Override
    public ContextType getContext(ContextZone[] zones, int chunkX, int chunkZ, int level) {
        return getContext(new Cartesian2D(chunkX,chunkZ));
    }

    @Override
    public ContextType getContext(Cartesian2D vertex) {
        ContextType context = ContextType.HIGHRISE;
        int distance;
        int minDistance = Integer.MAX_VALUE;
        for(VoronoiVertex voronoi : voronoiVertices){
            distance = voronoi.vertex.manhattanDistance(vertex);
            if(distance<minDistance){
                minDistance = distance;
                context = voronoi.context;
            }
        }
        return context;
    }
}
