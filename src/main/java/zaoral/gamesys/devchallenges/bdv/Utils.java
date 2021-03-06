package zaoral.gamesys.devchallenges.bdv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.err;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static zaoral.gamesys.devchallenges.bdv.BinaryDataVisualization.CANVASES;
import static zaoral.gamesys.devchallenges.bdv.BinaryDataVisualization.CANVAS_SIZE;

public class Utils {
    public static final int RGB_BYTES = 3;

    public static <T> Supplier<T> uncheckException(Callable<T> callable) {
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

    public static void main(String[] args) {
        tooManyBlockSizes().forEach(blockSize ->
                System.out.println(format("blockSize: %3d| blocks: %3d", blockSize, CANVAS_SIZE / blockSize)));
        System.out.println(tooManyBlockSizes().count());
    }

    public static File readFileFromParameters(List<String> argv) {
        if (argv.isEmpty() || !Files.isReadable(Paths.get(argv.get(0)))) {
            throw new IllegalArgumentException("First argument must be a file with readable data.");
        }
        return new File(argv.get(0));
    }

    public static Stream<Integer> tooManyBlockSizes() {
        return IntStream.iterate(1, blockSize -> blockSize + 1)
                .limit(CANVAS_SIZE / 10)
                .filter(blockSize -> (CANVAS_SIZE / (double) blockSize) % 1 == 0)
                .filter(blockSize -> (CANVAS_SIZE / blockSize & 1) == 0)
                .mapToObj(Integer::valueOf);
    }

    public static List<Boolean> readBits(File file) {
        final long maxDataRequired = CANVAS_SIZE * CANVAS_SIZE * RGB_BYTES * Byte.SIZE;
        try (FileReader reader = new FileReader(file)) {

            return generate(uncheckException(() -> reader.read() == '1'))
                    .limit(maxDataRequired)
                    .collect(toList());

        } catch (IOException exception) {
            err.println("Error reading data!");
            exception.printStackTrace();
        }
        return emptyList();
    }

    public static int blocks(int blockSize) {
        return CANVAS_SIZE / blockSize;
    }

    public static double getPercentagePainted(AtomicInteger paintedCanvases) {
        return 100 * paintedCanvases.getAndIncrement() / (double) CANVASES;
    }
}
