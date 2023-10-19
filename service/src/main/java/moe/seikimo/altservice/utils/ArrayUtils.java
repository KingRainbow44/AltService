package moe.seikimo.altservice.utils;

import java.util.Arrays;

public interface ArrayUtils {
    /**
     * Fills an array with the given value.
     *
     * @param length The array length.
     * @param value The value.
     * @return The array.
     */
    static Integer[] fill(int length, int value) {
        var array = new Integer[length];
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Creates a single-element array.
     *
     * @param value The value.
     * @return The array.
     */
    static Integer[] single(int value) {
        return new Integer[] { value };
    }

    /**
     * Casts an array of primitive integers to an array of boxed integers.
     *
     * @param array The array.
     * @return The array.
     */
    static Integer[] cast(int[] array) {
        var result = new Integer[array.length];
        for (var i = 0; i < array.length; i++)
            result[i] = array[i];
        return result;
    }
}
