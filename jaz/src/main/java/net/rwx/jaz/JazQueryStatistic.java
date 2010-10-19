package net.rwx.jaz;

import java.util.Calendar;

/**
 * Keep track of statistics about a JaZ query.
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazQueryStatistic
{
    /** Last time value was read from monitored server and updated. */
    private long timeLastValue;
    
    /** Last time value was requested by JaZ client. */
    private long timeLastGet;

    /** Last value that was read. */
    private String value;
    
    /**
     * Get current time (number of milliseconds since 01/01/1970).
     * 
     * @return
     */
    public long getTime()
    {
        return Calendar.getInstance().getTime().getTime();
    }
    
    /**
     * Getter method for the last time the value was read.
     * 
     * @return
     */
    public long getTimeLastValue()
    {
        return this.timeLastValue;
    }

    /**
     * Setter method for the last time the value was read.
     * 
     * @param timeLastValue
     */
    public void setTimeLastValue(long timeLastValue)
    {
        this.timeLastValue = timeLastValue;
    }

    /**
     * Getter method for the last value that was read.
     * @return
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Setter method for the last value that was read.
     * @param value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Getter method for the last time value was requested by JaZ client.
     *
     * @return
     */
    public long getTimeLastGet() {
        return this.timeLastGet;
    }

    /**
     * Setter method for the last time value was requested by JaZ client.
     *
     * @param timeLastGet
     */
    public void setTimeLastGet(long timeLastGet) {
        this.timeLastGet = timeLastGet;
    }
}
