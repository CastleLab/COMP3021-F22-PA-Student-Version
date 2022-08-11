package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.utils.ShouldNotReachException;

import java.io.InputStream;
import java.util.Arrays;
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
    public PlayerAction fetchAction(int[] playerIds) {
        if (playerIds.length > 2 || playerIds.length < 1) {
            throw new IllegalArgumentException("too few or too many players. Only 1 or 2 players are supported.");
        }
        PlayerAction playerAction = null;
        while (playerAction == null) {
            System.out.print(">>> ");
            var inputLine = terminalScanner.nextLine();
            var moveRegex = Pattern.compile("^(?<action>[WASDwasdRrHJKLhjklUu])(\\s*(?<repeat>\\d+))?$");
            var moveMatcher = moveRegex.matcher(inputLine);
            if (moveMatcher.find()) {
                var repeatR = moveMatcher.group("repeat");
                var repeat = repeatR != null ? Integer.parseInt(repeatR) : 1;
                var action = switch (moveMatcher.group("action").toUpperCase()) {
                    case "W", "K" -> new Move.Up();
                    case "A", "H" -> new Move.Left();
                    case "S", "J" -> new Move.Down();
                    case "D", "L" -> new Move.Right();
                    case "R", "U" -> new Undo();
                    default -> throw new ShouldNotReachException();
                };
                var playerId = switch (moveMatcher.group("action").toUpperCase()) {
                    case "W", "A", "S", "D", "R" -> playerIds[0];
                    case "H", "J", "K", "L", "U" -> playerIds.length == 1 ? playerIds[0] : playerIds[1];
                    default -> throw new ShouldNotReachException();
                };
                var actions = new Action[repeat];
                Arrays.fill(actions, action);
                playerAction = new PlayerAction(playerId, actions);
            } else if (inputLine.equalsIgnoreCase("exit")) {
                playerAction = new PlayerAction(null, new Exit());
            } else {
                System.out.println("Invalid input");
            }
        }
        return playerAction;
    }
}
