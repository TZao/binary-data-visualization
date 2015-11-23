package zaoral.gamesys.devchallenges.bdv;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import static java.util.stream.Stream.generate;
import static javafx.scene.paint.Color.WHITE;

@AllArgsConstructor
public class Setup {
    private final ColourDecoder colourDecoder;
    private final PathAlgorithm pathAlgorithm;
    private final File file;
    @Getter
    private final int blockSize;
    @Getter
    private final int blocks;

    public Stream<Coordinates> path() {
        return pathAlgorithm.apply(blocks * blocks);
    }

    public Stream<Color> colours() {
        try (FileReader fileReader = new FileReader(file)) {
            return colourDecoder.apply(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return generate(() -> WHITE);
    }

    public int canvasSize() {
        return blockSize * blocks;
    }
}
