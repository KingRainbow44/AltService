package moe.seikimo.altservice.utils;

public interface ThreadUtils {
    /**
     * Sleeps the current thread without an exception.
     *
     * @param duration The duration to sleep for.
     */
    static void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ignored) { }
    }
}
