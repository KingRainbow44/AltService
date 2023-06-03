package moe.seikimo.altservice.utils;

import java.util.Arrays;

public interface TimeUtils {

    /**
     * Parses a time input into a long.
     * A time input format looks something like this: 1y2w3d4h5m6s
     *
     * @param input The amount of time in the proper format.
     * @return The time in seconds.
     * @throws IllegalArgumentException If the input is invalid.
     */
    static long parseInputTime(String input) {
        var units = new Character[]{'s', 'm', 'h', 'd', 'w', 'y'};

        var time = 0L;
        try {
            StringBuilder value = new StringBuilder("0");
            for (int i = 0; i < input.length(); i++) {
                var c = input.charAt(i);
                if (Character.isDigit(c)) {
                    value.append(c);
                } else {
                    if (Arrays.stream(units).anyMatch(character -> character == c)) {
                        time += Long.parseLong(value.toString()) *
                                TimeUtils.getTimeUnitMultiplier(c);
                        value = new StringBuilder("0");
                    } else throw new IllegalArgumentException("Invalid time format.");
                }
            }
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentException("Invalid time format.");
        }

        return time;
    }

    /**
     * Gets the time unit multiplier.
     * Converts a time unit into seconds.
     * For example, m (minute) would be 60 seconds.
     *
     * @param unit The unit.
     * @return The multiplier.
     * @throws IllegalArgumentException If the unit is invalid (doesn't exist).
     */
    static int getTimeUnitMultiplier(char unit) throws IllegalArgumentException {
        return switch (unit) {
            case 's' -> // second
                    1;
            case 'm' -> // minute
                    60;
            case 'h' -> // hour
                    60 * 60;
            case 'd' -> // day
                    60 * 60 * 24;
            case 'w' -> // week
                    60 * 60 * 24 * 7;
            case 'y' -> // year
                    60 * 60 * 24 * 365;
            default -> throw new IllegalArgumentException("Invalid time unit.");
        };
    }
}
