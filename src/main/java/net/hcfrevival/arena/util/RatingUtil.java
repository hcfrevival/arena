package net.hcfrevival.arena.util;

import net.hcfrevival.arena.stats.impl.PlayerStatHolder;

public final class RatingUtil {
    private static final int BASE_K = 20;
    private static final double ALPHA = 0.2;
    private static final double BETA = 0.4;
    private static final double GAMMA = 0.4;
    private static final double DELTA = 0.2;
    private static final double MIN_LOSS = 5;

    private static double calculateDynamicK(double baseK, double sprintResetAccuracy1, double sprintResetAccuracy2,
                                     double totalHits1, double totalHits2, double totalDamage1, double totalDamage2,
                                     double potionAccuracy1, double potionAccuracy2) {
        return baseK + ALPHA * (potionAccuracy1 - potionAccuracy2) + BETA * (totalHits1 - totalHits2) +
                GAMMA * (totalDamage1 - totalDamage2) + DELTA * (sprintResetAccuracy1 - sprintResetAccuracy2);
    }

    private static double calculateDynamicK(boolean isWinner, double baseK, double sprintResetAccuracy1, double sprintResetAccuracy2,
                                     double totalHits1, double totalHits2, double totalDamage1, double totalDamage2,
                                     double potionAccuracy1, double potionAccuracy2) {
        double statsImpact = ALPHA * (potionAccuracy1 - potionAccuracy2) + BETA * (totalHits1 - totalHits2) +
                GAMMA * (totalDamage1 - totalDamage2) + DELTA * (sprintResetAccuracy1 - sprintResetAccuracy2);
        return isWinner ? (baseK + statsImpact) : (baseK - statsImpact);
    }

    public static int[] calculateRatingChangeExperimental(
            PlayerStatHolder winnerStats,
            PlayerStatHolder loserStats,
            int winnerRating,
            int loserRating
    ) {
        double winnerSprintResetAccuracy = winnerStats.getSprintResetAccuracy();
        double loserSprintResetAccuracy = loserStats.getSprintResetAccuracy();
        double winnerTotalHits = winnerStats.getTotalHits();
        double loserTotalHits = loserStats.getTotalHits();
        double winnerTotalDamage = winnerStats.getTotalDamage();
        double loserTotalDamage = loserStats.getTotalDamage();
        double winnerPotionAccuracy = winnerStats.getPotionAccuracy() * 100;
        double loserPotionAccuracy = loserStats.getPotionAccuracy() * 100;

        double expectedScoreWinner = 1 / (1 + Math.pow(10, (loserRating - winnerRating) / 400));
        double expectedScoreLoser = 1 - expectedScoreWinner;

        double dynamicKW = calculateDynamicK(true, BASE_K, winnerSprintResetAccuracy, loserSprintResetAccuracy,
                winnerTotalHits, loserTotalHits, winnerTotalDamage, loserTotalDamage,
                winnerPotionAccuracy, loserPotionAccuracy);

        double dynamicKL = calculateDynamicK(false, BASE_K, loserSprintResetAccuracy, winnerSprintResetAccuracy,
                loserTotalHits, winnerTotalHits, loserTotalDamage, winnerTotalDamage,
                loserPotionAccuracy, winnerPotionAccuracy);

        dynamicKL = Math.max(dynamicKL, -MIN_LOSS);

        int newEloWinner = (int)Math.round(winnerRating + dynamicKW * (1 - expectedScoreWinner));
        int newEloLoser = (int)Math.round(loserRating + dynamicKL * (0 - expectedScoreLoser));

        return new int[]{newEloWinner, newEloLoser};
    }
}
