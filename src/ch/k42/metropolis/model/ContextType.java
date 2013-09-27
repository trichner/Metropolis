package ch.k42.metropolis.model;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public enum ContextType {
    ROAD("road"),
    HIGHRISE("highrise"),
    MIDRISE("midrise"),
    NEIGHBOURHOOD("neighbourhood"),
    INDUSTRIAL("industrial"),
    FARM("farm"),
    PARK("park"),
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
