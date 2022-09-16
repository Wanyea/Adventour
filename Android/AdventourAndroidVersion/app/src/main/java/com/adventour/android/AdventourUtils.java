package com.adventour.android;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AdventourUtils {
    public static String formatBirthdateForDB(Calendar calendar) {
        return Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" +
                Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
                Integer.toString(calendar.get(Calendar.YEAR));
    }


    public static boolean isValidNickname(String nickname)
    {
        if (nickname.length() > 20 || nickname.length() < 6)
            return false;
        else
            return true;

    }

    public static boolean isValidEmail(String email)
    {
        /*if ()
            return false;
        else*/
            return true; //CHECK REGEX

    }

    public static boolean isUserOver13(int day, int month, int year)
    {

        GregorianCalendar cal = new GregorianCalendar();

        int age = Math.abs(year - cal.get(Calendar.YEAR));

        if ((month < cal.get(Calendar.MONTH))
                || ((month == cal.get(Calendar.MONTH)) && (day < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --age;
        }

        if(age > 13)
            return true;
        else
            return false;

    }

    public static boolean checkPasswordsMatch(String password, String confirmPassword) {

        if (password.equals(confirmPassword))
            return true;
        else
            return false;
    }
}
