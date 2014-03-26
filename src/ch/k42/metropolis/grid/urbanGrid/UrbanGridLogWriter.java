package ch.k42.metropolis.grid.urbanGrid;

import ch.k42.metropolis.grid.urbanGrid.parcel.Parcel;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Incr;
import ch.k42.metropolis.minions.Minions;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas on 26.03.14.
 */
public class UrbanGridLogWriter {
    public static final void logGrid(String filename, UrbanGrid grid){
        try {

            Parcel parcel;
            Map<Cartesian2D,Incr> aggregators = new HashMap<>();
            for(int x=0;x<grid.GRID_SIZE;x++){
                for(int y=0;y<grid.GRID_SIZE;y++){
                    parcel = grid.getParcel(x, y);
                    if(parcel!=null){
                        Incr incr = aggregators.get(parcel.getChunkSize());
                        if(incr==null){
                            incr = new Incr();
                        }
                        aggregators.put(parcel.getChunkSize(),incr.incr());
                    }
                }
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));

            for(Cartesian2D size : aggregators.keySet()){
                writer.write(buildMessage(size,aggregators.get(size)));
            }
            writer.close();
        } catch (FileNotFoundException e) {
            Minions.e(e);
        } catch (IOException e) {
            Minions.e(e);
        }
    }

    private static final String buildMessage(Cartesian2D size, Incr incrementor){
        return new StringBuilder().append("Size ").append(size.X).append("x").append(size.Y).append(" : ").append(incrementor.val()).append('\n').toString();
    }
}
