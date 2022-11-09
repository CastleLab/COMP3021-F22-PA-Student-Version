# COMP 3021 Programming Assignment 2 (PA2) (Fall 2022)

In this PA we are going to implement a graphical user
interface ([GUI](https://en.wikipedia.org/wiki/Graphical_user_interface)) based,
modified [Sokoban](https://en.wikipedia.org/wiki/Sokoban) game.

## Grading Policy

We explain the grading policy before task specification so that you will not miss it.

Your implementation will be validated via a **demonstration**.

| **Item**                                        | **Ratio** | **Notes**                                                                |
|-------------------------------------------------|-----------|--------------------------------------------------------------------------|
| Keeping your GitHub repository private          | 5%        | You must keep your repository **private** at all times.                  |
| Having at least three commits on different days | 5%        | You should commit three times during different days in your repository.  |
| Code style                                      | 10%       | You get 10% by default, and every 5 warnings from CheckStyle deducts 1%. |
| Project Demonstration                           | 80%       | See below in [Rubrics](#Demonstration Grading Rubrics)                   |

Please try to compile your code with `gradle build` before submission. You will not get any marks of public/hidden test
cases if your code does not compile.

## Implementing the Game

In PA2, the reference implementation of PA1 will be provided and your tasks are basically to implement a GUI view of the
game based on the skeleton code provided by us.
The information provided in PA1 will not be repeated in this README. You may check out
this [link](https://github.com/CastleLab/COMP3021-F22-PA-Student-Version/tree/PA1) if you want to revisit that.

## Submission

You should submit a single text file specified as follows:

- A file named `<itsc-id>.txt` containing the URL of your private repository at the first line. We will ask you to add
  the TAs' accounts as collaborators near the deadline.

For example, a student CHAN, Tai Man with ITSC ID `tmchanaa` having a repository
at `https://github.com/tai-man-chan/COMP3021-PA2` should submit a file named `tmchanaa.txt` with the following content:

```text
https://github.com/tai-man-chan/COMP3021-PA2
```

Note that we are using automatic scripts to process your submission.
**DO NOT add extra explanation** to the file; otherwise they will prevent our scripts from correctly processing your
submission.
Feel free to email us if you need clarification.

You need to submit the file to [CASS](https://cssystem.cse.ust.hk/UGuides/cass/index.html).
The deadline for this assignment is **November 13, 2022, 23:59:59** (inclusive).

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

The functionalities of PA2 are built on top of those in PA1.
The main content of PA2 is to design and implement a GUI view of the game.
All you need to implement are inside `hk.ust.comp3021.gui` package, while other sibling pacages are those you
implemented in PA1.
Below are the requirements of the GUI view.

### Two Scenes

The GUI game has two scenes: the `start scene` and the `game scene`.
The start scene serves as a menu to select game maps to play.
Users can load a map from file system by clicking `Load Map` button, and the loaded map will be displayed in the map
list.
The map in the list can be deleted by clicking the `Delete Map` button.

On clicking one map in the list, the GUI view will switch to the game scene, where the user can move players and win the
game.
Users can choose to exit the game scene at any time, and the GUI view will switch back to the start scene.

### Demonstration Grading Rubrics
If any uncaught exception occurs during the demonstration, you will get 0 for the corresponding functionality.

- (40%) Start Scene
    - (2%) By default, two built-in maps (`map00.map` and `map01.map`) (at `src/main/resources`) should be preloaded into the
      map list.
    - (20%) Load Map
        - (3%) `Load Map` button should open a file chooser to select a map file.
        - (2%) If an invalid map file is given, an error message should be displayed.
        - (5%) If a valid map file is selected, the map should be added in the map list.
        - (5%) The map list should be sorted according to the timestamp of loading each map.
        - (5%) Loading the same map should override the previous one (duplication is decided by the absolute path of map
          file), and update the load timestamp.
    - (5%) Drag map files
        - Dragging files to the start scene should work the same as clicking `Load Map` button.
        - (2%) Dragging multiple files should supported to load multiple map files at once.
        - (1%) When any dragged file is invalid, an error message should be displayed for this invalid file.
        - (2%) Even if some dragged map files are invalid, other valid files should be processed.
    - (5%) Open Map
        - (1%) `Open Map` button should be disabled when no map is selected in the map list.
        - (1%) `Open Map` button should be enabled when a map is selected in the map list.
        - (3%) Clicking `Open Map` button should switch to the game scene, and the selected map should be loaded.
    - (5%) Delete Map
        - (1%) `Delete Map` button should be disabled when no map is selected in the map list.
        - (1%) `Delete Map` button should be enabled when a map is selected in the map list.
        - (3%) Clicking `Delete Map` button should delete the selected map from the map list.
    - (3%) Items in the map list should be persistent when switching from start scene to game scene and back to start scene.
- (40%) Game Scene
    - (6%) Exit game
        - (3%) Clicking the `Exit` button should switch back to the start scene.
        - (3%) Exiting the game should discard any progress of current game, i.e., start game with the same map again should
          start a new game.
    - (15%) Game map
        - (3%) The game map should display the same as what is expected, i.e., each position should have the correct entity (
          or empty).
        - (2%) Each player and its corresponding boxes should have the same color.
        - (2%) When a box is moved to a destination place, a green tick `✓` should be displayed on top of the box.
        - (2%) When a box is moved out of a destination place, the green tick `✓` should be removed.
        - (2%) Below the game map, there should be a text area displaying the current undo quota left.
        - (2%) The undo quota should be updated when an undo action is performed.
        - (2%) The undo quota text should show `unlimited` if the undo quota is unlimited (-1 in the game map file).
    - (10%) Player Control panel
        - (2%) Each player should have its own control panel on the right of the game scene (4 players at most).
        - (4%) The control panel should display the player's picture in the middle, and buttons to move the player in the
          four directions.
        - (2%) The number of control panels should be the same as the number of players in the game.
        - (2%) Each control panel should only control the corresponding player.
    - (5%) Undo
        - (5%) Clicking the `Undo` button should revert to the previous checkpoint (same as PA1).
    - (4%) Messages
        - (2%) When a player performs an invalid move, there should be an error message displayed.
        - (2%) When the game wins, there should be a message displayed.

## Reference Implementation

We provide a demonstration of reference implementation of this assignment. 
The demonstration video can be found on [YouTube](https://youtu.be/LiIyaaoaWxI).

***A reference implementation is not available since we cannot release it without leaking solutions.***

## Run Check Style

We have pre-configured a gradle task to check style for you.
You can run `gradle checkstyleMain` in the integrated terminal of IntelliJ to check style.

## Academic Integrity

We trust that you are familiar with Honor Code of HKUST. If not, refer to
[this page](https://course.cse.ust.hk/comp3021/#honorcode).
