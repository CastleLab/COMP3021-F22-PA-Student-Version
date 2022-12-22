package hk.ust.comp3021.replay;


import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.actions.Exit;
import hk.ust.comp3021.game.AbstractSokobanGame;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.game.RenderingEngine;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

import static hk.ust.comp3021.utils.StringResources.*;

/**
 * A thread-safe Sokoban game.
 * The game should be able to run in a separate thread, and games running in parallel should not interfere with each other.
 * <p>
 * The game can run in two modes:
 * 1. {@link Mode#ROUND_ROBIN} mode: all input engines take turns to perform actions, starting from the first specified input engine.
 * Example: suppose there are two input engines, A and B, whose actions are [R, L], [R, L], respectively.
 * In this mode, the game will perform the following actions in order: A.R, B.R, A.L, B.L.
 * 2. {@link Mode#FREE_RACE} mode: all input engines perform actions simultaneously. The actions processed can be in any order.
 * There could be a chance that two runs of the same game process actions in different orders.
 * <p>
 * {@link hk.ust.comp3021.Sokoban#replayGame(int, String, Mode, int, String[])} runs multiple games in parallel.
 */
public class ReplaySokobanGame extends AbstractSokobanGame {
    /**
     * Mode of scheduling actions among input engines.
     */
    public enum Mode {
        /**
         * All input engines take turns to perform actions, starting from the first specified input engine.
         */
        ROUND_ROBIN,

        /**
         * All input engines perform actions concurrently without enforcing the order.
         */
        FREE_RACE,
    }

    protected final Mode mode;
    /**
     * Indicated the frame rate of the rendering engine (in FPS).
     */
    protected final int frameRate;

    /**
     * Default frame rate.
     */
    protected static final int DEFAULT_FRAME_RATE = 60;

    /**
     * The list of input engines to fetch inputs.
     */
    protected final List<? extends InputEngine> inputEngines;

    /**
     * The rendering engine to render the game status.
     */
    protected final RenderingEngine renderingEngine;

    private int exhaustedInputEngines = 0;

    /**
     * Create a new instance of ReplaySokobanGame.
     * Each input engine corresponds to an action file and will produce actions from the action file.
     *
     * @param mode            The mode of the game.
     * @param frameRate       Rendering fps.
     * @param gameState       The game state.
     * @param inputEngines    the input engines.
     * @param renderingEngine the rendering engine.
     * @throws IllegalArgumentException when there are more than two players in the map.
     */
    public ReplaySokobanGame(
        @NotNull Mode mode,
        int frameRate,
        @NotNull GameState gameState,
        @NotNull List<? extends InputEngine> inputEngines,
        @NotNull RenderingEngine renderingEngine
    ) {
        super(gameState);
        if (inputEngines.size() == 0)
            throw new IllegalArgumentException("No input engine specified");
        this.mode = mode;
        this.frameRate = frameRate;
        this.renderingEngine = renderingEngine;
        this.inputEngines = inputEngines;
    }

    /**
     * @param gameState       The game state.
     * @param inputEngines    the input engines.
     * @param renderingEngine the rendering engine.
     */
    public ReplaySokobanGame(
        @NotNull GameState gameState,
        @NotNull List<? extends InputEngine> inputEngines,
        @NotNull RenderingEngine renderingEngine) {
        this(Mode.FREE_RACE, DEFAULT_FRAME_RATE, gameState, inputEngines, renderingEngine);
    }

    @Override
    protected synchronized boolean shouldStop() {
        return (state.isWin() || exhaustedInputEngines == inputEngines.size());
    }

    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean firstRendered = new AtomicBoolean(true);
    private final Condition firstRenderCond = lock.newCondition();
    private final AtomicBoolean someActionAfterRender = new AtomicBoolean(false);
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
    private final Condition roundRobinCond = lock.newCondition();

    private Semaphore[] robinChain;

    /**
     * The implementation of the Runnable for each input engine thread.
     * Each input engine should run in a separate thread.
     * <p>
     * Assumption:
     * 1. the last action fetch-able from the input engine is always an {@link Exit} action.
     * <p>
     * Requirements:
     * 1. All actions fetched from input engine should be processed in the order they are fetched.
     * 2. All actions before (including) the first {@link Exit} action should be processed
     * (passed to {@link this#processAction} method).
     * 3. Any actions after the first {@link Exit} action should be ignored
     * (not passed to {@link this#processAction}).
     */
    private class InputEngineRunnable implements Runnable {
        private final int index;
        private final InputEngine inputEngine;

        private InputEngineRunnable(int index, @NotNull InputEngine inputEngine) {
            this.index = index;
            this.inputEngine = inputEngine;
        }

        @Override
        public void run() {
            var exited = false;
            try {
                while (true) {
                    final var action = inputEngine.fetchAction();
                    if (mode == Mode.ROUND_ROBIN) {
                        robinChain[index].acquire();
                    }
                    lock.lock();
                    try {
                        if (firstRendered.get()) {
                            firstRenderCond.await();
                        }
                        if (shouldStop()) {
                            firstRenderCond.signalAll();
                            roundRobinCond.signalAll();
                            break;
                        }
                        if (!exited) {
                            final var result = processAction(action);
                            if (result instanceof ActionResult.Failed failed) {
                                renderingEngine.message(failed.getReason());
                            }
                            if (action instanceof Exit) {
                                exhaustedInputEngines++;
                                exited = true;
                            } else {
                                someActionAfterRender.set(true);
                            }
                        }
                    } finally {
                        if (mode == Mode.ROUND_ROBIN) {
                            robinChain[(index + 1) % robinChain.length].release();
                        }
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The implementation of the Runnable for the rendering engine thread.
     * The rendering engine should run in a separate thread.
     * <p>
     * Requirements:
     * 1. The game map should be rendered at least once before any action is processed (the initial state should be rendered).
     * 2. The game map should be rendered after the last action is processed (the final state should be rendered).
     */
    private class RenderingEngineRunnable implements Runnable {
        /**
         * NOTE: You are NOT allowed to use {@link java.util.Timer} or {@link java.util.TimerTask} in this method.
         * Please use a loop with {@link Thread#sleep(long)} instead.
         */
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long renderCount = 0;
            do {
                try {
                    var nextRenderTime = startTime + renderCount * 1000 / frameRate;
                    var timeToWait = nextRenderTime < System.currentTimeMillis() ? 0 : nextRenderTime - System.currentTimeMillis();
                    //noinspection BusyWait
                    Thread.sleep(timeToWait);
                    renderCount++;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lock.lock();
                final var undoQuotaMessage = state.getUndoQuota()
                    .map(it -> String.format(UNDO_QUOTA_TEMPLATE, it))
                    .orElse(UNDO_QUOTA_UNLIMITED);
                renderingEngine.message(undoQuotaMessage);
                renderingEngine.render(state);
                if (firstRendered.get()) {
                    firstRendered.set(false);
                    firstRenderCond.signalAll();
                }
                someActionAfterRender.set(false);
                if (shouldStop() && !someActionAfterRender.get()) {
                    lock.unlock();
                    break;
                }
                lock.unlock();
            } while (true);
        }
    }

    /**
     * Start the game.
     * This method should spawn new threads for each input engine and the rendering engine.
     * This method should wait for all threads to finish before return.
     */
    @Override
    public void run() {
        robinChain = new Semaphore[inputEngines.size()];
        final var threads = new ArrayList<Thread>(inputEngines.size() + 1);
        for (var i = 0; i < inputEngines.size(); i++) {
            final var th = new Thread(new InputEngineRunnable(i, inputEngines.get(i)));
            final var s = new Semaphore(0);
            robinChain[i] = s;
            threads.add(i, th);
        }
        robinChain[0].release();
        threads.add(inputEngines.size(), new Thread(new RenderingEngineRunnable()));
        threads.forEach(Thread::start);
        threads.forEach(th -> {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        renderingEngine.message(GAME_EXIT_MESSAGE);
        if (state.isWin()) {
            renderingEngine.message(WIN_MESSAGE);
        }
    }

}
