# COMP 3021 Programming Assignment 1 (Fall 2022)

***PA1's solution and hidden tests are now released at [PA1](https://github.com/CastleLab/COMP3021-F22-PA-Student-Version/tree/PA1) branch of this repository. The original PA1 skeleton code is archived at tag [PA1-skeleton](https://github.com/CastleLab/COMP3021-F22-PA-Student-Version/tree/PA1-skeleton).***

In this PA we are going to implement a terminal user
interface ([TUI](https://en.wikipedia.org/wiki/Text-based_user_interface)) based,
modified [Sokoban](https://en.wikipedia.org/wiki/Sokoban) game.

## Grading Policy

We explain the grading policy before task specification so that you will not miss it.

Your implemenation will be validated by a set of test cases.
Some of the them are available to you (aka. **public test cases**) with the skeleton code so that you can check
whether your implementation supports the basic scenarios.
Passing all these test cases does not mean that you get full marks because we have some **hidden test cases** which
further validate the implementations that are not covered by the public test cases (e.g., some corner cases).

| **Item**                                        | **Ratio** | **Notes**                                                                |
|-------------------------------------------------|-----------|--------------------------------------------------------------------------|
| Keeping your GitHub repository private          | 5%        | You must keep your repository **private** at all times.                  |
| Having at least three commits on different days | 5%        | You should commit three times during different days in your repository.  |
| Code style                                      | 10%       | You get 10% by default, and every 5 warnings from CheckStyle deducts 1%. |
| Public test cases                               | 15%       | `(# of passing tests / # of provided tests) * 15%`                       |
| Hidden test cases                               | 65%       | `(# of passing tests / # of hidden tests) * 65%`                         |


Please try to compile your code with `gradle build` before submission.  You will not get any marks of public/hidden test cases if your code does not compile.

## Implementing the Game

You are encouraged to:

- Leverage the latest Java language features.
- Add your own classes and methods to faciliate your implementation.
- Construct extra test cases to throughly validate your implementation and share them with your classmates (or TAs).
  Note that no bonus will be given for this because we don't want the PA to be so [chur](https://learning.hku.hk/ccch9051/group-18/items/show/59).

You are not allowed to:

- Remove or modify the test code (in `src/test/java`) provided by us.
- Delete or change the signature (name, arguments, visibility, etc.) of any given classes or methods in the skeleton code.
- Share your implementation with your classmates.

## Submission

You should submit a single text file specified as follows:

- A file named `<itsc-id>.txt` containing the URL of your private repository at the first line. We will ask you to add the TAs' accounts as collaborators near the deadline.

For example, a student CHAN, Tai Man with ITSC ID `tmchanaa` having a repository at `https://github.com/tai-man-chan/COMP3021-PA1` should submit a file named `tmchanaa.txt` with the following content:
```text
https://github.com/tai-man-chan/COMP3021-PA1
```

Note that we are using automatic scripts to process your submission.
**DO NOT add extra explanation** to the file; otherwise they will prevent our scripts from correctly processing your submission.
Feel free to send us an email if you need clarification.

You need to submit the file to [CASS](https://cssystem.cse.ust.hk/UGuides/cass/index.html).
The deadline for this assignment is **September 30, 2022, 23:59:59** (inclusive).

**We will grade your submission based on the latest committed version before the deadline.**
Please make sure all the amendments are made before the deadline and do not make changes after the deadline.

## Q&A

Should you have any questions, please go to [Discussion](https://docs.github.com/en/discussions/quickstart#creating-a-new-discussion) of this repository to ask and view other students' questions and answers.
**There is a FAQ pinned on the top.**
Be sure that you read it through before ask questions.
TAs will assist you there.

## Game Specification

On top of the original Sokoban game, we add features to support multiple players playing simultaneously on the same game map. You need to implement according to the provided skeleton code.
The specification and requirements of each class and method are available in the JavaDoc in the skeleton code.
Here we list some key points.

### Text-based Representation

- `@` represents box destinations.
- `#` represents walls.
- `A-Z` upper-case letters represent players.
    - Each letter represents a player.
    - There should not be duplicate letters in a map.
- `a-z` lower-case letter represents boxes that can be moved by players with corresponding upper-case letters.
    - E.g. boxes represented by `a` can only be moved by player `A`.
- `.` represents empty position where a player can move to or move boxes to.
- ` ` A space represent a position where it is not part of the map (e.g., when the map is not rectangular).

### Players

- Each player exclusively takes one position, and other players and boxes cannot be moved to the position being occupied by a player.
- Players can only move one box at a time.
- Players have their own boxes and can only move boxes belonging to them. For example, player `A` can only move `a` boxes. `b` boxes are like walls to player `A`.
- The idea of the game is to support multiple players (more than 2). The core package `hk.ust.comp3021.game` should support arbitrary number of players.
- Each player is associated with an ID, which is the offset from letter `A` to its own letter representation. For example, Player `C` has ID `(int)('C' - 'A') = 2`.

### Actions

- In PA1, the terminal based game supports up to 2 players. Such checking should be done in `hk.ust.comp3021.tui`.
- Key `A,S,W,D` and `H,J,K,L` are used to move Player with ID 0 and 1 to `Left,Down,Up,Right` direction by 1 step, respectively
- Key `U` are used to undo the last box movement. Undo is not specific to players and will revert the last box movement regardless of which player moves the last box.

### Undo

- There is a limit on Undo actions, which is specified when game starts.
- A checkpoint should be marked after every time a box is moved.
- Undo actions revert the game state to the previous checkpoint. For example, suppose by now `cp1` and `cp2` have been recorded, `undo` action should reverts the game state to `cp1`. The principle is to revert the last movement of the box.
- Undo is global no matter which player moves a box.

### Winning Condition

- All destination positions have been occupied with boxes.

### Deadlock Condition

- Deadlock checking is not required. It is more about algorithm design and out of scope of this course.
- Game should exit either when the game is won or the player performs `Exit` action.

### Map Validation

- The map must be surrounded by walls.
- All players, boxes and destinations must be inside the map.
- Each player must have a box to move.
- The number of boxes must be equal to the number of destinations.
- There must not be more than one upper-case letter for each player.


## Reference Implementation

We provide a reference implementation of this assignment (it is obfuscated and you won't see solutions in it) [here](https://course.cse.ust.hk/comp3021/assignments/Sokoban-proguard.jar).
You can run it with the following command:
```bash
java --enable-preview -jar Sokoban-proguard.jar path/to/game/map/file.
```
We provide two sample game map in the `src/main/resources` folder.

## Run Check Style

We have preconfigrued a gradle task to check style for you.
You can run `gradle checkstyleMain` in the integrated terminal of IntelliJ to check style.

## Academic Integrity

We trust that you are familiar with HKUST's Honor Code. If not, refer to
[this page](https://course.cse.ust.hk/comp3021/#honorcode).
