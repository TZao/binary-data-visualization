package zaoral.gamesys.devchallenges.bdv;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Stream.generate;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.grayRgb;
import static javafx.scene.paint.Color.rgb;
import static zaoral.gamesys.devchallenges.bdv.Utils.uncheckException;

public enum ColourDecoder implements Function<Iterator<Boolean>, Stream<Color>> {
    BLACK_AND_WHITE {
        @Override
        public Stream<Color> apply(Iterator<Boolean> bits) {
            return generate(uncheckException(() -> bits.hasNext() && bits.next() ? BLACK : WHITE));
        }
    }, GRAY_SCALE {
        @Override
        public Stream<Color> apply(Iterator<Boolean> bits) {
            return generate(uncheckException(() -> grayRgb(readByte(bits))));
        }
    }, COLOUR {
        @Override
        public Stream<Color> apply(Iterator<Boolean> bits) {
            return generate(uncheckException(() -> rgb(readByte(bits), readByte(bits), readByte(bits))));
        }
    };

    private static int readByte(Iterator<Boolean> bits) throws IOException {
        int b = 0;
        for (int j = 1; j <= Byte.SIZE; j++) {
            if (bits.hasNext() && bits.next()) {
                b |= (1 << (Byte.SIZE - j));
            }
        }
        return b;
    }
}
