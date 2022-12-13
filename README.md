# COMP 3021 Programming Assignment 3 (PA3) (Fall 2022)

**PA3's solution and hidden tests are now released at PA3 branch of this repository. The original PA3 skeleton code is archived at tag PA3-skeleton.**

In this PA we are going to implement a multithreading,
modified [Sokoban](https://en.wikipedia.org/wiki/Sokoban) game.

## Grading Policy

We explain the grading policy before task specification so that you will not miss it.

| **Item**                                        | **Ratio** | **Notes**                                                                |
|-------------------------------------------------|-----------|--------------------------------------------------------------------------|
| Keeping your GitHub repository private          | 5%        | You must keep your repository **private** at all times.                  |
| Having at least three commits on different days | 5%        | You should commit three times during different days in your repository.  |
| Code style                                      | 10%       | You get 10% by default, and every 5 warnings from CheckStyle deducts 1%. |
| Regression test cases                           | 10%       | (# of passing tests / # of provided tests) * 10%                         |
| Public test cases                               | 20%       | (# of passing tests / # of provided tests) * 20%                         |
| Hidden test cases                               | 50%       | (# of passing tests / # of provided tests) * 50%                         |

Please try to compile your code with `gradle build` before submission.
You will not get any marks of public/hidden test
cases if your code does not compile.

## Implementing the Game

In PA3, the reference implementation of PA1 will be provided and
 your tasks are basically to implement the game with multithreading
 based on the skeleton code provided by us.
The information provided in PA1 will not be repeated in this README. You may check out
this [link](https://github.com/CastleLab/COMP3021-F22-PA-Student-Version/tree/PA1) if you want to revisit that.

## Submission

You should submit a single text file specified as follows:

- A file named `<itsc-id>.txt` containing the URL of your private repository at the first line. We will ask you to add
  the TAs' accounts as collaborators near the deadline.

For example, a student CHAN, Tai Man with ITSC ID `tmchanaa` having a repository
at `https://github.com/tai-man-chan/COMP3021-PA3` should submit a file named `tmchanaa.txt` with the following content:

```text
https://github.com/tai-man-chan/COMP3021-PA3
```

Note that we are using automatic scripts to process your submission.
**DO NOT add extra explanation** to the file; otherwise they will prevent our scripts from correctly processing your
submission.
Feel free to email us if you need clarification.

You need to submit the file to [CASS](https://cssystem.cse.ust.hk/UGuides/cass/index.html).
The deadline for this assignment is **November 30, 2022, 23:59:59** (inclusive).

**We will grade your submission based on the latest committed version before the deadline.**
Please make sure all the amendments are made before the deadline and do not make changes after the deadline.

## Q&A

Should you have any questions, please go
to [Discussion](https://docs.github.com/en/discussions/quickstart#creating-a-new-discussion) of this repository to ask
and view other students' questions and answers.
TAs will assist you there.
**There is a FAQ pinned on the top.**
Be sure that you read it through before ask questions.

## Game Specification

The functionalities of PA3 are built on top of those in PA1.
The main content of PA3 is to support multithreading.
All you need to implement are inside `hk.ust.comp3021.replay` package, while other sibling packages are those you
implemented in PA1.
Below are the specifications of PA3.

### Functionality Design

Playing the game manually cannot fully utilize the power of multithreading since the game needs to wait for user inputs and most of the time is spent on waiting.
Therefore, PA3 is designed to implement an automatic replay of the game.
The `ReplaySokobanGame` class implement the most of the functionalities.
Users of the program needs to specify a game map (as that in PA1) as well as one or more action files that contains the actions to be performed by each player.
Then the replay game will start and automatically perform actions of each player concurrently until the game ends (either the game wins or all actions are performed).

**Note that up to 26 players should be supported in PA3**

As an example, consider the following arguments to the main method:

```
java -jar Sokoban-PA3.jar 3 map02.map FREE_RACE 60 actions0.txt actions1.txt
```
It means that the game will take `map02.map` as `GameMap`.
Suppose there are two players in the game map, `actions0.txt` and `actions1.txt` files specifies the actions of the two players, respectively.
The number `3` means that the game will be repeatedly replayed for 3 times in parallel.
The remaining two arguments `FREE_RACE` and `60` are the game replay mode and the frameRate (frames per second) of rendering, respectively.
They are to be explained in [Multithreading Architecture](#Multithreading in ReplaySokobanGame).

The `map02.map` may look like this:
```text
2
########
#..@..@#
#A..a..#
#..a#@.#
#@.a#..#
#.b..B.#
########
```
The `actions0.txt` may look like this:
```text
0
J
K
L
L
U
L
H
H
J
E
```
The `actions1.txt` may look like this:
```text
1
J
K
L
U
L
U
K
E
```

#### Action File
The first line of an action file specifies the player ID.
All actions in the action file are performed by this player.
The following lines specify the actions of the player.
The actions are represented by the following characters:
- `H`: move the player left
- `J`: move the player down
- `K`: move the player up
- `L`: move the player right
- `U`: undo
- `E`: exit

### Multithreading Architecture

#### Thread-Safe `ReplaySokobanGame`

The `ReplaySokobanGame` class should be [thread-safe](https://en.wikipedia.org/wiki/Thread_safety).
Each game instance should be able to run in parallel with other game instances in different threads.
The `ReplaySokobanGame` implements `Runnable`, and it will execute in a thread as implemented in `hk.ust.ust.comp3021.SokobanGame#replayGame`.
In the example [above](#Functionality Design), there will be three game instances replayed in parallel in three threads.

**Correctness Requirement**:
- For an arbitrary list of games, running them in parallel should achieve the same result as running them in sequence.

#### Multithreading in ReplaySokobanGame

When instantiating a `ReplaySokobanGame`, it takes an array of `InputEngine` instances and a `RenderingEngine` instance, in addition to `GameState`.
Each `InputEngine` instance corresponds to an action file specified in the arguments of the main method.
Each `InputEngine` instance and `RenderingEngine` instance should be executed in a separate thread.
In the example [above](#Functionality Design), each game instance will have two `InputEngine` threads and one `RenderingEngine` thread.
In the `ReplaySokobanGame` class, we provide two inner wrapper classes `InputEngineRunnable` and `RenderingEngineRunnable` to wrap the `InputEngine` and `RenderingEngine` instances, respectively, so that they can be executed in a thread.
You need to implement them and perform proper thread synchronization to ensure the correctness of the game.

**Replay Mode**:
Multiple `InputEngine` threads perform actions concurrently.
There are two modes of scheduling between `InputEngine` threads: `FREE_RACE` and `ROUND_ROBIN`, which should be supported by `ReplaySokobanGame`.
- `ROUND_ROBIN`: all `InputEngine` threads perform actions in a round-robin fashion (turn by turn).
  In the example [above](#Functionality Design), the actions in two action files are scheduled to be processed in the following order: `PlayerA J`, `PlayerB J`, `PlayerA K`, `PlayerB K`, `PlayerA L`, `PlayerB L`, `PlayerA L`, `PlayerB U`, `PlayerA U`, `PlayerB L`, ...
- `FREE_RACE`: the `InputEngine` threads perform actions concurrently without any scheduling in the order.
  In this mode, the final order of processed actions are arbitrary and may be different across different runs.

**Frame Rate**:
The `RenderingEngine` thread renders the game state in a specified frameRate (frames per second).
In the example [above](#Functionality Design), the frameRate is set to 60, which means the `RenderingEngine` thread will render the game state (invoke `render` method) 60 times per second.

**Requirements**:
- The game must be rendered at least once before the first action is performed (i.e., the initial state of the game must be rendered).
- The game must be rendered at least once after the last action is performed (i.e., the final state of the game must be rendered).
  The trailing `Exit` action does not count.
- Method `run` starts the game by spawn threads for each `InputEngine` and `RenderingEngine` instance.
  When `run` method returns, all spawned threads should already terminate.
- For each action file (and the corresponding `InputEngine`), all actions before (inclusive) the first `Exit` (`E`) should be processed (i.e., fed to the `processAction` method).
- For each input engine, after the first `Exit` (`E`) is processed, all other actions in the action file of this input engine should be ignored (i.e., not fed to the `processAction` method).
- Actions in the same action file should be processed in the same order as they appear in the action file.
- The game ends when either:
  - The winning condition is satisfied (i.e., all boxes are placed on the destinations).
  - All actions in all action files (before the first `Exit`) have been processed.

**Assumption (Those already implemented, and you should not modify)**:
- The last action in an action file is always `Exit` (`E`).
- Each action file corresponds to one `InputEngine` instance, and they are passed in the same order as an array to `ReplaySokobanGame`.
- The `InputEngine` passed to `ReplaySokobanGame` is an instance of `StreamInputEngine` and `fetchAction` method will return the next action in the action file no matter whether there are `Exit` in the middle.
  If there are no more actions, `Exit` will be returned.

## Reference Implementation

We provide a reference implementation of the `ReplaySokobanGame` [here](https://course.cse.ust.hk/comp3021/assignments/Sokoban-proguard-PA3.jar).
You it as follows:
```bash
java --enable-preview -jar Sokoban-proguard-PA3.jar 3 src/main/resources/map02.map ROUND_ROBIN 100 src/main/resources/actions/actions0.txt src/main/resources/actions/actions1.txt
```

## Run Check Style

We have pre-configured a gradle task to check style for you.
You can run `gradle checkstyleMain` in the integrated terminal of IntelliJ to check style.

## Academic Integrity

We trust that you are familiar with Honor Code of HKUST. If not, refer to
[this page](https://course.cse.ust.hk/comp3021/#honorcode).
