package moe.seikimo.altservice.player;

import moe.seikimo.altservice.utils.ThreadUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for ticking the player instances.
 * This is used to fake a "client" tick for the server.
 * Some packets need to be sent ever x ticks.
 */
public final class PlayerTickThread extends Thread {

    /**
     * Duration between ticks. (milliseconds)
     */
    public static final int TICK_INTERVAL = 20;

    public final AtomicBoolean running = new AtomicBoolean(true);

    @Override
    public void run() {
        while (running.get()) {
            // Sleep for the tick interval.
            ThreadUtils.sleep(PlayerTickThread.TICK_INTERVAL);

            // Tick all players.
            PlayerManager.getPlayers().forEach(Player::tick);
        }
    }

    public void shutdown() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }
}
