package com.example.timeadder;

enum meridiem {
    AM,
    PM
}

public class timeStamp {
    // Always kept in 24 hr time.
    private int hour;
    private int minute;
    private int second;

    private meridiem meridian;

    public timeStamp(int hour, int minute, int second) {
        minute += second / 60;
        second %= 60;

        hour += minute / 60;
        minute %= 60;

        hour %= 24;

        this.hour = hour;
        this.minute = minute;
        this.second = second;

        if (hour < 12) {
            meridian = meridiem.AM;
        } else {
            meridian = meridiem.PM;
        }


    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String formatMilitaryTime() {
        String ret;

        if (hour == 0) {
            ret = "00:";
        } else if (hour < 10) {
            ret = "0" + hour + ":";
        } else {
            ret = String.valueOf(hour) + ":";
        }

        if (minute == 0) {
            ret += "00:";
        } else if (minute < 10) {
            ret += "0" + minute + ":";
        } else {
            ret += minute + ":";
        }

        if (second == 0) {
            ret += "00";
        } else if (second < 10) {
            ret += "0" + second;
        } else {
            ret += second;
        }

        return ret;
    }

    public String formatMeridiemTime() {

        String ret;

        if (hour - 12 > 0) {
            if (hour - 12 == 0) {
                ret = "00:";
            } else if (hour - 12 < 10) {
                ret = "0" + (hour - 12) + ":";
            } else {
                ret = String.valueOf(hour - 12) + ":";
            }
        } else {
            if (hour == 0) {
                ret = "00:";
            } else if (hour < 10) {
                ret = "0" + hour + ":";
            } else {
                ret = String.valueOf(hour) + ":";
            }
        }

        if (minute == 0) {
            ret += "00:";
        } else if (minute < 10) {
            ret += "0" + minute + ":";
        } else {
            ret += minute + ":";
        }

        if (second == 0) {
            ret += "00";
        } else if (second < 10) {
            ret += "0" + second;
        } else {
            ret += second;
        }

        ret += " " + meridian.toString();

        return ret;
    }

    public int getTotalTimeInSeconds() {
        return hour * 3600 + minute * 60 + second;
    }
}
