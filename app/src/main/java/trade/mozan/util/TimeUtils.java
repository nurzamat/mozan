package trade.mozan.util;

import java.util.Date;

/**
 * Created by igorkhomenko on 1/13/15.
 */
public class TimeUtils {
    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    private TimeUtils() {
    }

    /**
     * converts time (in milliseconds) to human-readable format
     *  "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration) {
        if(duration > 0) {
            duration = new Date().getTime() - duration;
        }
        if(duration < 0){
            duration = 0;
        }

        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                res.append(temp).append(" д.").append(temp > 1 ? "" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                res.append(temp).append(" ч.").append(temp > 1 ? "" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                res.append(temp).append(" мин.").append(temp > 1 ? "" : "");
            }

            if (!res.toString().equals("") && duration >= ONE_SECOND) {
                res.append(" и ");
            }

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" сек.").append(temp > 1 ? "" : "");
            }
            res.append(" назад");
            return res.toString();
        } else {
            return "только что";
        }
    }
}
