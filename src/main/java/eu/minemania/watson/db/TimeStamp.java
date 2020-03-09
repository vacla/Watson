package eu.minemania.watson.db;

import java.util.Calendar;
import java.util.Locale;

public class TimeStamp
{
    protected static Calendar _time = Calendar.getInstance();
    protected static Calendar _reference;

    static
    {
        _reference = Calendar.getInstance();
        _reference.add(Calendar.WEEK_OF_YEAR, 1);
    }

    public static long toMillis(int month, int dayOfMonth, int hour, int minute, int second)
    {
        _time.set(_reference.get(Calendar.YEAR), month - 1, dayOfMonth, hour, minute, second);
        if (_time.getTimeInMillis() > _reference.getTimeInMillis())
        {
            _time.add(Calendar.YEAR, -1);
        }
        return _time.getTimeInMillis();
    }

    public static long toMillis(int year, int month, int dayOfMonth, int hour, int minute, int second)
    {
        _time.set(year, month - 1, dayOfMonth, hour, minute, second);
        return _time.getTimeInMillis();
    }

    public static long toMillis(int[] ymd, int hour, int minute, int second)
    {
        if (ymd[0] != 0 && ymd[0] < 100)
        {
            ymd[0] += 2000;
        }
        return (ymd[0] == 0) ? toMillis(ymd[1], ymd[2], hour, minute, second) : toMillis(ymd[0], ymd[1], ymd[2], hour, minute, second);
    }

    public static String formatMonthDayTime(long millis)
    {
        _time.setTimeInMillis(millis);
        return String.format(Locale.US, "%02d-%02d %02d:%02d:%02d", _time.get(Calendar.MONTH) + 1, _time.get(Calendar.DAY_OF_MONTH), _time.get(Calendar.HOUR_OF_DAY), _time.get(Calendar.MINUTE), _time.get(Calendar.SECOND));
    }

    public static String formatQueryTime(long millis)
    {
        _time.setTimeInMillis(millis);
        return String.format(Locale.US, "%d.%d.%d %02d:%02d:%02d", _time.get(Calendar.DAY_OF_MONTH), _time.get(Calendar.MONTH) + 1, _time.get(Calendar.YEAR), _time.get(Calendar.HOUR_OF_DAY),	_time.get(Calendar.MINUTE), _time.get(Calendar.SECOND));
    }

    public static int[] parseYMD(String date)
    {
        int[] ymd = {0, 0, 0};
        String[] parts = date.split("-");
        if (parts.length == 2)
        {
            ymd[1] = Integer.parseInt(parts[0]);
            ymd[2] = Integer.parseInt(parts[1]);
        }
        else if (parts.length == 3)
        {
            ymd[0] = Integer.parseInt(parts[0]);
            ymd[1] = Integer.parseInt(parts[1]);
            ymd[2] = Integer.parseInt(parts[2]);
        }
        return ymd;
    }
}