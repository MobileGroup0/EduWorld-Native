package group0.eduworld;


import android.text.format.DateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Util {
    public static String formatDateTime(Date date){
        String result;
        Locale locale = Locale.getDefault();

        long passedDays = TimeUnit.MILLISECONDS.toDays(new Date().getTime() - date.getTime());

        if(passedDays < 1) {
            result = DateFormat.format(DateFormat.getBestDateTimePattern(locale, "m:h"), date).toString();
        }else if (passedDays < 2){
            result = "Yesterday";
        } else if (passedDays < 7){
            result = DateFormat.format(DateFormat.getBestDateTimePattern(locale, "EEE"), date).toString();
        } else if (passedDays < 365){
            result = DateFormat.format(DateFormat.getBestDateTimePattern(locale, "MMM d"), date).toString();
        } else {
            result = DateFormat.format(DateFormat.getBestDateTimePattern(locale, "MMM d, yyyy"), date).toString();
        }

        return result;
    }
}
