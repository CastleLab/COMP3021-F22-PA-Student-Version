package hk.ust.comp3021.actions;

import hk.ust.comp3021.game.Position;
import hk.ust.comp3021.utils.TestExtension;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TestExtension.class)
class MoveTest {

    private final Position pos = Position.of(233, 233);

    @Tag(TestKind.PUBLIC)
    @Test
    void moveLeft() {
        assertEquals(
                Position.of(232, 233),
                new Move.Left(-1).nextPosition(pos)
        );
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void moveRight() {
        assertEquals(
                Position.of(234, 233),
                new Move.Right(-1).nextPosition(pos)
        );
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void moveUp() {
        assertEquals(
                Position.of(233, 232),
                new Move.Up(-1).nextPosition(pos)
        );
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void moveDown() {
        assertEquals(
                Position.of(233, 234),
                new Move.Down(-1).nextPosition(pos)
        );
    }
}
