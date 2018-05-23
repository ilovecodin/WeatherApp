package com.example.manojd.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Weather {
    private String locationLabel;
    private String icon;
    private String summary;
    private long time;
    private double temperature;
    private double humidity;
    private double precipChance;
    private String timeZone;
    private Context context;

    public Weather() {
    }

    public Weather(String locationLabel, String icon, String summary, long time,
                   double temperature, double humidity, double precipChance, String timeZone) {
        this.locationLabel = locationLabel;
        this.icon = icon;
        this.summary = summary;
        this.time = time;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipChance = precipChance;
        this.timeZone = timeZone;
    }

    public String getLocationLabel() {
        return locationLabel;
    }

    public void setLocationLabel(String locationLabel) {
        this.locationLabel = locationLabel;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPrecipChance() {
        return precipChance;
    }

    public void setPrecipChance(double precipChance) {
        this.precipChance = precipChance;
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date date = new Date(time * 1000);
        return formatter.format(date);
    }

    public int getIconId() {
        int iconId = R.drawable.clear_day;

        switch(icon) {
            case "clear-day":
                iconId = R.drawable.clear_day;
                break;
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.cloudy_night;
                break;
        }

        return iconId;
    }
}
