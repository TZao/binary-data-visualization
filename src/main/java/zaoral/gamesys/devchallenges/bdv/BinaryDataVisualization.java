package zaoral.gamesys.devchallenges.bdv;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
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
import java.util.stream.Stream;

import static java.util.stream.Stream.iterate;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

public class BinaryDataVisualization extends Application {
    private static final int CANVAS_SIZE = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        File data = readFileFromParameters();
        primaryStage.setTitle("Binary data visualization");
        Group root = new Group();
        Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);

        try (
            FileReader fileReader = new FileReader(data)) {
            final Stream<Color> colors = Stream.generate(() -> {
                try {
                    if (fileReader.read() == '1') {
                        return BLACK;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return WHITE;
            });
            visualize(canvas.getGraphicsContext2D().getPixelWriter(), topLeftFirstDiagonalCrosshatch(), colors.iterator());
        } catch (IOException e) {
            System.err.println("IOException occurred.");
        }

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

    private void visualize(PixelWriter pw, Stream<Coordinates> pixelSelector, Iterator<Color> colors) {
        pixelSelector.limit(CANVAS_SIZE * CANVAS_SIZE).forEach(c -> pw.setColor(c.getX(), c.getY(), colors.next()));
    }

    private Stream<Coordinates> centerFirstClockwiseSnake() {
        return iterate(new Coordinates(CANVAS_SIZE / 2, CANVAS_SIZE / 2),
                previous -> {
                    if (previous.xLessThanOrEqualToY() && previous.sum() >= CANVAS_SIZE) {
                        return new Coordinates(previous.getX() - 1, previous.getY());
                    } else if (previous.xLessThanY()) {
                        return new Coordinates(previous.getX(), previous.getY() - 1);
                    } else if (previous.xGreaterOrEqualToY() && previous.sum() < CANVAS_SIZE) {
                        return new Coordinates(previous.getX() + 1, previous.getY());
                    } else {
                        return new Coordinates(previous.getX(), previous.getY() + 1);
                    }
                });
    }

    private Stream<Coordinates> topLeftRowFirstZigZag() {
        return iterate(new Coordinates(0, 0),
                previous -> {
                    if (previous.getY() % 2 == 0 && previous.getX() < CANVAS_SIZE - 1) {
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
                    if (previous.getX() == CANVAS_SIZE - 1) {
                        return new Coordinates(previous.getY() + 1, CANVAS_SIZE - 1);
                    } else if (previous.getY() == 0) {
                        return new Coordinates(0, previous.getX() + 1);
                    } else {
                        return new Coordinates(previous.getX() + 1, previous.getY() - 1);
                    }
                });
    }
}
