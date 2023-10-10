package moe.seikimo.altservice.utils;

import java.util.Random;

public interface RandomUtils {
    Random RANDOM = new Random();

    /**
     * Returns a random integer between min and max.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A random integer between min and max.
     */
    static int randomInt(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }
}
