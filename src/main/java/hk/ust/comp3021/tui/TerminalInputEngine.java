package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.actions.Undo;
import hk.ust.comp3021.game.InputEngine;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

/**
 * An input engine that fetches actions from terminal input.
 */
public class TerminalInputEngine implements InputEngine {

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
        terminalScanner.next();
        return Optional.of(new Undo());
    }
}
