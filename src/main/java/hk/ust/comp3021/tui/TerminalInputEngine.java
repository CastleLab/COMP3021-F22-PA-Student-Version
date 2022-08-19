package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.utils.ShouldNotReachException;

import java.io.InputStream;
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

    /**
     * @param terminalStream The stream to read terminal inputs.
     */
    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        var inputLine = terminalScanner.nextLine();
        var moveRegex = Pattern.compile("^(?<action>[WASDwasdRrHJKLhjklUu])$");
        var moveMatcher = moveRegex.matcher(inputLine);
        if (moveMatcher.find()) {
            var moveCommand = moveMatcher.group("action").toUpperCase();
            var playerId = switch (moveCommand) {
                case "W", "A", "S", "D", "R" -> 0;
                case "H", "J", "K", "L", "U" -> 1;
                default -> throw new ShouldNotReachException();
            };
            return switch (moveCommand) {
                case "W", "K" -> new Move.Up(playerId);
                case "A", "H" -> new Move.Left(playerId);
                case "S", "J" -> new Move.Down(playerId);
                case "D", "L" -> new Move.Right(playerId);
                case "R", "U" -> new Undo(playerId);
                default -> throw new ShouldNotReachException();
            };
        } else if (inputLine.equalsIgnoreCase("exit")) {
            return new Exit(-1);
        } else {
            return new InvalidInput(-1, "Invalid Input.");
        }
    }
}
