# Sokoban Game

## Players

- Support multiple players.
- Each player exclusively takes one position and other players and boxes cannot be moved to that place.
- Players can only move one box at once.
- For simplicity, we support at most 2 players (This constraint should only be checked in `InputEngine`. Other parts of the game should support arbitrary number ([1,26]) of players.).

## Actions

- Player 1 moves with `W,A,S,D`; player 2 moves with `H,J,K,L`.
- Player 1 undo moves with `R`; player 2 undo moves with `U`.
- A number can be given following actions to repeat the action for certain times.

## Winning Condition

- All destination positions have been occupied with boxes.

## Deadlock Condition

- None of the boxes in the map is movable.

## Undo

- There is a limit on Undo actions, which is specified when game starts.
- Checkpoint every time a box is moved.
- Undo actions reverts the game state to the last checkpoint.

## Text-based Representation

- `@` represents box destinations.
- `#` represents walls.
- `A-Z` upper-case letters represent players.
  - Each letter represents a player.
  - There should not be duplicate letters in a map.
- `a-z` lower-case letter represents boxes that can be moved by players with corresponding upper-case letters.
  - E.g. boxes represented by `a` can only be moved by player `A`.
- `.` represents empty position where a player can move to or move boxes to.

## Map Validation

- The map must be closed with walls.
- All players, boxes and destinations must be inside walls.
- For each box, there must be a corresponding player who can move this box.
- The number of boxes must be more than the number of destinations.
- There must not be more than one upper-case letter for each player.
- (Optional) The game must be winnable.
