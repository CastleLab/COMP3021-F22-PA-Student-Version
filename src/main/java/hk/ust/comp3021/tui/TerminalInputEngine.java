package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.actions.InvalidInput;
import hk.ust.comp3021.actions.Move;
import hk.ust.comp3021.actions.Undo;
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

    /**
     * @param terminalStream The stream to read terminal inputs.
     */
    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Optional<? extends Action> fetchAction() {
        System.out.print(">>> ");
        var inputLine = terminalScanner.nextLine();
        var moveRegex = Pattern.compile("^(?<direction>[LRUD])(\\s+(?<steps>\\d+))?$");
        var moveMatcher = moveRegex.matcher(inputLine);
        if (moveMatcher.find()) {
            var stepsStr = moveMatcher.group("steps");
            var steps = stepsStr != null ? Integer.parseInt(stepsStr) : 1;
            return switch (moveMatcher.group("direction")) {
                case "L" -> Optional.of(new Move.Left(steps));
                case "R" -> Optional.of(new Move.Right(steps));
                case "U" -> Optional.of(new Move.Up(steps));
                case "D" -> Optional.of(new Move.Down(steps));
                default -> throw new ShouldNotReachException();
            };
        }
        if (inputLine.equals("Undo"))
            return Optional.of(new Undo());
        return Optional.of(new InvalidInput("Invalid input sequence"));
    }
}
