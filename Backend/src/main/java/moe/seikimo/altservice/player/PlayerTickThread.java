package moe.seikimo.altservice.player;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for ticking the player instances.
 * This is used to fake a "client" tick for the server.
 * Some packets need to be sent ever x ticks.
 */
public class PlayerTickThread extends Thread {

    /**
     * Duration between ticks. (milliseconds)
     */
    public static final int TICK_INTERVAL = 20;

    public final AtomicBoolean running = new AtomicBoolean(true);

    @Override
    public void run() {
        while (running.get()) {
            try {
                // Sleep for the tick interval.
                Thread.sleep(PlayerTickThread.TICK_INTERVAL);

                // Tick all players.
                PlayerManager.getPlayers().forEach(Player::tick);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }
}
