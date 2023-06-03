package moe.seikimo.altservice.utils;

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
        var units = new String[]{"s", "m", "h", "d", "w", "y"};

        var time = 0L;
        try {
            for (int i = 0; i < units.length; i++) {
                var unit = units[i];
                var index = input.indexOf(unit);
                var indexLast = i -1 == -1 ? 0 : input.indexOf(units[i - 1]);
                if (index == -1) continue;

                var value = input.substring(indexLast, index);
                if (value.isEmpty()) continue;
                time += Long.parseLong(value) * getTimeUnitMultiplier(unit.charAt(0));
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
