package ch.k42.metropolis.grid.urbanGrid.statistics;


import java.util.HashMap;
import java.util.Map;

import ch.k42.metropolis.grid.urbanGrid.clipboard.Clipboard;
import ch.k42.metropolis.minions.Incr;

/**
 * Created by Thomas on 04.02.14.
 */
public class AthmosStat implements GridStatistics {

    private static class Size{
        private int x;
        private int z;

        private Size(int x, int z) {
            if(x>z){
                this.x = x;
                this.z = z;
            }else {
                this.x = z;
                this.z = x;
            }
        }

        @Override
        public String toString() {
            return " s: " + x + "/" + z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Size size = (Size) o;

            return x == size.x && z == size.z;

        }

        @Override
        public int hashCode() {
//            int result = x;
//            result = 31 * result + z;
//            return result;
            return (x << 16) | z;
        }
    }

    private Map<Clipboard,Incr> statsByName = new HashMap<>();
    private Map<Size,Incr> statsBySize = new HashMap<>();
    private Incr count = new Incr();

    private void logByName(Clipboard p){
        if(statsByName.containsKey(p)){
            statsByName.get(p).incr();
        }else{
            Incr i = new Incr();
            i.incr();
            statsByName.put(p, i);
        }
    }

    private void logBySize(Clipboard p){
        Size s = new Size(p.getSize().X,p.getSize().Z);
        if(statsBySize.containsKey(s)){
            statsBySize.get(s).incr();
        }else{
            Incr i = new Incr();
            i.incr();
            statsBySize.put(s, i);
        }
    }

    @Override
    public void logSchematic(Clipboard p) {
        if (p != null) {
            logByName(p);
            logBySize(p);
            count.incr();
        }
    }



    @Override
    public int getClipboardCount(Clipboard p) {
        if(statsByName.containsKey(p))
            return statsByName.get(p).val();
        return 0;
    }

    @Override
    public double getRelativeClipboardCount(Clipboard p) {
        int gcount = count.val();
        if(gcount==0) return 0;
        double pcount = getClipboardCount(p);
        double rel = pcount/gcount;
        return rel;
    }

    @Override
    public String printStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("- Statistics -----------\n");
        sb.append(" global count: ").append(count).append('\n');

        sb.append("---# By Name: \n");
        for(Clipboard c : statsByName.keySet()){
            sb.append(" ").append(String.format("%-10s", c.toString())).append(" : ").append(statsByName.get(c).val()).append('\n');
        }

        sb.append("---# By Size: \n");
        for(Size s : statsBySize.keySet()){
            sb.append(" ").append(String.format("%-10s", s.toString())).append(" : ").append(statsBySize.get(s).val()).append('\n');
        }

        return sb.toString();
    }
}
