package moe.seikimo.altservice.event;

import moe.seikimo.altservice.AltBackend;

import java.lang.reflect.InvocationTargetException;

/**
 * Represents an invokable and listenable event.
 */
public abstract class Event {
    /**
     * Makes an event.
     * Automatically calls it.
     *
     * @param eventClass The event class.
     * @param args The arguments.
     */
    public static void make(Class<? extends Event> eventClass, Object... args) {
        if (args.length % 2 != 0)
            throw new IllegalArgumentException("Arguments must be in pairs.");

        try {
            var constructorArgs = new Object[args.length / 2];
            var constructorArgsTypes = new Class<?>[args.length / 2];

            // Get the constructor arguments.
            for (var i = 0; i < args.length; i += 2) {
                constructorArgs[i / 2] = args[i + 1];
                constructorArgsTypes[i / 2] = (Class<?>) args[i];
            }

            // Create & call the event.
            var eventInstance = eventClass
                    .getDeclaredConstructor(constructorArgsTypes)
                    .newInstance(constructorArgs);
            eventInstance.call();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            AltBackend.getPluginManager().getLogger().warn("Unable to call event {}.", eventClass.getSimpleName(), e);
        }
    }

    /**
     * Calls this event.
     */
    public final void call() {
        AltBackend.getPluginManager().callEvent(this);
    }
}
