package zaoral.gamesys.devchallenges.bdv;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Stream.generate;
import static java.util.stream.Stream.iterate;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.grayRgb;
import static javafx.scene.paint.Color.rgb;

public class BinaryDataVisualization extends Application {
    private static final int BLOCK_SIZE = 4;
    private static final int BLOCKS = 150;
    private static final int CANVAS_SIZE = BLOCK_SIZE * BLOCKS;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        FileReader reader = new FileReader(readFileFromParameters());
        primaryStage.setTitle("Binary data visualization");
        Group root = new Group();
        Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);

        Stream<Color> colors = colorStream(reader).limit(BLOCKS * BLOCKS);
        visualize(canvas.getGraphicsContext2D(), centerFirstClockwiseSnake(), colors.iterator());

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private File readFileFromParameters() {
        final List<String> parameters = getParameters().getRaw();
        if (parameters.isEmpty() || !Files.isReadable(Paths.get(parameters.get(0)))) {
            throw new IllegalArgumentException("First argument must be a file with readable data.");
        }
        return new File(parameters.get(0));
    }

    private void visualize(GraphicsContext gc, Stream<Coordinates> pixelSelector, Iterator<Color> colors) {
        pixelSelector.limit(BLOCKS * BLOCKS).forEach(c -> {
            gc.setFill(colors.next());
            gc.fillRect(c.getX() * BLOCK_SIZE, c.getY() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        });
    }

    private Stream<Coordinates> centerFirstClockwiseSnake() {
        return iterate(new Coordinates(BLOCKS / 2, BLOCKS / 2),
                previous -> {
                    if (previous.xLessThanOrEqualToY() && previous.sum() >= BLOCKS) {
                        return new Coordinates(previous.getX() - 1, previous.getY());
                    } else if (previous.xLessThanY()) {
                        return new Coordinates(previous.getX(), previous.getY() - 1);
                    } else if (previous.xGreaterOrEqualToY() && previous.sum() < BLOCKS) {
                        return new Coordinates(previous.getX() + 1, previous.getY());
                    } else {
                        return new Coordinates(previous.getX(), previous.getY() + 1);
                    }
                });
    }

    private Stream<Coordinates> topLeftRowFirstZigZag() {
        return iterate(new Coordinates(0, 0),
                previous -> {
                    if (previous.getY() % 2 == 0 && previous.getX() < BLOCKS - 1) {
                        return new Coordinates(previous.getX() + 1, previous.getY());
                    } else if (previous.getY() % 2 == 1 && previous.getX() > 0) {
                        return new Coordinates(previous.getX() - 1, previous.getY());
                    } else {
                        return new Coordinates(previous.getX(), previous.getY() + 1);
                    }
                });
    }

    private Stream<Coordinates> topLeftFirstDiagonalCrosshatch() {
        return iterate(new Coordinates(0, 0),
                previous -> {
                    if (previous.getX() == BLOCKS - 1) {
                        return new Coordinates(previous.getY() + 1, BLOCKS - 1);
                    } else if (previous.getY() == 0) {
                        return new Coordinates(0, previous.getX() + 1);
                    } else {
                        return new Coordinates(previous.getX() + 1, previous.getY() - 1);
                    }
                });
    }

    private Stream<Coordinates> leftToRightTopToBottom() {
        return iterate(new Coordinates(0, 0),
                previous -> {
                    if (previous.getX() == BLOCKS - 1) {
                        return new Coordinates(0, previous.getY() + 1);
                    }
                    return new Coordinates(previous.getX() + 1, previous.getY());
                });
    }

    private Stream<Color> blackAndWhiteStream(FileReader fileReader) {
        return generate(wrap(() -> blackOrWhite(fileReader)));
    }

    private Stream<Color> grayScaleStream(FileReader fileReader) {
        return generate(wrap(() -> grayRgb(readByte(fileReader))));
    }

    private Stream<Color> colorStream(FileReader fileReader) {
        return generate(wrap(() -> color(fileReader)));
    }

    private Color color(FileReader fileReader) throws IOException {
        return rgb(readByte(fileReader), readByte(fileReader), readByte(fileReader));
    }

    private Color blackOrWhite(FileReader fileReader) throws IOException {
        return fileReader.read() == '1' ? BLACK : WHITE;
    }

    private int readByte(FileReader fileReader) throws IOException {
        int b = 0;
        for (int j = 1; j <= Byte.SIZE; j++) {
            int c = fileReader.read();
            if (c == '1') {
                b |= (1 << (Byte.SIZE - j));
            }
        }
        return b;
    }

    public static <T> Supplier<T> wrap(Callable<T> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
