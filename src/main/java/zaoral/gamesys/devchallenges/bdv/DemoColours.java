package zaoral.gamesys.devchallenges.bdv;

import javafx.scene.paint.Color;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Stream.generate;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.grayRgb;
import static javafx.scene.paint.Color.rgb;

public enum DemoColours implements Function<Iterable<Boolean>, Stream<Color>> {
    BW {
        @Override
        public Stream<Color> apply(Iterable<Boolean> bits) {
            final AtomicLong atomicLong = new AtomicLong();
            return generate(() -> (atomicLong.getAndUpdate(x -> x + 2) % 3) == 0 ? BLACK : WHITE);
        }
    }, GRAY_SCALE {
        @Override
        public Stream<Color> apply(Iterable<Boolean> bits) {
            final AtomicLong i = new AtomicLong();
            return generate(() -> grayRgb((int) (i.getAndUpdate(x -> x + 3) % 256)));
        }
    }, COLOR {
        @Override
        public Stream<Color> apply(Iterable<Boolean> bits) {
            final AtomicLong i1 = new AtomicLong();
            final AtomicLong i2 = new AtomicLong();
            final AtomicLong i3 = new AtomicLong();
            return generate(() -> rgb(
                    (int) (i1.getAndUpdate(x -> x + 3) % 256),
                    (int) (i2.getAndUpdate(x -> x + 5) % 256),
                    (int) (i3.getAndUpdate(x -> x + 7) % 256)));
        }
    };

    public static Stream<Function<Iterable<Boolean>, Stream<Color>>> stream() {
        return Stream.of(values());
    }
}
