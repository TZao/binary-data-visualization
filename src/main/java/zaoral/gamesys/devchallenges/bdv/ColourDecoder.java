package zaoral.gamesys.devchallenges.bdv;

import javafx.scene.paint.Color;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Stream.generate;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.grayRgb;
import static javafx.scene.paint.Color.rgb;

public enum ColourDecoder implements Function<Iterable<Boolean>, Stream<Color>> {
    B_W {
        @Override
        public Stream<Color> apply(Iterable<Boolean> bits) {
            final Iterator<Boolean> bitIt = bits.iterator();
            return generate(() -> bitIt.hasNext() && bitIt.next() ? BLACK : WHITE);
        }
    }, GRAY_SCALE {
        @Override
        public Stream<Color> apply(Iterable<Boolean> bits) {
            final Iterator<Boolean> bitIt = bits.iterator();
            return generate(() -> grayRgb(readByte(bitIt)));
        }
    }, COLOUR {
        @Override
        public Stream<Color> apply(Iterable<Boolean> bits) {
            final Iterator<Boolean> bitIt = bits.iterator();
            return generate(() -> rgb(readByte(bitIt), readByte(bitIt), readByte(bitIt)));
        }
    };

    private static int readByte(Iterator<Boolean> bitIt) {
        int b = 0;
        for (int j = 1; j <= Byte.SIZE; j++) {
            if (bitIt.hasNext() && bitIt.next()) {
                b |= (1 << (Byte.SIZE - j));
            }
        }
        return b;
    }

    public static Stream<ColourDecoder> stream() {
        return Stream.of(values());
    }
}
