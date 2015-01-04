package ch.k42.metropolis.minions;

import ch.k42.metropolis.minions.bitfields.Bitfield4x4;
import ch.k42.metropolis.minions.bitfields.Bitfield8Utils;
import ch.k42.metropolis.minions.bitfields.Bitfield8x8;

/**
 * Created on 04.01.2015.
 *
 * @author Thomas
 */
public class Test {

    public static void main(String[] args) {
        long[][] FIELD = Bitfield8Utils.FIELD;
        long TEST = FIELD[0][0] | FIELD[0][3] | FIELD[1][1] | FIELD[2][0] | FIELD[2][2] | FIELD[3][3];
        Bitfield8Utils.printBitfield(TEST);
        TEST = Bitfield8Utils.shiftN(TEST);
        TEST = Bitfield8Utils.shiftN(TEST);
        TEST = Bitfield8Utils.shiftN(TEST);

        TEST = Bitfield8Utils.shiftO(TEST);
        TEST = Bitfield8Utils.shiftO(TEST);


        Bitfield8Utils.printBitfield(TEST);

        Bitfield8x8 b8 = new Bitfield8x8(TEST);

        Bitfield4x4 b4 = b8.reduce();
        System.out.println(b4.toString());

    }
}
