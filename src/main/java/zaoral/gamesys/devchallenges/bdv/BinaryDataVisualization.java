package zaoral.gamesys.devchallenges.bdv;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.emptyList;
import static zaoral.gamesys.devchallenges.bdv.ColourDecoder.COLOUR;
import static zaoral.gamesys.devchallenges.bdv.PathAlgorithm.DIAGONAL;

public class BinaryDataVisualization extends Application {
    public static final int CANVAS_SIZE = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        final File file = readFileFromParameters();
        final List<Boolean> bits = readBits(file);

        final Setup setup = new Setup(COLOUR, DIAGONAL, bits, 6, 100);
        final Stage view = getView(stage, setup);
        view.show();
    }

    private Stage getView(Stage primaryStage, Setup setup) {
        primaryStage.setTitle("Binary data visualization");
        primaryStage.setScene(new Scene(new Group(paint(setup))));
        return primaryStage;
    }

    private Canvas paint(Setup setup) {
        final Iterator<Color> colours = setup.colours().iterator();
        return setup.path()
                .limit(CANVAS_SIZE * CANVAS_SIZE)
                .reduce(new Canvas(CANVAS_SIZE, CANVAS_SIZE), (canvas, coordinates) -> {
                    canvas.getGraphicsContext2D().setFill(colours.next());
                    canvas.getGraphicsContext2D().fillRect(
                            coordinates.getX() * setup.getBlockSize(),
                            coordinates.getY() * setup.getBlockSize(),
                            setup.getBlockSize(),
                            setup.getBlockSize());
                    return canvas;
                }, (c1, c2) -> c1);
    }

    private File readFileFromParameters() {
        final List<String> parameters = getParameters().getRaw();
        if (parameters.isEmpty() || !Files.isReadable(Paths.get(parameters.get(0)))) {
            throw new IllegalArgumentException("First argument must be a file with readable data.");
        }
        return new File(parameters.get(0));
    }

    private List<Boolean> readBits(File file) {
        final List<Boolean> bits = new LinkedList<>();
        try (FileReader reader = new FileReader(file)) {
            int c;
            while ((c = reader.read()) != -1) {
                bits.add(c == '1');
            }
            return bits;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emptyList();
    }
}
