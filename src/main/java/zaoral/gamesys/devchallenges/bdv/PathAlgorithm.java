package zaoral.gamesys.devchallenges.bdv;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Stream.iterate;

public enum PathAlgorithm implements Function<Integer, Stream<Coordinates>> {
    DIAGONAL {
        @Override
        public Stream<Coordinates> apply(Integer blocks) {
            return iterate(new Coordinates(0, 0),
                    previous -> {
                        if (previous.getX() == blocks - 1) {
                            return new Coordinates(previous.getY() + 1, blocks - 1);
                        } else if (previous.getY() == 0) {
                            return new Coordinates(0, previous.getX() + 1);
                        } else {
                            return new Coordinates(previous.getX() + 1, previous.getY() - 1);
                        }
                    });
        }
    }, SPIRAL {
        @Override
        public Stream<Coordinates> apply(Integer blocks) {
            return iterate(new Coordinates(blocks / 2, blocks / 2),
                    previous -> {
                        if (previous.xLessThanOrEqualToY() && previous.sum() >= blocks) {
                            return new Coordinates(previous.getX() - 1, previous.getY());
                        } else if (previous.xLessThanY()) {
                            return new Coordinates(previous.getX(), previous.getY() - 1);
                        } else if (previous.xGreaterOrEqualToY() && previous.sum() < blocks) {
                            return new Coordinates(previous.getX() + 1, previous.getY());
                        } else {
                            return new Coordinates(previous.getX(), previous.getY() + 1);
                        }
                    });
        }
    }, ZIG_ZAG {
        @Override
        public Stream<Coordinates> apply(Integer blocks) {
            return iterate(new Coordinates(0, 0),
                    previous -> {
                        if (previous.getY() % 2 == 0 && previous.getX() < blocks - 1) {
                            return new Coordinates(previous.getX() + 1, previous.getY());
                        } else if (previous.getY() % 2 == 1 && previous.getX() > 0) {
                            return new Coordinates(previous.getX() - 1, previous.getY());
                        } else {
                            return new Coordinates(previous.getX(), previous.getY() + 1);
                        }
                    });
        }
    }, ROW_FIRST {
        @Override
        public Stream<Coordinates> apply(Integer blocks) {
            return iterate(new Coordinates(0, 0), p -> (p.getX() == blocks - 1)
                    ? new Coordinates(0, p.getY() + 1)
                    : new Coordinates(p.getX() + 1, p.getY()));
        }
    }
}
