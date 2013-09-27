package ch.k42.metropolis.minions;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 27.09.13
 * Time: 20:42
 * To change this template use File | Settings | File Templates.
 */
public class Nimmersatt {
    private static enum State{
        TEXT,
        START,
        COMMENT,
        STRING,
        END,
        ESC
    }

    private static char SLASH = '/';
    private static char BSLASH = '\\';
    private static char STAR = '*';
    private static char QUOTE = '\"';

    /**
     * Removes all comments in the form: < slash >< star > ... < star >< slash >
     * This method was written to strap JSON documents of comments, nothing
     * is guaranteed.
     * @param text a (commented) JSON string
     * @return the same string without any comments
     */
    public static String friss(String text){
        StringBuffer buf = new StringBuffer(text.length()>>1);
        char c;
        State state = State.TEXT;
        for(int i=0;i<text.length();i++){
            c = text.charAt(i);
            switch (state){ //FSM (Finite State Machine)
                case TEXT:
                    if(c==SLASH){
                        state= State.START;
                        break;
                    }else if(c==QUOTE){
                        state= State.STRING;
                    }
                    buf.append(c);
                    break;
                case STRING:
                    if(c==BSLASH){
                        state= State.ESC;
                    }else if(c==QUOTE){
                        state= State.TEXT;
                    }
                    buf.append(c);
                    break;
                case START:
                    if(c==SLASH){
                        //nothing
                    }else if(c==STAR){
                        state= State.COMMENT;
                    }else {
                        state= State.TEXT;
                        buf.append('/');
                        buf.append(c);
                    }
                    break;

                case COMMENT:
                    if(c==STAR){
                        state= State.END;
                    }
                    break;

                case END:
                    if(c==SLASH){
                        state= State.TEXT;
                    }else if(c==STAR){
                        //nothing
                    }else {
                        state= State.COMMENT;
                    }
                    break;
                case ESC:
                    state= State.STRING;
                    buf.append(c);
                    break;
            }
        }

        return buf.toString();
    }
}
