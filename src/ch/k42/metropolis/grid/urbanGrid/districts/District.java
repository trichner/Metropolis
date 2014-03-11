package ch.k42.metropolis.grid.urbanGrid.districts;

import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.grid.urbanGrid.clipboard.Clipboard;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.parcel.ClipboardParcel;
import ch.k42.metropolis.grid.urbanGrid.parcel.EmptyParcel;
import ch.k42.metropolis.grid.urbanGrid.parcel.Parcel;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Minions;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * Created by Thomas on 06.03.14.
 */
public class District implements IDistrict {
    public static final int MAX_TRY = 5;

    private Cartesian2D base,size;
    private UrbanGrid grid;
    private ContextType context;

    @Override
    public void fillDistrict(){
        this.context = grid.getContextProvider().getContext(base);

        Set<Parcel> parcels = new HashSet<>();

        if(size.X>size.Y){
            recPartitionX(parcels, base,size);
        }else {
            recPartitionZ(parcels,base,size);
        }

        for(Parcel parcel : parcels){
            grid.fillParcels(parcel.getChunkX(),parcel.getChunkZ(),parcel);
        }
    }

    private Parcel placeRandom(Cartesian2D base,Cartesian2D size){
        Direction direction = findRoad(base, size);
        List<Clipboard> clips = grid.getClipboardProvider().getFit(size, context, direction);
        if(clips.size()!=0){ // can we place anything?
            Collections.shuffle(clips);
            for(Clipboard c : clips){
                if(grid.getRandom().getChance(c.getConfig().getOddsOfAppearance())){
                    return new ClipboardParcel(grid, base.X, base.Y, size.X, size.Y, c, context, direction);
                }
            }
        }
        return null;
    }

    private Parcel placeRandomForSure(Cartesian2D base,Cartesian2D size){
        Direction direction = findRoad(base, size);
        List<Clipboard> clips = grid.getClipboardProvider().getFit(size, context, direction);
        if(clips.size()!=0){ // can we place anything?
            Collections.shuffle(clips);
            for(int i=0;i<20;i++){
                for(Clipboard c : clips){
                    if(grid.getRandom().getChance(c.getConfig().getOddsOfAppearance())){
                        return new ClipboardParcel(grid, base.X, base.Y, size.X, size.Y, c, context, direction);
                    }
                }
            }
            Bukkit.getLogger().info("Couldn't place schem for sure! Tried 20 times, but all odds failed me.");
            // you failed! just take the first one
            return new ClipboardParcel(grid, base.X, base.Y, size.X, size.Y, clips.get(0), context, direction);
        }
        return null;
    }

    private int recPartitionX(Set<Parcel> parcels,Cartesian2D base, Cartesian2D size){
        Parcel parcel;
        if(size.X<2){ // can't make smaller
            if((parcel=placeRandom(base,size))!=null){
                parcels.add(parcel);
                return Minions.square(size.X * size.Y);
            }
            if(size.Y<2){
                if((parcel=placeRandomForSure(base,size))==null){
                    parcels.add(new EmptyParcel(grid,base.X,base.Y,size.X,size.Y));
                    return 0;
                }else {
                    parcels.add(parcel);
                    return Minions.square(size.X * size.Y);
                }
            }
            return recPartitionZ(parcels,base, size);
        }else {
            if((parcel=placeRandom(base,size))!=null){
                parcels.add(parcel);
                return Minions.square(size.X * size.Y);
            }else {
                int cut = Minions.makeCut(grid.getRandom(), size.X);
                int score = recPartitionZ(parcels,base, new Cartesian2D(cut, size.Y));
                return score + recPartitionZ(parcels,new Cartesian2D(base.X + cut, base.Y), new Cartesian2D(size.X - cut, size.Y));
            }
        }
}



    private int recPartitionZ(Set<Parcel> parcels,Cartesian2D base, Cartesian2D size){
        Parcel parcel;
        if(size.Y<2){ // can't make smaller
            if((parcel=placeRandom(base,size))!=null){
                parcels.add(parcel);
                return Minions.square(size.X * size.Y);
            }
            if(size.X<2){
                if((parcel=placeRandomForSure(base,size))==null){
                    parcels.add(new EmptyParcel(grid,base.X,base.Y,size.X,size.Y));
                    return 0;
                }else {
                    parcels.add(parcel);
                    return Minions.square(size.X * size.Y);
                }
            }
            return recPartitionX(parcels,base, size);
        }else {
            if((parcel=placeRandom(base,size))!=null){
                parcels.add(parcel);
                return Minions.square(size.X * size.Y);
            }else {
                int cut = Minions.makeCut(grid.getRandom(), size.Y);
                int score = recPartitionX(parcels,base, new Cartesian2D(size.X, cut));
                return score + recPartitionX(parcels,new Cartesian2D(base.X , base.Y+ cut), new Cartesian2D(size.X, size.Y - cut));
            }
        }
    }

    private Direction findRoad(Cartesian2D base,Cartesian2D size) {
        List<Direction> directions = new LinkedList<>();

        if(this.base.Y == base.Y)
            directions.add(Direction.NORTH);

        if((this.base.Y + this.size.Y) == (base.Y + size.Y))
            directions.add(Direction.SOUTH);

        if(this.base.X==base.X)
            directions.add(Direction.WEST);

        if((this.base.X+this.size.X)==(base.X + size.X))
            directions.add(Direction.EAST);

        if(directions.size()==0) return Direction.NONE;

        return directions.get(grid.getRandom().getRandomInt(directions.size()));
    }

    @Override
    public void initDistrict(Cartesian2D base, Cartesian2D size, UrbanGrid grid) {
        this.base = base;
        this.size = size;
        this.grid = grid;
    }
}
