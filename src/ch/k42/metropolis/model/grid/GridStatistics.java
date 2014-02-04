package ch.k42.metropolis.model.grid;


import ch.k42.metropolis.WorldEdit.Clipboard;

import javax.sound.sampled.Clip;

/**
 * This should keep track of statistics of a Grid, not
 * implemented yet.
 *
 * @author Thomas Richner
 */
public interface GridStatistics {

    public void logSchematic(Clipboard p);

    /**
     * How many times this clipboard has been placed
     * @param p clipboard to evaluate
     * @return absolute count of this clipboard on this grid
     */
    public int getCount(Clipboard p);

    /**
     * How many times a clipboard has been placed in relation
     * to all placed clipboards on this grid
     * @param p clipboard to evaluate
     * @return p_count/global_count or 0 if none placed
     */
    public double getRelativeCount(Clipboard p);

    /**
     * Returns the current statistics in a human readable format
     * @return
     */
    public String printStatistics();

}
