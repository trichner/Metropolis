package ch.k42.metropolis.model.grid;

import ch.k42.metropolis.WorldEdit.Clipboard;
import ch.k42.metropolis.minions.Incr;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas on 04.02.14.
 */
public class AthmosStat implements GridStatistics {

    private Map<Clipboard,Incr> stats = new HashMap<>();
    private Incr count = new Incr();

    @Override
    public void logSchematic(Clipboard p) {
        if(stats.containsKey(p)){
            stats.get(p).incr();
        }else{
            Incr i = new Incr();
            i.incr();
            stats.put(p,i);
        }
        count.incr();
    }

    @Override
    public int getCount(Clipboard p) {
        if(stats.containsKey(p))
            return stats.get(p).val();
        return 0;
    }

    @Override
    public double getRelativeCount(Clipboard p) {
        int gcount = count.val();
        if(gcount==0) return 0;
        double pcount = getCount(p);
        double rel = pcount/gcount;
        return rel;
    }

    @Override
    public String printStatistics() {
        StringBuffer sb = new StringBuffer();
        sb.append("- Statistics -----------\n");
        sb.append(" global count: ").append(count).append('\n');
        for(Clipboard c : stats.keySet()){
            sb.append(" ").append(c.toString()).append(" : ").append(stats.get(c).val()).append('\n');
        }
        return sb.toString();
    }
}
