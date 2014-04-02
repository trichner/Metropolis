package ch.k42.metropolis.grid.urbanGrid.districts;

import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.grid.urbanGrid.clipboard.Clipboard;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;
import ch.k42.metropolis.grid.urbanGrid.parcel.ClipboardParcel;
import ch.k42.metropolis.grid.urbanGrid.parcel.Parcel;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.plugin.PluginConfig;

import java.util.*;

/**
 * Created by Thomas on 06.03.14.
 *
 * FIXME Some spots seem to stay empty, not sure why...
 */
public class District implements IDistrict {
    private static final int MAX_ITERATIONS = PluginConfig.getIterations();

    private Cartesian2D base,size;
    private UrbanGrid grid;
    private ContextType context;

    @Override
    public void fillDistrict(){
        this.context = grid.getContextProvider().getContext(base);

        Set<ClipboardParcel> max = null;

        int score=Integer.MIN_VALUE,tscore;
        for(int i=0;i< MAX_ITERATIONS;i++){ // if we fail, try again
            Set<ClipboardParcel> parcels = new HashSet<>();
            tscore = nextRecPartition(parcels,base,size);
            if(tscore >= score){
                max = parcels;
                score = tscore;
            }
        }
        Minions.d("Final score: " + score);

        for(Parcel parcel : max){
            grid.fillParcels(parcel.getChunkX(),parcel.getChunkZ(),parcel);
        }

        //Check for empty spots, this is more a workaround than a solution
        for(int x = base.X; x<base.X+size.X; x++){
            for(int y = base.Y; y<base.Y+size.Y; y++){
                Parcel p=grid.getParcel(x,y);
                if(p==null){
                    Minions.d("Parcel wasn't filled by district, what wen't wrong here??? Context '%s'",context);
                    p = placeRandomBuildForSure(new Cartesian2D(x,y),new Cartesian2D(1,1),new HashSet<ClipboardParcel>());
                    if(p==null){
                        Minions.d("Can't find any clipboard for this spot? Seriously?");
                    } else {
                        grid.setParcel(x,y,p);
                    }
                }
            }
        }
    }

    private static final int CLONERADIUS = PluginConfig.getCloneRadius();

    private ClipboardParcel placeRandomBuild(Cartesian2D base, Cartesian2D size,Set<ClipboardParcel> previous,SchematicType schematicType){
        Direction direction = findRoad(base, size);

        Set<ClipboardParcel> neighbours = grid.getNeighbours(new Cartesian2D(base.X + size.X/2,base.Y+size.Y/2),CLONERADIUS);
        neighbours.addAll(previous);
        //try to place a build
        List<Clipboard> clips = grid.getClipboardProvider().getFit(size, context,schematicType, direction);
        ClipboardParcel p = placeRandomRec(base, size, direction, clips, neighbours, schematicType);
        return p;
    }

    private ClipboardParcel placeRandomBuildForSure(Cartesian2D base,Cartesian2D size,Set<ClipboardParcel> previous){
        ClipboardParcel p = placeRandomBuild(base,size,previous,SchematicType.BUILD);
        if(p!=null) return p;
        p = placeRandomBuild(base,size,previous,SchematicType.FILLER);
        if(p!=null) return p;

        Direction direction = findRoad(base, size);
        List<Clipboard> clips = grid.getClipboardProvider().getFit(size, context,SchematicType.FILLER, direction);
        if(clips.size()==0){ // fallback to no context?
            Minions.d("found no filler for context '%s', size '%s' and direction '%s'",context,size,direction);
            clips = grid.getClipboardProvider().getFit(size,SchematicType.FILLER, direction);
            if(clips.size()==0){
                Minions.d("FALLBACK: found no filler for size '%s' and direction '%s'",context,size,direction);
                return null;
            }
            return new ClipboardParcel(grid, base, size, clips.get(grid.getRandom().getRandomInt(clips.size())), context,SchematicType.BUILD, direction);
        }
        return new ClipboardParcel(grid, base, size, clips.get(grid.getRandom().getRandomInt(clips.size())), context,SchematicType.BUILD, direction);
    }

    private ClipboardParcel placeRandomRec(Cartesian2D base, Cartesian2D size, Direction direction, List<Clipboard> clips, Set<ClipboardParcel> neighbours, SchematicType type){
        if(clips.size()!=0){ // can we place anything?
            List<Integer> weights = getWeights(clips);
            Clipboard clip =clips.get(Minions.getRandomWeighted(weights,grid.getRandom()));
            ClipboardParcel p = new ClipboardParcel(grid, base, size, clip, context,type, direction);
            if(neighbours.contains(p)){
                //Minions.d("found duplicate");
                clips.remove(clip);
                return placeRandomRec(base, size, direction, clips, neighbours, type);
            }else {
                return p;
            }
        }
        return null;
    }

    private List<Integer> getWeights(Collection<Clipboard> clips){
        List<Integer> weights = new LinkedList<>();
        for(Clipboard c : clips){
            weights.add(c.getConfig().getOddsOfAppearance());
        }
        return weights;
    }

    private ClipboardParcel placeRandomBuild(Cartesian2D base, Cartesian2D size,Set<ClipboardParcel> previous){
        if((grid.getRandom().getChance(BUILD_CHANCE))){
            ClipboardParcel p = placeRandomBuild(base,size,previous,SchematicType.BUILD);
            if(p!=null) return p;
        }

        if((grid.getRandom().getChance(FILLER_CHANCE))){
            return placeRandomBuild(base,size,previous,SchematicType.FILLER);
        }
        return null;
    }

    private int BUILD_CHANCE = PluginConfig.getBuildChance();
    private int FILLER_CHANCE = PluginConfig.getFillerChance();

    private int nextRecPartition(Set<ClipboardParcel> parcels,Cartesian2D base, Cartesian2D size){
        ClipboardParcel parcel;

        if(size.X<=1 && size.Y<=1){ // are we already at smallest possible size?
            if((parcel= placeRandomBuildForSure(base, size,parcels))!=null){
                parcels.add(parcel);
                return scoreParcel(parcel,size);
            }else {
                Minions.d("Found absolutely no schems for context '%s' and size '%s' ",context,size);
                parcels.add(new ClipboardParcel(grid, base, size, null, context,SchematicType.FILLER, Direction.NONE));
                return Integer.MIN_VALUE;
            }
        }

        // let's try to place a schematic
        if((parcel= placeRandomBuild(base, size,parcels))!=null){
            parcels.add(parcel);
            return scoreParcel(parcel, size);
        }

        if(size.X<size.Y){      //partition Y side
            int cut = Minions.makeCut(grid.getRandom(), size.Y);
            int score = nextRecPartition(parcels,base, new Cartesian2D(size.X, cut));
            return score + nextRecPartition(parcels,new Cartesian2D(base.X , base.Y+ cut), new Cartesian2D(size.X, size.Y - cut));
        }else {                 //partition X side
            int cut = Minions.makeCut(grid.getRandom(), size.X);
            int score = nextRecPartition(parcels,base, new Cartesian2D(cut, size.Y));
            return score + nextRecPartition(parcels,new Cartesian2D(base.X + cut, base.Y), new Cartesian2D(size.X - cut, size.Y));
        }
    }



    private void fastPartition(Set<ClipboardParcel> parcels,Cartesian2D base, Cartesian2D size){
        ClipboardParcel parcel;

        if((parcel = placeRandomBuildForSure(base, size, parcels)) != null){
            parcels.add(parcel);
        } else {
            Minions.d("Found absolutely no schems for context '%s' and size '%s' ",context,size);
            parcels.add(new ClipboardParcel(grid, base, size, null, context,SchematicType.FILLER, Direction.NONE));
        }
    }


    private static final int scoreParcel(ClipboardParcel parcel,Cartesian2D size){
        if(parcel.getSchematicType().equals(SchematicType.BUILD))
            return scoreArea(size);//*parcel.getClipboard().getConfig().getOddsOfAppearance(); //might keep OddsOfAppearance out, they are veeery fuzzy
        return -scoreArea(size);
    }

    private static final int scoreArea(Cartesian2D size){
        return (size.X*size.Y);
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
