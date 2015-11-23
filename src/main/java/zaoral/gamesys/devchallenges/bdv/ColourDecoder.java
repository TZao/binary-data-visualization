package zaoral.gamesys.devchallenges.bdv;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Stream.generate;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.grayRgb;
import static javafx.scene.paint.Color.rgb;
import static zaoral.gamesys.devchallenges.bdv.Utils.uncheckException;

public enum ColourDecoder implements Function<Reader, Stream<Color>> {
    BLACK_AND_WHITE {
        @Override
        public Stream<Color> apply(Reader fileReader) {
            return generate(uncheckException(() -> fileReader.read() == '1' ? BLACK : WHITE));
        }
    }, GRAY_SCALE {
        @Override
        public Stream<Color> apply(Reader reader) {
            return generate(uncheckException(() -> grayRgb(readByte(reader))));
        }
    }, COLOUR {
        @Override
        public Stream<Color> apply(Reader reader) {
            return generate(uncheckException(() -> rgb(readByte(reader), readByte(reader), readByte(reader))));
        }
    };

    private static int readByte(Reader fileReader) throws IOException {
        int b = 0;
        for (int j = 1; j <= Byte.SIZE; j++) {
            int c = fileReader.read();
            if (c == '1') {
                b |= (1 << (Byte.SIZE - j));
            }
        }
        return b;
    }
}
