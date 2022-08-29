package com.example.timeadder;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class timeStampManipulator {
    // For what order they were entered
    private ArrayList<timeStamp> log = new ArrayList<>();
    // Potential clear() insurance
    private ArrayList<timeStamp> prevous = new ArrayList<>();

    private int totalHour, totalMinute, totalSecond;

    public timeStampManipulator() {}

    public void add(timeStamp stamp) {
        log.add(stamp);
    }

    public void remove(int index) {
        if (index < log.size() && index >= 0) {
            log.remove(index);
        }
    }

    public ArrayList<timeStamp> getLog() {
        return log;
    }

    public ArrayList<timeStamp> getSorted() {
        ArrayList<timeStamp> sorted = new ArrayList<>(log);
        timeStamp temp;

        for (int i = 0; i < sorted.size(); i++) {
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(j).getHour() < sorted.get(i).getHour() ||
                        sorted.get(j).getHour() == sorted.get(i).getHour() && sorted.get(j).getMinute() < sorted.get(i).getMinute() ||
                        sorted.get(j).getHour() == sorted.get(i).getHour() && sorted.get(j).getMinute() == sorted.get(i).getMinute() && sorted.get(j).getSecond() < sorted.get(i).getSecond()) {
                    temp = sorted.get(j);
                    sorted.set(j, sorted.get(i));
                    sorted.set(i, temp);
                }
            }
        }

        return sorted;
    }

    public String sortedToString(boolean isMilitary) {
        ArrayList<timeStamp> sorted = getSorted();
        String ret = "";

        if (isMilitary) {
            for (int i = 0; i < sorted.size(); i++) {
                ret += sorted.get(i).formatMeridiemTime() + "\n";
            }
        } else {
            for (int i = 0; i < sorted.size(); i++) {
                ret += sorted.get(i).formatMilitaryTime() + "\n";
            }
        }

        return ret;
    }

    public String logToString(boolean isMilitary) {
        String ret = "";

        if (isMilitary) {
            for (int i = 0; i < log.size(); i++) {
                ret += log.get(i).formatMeridiemTime() + "\n";
            }
        } else {
            for (int i = 0; i < log.size(); i++) {
                ret += log.get(i).formatMilitaryTime() + "\n";
            }
        }

        return ret;
    }

    public void updateTimeTotal() {
        totalHour = 0;
        totalMinute = 0;
        totalSecond = 0;
        int tempDifference = 0;
        ArrayList<timeStamp> sorted = getSorted();

        for (int i = 0; i < log.size() - 1; i++) {
            if (i % 2 == 0) {
                tempDifference = sorted.get(i + 1).getTotalSeconds() - sorted.get(i).getTotalSeconds();
                totalHour += tempDifference / 3600;
                tempDifference %= 3600;
                totalMinute += tempDifference / 60;
                tempDifference %= 60;
                totalSecond += tempDifference;
            }
        }
    }

    public String totalToString() {
        updateTimeTotal();
        String ret = "";

        if (totalHour < 10) {
            ret += "0";
        }

        ret += totalHour + ":";

        if (totalMinute < 10) {
            ret += "0";
        }

        ret += totalMinute + ":";

        if (totalSecond < 10) {
            ret += "0";
        }

        ret += totalSecond;

        return ret;
    }

    public void clear() {
        prevous = new ArrayList<>(log);
        log.clear();
        totalHour = 0;
        totalMinute = 0;
        totalSecond = 0;
    }

}
