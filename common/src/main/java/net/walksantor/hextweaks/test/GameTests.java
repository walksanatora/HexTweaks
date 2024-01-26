package net.walksantor.hextweaks.test;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class GameTests {
    @GameTest
    public static void basic(GameTestHelper ctx) {
        ctx.succeed();
    }
}
