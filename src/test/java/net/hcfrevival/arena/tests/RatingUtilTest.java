package net.hcfrevival.arena.tests;

import net.hcfrevival.arena.stats.impl.PlayerStatHolder;
import net.hcfrevival.arena.util.RatingUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class RatingUtilTest {
    @Test
    public void TestCalc() {
        PlayerStatHolder winner = new PlayerStatHolder(UUID.randomUUID(), "Winner");
        PlayerStatHolder loser = new PlayerStatHolder(UUID.randomUUID(), "Loser");

        winner.setTotalHits(280);
        winner.setBiggestCombo(8);
        winner.setSprintResetHits(140);
        winner.addPotionAccuracy(0.89);

        loser.setTotalHits(320);
        loser.setBiggestCombo(6);
        loser.setSprintResetHits(120);
        loser.addPotionAccuracy(0.91);

        /* winner.setTotalHits(400);
        winner.setBiggestCombo(10);
        winner.setSprintResetHits(300);
        winner.addPotionAccuracy(0.95);

        loser.setTotalHits(180);
        loser.setBiggestCombo(4);
        loser.setSprintResetHits(40);
        loser.addPotionAccuracy(0.79); */

        int[] calculations = RatingUtil.calculateRatingChangeExperimental(winner, loser, 1200, 980);

        System.out.println("Calculations: " + calculations[0] + ", " + calculations[1]);
        Assert.assertEquals(calculations.length, 2);
    }
}
