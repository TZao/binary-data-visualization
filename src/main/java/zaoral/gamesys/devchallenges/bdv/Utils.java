package zaoral.gamesys.devchallenges.bdv;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Utils {
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
}
