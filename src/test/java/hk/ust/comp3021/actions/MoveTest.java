package hk.ust.comp3021.actions;

import hk.ust.comp3021.game.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    private final Position pos = Position.of(233, 233);

    @Test
    void moveLeft() {
        assertEquals(
                Position.of(232, 233),
                new Move.Left(-1).nextPosition(pos)
        );
    }

    @Test
    void moveRight() {
        assertEquals(
                Position.of(234, 233),
                new Move.Left(-1).nextPosition(pos)
        );
    }

    @Test
    void moveUp() {
        assertEquals(
                Position.of(233, 232),
                new Move.Left(-1).nextPosition(pos)
        );
    }

    @Test
    void moveDown() {
        assertEquals(
                Position.of(233, 234),
                new Move.Left(-1).nextPosition(pos)
        );
    }
}
