package ch.k42.metropolis.grid.urbanGrid.clipboard;


import java.io.FileNotFoundException;
import java.util.List;

import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.plugin.MetropolisPlugin;

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
     *
     * @param plugin the metropolis plugin
     * @throws Exception
     */
    public void loadClips(MetropolisPlugin plugin) throws FileNotFoundException;


    /**
     * Returns a list containing all available clipboards that match the size and context
     *
     * @deprecated use getRoadFit(roadType,contextType) instead
     * @param roadType    defines the type of the road, only applies if context is STREET
     * @return list containing all matching clipboards, might be empty but never null
     */
    @Deprecated
    public List<Clipboard> getRoadFit(RoadType roadType);

    /**
     * Returns a list containing all available clipboards that match the size and context
     *
     * @param roadType    defines the type of the road, only applies if context is STREET
     * @param contextType the context of the road
     * @return list containing all matching clipboards, might be empty but never null
     */
    public List<Clipboard> getRoadFit(RoadType roadType,ContextType contextType);

    /**
     * Returns a list containing all available clipboards that match the size and context
     *
     * @param size  size in chunks
     * @param contextType context of the structure
     * @return list containing all matching clipboards, might be empty but never null
     */
    public List<Clipboard> getFit(Cartesian2D size, ContextType contextType,SchematicType schematicType, Direction roadDir);

    /**
     * Returns a list containing all available clipboards that match the size and context
     *
     * @param size  size in chunks
     * @param schematicType the type of the schematic context of the structure
     * @return list containing all matching clipboards, might be empty but never null
     */
    public List<Clipboard> getFit(Cartesian2D size, SchematicType schematicType, Direction roadDir);

}
