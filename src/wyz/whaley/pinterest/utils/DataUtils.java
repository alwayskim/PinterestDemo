package wyz.whaley.pinterest.utils;

import java.util.Random;

public class DataUtils {
    // private static final int[] mResolutionHeight = { 200, 220, 240, 260, 280
    // };
    // private static final int[] mResolutionWidth = { 130, 140, 150, 160, 170
    // };
     private static final int[] mResolutionHeight = { 150 };
     private static final int[] mResolutionWidth = { 100 };

    private static Random random = new Random();

    public static int getRandomWidth() {
        return mResolutionWidth[random.nextInt(mResolutionWidth.length)];
    }

    public static int getRandomHeight() {
        return mResolutionHeight[random.nextInt(mResolutionHeight.length)];
    }
}
