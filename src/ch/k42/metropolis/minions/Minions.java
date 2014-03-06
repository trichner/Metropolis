package ch.k42.metropolis.minions;

/**
 * Created by Thomas on 06.03.14.
 */
public class Minions {
    /**
     * Limits value x between [0,max)
     * @param max
     * @param x
     * @return "Math.min(max,Math.max (0,x))";
     */
    public static final int limit(int max, int x){
        if(x<0) return 0;
        else if(x>=max) return max-1;
        return x;
    }
}
