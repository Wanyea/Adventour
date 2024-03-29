package com.adventour.android;

import static java.lang.Math.toIntExact;

import android.text.format.DateFormat;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdventourUtils {
    public static String formatCalendarToString(Calendar calendar) {
        return Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" +
                Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
                Integer.toString(calendar.get(Calendar.YEAR));
    }

    public static String formatBirthdateFromDatabase(Timestamp timestamp)
    {
        Date date = timestamp.toDate();
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

        Calendar c1 = Calendar.getInstance();
        c1.set(year, month, day, 0, 0); // as MONTH in calender is 0 based.

        Calendar c2 = Calendar.getInstance();
        int age = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) > c2.get(Calendar.MONTH) ||
                (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DATE) > c2.get(Calendar.DATE))) {
            age--;
        }

        if(age >= 13)
            return true;
        else
            return false;

    }

    public static boolean isUserOver21Plus(int day, int month, int year)
    {
        Calendar c1 = Calendar.getInstance();
        c1.set(year, month, day, 0, 0); // as MONTH in calender is 0 based.

        Calendar c2 = Calendar.getInstance();
        int age = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) > c2.get(Calendar.MONTH) ||
                (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DATE) > c2.get(Calendar.DATE))) {
            age--;
        }

        if(age >= 21)
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

    public static boolean isProfilePictureSelected(long androidPfpRef)
    {
        return androidPfpRef != 6;
    }

    public static int iOSToAndroidPfpRef(String iOSPfpRef)
    {
        switch (iOSPfpRef)
        {
            case "profpic_cheetah":
                return 0;
            case "profpic_elephant":
                return 1;
            case "profpic_ladybug":
                return 2;
            case "profpic_monkey":
                return 3;
            case "profpic_fox":
                return 4;
            case "profpic_penguin":
                return 5;
            default:
                return 6;
        }
    }

    public static boolean isValidAndroidPfpRef(DocumentSnapshot response)
    {
        if (response.get("androidPfpRef") != null)
        {
            int androidPfpRef = toIntExact((long) response.get("androidPfpRef"));
                return androidPfpRef == 0 || androidPfpRef == 1 || androidPfpRef == 2 || androidPfpRef == 3 || androidPfpRef == 4 || androidPfpRef == 5;
        }

        return false;
    }

    public static boolean isValidIOSPfpRef(DocumentSnapshot response)
    {
        return response.get("iosPfpRef") != null;
    }
}


