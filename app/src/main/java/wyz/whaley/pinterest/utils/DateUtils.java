package wyz.whaley.pinterest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by alwayking on 16/3/13.
 */
public class DateUtils {

    public static String convertTZDate(String tz) {
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = df.parse(tz);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("MMM dd, yyyy").format(date);
    }

}
