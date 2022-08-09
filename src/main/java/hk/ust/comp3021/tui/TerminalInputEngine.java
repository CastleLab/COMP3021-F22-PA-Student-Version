package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.utils.ShouldNotReachException;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * An input engine that fetches actions from terminal input.
 */
public class TerminalInputEngine implements InputEngine {

    /**
     * The {@link Scanner} for reading input from the terminal.
     */
    private final Scanner terminalScanner;

    private final int[] playerIds;

    /**
     * @param terminalStream The stream to read terminal inputs.
     */
    public TerminalInputEngine(InputStream terminalStream, int[] playerIds) {
        this.terminalScanner = new Scanner(terminalStream);
        this.playerIds = playerIds;
    }

    @Override
    public Optional<? extends PlayerAction> fetchAction() {
        System.out.print(">>> ");
        var playerId = playerIds[0];
        var inputLine = terminalScanner.nextLine();
        var moveRegex = Pattern.compile("^(?<direction>[LRUDlrud])(\\s+(?<steps>\\d+))?$");
        var moveMatcher = moveRegex.matcher(inputLine);
        if (moveMatcher.find()) {
            var stepsStr = moveMatcher.group("steps");
            var steps = stepsStr != null ? Integer.parseInt(stepsStr) : 1;
            return switch (moveMatcher.group("direction").toUpperCase()) {
                case "L" -> Optional.of(new PlayerAction(playerId, new Move.Left(steps)));
                case "R" -> Optional.of(new PlayerAction(playerId, new Move.Right(steps)));
                case "U" -> Optional.of(new PlayerAction(playerId, new Move.Up(steps)));
                case "D" -> Optional.of(new PlayerAction(playerId, new Move.Down(steps)));
                default -> throw new ShouldNotReachException();
            };
        }
        if (inputLine.equalsIgnoreCase("Undo"))
            return Optional.of(new PlayerAction(playerIds[0], new Undo()));
        return Optional.of(new PlayerAction(playerIds[0], new InvalidInput("Invalid input sequence")));
    }
}
