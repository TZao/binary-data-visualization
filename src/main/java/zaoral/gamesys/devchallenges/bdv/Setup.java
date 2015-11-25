package zaoral.gamesys.devchallenges.bdv;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public class Setup {
    private final ColourDecoder colourDecoder;
    private final PathAlgorithm pathAlgorithm;
    private final Iterable<Boolean> bits;
    @Getter
    private final int blockSize;
    @Getter
    private final int blocks;

    public Stream<Coordinates> path() {
        return pathAlgorithm.apply(blocks * blocks);
    }

    public Stream<Color> colours() {
        return colourDecoder.apply(bits.iterator());
    }

    public int canvasSize() {
        return blockSize * blocks;
    }
}
