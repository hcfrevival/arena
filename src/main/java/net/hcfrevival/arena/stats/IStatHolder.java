package net.hcfrevival.arena.stats;

import java.util.List;
import java.util.OptionalDouble;
import java.util.UUID;

public interface IStatHolder {
    UUID getOwner();
    long getSprintResetHits();
    long getTotalHits();
    long getTotalDamage();
    long getBiggestCombo();
    long getCurrentCombo();
    List<Double> getPotionAccuracyValues();

    default double getPotionAccuracy() {
        final OptionalDouble avg = getPotionAccuracyValues().stream().mapToDouble(a -> a).average();

        if (avg.isPresent()) {
            return avg.getAsDouble();
        }

        return 0.0;
    }

    void setSprintResetHits(long l);
    void setTotalHits(long l);
    void setTotalDamage(long l);
    void setBiggestCombo(long l);
    void setCurrentCombo(long l);

    default void addSprintResetHit() {
        setSprintResetHits(getSprintResetHits() + 1);
    }

    default void addTotalDamage(long dmg) {
        setTotalDamage(getTotalDamage() + dmg);
    }

    default void addTotalHits() {
        setTotalHits(getTotalHits() + 1);
    }

    default void addCurrentCombo() {
        setCurrentCombo(getCurrentCombo() + 1);

        if (getCurrentCombo() > getBiggestCombo()) {
            setBiggestCombo(getCurrentCombo());
        }
    }

    default void resetCurrentCombo() {
        setCurrentCombo(0);
    }

    default void addPotionAccuracy(double intensity) {
        getPotionAccuracyValues().add(intensity);
    }
}
