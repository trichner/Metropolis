package ch.k42.metropolis.minions;

/**
 * Created on 04.01.2015.
 *
 * @author Thomas
 */
public class Bitfield4Utils {
    /*
     *
     * 3 12 13 14 15
     * 2  8  9 10 11
     * 1  4  5  6  7
     * 0  0  1  2  3
     *    0  1  2  3
     */
    public static short BIT00 = (short) (0x1 << 0);
    public static short BIT01 = (short) (0x1 << 1);
    public static short BIT02 = (short) (0x1 << 2);
    public static short BIT03 = (short) (0x1 << 3);
    public static short BIT10 = (short) (0x1 << 4);
    public static short BIT11 = (short) (0x1 << 5);
    public static short BIT12 = (short) (0x1 << 6);
    public static short BIT13 = (short) (0x1 << 7);
    public static short BIT20 = (short) (0x1 << 8);
    public static short BIT21 = (short) (0x1 << 9);
    public static short BIT22 = (short) (0x1 << 10);
    public static short BIT23 = (short) (0x1 << 11);
    public static short BIT30 = (short) (0x1 << 12);
    public static short BIT31 = (short) (0x1 << 13);
    public static short BIT32 = (short) (0x1 << 14);
    public static short BIT33 = (short) (0x1 << 15);

    final public static short[] BIT = {BIT00, BIT01, BIT02, BIT03, BIT10, BIT11, BIT12, BIT13, BIT20,
            BIT21, BIT22, BIT23, BIT30, BIT31, BIT32, BIT33};

    final public static short[][] FIELD =  {{BIT00, BIT01, BIT02, BIT03},
                                           {BIT10, BIT11, BIT12, BIT13},
                                           {BIT20, BIT21, BIT22, BIT23},
                                           {BIT30, BIT31, BIT32, BIT33}};

    final static short BORDER_L  = (short) (BIT00 | BIT10 | BIT20 | BIT30);
    final static short BORDER_R  = (short) (BIT03 | BIT13 | BIT23 | BIT33);
    final static short BORDER_U  = (short) (0xF << 12);
    final static short BORDER_D  = (short) 0xF;

    /**
     * Shifts a (long) as 8x8 Bitfield one to the left
     * @param BITFIELD a (long) to shift
     */
    final public static short shiftLeft(long BITFIELD){
        BITFIELD >>>= 1;
        BITFIELD &= ~BORDER_R;
        return (short) BITFIELD;
    }
    /**
     * Shifts a (long) as 8x8 Bitfield one to the right
     * @param BITFIELD a (long) to shift
     */
    final public static short shiftRight(long BITFIELD){
        BITFIELD <<= 1;
        BITFIELD &= ~BORDER_L;
        return (short) BITFIELD;
    }
    /**
     * Shifts a (long) as 8x8 Bitfield one up
     * @param BITFIELD a (long) to shift
     */
    final public static short shiftUp(long BITFIELD){
        BITFIELD <<= 4;
        return (short) BITFIELD;
    }
    /**
     * Shifts a (long) as 8x8 Bitfield one down
     * @param BITFIELD a (long) to shift
     */
    final public static short shiftDown(long BITFIELD){
        BITFIELD >>>= 4;
        return (short) BITFIELD;
    }

    /**
     * Returns a Bitfield on which only the
     * given Bit ist set
     * @param x x-Coordinate
     * @param y y-Coordinate
     */
    final public static short xyToBit(int x, int y){
        if(x>7||y>7||x<0||y<0) return 0;
        return FIELD[x][y];
    }

    final public static short map8x8(long field8x8){
        short field = (short) (field8x8 & 0xF);
        field |= (short) ((field8x8 & (0xF << 8))  >>> 4);
        field |= (short) ((field8x8 & (0xF << 16)) >>> 8);
        field |= (short) ((field8x8 & (0xF << 24)) >>> 12);
        return field;
    }

    /**
     * Looks if the bit at the given Coordinates is set
     * @param x x-Coordinate
     * @param y y-Coordinate
     * @param BITFIELD bitfield
     * @return true if set, else false
     */
    final public static boolean isSet(int x, int y, short BITFIELD){
        //             xyToBit
        if(x>7||y>7||x<0||y<0) return false;
        return isSet(FIELD[x][y],BITFIELD);
    }
    /**
     * Looks if the given bit is set.
     * If BIT has several bits set, it looks if
     * at least one is set on BITFIELD
     * @param BIT the bit
     * @param BITFIELD the bitfield to test
     * @return true if set, else false
     */
    final public static boolean isSet(short BIT, short BITFIELD){
        return (BIT & BITFIELD) != 0;
    }

    /**
     * Gives back a String representation of a 8x8 Bitfield
     * @param BITFIELD Bitfield to represent
     * @return a String containing the representation
     */
    final public static String toString(short BITFIELD){
        StringBuffer str = new StringBuffer();
        str.append("-BITFIELD ---\n");
        int start = 12,end = 16;
        //first row
        while(start>=0){
            for(int i=start;i<end;i++){
                str.append(isSet(BIT[i],BITFIELD) ? " X" : " O");
            }
            str.append('\n');
            start -=4; end -=4;
        }
        str.append("-------------\n");
        return str.toString();
    }
}
