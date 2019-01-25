package uk.ac.ebi.ddi.pipeline.indexer.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeRanger {

    // The opening date of OmicsDI
    public static final Date START_TIME = new Date(1394323200000L);

    private Date fromDate;

    private Date toDate;

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public TimeRanger(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public static TimeRanger getTimeRanger(TimeRangerType timeRangerType) {
        Calendar cal;
        switch (timeRangerType) {
            case ALL_TIMES:
                return new TimeRanger(START_TIME, new Date());
            case LAST_7_DAYS:
                cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -7);
                return new TimeRanger(cal.getTime(), new Date());
            case LAST_10_DAYS:
                cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -10);
                return new TimeRanger(cal.getTime(), new Date());
            default:
                throw new IllegalArgumentException(timeRangerType.toString());
        }
    }
}
