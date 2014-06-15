package ch.k42.metropolis.grid.urbanGrid.context;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.k42.metropolis.grid.common.GridProvider;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.minions.voronoi.Pnt;
import ch.k42.metropolis.minions.voronoi.Triangle;
import ch.k42.metropolis.minions.voronoi.Triangulation;

/**
 * Created by thomas on 3/5/14.
 */
public class ContextProviderVoronoi2 implements ContextProvider {

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

    private Set<Cartesian2D> delauneyNodes = new HashSet<>();
    private Triangulation dt;
    private GridRandom random;

    public ContextProviderVoronoi2(GridRandom random){
        this.random = random;
        initDT();
    }

    private void initDT(){
        //Init triangle containing the full grid
        Pnt[] initPnt = new Pnt[3];
        initPnt[0] = new Pnt(-1.5*GRID_SIZE,-1.5*GRID_SIZE);
        initPnt[1] = new Pnt(2.5*GRID_SIZE,-1.5*GRID_SIZE);
        initPnt[2] = new Pnt(.5 * GRID_SIZE,2.5*GRID_SIZE);
        Triangle init = new Triangle(initPnt);
        dt = new Triangulation(init);
        // add some points in the grid

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
                dt.delaunayPlace(new Pnt(x,y));
            }
        }
    }

    private static final int limit(int x){
        if(x<0) return 0;
        else if(x>=GRID_SIZE) return GRID_SIZE-1;
        return x;
    }

    @Override
    public ContextType getContext(ContextZone[] zones, int chunkX, int chunkZ, int level) {
        return getContext(new Cartesian2D(chunkX,chunkZ));
    }

    @Override
    public ContextType getContext(Cartesian2D place) {
        return null;
    }
}
