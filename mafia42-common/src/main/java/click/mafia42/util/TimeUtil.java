package click.mafia42.util;

import java.time.Instant;

public class TimeUtil {
    public static boolean isTimeOver(long endTimeSecond) {
        return Instant.now().getEpochSecond() >= endTimeSecond;
    }

    public static long getRemainingTime(long endTimeSecond) {
        return Math.max(0, endTimeSecond - Instant.now().getEpochSecond());
    }
}
