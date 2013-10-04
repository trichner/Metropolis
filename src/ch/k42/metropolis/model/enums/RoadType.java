package ch.k42.metropolis.model.enums;

/**
 * Defines the exact type of road/street
 *
 * @author Thomas Richner
 */
public enum RoadType {
//            street_curve-EN_v1.schematic   street_straight-NS_v1.schematic
//            street_curve_SE_v1.schematic   street_straight-WE_v1.schematic
//            street_curve-SW_v1.schematic   street_t-cross-EW_v1.schematic
//            street_curve-WN_v1.schematic   street_t-cross-NS_v1.schematic
//            street_deadend-E_v1.schematic  street_t-cross_SN_v1.schematic
//            street_deadend-N_v1.schematic  street_t-cross-WE_v1.schematic
//            street_deadend-S_v1.schematic  street_x-cross_v1.schematic
//            street_deadend_W_v1.schematic
    ROAD_X,
    ROAD_I_NS,
    ROAD_I_EW,
    ROAD_T_NS_W,
    ROAD_T_NS_E,
    ROAD_T_EW_N,
    ROAD_T_EW_S,
    ROAD_C_SE,
    ROAD_C_SW,
    ROAD_C_NW,
    ROAD_C_NE,
    ROAD_D_N,
    ROAD_D_S,
    ROAD_D_E,
    ROAD_D_W,
    HIGHWAY_CORNER_NE,
    HIGHWAY_CORNER_NW,
    HIGHWAY_CORNER_SE,
    HIGHWAY_CORNER_SW,
    HIGHWAY_SIDE_N,
    HIGHWAY_SIDE_E,
    HIGHWAY_SIDE_S,
    HIGHWAY_SIDE_W,
    HIGHWAY_SIDE_T_N,
    HIGHWAY_SIDE_T_E,
    HIGHWAY_SIDE_T_S,
    HIGHWAY_SIDE_T_W,
    NONE;
}
