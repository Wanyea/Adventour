package com.adventour.android;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdventourUtils {
    public static String formatBirthdateForDB(Calendar calendar) {
        return Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" +
                Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
                Integer.toString(calendar.get(Calendar.YEAR));
    }

    public static String formatBirthdateFromDatabase(Date date)
    {
        return DateFormat.format("MM/dd/yyyy", date).toString();
    }


    public static boolean isValidNickname(String nickname)
    {
        if (nickname.length() > 20 || nickname.length() == 0)
            return false;
        else
            return true;

    }

    public static boolean isValidEmail(String email)
    {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches())
            return true;
        else
            return false;

    }

    public static boolean isUserOver13(int day, int month, int year)
    {

        GregorianCalendar cal = new GregorianCalendar();

        int age = Math.abs(year - cal.get(Calendar.YEAR));

        if ((month < cal.get(Calendar.MONTH)) || ((month == cal.get(Calendar.MONTH)) && (day < cal.get(Calendar.DAY_OF_MONTH))))
            --age;

        if(age > 13)
            return true;
        else
            return false;

    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches())
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

    public static boolean isEmailEmpty(String email) {
        if(email.isEmpty())
            return true;
        else
            return false;

    }

    public static boolean isPasswordEmpty(String password) {
        if(password.isEmpty())
            return true;
        else
            return false;
    }
}


