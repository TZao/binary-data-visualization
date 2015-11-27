package zaoral.gamesys.devchallenges.bdv;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static javafx.application.Platform.runLater;
import static zaoral.gamesys.devchallenges.bdv.Utils.blocks;
import static zaoral.gamesys.devchallenges.bdv.Utils.getPercentagePainted;
import static zaoral.gamesys.devchallenges.bdv.Utils.readBits;
import static zaoral.gamesys.devchallenges.bdv.Utils.readFileFromParameters;

public class BinaryDataVisualization extends Application {
    public static final int CANVAS_SIZE = 600;
    public static final List<Integer> BLOCK_SIZES = asList(10, 6, 5, 4, 3, 2);
    public static final int CANVASES = ColourDecoder.values().length
            * PathAlgorithm.values().length
            * BLOCK_SIZES.size();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        final File file = readFileFromParameters(getParameters().getRaw());

        out.println("Reading bits...");

        final List<Boolean> bits = readBits(file);

        out.println("Painting canvases...");

        final List<Canvas> canvases = paintCanvases(bits);

        final Stage view = getView(stage, canvases);
        view.show();
    }

    private List<Canvas> paintCanvases(List<Boolean> bits) {
        final AtomicInteger paintedCanvases = new AtomicInteger();
        return ColourDecoder.stream().parallel().flatMap(colourDecoder ->
                PathAlgorithm.stream().parallel().flatMap(pathAlgorithm ->
                        BLOCK_SIZES.stream().map(blockSize -> {
                            out.printf("%.1f %%\n", getPercentagePainted(paintedCanvases));
                            return paint(colourDecoder.apply(bits), pathAlgorithm.apply(blocks(blockSize)), blockSize);
                        })))
                .collect(toList());
    }

    private Canvas paint(Stream<Color> colors, Stream<Coordinates> path, int blockSize) {
        final Iterator<Color> colours = colors.iterator();
        return path
                .limit(CANVAS_SIZE * CANVAS_SIZE)
                .reduce(new Canvas(CANVAS_SIZE, CANVAS_SIZE), (canvas, coordinates) -> {
                    canvas.getGraphicsContext2D().setFill(colours.next());
                    canvas.getGraphicsContext2D().fillRect(
                            coordinates.getX() * blockSize,
                            coordinates.getY() * blockSize,
                            blockSize,
                            blockSize);
                    return canvas;
                }, (c1, c2) -> c1);
    }

    public Slider getSlider(Consumer<Canvas> selectedValueConsumer, List<Canvas> canvases) {
        final Slider slider = new Slider(1, canvases.size(), 1);
        slider.setShowTickMarks(TRUE);
        slider.setSnapToTicks(TRUE);
        slider.setBlockIncrement(1);
        slider.valueProperty().addListener((observable, oldValue, newValue) ->
                selectedValueConsumer.accept(canvases.get(newValue.intValue() - 1)));
        return slider;
    }


    private Stage getView(Stage primaryStage, List<Canvas> canvases) {
        primaryStage.setTitle("Binary data visualization");
        final BorderPane borderPane = new BorderPane(canvases.get(0));
        borderPane.setPadding(new Insets(5));
        borderPane.setBottom(getSlider(selectedCanvas -> runLater(() -> borderPane.setCenter(selectedCanvas)), canvases));
        primaryStage.setScene(new Scene(borderPane));
        return primaryStage;
    }
}
