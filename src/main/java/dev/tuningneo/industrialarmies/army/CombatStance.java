package dev.tuningneo.industrialarmies.army;

public enum CombatStance {
    PASSIVE,
    DEFENSIVE,
    AGGRESSIVE;

    public static CombatStance byId(int id) {
        CombatStance[] values = values();
        return id >= 0 && id < values.length ? values[id] : DEFENSIVE;
    }

    public CombatStance next() {
        CombatStance[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
