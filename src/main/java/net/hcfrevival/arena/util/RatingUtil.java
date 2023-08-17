package net.hcfrevival.arena.util;

public final class RatingUtil {
    private static final int K_FACTOR = 30;

    /**
     * Calculates the expected outcome chance between
     * the two provided ratings.
     *
     * @param rating Rating A
     * @param otherRating Rating B
     * @return Expected outcome ratio
     */
    public static double calculateExpectedOutcome(int rating, int otherRating) {
        return 1.0 / (1.0 + Math.pow(10.0, (otherRating - rating) / 400.0));
    }

    /**
     * Calculate the rating change between the two provided ratings
     * @param a Rating A
     * @param b Rating B
     * @param outcome 1 if A wins, 0.5 for a draw, 0 is B wins
     * @return int array, index 0 is A, index 1 is B
     */
    public static int[] calculateRatingChange(int a, int b, double outcome) {
        final double expectedA = calculateExpectedOutcome(a, b);
        final double expectedB = 1 - expectedA;

        final int updatedA = a + (int)(K_FACTOR * (outcome - expectedA));
        final int updatedB = b + (int)(K_FACTOR * ((1 - outcome) - expectedB));

        return new int[]{updatedA, updatedB};
    }
}
