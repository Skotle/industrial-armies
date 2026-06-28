package dev.tuningneo.industrialarmies.army;

public enum SoldierOrder {
    FOLLOW,
    HOLD;

    public static SoldierOrder byId(int id) {
        SoldierOrder[] values = values();
        return id >= 0 && id < values.length ? values[id] : FOLLOW;
    }
}
