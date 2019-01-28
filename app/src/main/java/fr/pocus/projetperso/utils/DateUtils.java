package fr.pocus.projetperso.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pocus on 24/01/2019.
 */

public class DateUtils
{
    private static SimpleDateFormat formatNormal = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat formatDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static SimpleDateFormat formatJson = new SimpleDateFormat("yyyy-MM-dd");


    public static Date toDate(String string)
    {
        Date dateFinal = new Date();
        try
        {
            dateFinal = formatJson.parse(string);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return dateFinal;
    }

    public static String toString(Date date)
    {
        return formatNormal.format(date);
    }

    public static String toDateTimeString(Date date)
    {
        if (date!=null) return formatDateTime.format(date);
        else return "no date";
    }

    public static String toJsonString(Date date)
    {
        return formatJson.format(date);
    }

    public static String formatDate(String string)
    {
        Date date = new Date();
        try
        {
            date = formatJson.parse(string);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return formatNormal.format(date);
    }
}
