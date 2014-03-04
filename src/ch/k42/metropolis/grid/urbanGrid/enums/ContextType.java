package ch.k42.metropolis.grid.urbanGrid.enums;

/**
 * Enum for diffrent Contexts
 * Possible values: road, highrise,midrise,neighbourhood,industrial,farm,park,undefined
 *
 * @author Thomas Richner
 */
public enum ContextType {
    STREET("street"),
    HIGHRISE("highrise"),
    MIDRISE("midrise"),
    LOWRISE("lowrise"),
    RESIDENTIAL("residential"),
    INDUSTRIAL("industrial"),
    FARM("farm"),
    PARK("park"),
    HIGHWAY("highway"),
    UNDEFINED("undefined");
    public String string;

    ContextType(String str) {
        this.string = str;
    }

    public static ContextType getByString(String string) {
        for (ContextType d : ContextType.values()) {
            if (d.string.equals(string))
                return d;
        }
        return null;
    }
}
