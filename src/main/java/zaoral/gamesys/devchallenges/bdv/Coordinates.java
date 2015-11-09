package zaoral.gamesys.devchallenges.bdv;

import lombok.Data;

@Data
public class Coordinates {
    private final int x;
    private final int y;

    public boolean xGreaterThanY() {
        return x > y;
    }

    public boolean xLessThanY() {
        return x < y;
    }

    public boolean xGreaterOrEqualToY() {
        return x >= y;
    }

    public boolean xLessThanOrEqualToY() {
        return x <= y;
    }

    public boolean xEqualY() {
        return x == y;
    }

    public boolean xNotEqualToY() {
        return x != y;
    }

    public int sum() {
        return x + y;
    }
}
