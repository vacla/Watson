package eu.minemania.watson.db;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

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

    public static long toMillis(int year, int month, int dayOfMonth, int hour, int minute, int second, String timezone)
    {
        TimeZone zone = null;
        if(timezone.equals(""))
        {
            zone = TimeZone.getDefault();
        }
        else if(!ZoneId.getAvailableZoneIds().contains(timezone))
        {
            zone = TimeZone.getTimeZone(ZoneId.of(Timezone.valueOf(timezone).getOffset()));
        }
        if(zone == null)
        {
            zone = TimeZone.getTimeZone(timezone);
        }
        _time.setTimeZone(zone);
        _time.set(year, month - 1, dayOfMonth, hour, minute, second);
        return _time.getTimeInMillis();
    }

    public static long toMillis(int[] ymd, int hour, int minute, int second)
    {
        if (ymd[0] != 0 && ymd[0] < 100)
        {
            ymd[0] += 2000;
        }
        return (ymd[0] == 0) ? toMillis(ymd[1], ymd[2], hour, minute, second) : toMillis(ymd[0], ymd[1], ymd[2], hour, minute, second, "");
    }

    public static String formatMonthDayTime(long millis)
    {
        _time.setTimeInMillis(millis);
        return String.format("%02d-%02d %02d:%02d:%02d", _time.get(Calendar.MONTH) + 1, _time.get(Calendar.DAY_OF_MONTH), _time.get(Calendar.HOUR_OF_DAY), _time.get(Calendar.MINUTE), _time.get(Calendar.SECOND));
    }

    public static String formatQueryTime(long millis)
    {
        _time.setTimeInMillis(millis);
        return String.format("%d.%d.%d %02d:%02d:%02d", _time.get(Calendar.DAY_OF_MONTH), _time.get(Calendar.MONTH) + 1, _time.get(Calendar.YEAR), _time.get(Calendar.HOUR_OF_DAY),	_time.get(Calendar.MINUTE), _time.get(Calendar.SECOND));
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

    public static long timeDiff(int month, int dayOfMonth, int hour, int minute, int second)
    {
        Calendar time = Calendar.getInstance();
        try
        {
            time.add(Calendar.MONTH, -month);
            time.add(Calendar.DAY_OF_MONTH, -dayOfMonth);
            time.add(Calendar.HOUR_OF_DAY, -hour);
            time.add(Calendar.MINUTE, -minute);
            time.add(Calendar.SECOND, -second);
            return time.getTimeInMillis();
        }
        catch (Exception e) {
            return 0;
        }
    }

    public enum Timezone
    {
        CEST("GMT+2"),
        PDT("GMT-7"),
        BST("GMT+1"),
        EDT("GMT-4"),
        CDT("GMT-45"),
        MDT("GMT-6"),
        AEDT("GMT+11"),
        ACDT("GMT+10:30"),
        ADT("GMT-3"),
        AKDT("GMT-8"),
        AMST("GMT+5"),
        AWST("GMT+8"),
        AZOST("GMT+0"),
        CHADT("GMT+13:45"),
        CHOST("GMT+9"),
        CIDST("GMT-4"),
        CLST("GMT-3"),
        EASST("GMT-5"),
        EEST("GMT+3"),
        EGST("GMT+0"),
        FJST("GMT+13"),
        FKST("GMT-3"),
        HDT("GMT-9"),
        HOVST("GMT+8"),
        IDT("GMT+3"),
        IRDT("GMT+4:30"),
        LHDT("GMT+11"),
        MSD("GMT+4"),
        NDT("GMT-2:30"),
        NFDT("GMT+12"),
        NZDT("GMT+13"),
        PMDT("GMT-2"),
        WEST("GMT+1"),
        ANAST("GMT+12"),
        AWDT("GMT+9"),
        AZST("GMT+5"),
        BRST("GMT-2"),
        IRKST("GMT+9"),
        IST("GMT+1"),
        KRAST("GMT+8"),
        KUYT("GMT+4"),
        MAGST("GMT+12"),
        NOVST("GMT+7"),
        OMSST("GMT+7"),
        PETST("GMT+12"),
        PYST("GMT-3"),
        TOST("GMT-14"),
        ULAST("GMT+9"),
        UYST("GMT-2"),
        VLAST("GMT+11"),
        WARST("GMT-3"),
        WAST("GMT+2"),
        WGST("GMT-2"),
        WST("GMT+13"),
        YAKST("GMT+10"),
        YEKST("GMT+6");

        private final String offset;

        private Timezone(String offset)
        {
            this.offset = offset;
        }

        public String getOffset()
        {
            return this.offset;
        }
    }
}