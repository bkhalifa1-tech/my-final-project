package com.example.myfinalproject;

public class Appointment {
    private long id, userId;
    private String title, doctorName, date, time, location, phone;

    public Appointment() {}

    public Appointment(long userId, String title, String doctorName, String date, String time, String location, String phone) {
        this.userId = userId;
        this.title = title;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.location = location;
        this.phone = phone;
    }

    public Appointment(long id, long userId, String title, String doctorName, String date, String time, String location, String phone) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.location = location;
        this.phone = phone;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
