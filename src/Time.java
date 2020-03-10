/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handin;

/**
 *
 * @author jmm4115
 */
public class Time implements Comparable<Time>{
    private int hours;
    private int mins;
    private int secs;
    
    public Time()
    {
        this(0, 0, 0);
    }
    
    public Time(int hours)
    {
        this(hours, 0, 0);
    }
    
    public Time(int hours, int mins)
    {
        this(hours, mins, 0);
    }
    
    public Time(int hours, int mins, int secs)
    {
        this.hours = 0;
        this.mins = 0;
        this.secs = 0;
        
        setHours(hours);
        setMinutes(mins);
        setSeconds(secs);
    }
    
    public void setSeconds(int secs)
    {
        if (secs >= 0 && secs <= 59)
            this.secs = secs;
    }
    
    public void setMinutes(int mins)
    {
        if (mins >= 0 && mins <= 59)
            this.mins = mins;
    }
    
    public void setHours(int hours)
    {
        if (hours >= 0 && hours <= 23)
            this.hours = hours;
    }
    
    public int getSeconds()
    {
        return secs;
    }
    
    public int getMinutes()
    {
        return mins;
    }
    
    public int getHours()
    {
        return hours;
    }
    
    @Override
    public boolean equals(Object otherTime)
    {
        if (otherTime instanceof Time)
        {
            Time t = (Time) otherTime;
            return this.compareTo(t) == 0;
        }
        return false;   
    }
    
    @Override
    public int compareTo(Time o) 
    {
        if (hours == o.hours)
        {
            if (mins == o.mins)
                return secs - o.secs;
            else
                return mins - o.mins;
        }
        return hours - o.hours;
    }
    
    @Override
    public String toString()
    {
        String format = "%02d:%02d:%02d";
        return String.format(format, hours, mins, secs);
    }
}
