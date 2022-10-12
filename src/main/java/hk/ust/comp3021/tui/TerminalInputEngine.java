package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.utils.ShouldNotReachException;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

import static hk.ust.comp3021.utils.StringResources.EXIT_COMMAND_TEXT;
import static hk.ust.comp3021.utils.StringResources.INVALID_INPUT_MESSAGE;

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

    private static final Pattern MOVE_REGEX = Pattern.compile("^(?<action>[WASDwasdRrHJKLhjklUu])$");

    /**
     * Fetch an action from user in terminal to process.
     *
     * @return the user action.
     */
    @Override
    public @NotNull Action fetchAction() {
        final var inputLine = terminalScanner.nextLine();
        final var moveMatcher = MOVE_REGEX.matcher(inputLine);
        if (moveMatcher.find()) {
            final var moveCommand = moveMatcher.group("action").toUpperCase();
            final var playerId = switch (moveCommand) {
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
        } else if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
            return new Exit(-1);
        } else {
            return new InvalidInput(-1, INVALID_INPUT_MESSAGE);
        }
    }
}
