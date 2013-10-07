package ch.k42.metropolis.model.enums;

/**
 *
 * Enum for diffrent Contexts
 * Possible values: road, highrise,midrise,neighbourhood,industrial,farm,park,undefined
 * @author Thomas Richner
 *
 */
public enum ContextType {
    ROAD("road"),
    HIGHRISE("highrise"),
    MIDRISE("midrise"),
    RESIDENTIAL("residential"),
    INDUSTRIAL("industrial"),
    FARM("farm"),
    PARK("park"),
    HIGHWAY("highway"),
    UNDEFINED("undefined");
    public String string;
    ContextType(String str){
        this.string=str;
    }
    public static ContextType getByString(String string){
        for(ContextType d : ContextType.values()){
            if(d.string.equals(string))
                return d;
        }
        return null;
    }
}
