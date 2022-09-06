package com.adventour.android;

import java.util.Calendar;

public class AdventourUtils {
    public static String formatBirthdateForDB(Calendar calendar) {
        return Integer.toString(calendar.get(Calendar.MONTH)) + "/" +
                Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
                Integer.toString(calendar.get(Calendar.MONTH));
    }
}
