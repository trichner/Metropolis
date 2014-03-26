package ch.k42.metropolis.minions;

/**
 * Incrementor
 * Created by Thomas on 04.02.14.
 */
public class Incr {
    private int value=0;

    public Incr incr(){
        value++;
        return this;
    }

    public void decr(){
        value--;
    }

    public int val(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Incr inc = (Incr) o;

        if (value != inc.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(val());
    }
}
