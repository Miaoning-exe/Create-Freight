package space.miaoning.create_freight.util;

import javax.annotation.Nullable;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ExecutionChecker {
    private final FrequencyType frequency;
    private final List<Integer> offsetHours;

    public ExecutionChecker(String frequencyFlag, List<? extends Integer> offsetHours) {
        this.frequency = FrequencyType.valueOf(frequencyFlag.toUpperCase());
        this.offsetHours = offsetHours.stream()
                .map(Number::intValue)
                .toList();
    }

    public boolean shouldExecute(@Nullable ZonedDateTime lastTime) {
        if (lastTime == null) {
            return true;
        }

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime baseTime = getBaseTime(now);

        for (int offsetHour : offsetHours) {
            ZonedDateTime scheduledTime = baseTime.plusHours(offsetHour);

            if (now.isAfter(scheduledTime) && lastTime.isBefore(scheduledTime)) {
                return true;
            }
        }

        return false;
    }

    private ZonedDateTime getBaseTime(ZonedDateTime now) {
        return (switch (frequency) {
            case DAILY -> now;
            case WEEKLY -> now.with(DayOfWeek.MONDAY);
            case MONTHLY -> now.withDayOfMonth(1);
        }).truncatedTo(ChronoUnit.DAYS);
    }

    private enum FrequencyType {
        DAILY, WEEKLY, MONTHLY
    }
}
