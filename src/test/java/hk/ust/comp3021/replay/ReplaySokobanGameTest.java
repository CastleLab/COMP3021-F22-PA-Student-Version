package hk.ust.comp3021.replay;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.game.*;
import hk.ust.comp3021.utils.TestExtension;
import hk.ust.comp3021.utils.TestHelper;
import hk.ust.comp3021.utils.TestKind;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.apache.commons.collections4.CollectionUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(TestExtension.class)
class ReplaySokobanGameTest {

    @DisplayName("Game's run method should spawn a new thread for rendering engine")
    @Test
    @Tag(TestKind.PUBLIC)
    void testRenderingEngineThread() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(InputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = new TestGame(gameState, List.of(inputEngine), renderingEngine);

        final var renderThreadIds = new ConcurrentLinkedQueue<Long>();
        doAnswer(invocation -> {
            final var threadID = Thread.currentThread().getId();
            renderThreadIds.add(threadID);
            return null;
        }).when(renderingEngine).render(any());
        when(inputEngine.fetchAction())
                .thenAnswer(new RandomlyPausedActionProducer(new Move.Right(0), new Exit()));

        game.run();

        assertTrue(renderThreadIds.size() > 0);
        final var renderThreadId = renderThreadIds.poll();
        while (!renderThreadIds.isEmpty()) {
            assertEquals(renderThreadId, renderThreadIds.poll());
        }
    }

    @DisplayName("Game's run method should spwan one thread for each input engine")
    @Test
    @Tag(TestKind.PUBLIC)
    void testInputEngineThread() {
        final var gameState = mock(GameState.class);
        final var inputEngine0 = mock(InputEngine.class);
        final var inputEngine1 = mock(InputEngine.class);
        final var inputEngine2 = mock(InputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = new TestGame(gameState, List.of(inputEngine0, inputEngine1, inputEngine2), renderingEngine);

        final var threadIds0 = new ConcurrentLinkedQueue<Long>();
        final var threadIds1 = new ConcurrentLinkedQueue<Long>();
        final var threadIds2 = new ConcurrentLinkedQueue<Long>();
        final var actionProducer0 = new RandomlyPausedActionProducer(new Move.Right(0), new Exit());
        final var actionProducer1 = new RandomlyPausedActionProducer(new Move.Right(1), new Exit());
        final var actionProducer2 = new RandomlyPausedActionProducer(new Move.Right(2), new Exit());
        when(inputEngine0.fetchAction()).thenAnswer(invocation -> {
            final var threadID = Thread.currentThread().getId();
            threadIds0.add(threadID);
            return actionProducer0.produce();
        });
        when(inputEngine1.fetchAction()).thenAnswer(invocation -> {
            final var threadID = Thread.currentThread().getId();
            threadIds1.add(threadID);
            return actionProducer1.produce();
        });
        when(inputEngine2.fetchAction()).thenAnswer(invocation -> {
            final var threadID = Thread.currentThread().getId();
            threadIds2.add(threadID);
            return actionProducer2.produce();
        });
        game.run();

        assertTrue(threadIds0.size() > 0);
        assertTrue(threadIds1.size() > 0);
        assertTrue(threadIds2.size() > 0);
        final var threadIds = new HashSet<Long>();
        threadIds.add(Thread.currentThread().getId());
        final var th0 = threadIds0.poll();
        while (!threadIds0.isEmpty()) {
            assertEquals(th0, threadIds0.poll());
        }
        threadIds.add(th0);
        final var th1 = threadIds1.poll();
        while (!threadIds1.isEmpty()) {
            assertEquals(th1, threadIds1.poll());
        }
        threadIds.add(th1);
        final var th2 = threadIds2.poll();
        while (!threadIds2.isEmpty()) {
            assertEquals(th2, threadIds2.poll());
        }
        threadIds.add(th2);
        assertEquals(4, threadIds.size());
    }

    @DisplayName("Game's run method should wait all threads to finish before returning")
    @Test
    @Tag(TestKind.HIDDEN)
    void testWaitThreadExit() {
        final var gameState = mock(GameState.class);
        final var inputEngine0 = mock(InputEngine.class);
        final var inputEngine1 = mock(InputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = new TestGame(gameState, List.of(inputEngine0, inputEngine1), renderingEngine);

        final var threads = new ConcurrentLinkedQueue<Thread>();
        final var actionProducer0 = new RandomlyPausedActionProducer(new Move.Right(0));
        final var actionProducer1 = new RandomlyPausedActionProducer(new Move.Right(1));
        when(inputEngine0.fetchAction()).thenAnswer(invocation -> {
            final var thread = Thread.currentThread();
            threads.add(thread);
            return actionProducer0.produce();
        });
        when(inputEngine1.fetchAction()).thenAnswer(invocation -> {
            final var thread = Thread.currentThread();
            threads.add(thread);
            return actionProducer1.produce();
        });
        doAnswer(invocation -> {
            final var thread = Thread.currentThread();
            threads.add(thread);
            return null;
        }).when(renderingEngine).render(any());

        game.run();

        assertAll(threads.stream()
                .map(thread -> () -> assertFalse(thread.isAlive())));
    }

    @Test
    @DisplayName("There should be a render invocation before the first movement")
    @Tag(TestKind.HIDDEN)
    void testRenderBeforeFirstMove() {
        final var map = """
                -1
                ######
                #Aa.@#
                ######
                """;
        final var gameMap = TestHelper.parseGameMap(map);
        final var gameState = spy(new GameState(gameMap));
        final var renderingEngine = mock(RenderingEngine.class);
        final var inputEngine = mock(InputEngine.class);

        final var rendered = new AtomicBoolean(false);
        final var firstMove = new AtomicBoolean(true);
        final var renderBeforeFirst = new AtomicBoolean(false);
        doAnswer(invocation -> {
            Thread.sleep(new Random().nextInt(10));
            rendered.set(true);
            return null;
        }).when(renderingEngine).render(any());
        when(inputEngine.fetchAction()).thenReturn(new Move.Right(0), new Exit());
        doAnswer(invocation -> {
            Thread.sleep(new Random().nextInt(10));
            if (firstMove.get())
                renderBeforeFirst.set(rendered.get());
            return null;
        }).when(gameState).move(any(), any());

        final var game = new ReplaySokobanGame(gameState, List.of(inputEngine), renderingEngine);
        game.run();

        assertTrue(renderBeforeFirst.get());
    }

    /**
     *
     */
    @DisplayName("Render method should be called after the last movement is processed")
    @Test
    @Tag(TestKind.HIDDEN)
    void testRenderAfterLastMove() {
        final var map = """
                -1
                ######
                #Aa.@#
                ######
                """;
        final var gameMap = TestHelper.parseGameMap(map);
        final var gameState = spy(new GameState(gameMap));
        final var inputEngine = mock(StreamInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);

        final var lastMoveRendered = new AtomicBoolean(false);
        doAnswer(invocation -> {
            lastMoveRendered.set(true);
            return null;
        }).when(renderingEngine).render(any());
        doAnswer(invocation -> {
            lastMoveRendered.set(false);
            return null;
        }).when(gameState).move(any(), any());
        when(inputEngine.fetchAction()).thenReturn(new Move.Right(0), new Move.Right(0), new Exit());

        final var game = new ReplaySokobanGame(gameState, List.of(inputEngine), renderingEngine);
        game.run();

        assertTrue(lastMoveRendered.get());
    }

    @DisplayName("All moves in the input engine should be processed (single input engine)")
    @Test
    @Tag(TestKind.HIDDEN)
    void testAllMovesProcessedSingle() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(StreamInputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = spy(new TestGame(gameState, List.of(inputEngine), renderingEngine));

        final var actions = Arrays.<Action>asList(
                new Move.Right(0),
                new Move.Right(0),
                new Move.Right(0),
                new Move.Right(0),
                new Move.Down(0),
                new Move.Up(0)
        );
        when(inputEngine.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions));
        final var processedActions = new ActionList();
        doAnswer(invocation -> {
            processedActions.add(invocation.getArgument(0));
            return null;
        }).when(game).processAction(any());

        game.run();

        assertAll(actions.stream()
                .map(action -> () -> assertTrue(processedActions.contains(action))));
    }

    @DisplayName("All moves in the input engine should be processed (multiple input engines)")
    @Test
    @Tag(TestKind.HIDDEN)
    void testAllMovesProcessedMultiple() {
        final var map = """
                -1
                ######
                #Aa.@#
                ######
                """;
        final var gameMap = TestHelper.parseGameMap(map);
        final var gameState = spy(new GameState(gameMap));
        final var inputEngine = mock(StreamInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        final var game = spy(new TestGame(gameState, List.of(inputEngine), renderingEngine));

        final var processedActions = new ActionList();
        final var actions =
                Arrays.<Action>asList(
                        new Move.Right(0),
                        new Move.Left(0),
                        new Move.Left(0),
                        new Move.Down(0),
                        new Move.Right(0),
                        new Move.Right(0)
                );
        when(inputEngine.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions));
        doAnswer(invocation -> {
            final var action = invocation.getArgument(0, Action.class);
            processedActions.add(action);
            return invocation.callRealMethod();
        }).when(game).processAction(any());

        game.run();

        assertArrayEquals(actions.toArray(), processedActions.toArray());
    }

    @DisplayName("Moves from the same input engine should be processed in the same order (single input engine)")
    @Test
    @Tag(TestKind.HIDDEN)
    void testMovesOrderSingle() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(StreamInputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = spy(new TestGame(gameState, List.of(inputEngine), renderingEngine));

        final var actions = Arrays.<Action>asList(new Move.Right(0), new Move.Right(0), new Move.Right(0), new Move.Right(0), new Move.Down(0), new Move.Up(0));
        when(inputEngine.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions));
        final var processedActions = new ActionList();
        doAnswer(invocation -> {
            processedActions.add(invocation.getArgument(0));
            return invocation.callRealMethod();
        }).when(game).processAction(any());

        game.run();

        assertArrayEquals(actions.toArray(), processedActions.toArray());
    }

    @DisplayName("Moves from the same input engine should be processed in the same order (multiple input engine)")
    @Test
    @Tag(TestKind.PUBLIC)
    void testMovesOrderMultiple() {
        final var gameState = mock(GameState.class);
        final var inputEngine0 = mock(StreamInputEngine.class);
        final var inputEngine1 = mock(StreamInputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = spy(new TestGame(gameState, List.of(inputEngine0, inputEngine1), renderingEngine));

        final var actions0 = Arrays.<Action>asList(new Move.Left(0), new Move.Right(0), new Move.Right(0), new Move.Right(0), new Move.Down(0), new Move.Up(0));
        final var actions1 = Arrays.<Action>asList(new Move.Left(1), new Move.Right(1), new Move.Right(1), new Move.Right(1), new Move.Down(1), new Move.Up(1));
        when(inputEngine0.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions0));
        when(inputEngine1.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions1));
        final var processedActions = new ActionList();
        doAnswer(invocation -> {
            processedActions.add(invocation.getArgument(0));
            return invocation.callRealMethod();
        }).when(game).processAction(any());

        game.run();

        assertArrayEquals(actions0.toArray(), processedActions.stream().filter(action -> action.getInitiator() == 0).toArray());
        assertArrayEquals(actions1.toArray(), processedActions.stream().filter(action -> action.getInitiator() == 1).toArray());
    }

    @DisplayName("No further invocations to the processAction method if one exit action is received")
    @Test
    @Tag(TestKind.HIDDEN)
    void testNoMoreProcessActionAfterExit() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(StreamInputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = spy(new TestGame(gameState, List.of(inputEngine), renderingEngine));

        final var actions = Arrays.asList(new Move.Right(0), new Move.Right(0), new Exit(), new Move.Right(0));
        when(inputEngine.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions));
        final var processedActions = new ActionList();
        doAnswer(invocation -> {
            processedActions.add(invocation.getArgument(0));
            return invocation.callRealMethod();
        }).when(game).processAction(any());

        game.run();

        assertEquals(2, processedActions.size());
    }

    @DisplayName("Game should stop when all input engines are exhausted (if the game is not won)")
    @Test
    @Tag(TestKind.HIDDEN)
    void testWaitOtherInputEngineExhausted() {
        final var gameState = mock(GameState.class);
        final var inputEngine0 = mock(StreamInputEngine.class);
        final var inputEngine1 = mock(StreamInputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = spy(new TestGame(gameState, List.of(inputEngine0, inputEngine1), renderingEngine));

        final var actions0 = List.<Action>of(new Move.Right(0));
        final var actions1 = Arrays.<Action>asList(new Move.Left(1), new Move.Right(1), new Move.Right(1), new Move.Right(1), new Move.Down(1), new Move.Up(1));
        final var processAction1 = new ActionList();
        when(inputEngine0.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(0, actions0));
        when(inputEngine1.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(50, actions1));
        doAnswer(invocation -> {
            final var action = invocation.getArgument(0, Action.class);
            if (action.getInitiator() == 1)
                processAction1.add(action);
            return invocation.callRealMethod();
        }).when(game).processAction(any());

        game.run();

        assertArrayEquals(actions1.toArray(), processAction1.toArray());
    }

    @DisplayName("ReplaySokobanGame object itself should be thread safe, i.e., multiple games can be run in parallel")
    @Test
    @Tag(TestKind.HIDDEN)
    void testThreadSafe() throws InterruptedException {
        final var gameState00 = mock(GameState.class);
        final var inputEngine00 = mock(StreamInputEngine.class);
        final var renderingEngine00 = mock(RenderingEngine.class);
        final var game00 = spy(new TestGame(gameState00, List.of(inputEngine00), renderingEngine00));

        final var gameState01 = mock(GameState.class);
        final var inputEngine01 = mock(StreamInputEngine.class);
        final var renderingEngine01 = mock(RenderingEngine.class);
        final var game01 = spy(new TestGame(gameState01, List.of(inputEngine01), renderingEngine01));

        final var gameState10 = mock(GameState.class);
        final var inputEngine10 = mock(StreamInputEngine.class);
        final var renderingEngine10 = mock(RenderingEngine.class);
        final var game10 = spy(new TestGame(gameState10, List.of(inputEngine10), renderingEngine10));

        final var gameState11 = mock(GameState.class);
        final var inputEngine11 = mock(StreamInputEngine.class);
        final var renderingEngine11 = mock(RenderingEngine.class);
        final var game11 = spy(new TestGame(gameState11, List.of(inputEngine11), renderingEngine11));

        final var actions0 = Arrays.<Action>asList(new Move.Down(0), new Move.Right(0), new Move.Left(0));
        final var actions1 = Arrays.<Action>asList(new Move.Left(1), new Move.Right(1), new Move.Right(1), new Move.Up(1));
        when(inputEngine00.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions0));
        when(inputEngine01.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions0));
        when(inputEngine10.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions1));
        when(inputEngine11.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions1));
        final var processedActions00 = new ActionList();
        final var processedActions01 = new ActionList();
        final var processedActions10 = new ActionList();
        final var processedActions11 = new ActionList();
        doAnswer(invocation -> {
            processedActions00.add(invocation.getArgument(0));
            return invocation.callRealMethod();
        }).when(game00).processAction(any());
        doAnswer(invocation -> {
            processedActions01.add(invocation.getArgument(0));
            return invocation.callRealMethod();
        }).when(game01).processAction(any());
        doAnswer(invocation -> {
            processedActions10.add(invocation.getArgument(0));
            return invocation.callRealMethod();
        }).when(game10).processAction(any());
        doAnswer(invocation -> {
            processedActions11.add(invocation.getArgument(0));
            return invocation.callRealMethod();
        }).when(game11).processAction(any());

        final var thread00 = new Thread(game00);
        final var thread01 = new Thread(game01);
        final var thread10 = new Thread(game10);
        final var thread11 = new Thread(game11);
        // parallel
        thread00.start();
        thread10.start();
        thread00.join();
        thread10.join();

        // sequential
        thread01.start();
        thread01.join();
        thread11.start();
        thread11.join();

        assertArrayEquals(processedActions00.toArray(), processedActions01.toArray());
        assertArrayEquals(processedActions10.toArray(), processedActions11.toArray());
    }

    @DisplayName("Action order should be enforced in ROUND_ROBIN mode (all input engines have same length of actions")
    @Test
    @Tag(TestKind.PUBLIC)
    void testRoundRobinModeEqualLength() {
        final var gameState = mock(GameState.class);
        final var inputEngine0 = mock(StreamInputEngine.class);
        final var inputEngine1 = mock(StreamInputEngine.class);
        final var inputEngine2 = mock(StreamInputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var inputEngines = List.of(inputEngine0, inputEngine1, inputEngine2);
        final var game = spy(new TestGame(ReplaySokobanGame.Mode.ROUND_ROBIN, gameState, inputEngines, renderingEngine));

        final var actions0 = Arrays.<Action>asList(new Move.Down(0), new Move.Right(0), new Move.Left(0), new Move.Up(0), new Move.Down(0));
        final var actions1 = Arrays.<Action>asList(new Move.Left(1), new Move.Right(1), new Move.Right(1), new Move.Up(1), new Move.Down(1));
        final var actions2 = Arrays.<Action>asList(new Move.Left(2), new Move.Right(2), new Move.Right(2), new Move.Up(2), new Move.Down(2));
        final var actionsLists = new List[]{actions0, actions1, actions2};
        final var processActions = new ActionList();
        when(inputEngine0.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions0));
        when(inputEngine1.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions1));
        when(inputEngine2.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions2));
        doAnswer(invocation -> {
            final var action = invocation.getArgument(0, Action.class);
            processActions.add(action);
            return invocation.callRealMethod();
        }).when(game).processAction(any());

        game.run();

        int i = 0;
        while (i < actions0.size() && i < actions1.size()) {
            final var round = i % inputEngines.size();
            final var index = i / inputEngines.size();
            final var actionList = actionsLists[round];
            if (index < actionList.size()) {
                assertEquals(actionList.get(index), processActions.get(i));
            }
            i++;
        }
    }

    @DisplayName("Action order should be enforced in ROUND_ROBIN mode (all input engines have different length of actions")
    @Test
    @Tag(TestKind.HIDDEN)
    void testRoundRobinModeDifferentLength() {
        final var gameState = mock(GameState.class);
        final var inputEngine0 = mock(StreamInputEngine.class);
        final var inputEngine1 = mock(StreamInputEngine.class);
        final var inputEngine2 = mock(StreamInputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var inputEngines = List.of(inputEngine0, inputEngine1, inputEngine2);
        final var game = spy(new TestGame(ReplaySokobanGame.Mode.ROUND_ROBIN, gameState, inputEngines, renderingEngine));

        final var actions0 = Arrays.<Action>asList(new Move.Down(0), new Move.Right(0), new Move.Left(0), new Move.Up(0), new Move.Down(0));
        final var actions1 = List.<Action>of(new Move.Left(1));
        final var actions2 = Arrays.<Action>asList(new Move.Left(2), new Move.Right(2), new Move.Up(2), new Move.Down(2));
        final var actionsLists = new List[]{actions0, actions1, actions2};
        final var processActions = new ActionList();
        when(inputEngine0.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions0));
        when(inputEngine1.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions1));
        when(inputEngine2.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions2));
        doAnswer(invocation -> {
            final var action = invocation.getArgument(0, Action.class);
            processActions.add(action);
            return invocation.callRealMethod();
        }).when(game).processAction(any());

        game.run();

        int i = 0;
        while (i < actions0.size() && i < actions1.size()) {
            final var round = i % inputEngines.size();
            final var index = i / inputEngines.size();
            final var actionList = actionsLists[round];
            if (index < actionList.size()) {
                assertEquals(actionList.get(index), processActions.get(i));
            }
            i++;
        }
    }

    @DisplayName("Action order should be random in FREE_RACE mode")
    @Test
    @Tag(TestKind.HIDDEN)
    void testFreeRaceMode() {
        class GameRun implements Runnable {
            final List<Action> processActions = new ActionList();

            @Override
            public void run() {
                final var gameState = mock(GameState.class);
                final var inputEngine0 = mock(StreamInputEngine.class);
                final var inputEngine1 = mock(StreamInputEngine.class);
                final var inputEngine2 = mock(StreamInputEngine.class);
                final var renderingEngine = mock(RenderingEngine.class);
                final var inputEngines = List.of(inputEngine0, inputEngine1, inputEngine2);
                final var game = spy(new TestGame(gameState, inputEngines, renderingEngine));

                final var actions0 = Arrays.<Action>asList(new Move.Down(0), new Move.Right(0), new Move.Left(0), new Move.Up(0), new Move.Down(0));
                final var actions1 = Arrays.<Action>asList(new Move.Left(1), new Move.Right(1), new Move.Right(1), new Move.Up(1), new Move.Down(1));
                final var actions2 = Arrays.<Action>asList(new Move.Left(2), new Move.Right(2), new Move.Right(2), new Move.Up(2), new Move.Down(2));
                final var actionsLists = new List[]{actions0, actions1, actions2};
                when(inputEngine0.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions0));
                when(inputEngine1.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions1));
                when(inputEngine2.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions2));
                doAnswer(invocation -> {
                    final var action = invocation.getArgument(0, Action.class);
                    processActions.add(action);
                    return invocation.callRealMethod();
                }).when(game).processAction(any());

                game.run();
            }

            @Override
            public String toString() {
                final var sb = new StringBuilder();
                for (final var action : processActions) {
                    sb.append(action.toString());
                }
                return sb.toString();
            }
        }

        final var repeat = 10;
        final var runResults = new HashSet<String>();
        for (int i = 0; i < repeat; i++) {
            final var runner = new GameRun();
            runner.run();
            runResults.add(runner.toString());
        }
        assertTrue(runResults.size() > 1);
    }

    @ParameterizedTest
    @CsvSource(value = {"H,L:L,L,U", "H,J,U:H,J,K", "H,K,U,J:L,U,L,H"}, delimiter = ':')
    @Tag(TestKind.HIDDEN)
    void testStateConsistency(String a0, String a1) {
        final var map = """
                1
                ########
                #...@..#
                #..A.b.#
                #.B.a..#
                #..@...#
                ########
                """;
        final var gameMap = TestHelper.parseGameMap(map);
        final var gameState = new GameState(gameMap);
        final var inputEngine0 = mock(StreamInputEngine.class);
        final var inputEngine1 = mock(StreamInputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = new TestGame(gameState, List.of(inputEngine0, inputEngine1), renderingEngine);

        final var actions0 = Arrays.stream(a0.split(",")).map(a -> StreamInputEngine.char2Action(0, a.charAt(0))).toList();
        final var actions1 = Arrays.stream(a1.split(",")).map(a -> StreamInputEngine.char2Action(1, a.charAt(0))).toList();
        when(inputEngine0.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions0));
        when(inputEngine1.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(actions1));
        game.run();

        class SingleThreadedGame extends AbstractSokobanGame {
            private InputEngine inputEngine;

            protected SingleThreadedGame(@NotNull GameState gameState, InputEngine inputEngine) {
                super(gameState);
                this.inputEngine = inputEngine;
            }

            @Override
            public void run() {
                while (!shouldStop()) {
                    final var action = inputEngine.fetchAction();
                    processAction(action);
                }
            }
        }

        final var permutations = CollectionUtils.permutations(
                Stream.concat(actions0.stream(), actions1.stream()).toList()
        );
        assertTrue(permutations.stream().anyMatch(actions -> {
            final var gameStateSerial = new GameState(gameMap);
            final var inputEngine = mock(StreamInputEngine.class);
            final var gameSerial = new SingleThreadedGame(gameStateSerial, inputEngine);
            when(inputEngine.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(0, actions));
            gameSerial.run();
            return gameStateSerial.equals(gameState);
        }));
    }

    @DisplayName("FPS parameter should specify the times render method is invoked per second")
    @Test
    @Timeout(5)
    @Tag(TestKind.PUBLIC)
    void testFPS() {
        final var fps = 50;
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(InputEngine.class);
        final var renderingEngine = mock(RenderingEngine.class);
        final var game = new TestGame(ReplaySokobanGame.Mode.FREE_RACE, fps, gameState, List.of(inputEngine), renderingEngine);

        final var actions = Arrays.<Action>asList(
                new Move.Down(0),
                new Move.Right(0),
                new Move.Right(0),
                new Move.Left(0),
                new Move.Up(0)
        );
        final var renderTimes = new ArrayList<Date>();
        when(inputEngine.fetchAction()).thenAnswer(new RandomlyPausedActionProducer(90, 110, actions));
        doAnswer(invocation -> {
            renderTimes.add(new Date());
            return null;
        }).when(renderingEngine).render(any());

        game.run();

        assertTrue(renderTimes.size() > 0);
        final var timeElapsed = renderTimes.get(renderTimes.size() - 1).getTime() - renderTimes.get(0).getTime();
        final var expected = (float) timeElapsed / 1000 * fps;
        assertEquals(expected, renderTimes.size(), (float) (expected * 0.1)); // 10% error tolerance
    }
}

class TestGame extends ReplaySokobanGame {
    public TestGame(GameState gameState, List<? extends InputEngine> inputEngines, RenderingEngine renderingEngine) {
        super(gameState, inputEngines, renderingEngine);
    }

    public TestGame(Mode mode, GameState gameState, @NotNull List<? extends InputEngine> inputEngines, RenderingEngine renderingEngine) {
        super(mode, 60, gameState, inputEngines, renderingEngine);
    }

    public TestGame(Mode mode, int fps, GameState gameState, @NotNull List<? extends InputEngine> inputEngines, RenderingEngine renderingEngine) {
        super(mode, fps, gameState, inputEngines, renderingEngine);
    }


    @Override
    public ActionResult processAction(@NotNull Action action) {
        return super.processAction(action);
    }
}

/**
 * Thread-safe
 * Action producer will by default add a trailing Exit action.
 */
class RandomlyPausedActionProducer implements Answer<Action> {
    private final int pauseLowerBound;
    private final int pauseUpperBound;
    private final List<Action> actions;
    private Action lastAction = null;

    RandomlyPausedActionProducer(int pauseLowerBound, int pauseUpperBound, List<Action> actions) {
        if (pauseUpperBound < 0)
            throw new IllegalArgumentException("pauseBound must be non-negative");
        if (actions.isEmpty())
            throw new IllegalArgumentException("Actions cannot be empty");
        this.pauseLowerBound = pauseLowerBound;
        this.pauseUpperBound = pauseUpperBound;
        this.actions = new ArrayList<>(actions);
        this.actions.add(new Exit());
    }

    RandomlyPausedActionProducer(List<Action> actions) {
        this(0, 10, actions);
    }

    RandomlyPausedActionProducer(int upperBound, List<Action> actions) {
        this(0, upperBound, actions);
    }

    RandomlyPausedActionProducer(Action... actions) {
        this(Arrays.asList(actions));
    }

    @NotNull
    synchronized Action produce() {
        if (this.actions.size() > 0) {
            this.lastAction = this.actions.remove(0);
        }
        return this.lastAction;
    }

    @Override
    public synchronized Action answer(InvocationOnMock invocation) throws Throwable {
        if (this.pauseUpperBound > 0)
            Thread.sleep(new Random().nextInt(this.pauseLowerBound, this.pauseUpperBound));
        return produce();
    }
}

class ActionList extends ArrayList<Action> {
    @Override
    public boolean add(Action action) {
        if (action instanceof Exit)
            return false;
        return super.add(action);
    }
}
