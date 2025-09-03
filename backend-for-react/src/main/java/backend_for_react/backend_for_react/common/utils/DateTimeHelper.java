package backend_for_react.backend_for_react.common.utils;

import java.time.LocalDateTime;

public class DateTimeHelper {
    public static LocalDateTime calculateFromDate(String timeUnit, int amount) {
        LocalDateTime now = LocalDateTime.now();
        switch (timeUnit) {
            case "week":
                return now.minusWeeks(amount);  // lùi N tuần
            case "month":
                return now.minusMonths(amount); // lùi N tháng
            default:
                throw new IllegalArgumentException(
                        "timeUnit must be WEEK or MONTH"
                );
        }
    }
}
