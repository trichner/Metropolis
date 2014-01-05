package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.RoadType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 13.10.13
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public interface ClipboardProvider {

    /**
     * Loads all available schematics, this method must be executed before any
     * get is called in order to get actual schematics
     * @param generator the generator
     * @throws Exception
     */
    public void loadClips(MetropolisGenerator generator) throws Exception;


    /**
     * Returns a list containing all available clipboards that match the size and context
     * @param chunkX chunksize in X direction
     * @param chunkZ chunksize in Z direction
     * @param contextType context of the structure
     * @param roadType defines the type of the road, only applies if context is STREET
     * @return list containing all matching clipboards, might be empty but never null
     */
    public List<Clipboard> getRoadFit(int chunkX, int chunkZ, ContextType contextType, RoadType roadType);

    /**
     * Returns a list containing all available clipboards that match the size and context
     * @param chunkX chunksize in X direction
     * @param chunkZ chunksize in Z direction
     * @param contextType context of the structure
     * @return list containing all matching clipboards, might be empty but never null
     */
    public List<Clipboard> getFit(int chunkX, int chunkZ,ContextType contextType, Direction roadDir, boolean roadFacing);

}
