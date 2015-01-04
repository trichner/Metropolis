package ch.k42.metropolis.minions.bitfields;

import ch.k42.metropolis.minions.vectors.Vec2D;

/**
 * Created on 04.01.2015.
 *
 * @author Thomas
 */
public class Bitfield8x8 {

    private long field = 0L;

    public Bitfield8x8(long field) {
        this.field = field;
    }

    public Bitfield8x8() {}

    public boolean isSet(int x,int y){
        return Bitfield8Utils.isSet(x, y, field);
    }

    public Bitfield8x8 set(int x, int y){
        field |= Bitfield8Utils.xyToBit(x, y);
        return this;
    }

    public Bitfield8x8 shiftLeft(){
        field = Bitfield8Utils.shiftW(field);
        return  this;
    }

    public Bitfield8x8 shiftUp(){
        field = Bitfield8Utils.shiftN(field);
        return  this;
    }

    public Bitfield8x8 shiftRight(){
        field = Bitfield8Utils.shiftO(field);
        return  this;
    }

    public Bitfield8x8 shiftDown(){
        field = Bitfield8Utils.shiftS(field);
        return  this;
    }

    public Bitfield4x4 reduce(){
        //--- align
        int dx = 0, dy = 0;
        if(field!=0) {
            while ((field & Bitfield8Utils.BORDER_D) == 0) {
                shiftDown();
                dy++;
            }
            while ((field & Bitfield8Utils.BORDER_L) == 0) {
                shiftLeft();
                dx++;
            }
        }
        //--- map
        short field4 = Bitfield4Utils.map8x8(field);
        return new Bitfield4x4(field4,new Vec2D(dx,dy));
    }


    @Override
    public String toString() {
        return Bitfield8Utils.toString(field);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bitfield8x8 that = (Bitfield8x8) o;

        if (field != that.field) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (field ^ (field >>> 32));
    }
}
