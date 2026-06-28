package dev.tuningneo.industrialarmies.army;

public enum SoldierRank {
    RECRUIT,
    REGULAR,
    VETERAN,
    SERGEANT;

    public static SoldierRank byId(int id) {
        SoldierRank[] values = values();
        return id >= 0 && id < values.length ? values[id] : RECRUIT;
    }
}
