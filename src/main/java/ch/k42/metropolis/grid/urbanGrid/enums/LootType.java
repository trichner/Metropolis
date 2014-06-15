package ch.k42.metropolis.grid.urbanGrid.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 29.09.13
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public enum LootType {
    INDUSTRIAL("industrial"),
    RESIDENTIAL("residential"),
    OFFICE("office"),
    MILITARY("military"),
    MEDICAL("medical"),
    STORE("store"),
    GRAVE("grave"),
    HIDDEN("hidden");
    public final String name;

    private LootType(String name) {
        this.name = name;
    }
}
