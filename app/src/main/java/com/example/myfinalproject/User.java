package com.example.myfinalproject;

public class User {
    private long id;
    private String name, email, phone, lastPeriodDate, dueDate, profileImagePath;

    public User() {}

    public User(long id, String name, String email, String phone, String lastPeriodDate, String dueDate, String profileImagePath) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.lastPeriodDate = lastPeriodDate;
        this.dueDate = dueDate;
        this.profileImagePath = profileImagePath;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLastPeriodDate() { return lastPeriodDate; }
    public void setLastPeriodDate(String lastPeriodDate) { this.lastPeriodDate = lastPeriodDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }

    public boolean hasPregnancyData() {
        return lastPeriodDate != null && !lastPeriodDate.trim().isEmpty()
                && dueDate != null && !dueDate.trim().isEmpty();
    }
}
